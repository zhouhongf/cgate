package com.myworld.cgate.auth.data.repository


import com.myworld.cgate.auth.data.entity.UserAvatar
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAvatarRepository : MongoRepository<UserAvatar, String>{
}
