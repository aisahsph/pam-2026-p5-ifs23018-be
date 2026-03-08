package org.delcom

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.* // PENTING: Untuk akses file statis
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.* // PENTING: Untuk routing
import kotlinx.serialization.json.Json
import org.delcom.helpers.JWTConstants
import org.delcom.helpers.configureDatabases
import org.delcom.module.appModule
import org.koin.ktor.plugin.Koin
import java.io.File

fun main(args: Array<String>) {
    // Memuat file .env agar variabelnya bisa dibaca oleh application.yaml
    val dotenv = dotenv {
        directory = "."
        ignoreIfMissing = true
    }

    dotenv.entries().forEach {
        System.setProperty(it.key, it.value)
    }

    EngineMain.main(args)
}

fun Application.module() {
    // Ambil secret dari config (application.yaml)
    val jwtSecret = environment.config.propertyOrNull("ktor.jwt.secret")?.getString() ?: "default_secret"

    install(Authentication) {
        jwt(JWTConstants.NAME) {
            realm = JWTConstants.REALM

            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(JWTConstants.ISSUER)
                    .withAudience(JWTConstants.AUDIENCE)
                    .build()
            )

            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                if (!userId.isNullOrBlank()) JWTPrincipal(credential.payload) else null
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("status" to "error", "message" to "Token tidak valid atau kedaluwarsa")
                )
            }
        }
    }

    install(CORS) {
        anyHost() // Izinkan akses dari IP mana pun (Frontend/HP/Browser)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
    }

    install(ContentNegotiation) {
        json(Json {
            explicitNulls = false
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }

    install(Koin) {
        modules(appModule(jwtSecret))
    }

    // Konfigurasi Database
    configureDatabases()

    // --- FIX UNTUK FOTO ---
    routing {
        // Mengizinkan folder "uploads" di laptop diakses lewat URL /uploads
        // Contoh: http://172.28.43.54:8000/uploads/foto.jpg
        staticFiles("/uploads", File("uploads"))

        // Memanggil route API lainnya
        configureRouting()
    }
}