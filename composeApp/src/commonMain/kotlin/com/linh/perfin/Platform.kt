package com.linh.perfin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform