package com.myworld.cgate.auth.data.repository

import com.myworld.cgate.auth.data.entity.SysUser
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SysUserRepository : MongoRepository<SysUser, String>{

    fun findByUsername(username: String): SysUser?
}
