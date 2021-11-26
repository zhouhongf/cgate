package com.myworld.cgate.auth.data.repository

import com.myworld.cgate.auth.data.entity.VisitWatch
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface VisitWatchRepository : MongoRepository<VisitWatch, Long>{
    fun countByIpAddress(ip: String): Int
    fun deleteAllByIpAddress(ip: String)

    fun countByWid(wid: String): Int
    fun deleteAllByWid(wid: String)
}
