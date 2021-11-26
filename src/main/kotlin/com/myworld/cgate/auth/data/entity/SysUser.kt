package com.myworld.cgate.auth.data.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable
import java.util.*

@Document(collection = "sysuser")
class SysUser(
    /**
     * 编号MYUSER+系统时间
     */
    @Id
    @Field("_id")
    var wid: String,
    var sysroles: Set<String>,

    /**
     * 用户账号，即手机号
     */
    var username: String? = null,
    @JsonIgnore
    var usernameold: String? = null,
    @JsonIgnore
    var password: String? = null,
    @JsonIgnore
    var passwordold: String? = null,

    @JsonIgnore
    var creator: String? = null,
    @JsonIgnore
    var updater: String? = null,
    @JsonIgnore
    @Field("create_time")
    var createTime: Long = Date().time,
    @Field("update_time")
    var updateTime: Long = Date().time
    ) : Serializable
