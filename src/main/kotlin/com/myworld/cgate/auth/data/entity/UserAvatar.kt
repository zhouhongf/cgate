package com.myworld.cgate.auth.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

@Document(collection = "user_avatar")
class UserAvatar(
    @Id
    @Field("_id")
    var wid: String,
    @Field("file_name")
    var fileName: String? = null,
    @Field("extension_type")
    var extensionType: String? = null,
    @Field("file_byte")
    var fileByte: ByteArray? = null
) : Serializable
