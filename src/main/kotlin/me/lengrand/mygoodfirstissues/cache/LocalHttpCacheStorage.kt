package me.lengrand.mygoodfirstissues.cache

import io.ktor.client.features.cache.*
import io.ktor.client.features.cache.storage.*
import io.ktor.http.*
import io.ktor.util.*

@KtorExperimentalAPI
class LocalHttpCacheStorage : HttpCacheStorage() {
    override fun find(url: Url, varyKeys: Map<String, String>): HttpCacheEntry? {
        TODO("Not yet implemented")
    }

    override fun findByUrl(url: Url): Set<HttpCacheEntry> {
        TODO("Not yet implemented")
    }

    override fun store(url: Url, value: HttpCacheEntry) {
        TODO("Not yet implemented")
    }
}