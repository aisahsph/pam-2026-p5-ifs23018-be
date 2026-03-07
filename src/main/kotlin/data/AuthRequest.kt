package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.User

@Serializable
data class AuthRequest(
    var name: String = "",
    var username: String = "",
    var password: String = "",
    var newPassword: String = "",

    // [NEW] Tambahan properti untuk menangkap informasi Tentang
    var about: String? = null
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "username" to username,
            "password" to password,
            "newPassword" to newPassword,
            "about" to about // [NEW] Sertakan ke dalam map
        )
    }

    fun toEntity(): User {
        return User(
            name = name,
            username = username,
            password = password,
            about = about, // [NEW] Teruskan ke Entity User
            updatedAt = Clock.System.now()
        )
    }
}