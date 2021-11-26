package com.myworld.cgate.siteinfo.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.util.*

@Document(collection = "myfile")
class MyFile(
    @Id
    @Field("_id")
    var id: String,
    @Field("file_name")
    var fileName: String? = null,
    @Field("extension_type")
    var extensionType: String? = null,
    var size: Long? = null,

    @JsonIgnore
    @Field("file_byte")
    var fileByte: ByteArray? = null,
    @JsonIgnore
    @Field("big_file_id")
    var bigFileId: ObjectId? = null,

    @Field("version_number")
    var versionNumber: String? = null,
    @Field("official_name")
    var officialName: String,

    @JsonIgnore
    @Field("create_time")
    var createTime: Long = Date().time,
    @Field("update_time")
    var updateTime: Long? = null,
    @JsonIgnore
    var updater: String? = null
) : Serializable
