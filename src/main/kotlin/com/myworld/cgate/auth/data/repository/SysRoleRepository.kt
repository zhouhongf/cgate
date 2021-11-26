package com.myworld.cgate.auth.data.repository

import com.myworld.cgate.auth.data.entity.SysRole
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface SysRoleRepository : MongoRepository<SysRole, String>
