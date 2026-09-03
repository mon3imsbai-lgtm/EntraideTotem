package ma.entraide.totem.map

data class MapPoint(
    val id: String,
    val labelAr: String,
    val labelFr: String,
    val latitude: Double,
    val longitude: Double
)

interface MapProvider {
    fun render(points: List<MapPoint>, selectedPointId: String?)
}

