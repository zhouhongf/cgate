package com.myworld.cgate.siteinfo.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.util.*


@Document(collection = "writing")
class Writing(
    @Id
    @Field("_id")
    var id: String,
    var title: String,
    var author: String,
    var type: String,
    var content: String? = null,
    @Field("can_release")
    var canRelease: Boolean = false,

    @JsonIgnore
    @Field("create_time")
    var createTime: Long = Date().time,
    @JsonIgnore
    var updater: String? = null,

    @Field("update_time")
    var updateTime: Long = Date().time
) : Serializable
