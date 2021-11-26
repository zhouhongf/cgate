package com.myworld.cgate.auth.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

@Document(collection = "pcds")
class Pcds(
    @Id
    @Field("_id")
    var id: Long? = null,
    var fullname: String? = null,
    var citycode: String? = null,
    var adcode: String? = null,
    var name: String? = null,
    var center: String? = null,
    var level: String? = null
) : Serializable
