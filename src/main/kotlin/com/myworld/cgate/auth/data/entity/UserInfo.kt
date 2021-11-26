package com.myworld.cgate.auth.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.util.*

@Document(collection = "userinfo")
class UserInfo(
    /**
     * 编号MYUSER+系统时间
     */
    @Id
    @Field("_id")
    var wid: String,
    var token: String,
    @Field("expire_time")
    var expireTime: Long,

    @Field("last_login_time")
    var lastLoginTime: Long = Date().time,
    @Field("last_login_useragent")
    var lastLoginUserAgent: String? = null,
    @Field("last_login_referer")
    var lastLoginReferer: String? = null,

    @Field("last_login_ip")
    var lastLoginIp: String? = null,
    @Field("last_login_city")
    var lastLoginCity: String? = null
) : Serializable
