package com.myworld.cgate.siteinfo.repository

import com.myworld.cgate.siteinfo.entity.MyFile
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MyFileRepository : MongoRepository<MyFile, String> {

    fun findByOfficialName(officialName: String): MyFile?
}
