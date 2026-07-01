package org.example.holypresenter_songs

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform