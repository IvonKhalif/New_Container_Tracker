package com.example.containertracker.domain.user

class SignInUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        userName: String,
        password: String
    ) = userRepository.signIn(userName, password)
}