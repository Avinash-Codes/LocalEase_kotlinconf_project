package com.avinash.localease

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform