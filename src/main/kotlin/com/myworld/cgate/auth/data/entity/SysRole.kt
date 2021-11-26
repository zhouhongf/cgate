package com.myworld.cgate.auth.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable


@Document(collection = "sysrole")
class SysRole(
    @Id
    var name: String
) : Serializable
