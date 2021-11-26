package com.myworld.cgate.auth.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.util.*

/**
 * 存储在MongoDB中，每天0点定时清空
 * 如果一个IP，当天访问次数超过1000次，则列入黑名单
 * 如果一个WID，当然访问次数超过1000次，则列入黑名单
 */
@Document(collection = "visit_watch")
class VisitWatch(
    @Id
    @Field("_id")
    var id: Long = Date().time,

    @Field("wid")
    var wid: String? = null,
    @Field("ip_address")
    var ipAddress: String? = null,
    var city: String? = null,

    @Field("visit_useragent")
    var visitUserAgent: String? = null,
    @Field("visit_referer")
    var visitReferer: String? = null,

    @Field("visit_path")
    var visitPath: String
) : Serializable
