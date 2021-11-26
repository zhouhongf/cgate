package com.myworld.cgate.service

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoDatabase
import com.mongodb.client.gridfs.GridFSBucket
import com.mongodb.client.gridfs.GridFSBuckets
import com.myworld.cgate.auth.authenticate.config.UserContextHolder
import com.myworld.cgate.common.ApiResult
import com.myworld.cgate.common.PlayerType
import com.myworld.cgate.common.ResultUtil
import com.myworld.cgate.common.SecurityConstants
import com.myworld.cgate.siteinfo.entity.MyFile
import com.myworld.cgate.siteinfo.entity.MySlide
import com.myworld.cgate.siteinfo.entity.Writing
import com.myworld.cgate.siteinfo.repository.MyFileRepository
import com.myworld.cgate.siteinfo.repository.MySlideRepository
import com.myworld.cgate.siteinfo.repository.WritingRepository
import org.apache.logging.log4j.LogManager
import org.bson.BsonValue
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Service
class SiteInfoService {

    private val log = LogManager.getRootLogger()

    @Autowired
    private lateinit var writingRepository: WritingRepository
    @Autowired
    private lateinit var myFileRepository: MyFileRepository
    @Autowired
    private lateinit var gridFsTemplate: GridFsTemplate
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate
    @Autowired
    private lateinit var mySlideRepository: MySlideRepository

    fun getWritingList(type: String): ApiResult<*> {
        val userDetail = UserContextHolder.getUserContext() ?: return ResultUtil.failure(msg = "用户没有权限")
        val writings = writingRepository.findByType(type) ?: return ResultUtil.failure()
        val writingList: MutableList<Any> = ArrayList()
        for (writing in writings) {
            val map: MutableMap<String, Any> = HashMap()
            map["id"] = writing.id
            map["title"] = writing.title
            map["author"] = writing.author
            map["updateTime"] = writing.updateTime
            map["canRelease"] = writing.canRelease
            writingList.add(map)
        }
        return ResultUtil.success(data = writingList)
    }

    fun setWriting(writingRaw: Writing): ApiResult<*> {
        val userDetail = UserContextHolder.getUserContext() ?: return ResultUtil.failure(msg = "用户没有权限")
        val roles = userDetail.roles
        if (PlayerType.ADMIN_SUPER.name !in roles) {
            return ResultUtil.failure(msg = "用户没有权限发布文章")
        }
        val idRaw = writingRaw.id

        val id = SecurityConstants.WRITING_PREFIX + Date().time
        val writing = if (idRaw.isEmpty()) {
            Writing(id = id, title = writingRaw.title, author = writingRaw.author, type = writingRaw.type)
        } else {
            val optional = writingRepository.findById(idRaw)
            if (optional.isPresent) {
                optional.get()
            } else {
                Writing(id = id, title = writingRaw.title, author = writingRaw.author, type = writingRaw.type)
            }
        }

        writing.type = writingRaw.type
        writing.author = writingRaw.author
        writing.title = writingRaw.title
        writing.content = writingRaw.content
        writing.canRelease = writingRaw.canRelease
        writing.updater = userDetail.wid
        writing.updateTime = Date().time
        writingRepository.save(writing)
        return ResultUtil.success()
    }

    fun delWriting(id: String): ApiResult<*> {
        val userDetail = UserContextHolder.getUserContext() ?: return ResultUtil.failure(msg = "用户没有权限")
        val roles = userDetail.roles
        if (PlayerType.ADMIN_SUPER.name !in roles) {
            return ResultUtil.failure(msg = "用户没有权限删除文章")
        }
        writingRepository.deleteById(id)
        return ResultUtil.success()
    }

    fun getWriting(id: String): ApiResult<*> {
        val optional = writingRepository.findById(id)
        return if (optional.isPresent) {
            ResultUtil.success(data = optional.get())
        } else {
            ResultUtil.failure()
        }
    }

    fun getWritingByTitle(title: String): ApiResult<*> {
        val writing = writingRepository.findByCanReleaseAndTitle(title = title) ?: ResultUtil.failure()
        return ResultUtil.success(data = writing)
    }

    fun getWritingListByTypeAndAuthor(type: String, author: String): ApiResult<*> {
        val writings = writingRepository.findByCanReleaseAndTypeAndAuthor(type = type, author = author) ?: return ResultUtil.failure()
        val writingList: MutableList<Any> = ArrayList()
        for (writing in writings) {
            val map: MutableMap<String, Any> = HashMap()
            map["id"] = writing.id
            map["title"] = writing.title
            map["author"] = writing.author
            map["type"] = writing.type
            writingList.add(map)
        }
        return ResultUtil.success(data = writingList)
    }




    fun getFileList(): ApiResult<*> {
        val userDetail = UserContextHolder.getUserContext() ?: return ResultUtil.failure(msg = "用户没有权限")
        val myfiles = myFileRepository.findAll(Sort.by(Sort.Direction.DESC, "update_time"))
        return if (myfiles.isNullOrEmpty()) {
            ResultUtil.failure()
        } else {
            ResultUtil.success(data = myfiles)
        }
    }

