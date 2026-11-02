package com.apps.stackoverflow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform