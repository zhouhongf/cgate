package com.myworld.cgate.siteinfo.repository

import com.myworld.cgate.siteinfo.entity.MySlide
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MySlideRepository : MongoRepository<MySlide, String> {
}
