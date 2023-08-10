package com.example.springjpaplayground.repository

import com.example.springjpaplayground.entity.UserEntity
import com.example.springjpaplayground.model.UserSurnameProjection
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {

    @EntityGraph(value = "with-tags")
    fun findOneFullById(uuid: UUID): UserEntity?

    fun findSurnameViewById(uuid: UUID): UserSurnameProjection?
}
