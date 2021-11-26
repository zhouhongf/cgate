package com.myworld.cgate.siteinfo.repository

import com.myworld.cgate.siteinfo.entity.Writing
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface WritingRepository : MongoRepository<Writing, String> {

    fun findByType(type: String): MutableList<Writing>?
    fun findByCanReleaseAndTitle(canRelease: Boolean = true, title: String): Writing?
    fun findByCanReleaseAndTypeAndAuthor(canRelease: Boolean = true, type: String, author: String): MutableList<Writing>?
}
