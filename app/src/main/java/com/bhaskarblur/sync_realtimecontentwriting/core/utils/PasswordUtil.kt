package com.bhaskarblur.sync_realtimecontentwriting.core.utils

import at.favre.lib.crypto.bcrypt.BCrypt


object PasswordUtil {
    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun verifyPassword(inputPassword: String, hashedPassword: String): Boolean {
        return try {
            BCrypt.verifyer().verify(inputPassword.toCharArray(), hashedPassword).verified
        } catch (e : Exception) {
            false
        }
    }
}