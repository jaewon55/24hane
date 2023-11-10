package com.hane24.hoursarenotenough24.error

import retrofit2.HttpException


class NetworkException(message: String?): Exception(message) {
    constructor(): this(null)
}

class ServerException(message: String?): Exception(message) {
    constructor(): this(null)
}

class LoginException(message: String?): Exception(message) {
    constructor(): this(null)
}

class ExceptionFactory {
    companion object {
        fun create(exception: Exception): Exception {
            return when {
                exception is HttpException && exception.code() == 401 -> LoginException()
                exception is HttpException -> ServerException()
                else -> NetworkException()
            }
        }
    }
}

