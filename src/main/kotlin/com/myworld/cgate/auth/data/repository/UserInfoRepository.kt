package com.myworld.cgate.auth.data.repository

import com.myworld.cgate.auth.data.entity.UserInfo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserInfoRepository : MongoRepository<UserInfo, String>{

    fun findByToken(token: String): UserInfo?
}
