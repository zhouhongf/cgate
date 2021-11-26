package com.myworld.cgate.auth.data.repository

import com.myworld.cgate.auth.data.entity.Pcds
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PcdsRepository : MongoRepository<Pcds, Long> {

    fun findByNameAndLevel(name: String, level: String): List<Pcds>
    fun findByFullname(fullname: String): Pcds?

}
