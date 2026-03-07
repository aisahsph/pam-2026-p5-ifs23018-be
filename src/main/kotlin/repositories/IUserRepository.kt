package org.delcom.repositories

import org.delcom.entities.User

interface IUserRepository {
    suspend fun getById(userId: String): User?
    suspend fun getByUsername(username: String): User?
    suspend fun create(user: User): String

    // Fungsi update ini akan digunakan untuk mengubah profile, password, dan 'about'
    suspend fun update(id: String, newUser: User): Boolean

    suspend fun delete(id: String): Boolean
}