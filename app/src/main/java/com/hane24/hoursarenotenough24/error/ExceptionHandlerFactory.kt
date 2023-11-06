package com.hane24.hoursarenotenough24.error


class ExceptionHandlerFactory {
    companion object {
        fun create(exception: Exception): ExceptionHandler {
            return when (val customException = ExceptionFactory.create(exception)) {
                is NetworkException -> NetworkExceptionHandler(customException)
                is ServerException -> ServerExceptionHandler(customException)
                is LoginException -> LoginExceptionHandler(customException)
                else -> UnknownExceptionHandler(customException)
            }
        }
    }
}