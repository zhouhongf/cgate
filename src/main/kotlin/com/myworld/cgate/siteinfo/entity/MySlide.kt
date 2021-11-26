package com.myworld.cgate.siteinfo.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.util.*


@Document(collection = "myslide")
class MySlide(
    @Id
    @Field("_id")
    var id: String? = null,

    var title: String? = null,
    var description: String? = null,
    var image: String? = null,
    var link: String? = null,
    @Field("update_time")
    var updateTime: Long? = Date().time,
    @JsonIgnore
    var updater: String? = null
) : Serializable