    fun setFile(officialName: String, versionNumber: String, filePart: FilePart): ApiResult<*> {
        val userDetail = UserContextHolder.getUserContext() ?: return ResultUtil.failure(msg = "用户没有权限")
        val roles = userDetail.roles
        if (PlayerType.ADMIN_SUPER.name !in roles) {
            return ResultUtil.failure(msg = "用户没有权限发布文件")
        }

        // 设定了officialName为唯一值
        var myfile: MyFile? = myFileRepository.findByOfficialName(officialName)
        if (myfile == null) {
            val id = SecurityConstants.MYFILE_PREFIX + Date().time
            myfile = MyFile(id = id, officialName = officialName)
        }
        // 创建一个临时文件，从WebFlux的filePart中取出file
        val tempFile: Path = Files.createTempFile(SecurityConstants.MYFILE_PREFIX, filePart.filename())
        val file: File = tempFile.toFile()
        filePart.transferTo(file)

        // 删除旧的大文件
        val db: MongoDatabase = mongoTemplate.db
        val gridFSBucket: GridFSBucket = GridFSBuckets.create(db)
        val objectIdOld = myfile.bigFileId
        if (objectIdOld != null) {
            gridFSBucket.delete(objectIdOld)
        }

        // 保存文件，如果大于16M，则保存为大文件，否则保存为一般文件
        val size: Long = file.length()
        if (size > SecurityConstants.FILE_MAX_SIZE) {
            log.info("【超过16M的大文件，大小为：{}】", size)
            val objectId: ObjectId = gridFSBucket.uploadFromStream(file.name, file.inputStream())
            myfile.bigFileId = objectId
            myfile.fileByte = null
        } else {
            myfile.bigFileId = null
            myfile.fileByte = file.readBytes()
        }

        // 保存文件的其他属性
        myfile.extensionType = file.extension
        myfile.fileName = file.name
        myfile.size = size
        myfile.versionNumber = versionNumber
        myfile.updateTime = Date().time
        myfile.updater = userDetail.wid
        myFileRepository.save(myfile)
        return ResultUtil.success()
    }

    fun delFile(id: String): ApiResult<*> {
        val userDetail = UserContextHolder.getUserContext() ?: return ResultUtil.failure(msg = "用户没有权限")
        val roles = userDetail.roles
        if (PlayerType.ADMIN_SUPER.name !in roles) {
            return ResultUtil.failure(msg = "用户没有权限删除文件")
        }
        val optional = myFileRepository.findById(id)
        if (optional.isPresent) {
            val objectId = optional.get().bigFileId
            if (objectId != null) {
                val db: MongoDatabase = mongoTemplate.db
                val gridFSBucket: GridFSBucket = GridFSBuckets.create(db)
                gridFSBucket.delete(objectId)
            }
        }
        myFileRepository.deleteById(id)
        return ResultUtil.success()
    }

    fun getFile(officialName: String, ctx: ServerWebExchange): Mono<Void> {
        val response = ctx.response
        val myfile = myFileRepository.findByOfficialName(officialName) ?: return response.setComplete()

        val fileBytes = myfile.fileByte
        if (fileBytes != null) {
            val bodyDataBuffer = response.bufferFactory().wrap(fileBytes)
            return response.writeWith(Mono.just(bodyDataBuffer))
        } else {
            val objectId = myfile.bigFileId ?: return response.setComplete()

            val outputStream = ByteArrayOutputStream()
            val db: MongoDatabase = mongoTemplate.db
            val gridFSBucket: GridFSBucket = GridFSBuckets.create(db)
            gridFSBucket.downloadToStream(objectId, outputStream)

            val bodyDataBuffer = response.bufferFactory().wrap(outputStream.toByteArray())
            return response.writeWith(Mono.just(bodyDataBuffer))
        }
    }


    fun setSlide(title: String, description: String, link: String, base64: String): ApiResult<*> {
        val userDetail = UserContextHolder.getUserContext() ?: return ResultUtil.failure(msg = "用户没有权限")
        val roles = userDetail.roles
        if (PlayerType.ADMIN_SUPER.name !in roles) {
            return ResultUtil.failure(msg = "用户不是管理员")
        }
        val slideNum = mySlideRepository.count()
        if (slideNum > 4) {
            return ResultUtil.failure(msg = "Slide数量不能超过5张")
        }
        val id = SecurityConstants.MYSILDE_PREFIX + Date().time
        val mySlide = MySlide(id = id, title = title, description = description, image = base64, link = link, updater = userDetail.wid)
        mySlideRepository.save(mySlide)
        return ResultUtil.success()
    }

    fun delSlide(id: String): ApiResult<*> {
        val userDetail = UserContextHolder.getUserContext() ?: return ResultUtil.failure(msg = "用户没有权限")
        val roles = userDetail.roles
        if (PlayerType.ADMIN_SUPER.name !in roles) {
            return ResultUtil.failure(msg = "用户没有权限删除文件")
        }
        mySlideRepository.deleteById(id)
        return ResultUtil.success()
    }

    fun getSlideList(): ApiResult<*> {
        val mySlides = mySlideRepository.findAll(Sort.by(Sort.Direction.DESC, "updateTime"))
        return if (mySlides.isNullOrEmpty()) {
            ResultUtil.failure()
        } else {
            ResultUtil.success(data = mySlides)
        }
    }
}
