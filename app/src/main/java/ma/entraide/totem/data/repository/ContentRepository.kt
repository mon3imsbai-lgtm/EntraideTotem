package ma.entraide.totem.data.repository

import ma.entraide.totem.data.api.PublicSnapshot

interface ContentRepository {
    suspend fun snapshot(): PublicSnapshot
    suspend fun sync(): SyncResult
}

data class SyncResult(
    val usedCache: Boolean,
    val syncedAt: String?,
    val errorMessage: String?
)

