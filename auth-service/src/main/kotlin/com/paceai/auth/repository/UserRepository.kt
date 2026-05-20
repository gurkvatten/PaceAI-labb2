package com.paceai.auth.repository

import com.paceai.auth.entity.User
import org.springframework.data.jpa.repository.JpaRepository




interface UserRepository {
    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}