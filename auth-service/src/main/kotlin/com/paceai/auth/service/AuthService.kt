package com.paceai.auth.service

import com.paceai.auth.entity.User
import com.paceai.auth.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(username: String, email: String, password: String): String {
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("Användarnamnet är redan taget")
        }
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("E-postadressen är redan registrerad")
        }

        val user = User(
            username = username,
            email = email,
            password = passwordEncoder.encode(password)!!
        )
        val saved = userRepository.save(user)
        return jwtService.generateToken(saved.id, saved.username)
    }

    fun login(username: String, password: String): String {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("Fel användarnamn eller lösenord")

        if (!passwordEncoder.matches(password, user.password)) {
            throw IllegalArgumentException("Fel användarnamn eller lösenord")
        }

        return jwtService.generateToken(user.id, user.username)
    }
}