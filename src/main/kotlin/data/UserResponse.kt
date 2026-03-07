package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    var id: String = "",
    var name: String = "",
    var username: String = "",
    var photo: String? = null, // [NEW] Ditambahkan agar Android bisa menerima data foto profil
    var about: String? = null, // [NEW] Tambahan properti untuk informasi Tentang
    var createdAt: Instant = Clock.System.now(),
    var updatedAt: Instant = Clock.System.now(),
)