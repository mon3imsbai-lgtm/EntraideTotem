package ma.entraide.totem.data.api

interface PublicContentApi {
    suspend fun getSnapshot(): PublicSnapshot
    suspend fun getChanges(sinceIso: String): PublicSnapshot
}

data class PublicSnapshot(
    val generatedAt: String,
    val settings: Map<String, String>,
    val services: List<Service>,
    val centers: List<Center>,
    val attractSlides: List<AttractSlide>
)

data class Service(
    val id: String,
    val titleAr: String,
    val titleFr: String,
    val shortDescriptionAr: String,
    val shortDescriptionFr: String,
    val imageUrl: String?,
    val updatedAt: String
)

data class Center(
    val id: String,
    val nameAr: String,
    val nameFr: String,
    val cityNameAr: String?,
    val cityNameFr: String?,
    val addressAr: String,
    val addressFr: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String?,
    val imageUrl: String?,
    val services: List<Service>,
    val updatedAt: String
)

data class AttractSlide(
    val id: String,
    val titleAr: String,
    val titleFr: String,
    val subtitleAr: String?,
    val subtitleFr: String?,
    val imageUrl: String,
    val displayDurationSeconds: Int,
    val active: Boolean,
    val updatedAt: String
)

