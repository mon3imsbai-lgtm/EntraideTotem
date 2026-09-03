package ma.entraide.totem.ui

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material.icons.rounded.Woman
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import java.net.HttpURLConnection
import java.net.URL
import ma.entraide.totem.R

private const val SnapshotUrl = "http://10.0.2.2:4000/api/public/snapshot"
private const val EmulatorHostBaseUrl = "http://10.0.2.2:4000"
private const val LocalhostBaseUrl = "http://localhost:4000"
private const val LoopbackBaseUrl = "http://127.0.0.1:4000"

private fun emulatorReachableUrl(url: String?): String? {
    val value = url?.takeIf { it.isNotBlank() } ?: return null
    return value
        .replace(LocalhostBaseUrl, EmulatorHostBaseUrl)
        .replace(LoopbackBaseUrl, EmulatorHostBaseUrl)
}

private enum class LocaleMode { AR, FR }

private enum class TotemScreen {
    Home,
    Institution,
    InterventionAreas,
    Services,
    Programs,
    Centers,
    Statistics,
    Assistant,
    CenterDetails,
    Idle
}

private object KioskColors {
    val Ink = Color(0xFF103B31)
    val Green = Color(0xFF0E5A45)
    val GreenDark = Color(0xFF063229)
    val GreenMid = Color(0xFF2C7967)
    val GreenSoft = Color(0xFFE7F1EC)
    val Cream = Color(0xFFF8F4EC)
    val Warm = Color(0xFFFBF8F1)
    val Surface = Color(0xFFFFFFFF)
    val Line = Color(0xFFE3DCCF)
    val Muted = Color(0xFF66746E)
    val MutedStrong = Color(0xFF465650)
    val Gold = Color(0xFFDCA83D)
    val GoldSoft = Color(0xFFF4E6C5)
    val Red = Color(0xFFC4493D)
    val Purple = Color(0xFF6557A8)
}

private data class NavTile(
    val screen: TotemScreen,
    val icon: String,
    val titleAr: String,
    val titleFr: String,
    val bodyAr: String,
    val bodyFr: String
)

@Composable
fun TotemApp() {
    var locale by remember { mutableStateOf(LocaleMode.AR) }
    var screen by remember { mutableStateOf(TotemScreen.Home) }
    var previousScreen by remember { mutableStateOf(TotemScreen.Home) }
    var selectedCenter by remember { mutableStateOf<CenterContent?>(null) }
    var snapshotState by remember { mutableStateOf<SnapshotState>(SnapshotState.Loading) }
    var snapshotReloadToken by remember { mutableLongStateOf(0L) }
    var lastInteraction by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(snapshotReloadToken) {
        snapshotState = SnapshotState.Loading
        snapshotState = runCatching { PublicSnapshotClient.load() }
            .fold(onSuccess = { SnapshotState.Ready(it) }, onFailure = { SnapshotState.Error(it.message ?: "Network error") })
    }

    val snapshot = (snapshotState as? SnapshotState.Ready)?.snapshot
    val returnHomeSeconds = snapshot?.settings?.get("return_home_timeout_seconds")?.toLongOrNull() ?: 75L
    val idleSeconds = snapshot?.settings?.get("idle_timeout_seconds")?.toLongOrNull() ?: 45L

    LaunchedEffect(lastInteraction, screen, returnHomeSeconds) {
        if (screen != TotemScreen.Home && screen != TotemScreen.Idle) {
            delay(returnHomeSeconds * 1000)
            if (System.currentTimeMillis() - lastInteraction >= returnHomeSeconds * 1000) {
                selectedCenter = null
                screen = TotemScreen.Home
            }
        }
    }

    LaunchedEffect(lastInteraction, screen, idleSeconds) {
        if (screen == TotemScreen.Home) {
            delay(idleSeconds * 1000)
            if (System.currentTimeMillis() - lastInteraction >= idleSeconds * 1000) {
                screen = TotemScreen.Idle
            }
        }
    }

    LaunchedEffect(screen) {
        if (screen == TotemScreen.Idle) {
            while (true) {
                runCatching { PublicSnapshotClient.load() }
                    .onSuccess { snapshotState = SnapshotState.Ready(it) }
                delay(60_000)
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides if (locale == LocaleMode.AR) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            awaitPointerEvent()
                            lastInteraction = System.currentTimeMillis()
                            if (screen == TotemScreen.Idle) screen = TotemScreen.Home
                        }
                    }
                },
            color = KioskColors.Cream
        ) {
            when (val state = snapshotState) {
                SnapshotState.Loading -> LoadingScreen(locale)
                is SnapshotState.Error -> OfflineScreen(locale, state.message) { snapshotReloadToken++ }
                is SnapshotState.Ready -> {
                    if (screen == TotemScreen.Idle) {
                        IdleSlideshow(locale, state.snapshot) {
                            lastInteraction = System.currentTimeMillis()
                            screen = TotemScreen.Home
                        }
                    } else {
                        KioskShell(
                            locale = locale,
                            screen = screen,
                            showBack = screen != TotemScreen.Home,
                            onLocaleToggle = { locale = if (locale == LocaleMode.AR) LocaleMode.FR else LocaleMode.AR },
                            onHome = {
                                selectedCenter = null
                                screen = TotemScreen.Home
                            },
                            onBack = {
                                screen = previousScreen
                                if (screen != TotemScreen.CenterDetails) selectedCenter = null
                            }
                        ) {
                            AnimatedContent(
                                targetState = screen,
                                transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
                                label = "screen"
                            ) { activeScreen ->
                                when (activeScreen) {
                                    TotemScreen.Home -> HomeScreen(locale, state.snapshot) {
                                        previousScreen = TotemScreen.Home
                                        screen = it
                                    }
                                    TotemScreen.Institution -> InstitutionScreen(locale, state.snapshot)
                                    TotemScreen.InterventionAreas -> InterventionAreasScreen(locale, state.snapshot)
                                    TotemScreen.Services -> ServicesScreen(locale, state.snapshot) {
                                        previousScreen = TotemScreen.Services
                                        screen = TotemScreen.Centers
                                    }
                                    TotemScreen.Programs -> ProgramsScreen(locale, state.snapshot)
                                    TotemScreen.Centers -> CentersScreen(locale, state.snapshot) {
                                        selectedCenter = it
                                        previousScreen = TotemScreen.Centers
                                        screen = TotemScreen.CenterDetails
                                    }
                                    TotemScreen.Statistics -> StatisticsScreen(locale, state.snapshot)
                                    TotemScreen.Assistant -> AssistantScreen(
                                        locale = locale,
                                        snapshot = state.snapshot,
                                        onShowCenters = {
                                            selectedCenter = it
                                            previousScreen = TotemScreen.Assistant
                                            screen = TotemScreen.Centers
                                        },
                                        onShowServices = {
                                            previousScreen = TotemScreen.Assistant
                                            screen = TotemScreen.Services
                                        }
                                    )
                                    TotemScreen.CenterDetails -> CenterDetailsScreen(locale, selectedCenter)
                                    TotemScreen.Idle -> Unit
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KioskShell(
    locale: LocaleMode,
    screen: TotemScreen,
    showBack: Boolean,
    onLocaleToggle: () -> Unit,
    onHome: () -> Unit,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 34.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Header(locale, screen, showBack, onLocaleToggle, onHome, onBack)
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

@Composable
private fun Header(
    locale: LocaleMode,
    screen: TotemScreen,
    showBack: Boolean,
    onLocaleToggle: () -> Unit,
    onHome: () -> Unit,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(8.dp),
        color = KioskColors.Warm,
        border = BorderStroke(1.dp, KioskColors.Line)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (showBack) {
                    HeaderIconButton(Icons.AutoMirrored.Rounded.ArrowBack, if (locale == LocaleMode.AR) "رجوع" else "Retour", onBack, enabled = true)
                    HeaderIconButton(Icons.Rounded.Home, if (locale == LocaleMode.AR) "الرئيسية" else "Accueil", onHome, enabled = true)
                } else {
                    Spacer(Modifier.width(98.dp))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BrandMark()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (locale == LocaleMode.AR) "التعاون الوطني" else "Entraide Nationale", color = KioskColors.Ink, fontSize = 24.sp, fontWeight = FontWeight.Black, maxLines = 1)
                    Text(screenTitle(screen, locale), color = KioskColors.MutedStrong, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            HeaderLanguageButton(if (locale == LocaleMode.AR) "FR" else "عربي", onLocaleToggle)
        }
    }
}

@Composable
private fun BrandMark() {
    Box(
        modifier = Modifier
            .width(54.dp)
            .height(40.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.entraide_logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun HeaderIconButton(icon: ImageVector, label: String, onClick: () -> Unit, enabled: Boolean) {
    val alpha = if (enabled) 1f else 0.32f
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(KioskColors.GreenSoft.copy(alpha = alpha))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = label, tint = KioskColors.Green.copy(alpha = alpha), modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun HeaderLanguageButton(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(KioskColors.GoldSoft)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(Icons.Rounded.Language, contentDescription = null, tint = KioskColors.Ink, modifier = Modifier.size(20.dp))
        Text(label, color = KioskColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun LogoLockup(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.width(112.dp).height(62.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, KioskColors.Line)
    ) {
        Image(
            painter = painterResource(R.drawable.entraide_logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun HomeScreen(locale: LocaleMode, snapshot: PublicSnapshot, onNavigate: (TotemScreen) -> Unit) {
    val hero = snapshot.homeSections.firstOrNull { it.key == "home_hero" }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        HeroStage(locale, hero, onNavigate)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false
        ) {
            items(homeTiles()) { tile ->
                ActionTile(tile, locale) { onNavigate(tile.screen) }
            }
        }
        StatisticStrip(locale, snapshot.statistics.take(3))
    }
}

@Composable
private fun HeroStage(locale: LocaleMode, hero: HomeSectionContent?, onNavigate: (TotemScreen) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(205.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(KioskColors.GreenDark)
    ) {
        if (!hero?.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = emulatorReachableUrl(hero?.imageUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = if (locale == LocaleMode.AR) {
                            listOf(KioskColors.GreenDark.copy(alpha = 0.96f), KioskColors.Green.copy(alpha = 0.72f), KioskColors.GreenSoft.copy(alpha = 0.08f))
                        } else {
                            listOf(KioskColors.GreenSoft.copy(alpha = 0.08f), KioskColors.Green.copy(alpha = 0.72f), KioskColors.GreenDark.copy(alpha = 0.96f))
                        }
                    )
                )
        )
        InstitutionalPattern(Modifier.matchParentSize())
        LogoLockup(
            modifier = Modifier
                .align(if (locale == LocaleMode.AR) Alignment.BottomStart else Alignment.BottomEnd)
                .padding(18.dp)
        )
        Column(
            modifier = Modifier
                .align(if (locale == LocaleMode.AR) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(horizontal = 28.dp, vertical = 22.dp)
                .fillMaxWidth(0.78f),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            horizontalAlignment = if (locale == LocaleMode.AR) Alignment.End else Alignment.Start
        ) {
            Text(hero?.title(locale) ?: if (locale == LocaleMode.AR) "التعاون الوطني" else "Entraide Nationale", color = Color.White, fontSize = 36.sp, lineHeight = 42.sp, fontWeight = FontWeight.Black)
            Text(hero?.subtitle(locale) ?: if (locale == LocaleMode.AR) "في خدمة الإنسان والتنمية الاجتماعية" else "Au service de l'humain et du developpement social", color = Color.White.copy(alpha = 0.92f), fontSize = 19.sp, lineHeight = 26.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth(0.86f)) {
                KioskButton(if (locale == LocaleMode.AR) "اكتشف المؤسسة" else "Decouvrir l'institution", { onNavigate(TotemScreen.Institution) }, Modifier.weight(1f))
                KioskButton(if (locale == LocaleMode.AR) "المساعدة" else "Besoin d'aide ?", { onNavigate(TotemScreen.Assistant) }, Modifier.weight(1f), quiet = true)
            }
        }
    }
}

@Composable
private fun ActionTile(tile: NavTile, locale: LocaleMode, onClick: () -> Unit) {
    val featured = tile.screen == TotemScreen.Institution || tile.screen == TotemScreen.Assistant
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.985f else 1f, label = "tileScale")
    val color by animateColorAsState(
        if (pressed) KioskColors.GreenSoft else if (featured) KioskColors.Warm else KioskColors.Surface,
        label = "tileColor"
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(102.dp)
            .scale(scale)
            .shadow(if (featured) 8.dp else 3.dp, RoundedCornerShape(8.dp), clip = false)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        border = BorderStroke(1.dp, KioskColors.Line)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconBadge(tile.icon, compact = false)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = if (locale == LocaleMode.AR) Alignment.End else Alignment.Start
            ) {
                Text(tile.title(locale), color = KioskColors.Ink, fontSize = 18.sp, lineHeight = 22.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(tile.body(locale), color = KioskColors.MutedStrong, fontSize = 12.sp, lineHeight = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun StatisticStrip(locale: LocaleMode, stats: List<StatisticContent>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        stats.ifEmpty { fallbackStats() }.forEach { stat ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(76.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = KioskColors.GreenSoft),
                border = BorderStroke(1.dp, KioskColors.Line)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    NumericText(stat.number, color = KioskColors.Purple, fontSize = 22)
                    Text(stat.label(locale), color = KioskColors.Ink, fontSize = 13.sp, lineHeight = 17.sp, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun InstitutionScreen(locale: LocaleMode, snapshot: PublicSnapshot) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            FeatureIntro(
                title = if (locale == LocaleMode.AR) "من نحن" else "Qui sommes-nous ?",
                body = if (locale == LocaleMode.AR) "مؤسسة عمومية ذات رسالة اجتماعية تعمل على تقريب الدعم والمواكبة من الفئات في وضعية هشاشة، وتيسير الولوج إلى الخدمات والبرامج الاجتماعية." else "Un etablissement public a mission sociale qui rapproche l'appui, l'orientation et l'accompagnement des publics en situation de vulnerabilite."
            )
        }
        if (snapshot.timelineEvents.isNotEmpty()) {
            item { Text(if (locale == LocaleMode.AR) "محطات أساسية" else "Reperes cles", color = KioskColors.Ink, fontSize = 26.sp, fontWeight = FontWeight.Black) }
            items(snapshot.timelineEvents.take(4)) { event ->
                TimelineRow(locale, event)
            }
        }
        item {
            Text(if (locale == LocaleMode.AR) "المهام" else "Missions", color = KioskColors.Ink, fontSize = 26.sp, fontWeight = FontWeight.Black)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                MissionCard(Icons.Rounded.Favorite, if (locale == LocaleMode.AR) "العون والمساعدة" else "Aide et assistance", if (locale == LocaleMode.AR) "استقبال وتوجيه ومواكبة الفئات المحتاجة." else "Accueil, orientation et accompagnement.", Modifier.weight(1f))
                MissionCard(Icons.Rounded.Groups, if (locale == LocaleMode.AR) "النهوض الاجتماعي" else "Promotion sociale", if (locale == LocaleMode.AR) "دعم الأسرة والمبادرات ذات الأثر الاجتماعي." else "Appui aux familles et aux initiatives sociales.", Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                MissionCard(Icons.Rounded.School, if (locale == LocaleMode.AR) "التكوين" else "Formation", if (locale == LocaleMode.AR) "تقوية القدرات والتأهيل للاندماج." else "Renforcement des capacites et qualification.", Modifier.weight(1f))
                MissionCard(Icons.Rounded.Navigation, if (locale == LocaleMode.AR) "الإدماج" else "Insertion", if (locale == LocaleMode.AR) "تيسير الولوج إلى البرامج والمراكز." else "Faciliter l'acces aux programmes et centres.", Modifier.weight(1f))
            }
        }
        if (snapshot.targetAudiences.isNotEmpty()) {
            item {
                SectionCard(if (locale == LocaleMode.AR) "الفئات المستهدفة" else "Populations cibles") {
                    ChipWrap(snapshot.targetAudiences.map { it.title(locale) })
                }
            }
        }
    }
}

@Composable
private fun InterventionAreasScreen(locale: LocaleMode, snapshot: PublicSnapshot) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        item(span = { GridItemSpan(2) }) {
            FeatureIntro(
                title = if (locale == LocaleMode.AR) "مجالات التدخل" else "Domaines d'intervention",
                body = if (locale == LocaleMode.AR) "محاور عمل مترابطة تجمع بين الاستقبال، الرعاية، الإدماج، والعمل الإنساني." else "Des axes complementaires autour de l'accueil, la prise en charge, l'insertion et l'action humanitaire."
            )
        }
        items(snapshot.interventionAreas) { area ->
            VisualInfoCard(area.icon ?: "area", area.title(locale), area.description(locale))
        }
    }
}

@Composable
private fun ServicesScreen(locale: LocaleMode, snapshot: PublicSnapshot, onFindCenters: () -> Unit) {
    var audienceFilter by remember { mutableStateOf<String?>(null) }
    val allLabel = if (locale == LocaleMode.AR) "الكل" else "Tous"
    val services = snapshot.services.filter { service ->
        audienceFilter == null || service.targetAudience(locale).contains(audienceFilter.orEmpty(), ignoreCase = true)
    }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FeatureIntro(
            title = if (locale == LocaleMode.AR) "الخدمات" else "Services",
            body = if (locale == LocaleMode.AR) "اختر الفئة المستفيدة أو استعرض الخدمات المتاحة للوصول إلى المركز المناسب." else "Selectionnez un public ou consultez les prestations disponibles."
        )
        FilterRow(listOf(allLabel) + snapshot.targetAudiences.map { it.title(locale) }, audienceFilter ?: allLabel) {
            audienceFilter = if (it == allLabel) null else it
        }
        if (services.size <= 1) {
            val service = services.firstOrNull() ?: snapshot.services.firstOrNull()
            if (service != null) {
                LargeServicePanel(locale, service, onFindCenters, Modifier.weight(1f))
            } else {
                EmptyState(if (locale == LocaleMode.AR) "لا توجد خدمات منشورة حاليا" else "Aucun service publie")
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                items(services) { service ->
                    ServiceCard(locale, service, onFindCenters)
                }
            }
        }
    }
}

@Composable
private fun ProgramsScreen(locale: LocaleMode, snapshot: PublicSnapshot) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(snapshot.programs) { program ->
            SectionCard(program.title(locale)) {
                Text(program.description(locale), color = KioskColors.Muted, fontSize = 17.sp, lineHeight = 23.sp)
                Text(program.targetAudience(locale), color = KioskColors.Green, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CentersScreen(locale: LocaleMode, snapshot: PublicSnapshot, onDetails: (CenterContent) -> Unit) {
    var search by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedRegion by remember { mutableStateOf<String?>(null) }
    var selectedService by remember { mutableStateOf<String?>(null) }
    val centers = snapshot.centers.filter { center ->
        val haystack = listOf(center.name(locale), center.cityName(locale), center.provinceName(locale), center.regionName(locale), center.centerTypeName(locale), center.address(locale))
            .joinToString(" ")
            .lowercase()
        (search.isBlank() || haystack.contains(search.lowercase())) &&
            (selectedType == null || center.centerTypeName(locale) == selectedType) &&
            (selectedRegion == null || center.regionName(locale) == selectedRegion) &&
            (selectedService == null || center.services.any { service -> service.title(locale) == selectedService })
    }
    val mappableCenters = centers.filter { it.mapPosition() != null }
    var activeCenter by remember { mutableStateOf<CenterContent?>(null) }
    LaunchedEffect(search, selectedType, selectedRegion, selectedService, locale) {
        activeCenter = if (mappableCenters.size == 1) mappableCenters.firstOrNull() else null
    }
    val types = snapshot.centers.map { it.centerTypeName(locale) }.distinct().take(4)
    val regions = snapshot.centers.map { it.regionName(locale) }.filter { it.isNotBlank() }.distinct().take(4)
    val services = snapshot.services.map { it.title(locale) }.filter { it.isNotBlank() }.distinct().take(4)
    val allLabel = if (locale == LocaleMode.AR) "الكل" else "Tous"
    val resetFilters = {
        search = ""
        selectedType = null
        selectedRegion = null
        selectedService = null
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SearchField(
            value = search,
            onValueChange = { search = it },
            placeholder = if (locale == LocaleMode.AR) "ابحث عن مركز، مدينة، جهة أو إقليم..." else "Rechercher un centre, une ville, une region..."
        )
        MapFilterGroup(if (locale == LocaleMode.AR) "الجهة" else "Region", listOf(allLabel) + regions, selectedRegion ?: allLabel) { selectedRegion = if (it == allLabel) null else it }
        MapFilterGroup(if (locale == LocaleMode.AR) "نوع المركز" else "Type", listOf(allLabel) + types, selectedType ?: allLabel) { selectedType = if (it == allLabel) null else it }
        MapFilterGroup(if (locale == LocaleMode.AR) "الخدمة" else "Service", listOf(allLabel) + services, selectedService ?: allLabel) { selectedService = if (it == allLabel) null else it }
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                centers.isEmpty() -> MapEmptyState(
                    if (locale == LocaleMode.AR) "لا توجد مراكز مطابقة لمعايير البحث." else "Aucun centre ne correspond aux criteres.",
                    if (locale == LocaleMode.AR) "إعادة ضبط البحث" else "Reinitialiser",
                    resetFilters
                )
                mappableCenters.isEmpty() -> MapEmptyState(
                    if (locale == LocaleMode.AR) "المراكز المطابقة تحتاج إلى تحديد إحداثيات صحيحة قبل عرضها على الخريطة." else "Les centres trouves doivent avoir des coordonnees valides pour apparaitre sur la carte.",
                    if (locale == LocaleMode.AR) "إعادة ضبط البحث" else "Reinitialiser",
                    resetFilters
                )
                else -> {
                    MoroccoMap(mappableCenters, activeCenter, locale, onShowAll = { activeCenter = null }) { activeCenter = it }
                    activeCenter?.let {
                        CenterPreview(
                            locale = locale,
                            center = it,
                            onDetails = onDetails,
                            modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MapFilterGroup(label: String, options: List<String>, selected: String?, onSelected: (String?) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = KioskColors.MutedStrong, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        FilterRow(options, selected, onSelected)
    }
}

@Composable
private fun MapEmptyState(message: String, actionLabel: String, onReset: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(KioskColors.Surface),
        border = BorderStroke(1.dp, KioskColors.Line),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(22.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = KioskColors.Green, modifier = Modifier.size(42.dp))
            Spacer(Modifier.height(14.dp))
            Text(message, color = KioskColors.MutedStrong, fontSize = 19.sp, lineHeight = 27.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(18.dp))
            KioskButton(actionLabel, onReset, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun MoroccoMap(
    centers: List<CenterContent>,
    activeCenter: CenterContent?,
    locale: LocaleMode,
    onShowAll: () -> Unit = {},
    onSelect: (CenterContent) -> Unit
) {
    val context = LocalContext.current
    var mapRef by remember { mutableStateOf<MapboxMap?>(null) }
    Box(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, KioskColors.Line, RoundedCornerShape(8.dp)),
            factory = {
                Mapbox.getInstance(context)
                MapView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    onCreate(null)
                    onStart()
                    onResume()
                    getMapAsync { map ->
                        mapRef = map
                        map.setStyle(Style.Builder().fromJson(moroccoBaseMapStyleJson()))
                        map.uiSettings.isRotateGesturesEnabled = false
                        map.uiSettings.isTiltGesturesEnabled = false
                        map.uiSettings.isCompassEnabled = false
                        map.uiSettings.isAttributionEnabled = true
                        updateMapMarkers(map, context, centers, activeCenter, locale, onSelect)
                        focusMap(map, centers, activeCenter, animate = false)
                        post {
                            focusMap(map, centers, activeCenter, animate = false)
                        }
                    }
                }
            },
            update = { mapView ->
                mapView.getMapAsync { map ->
                    mapRef = map
                    updateMapMarkers(map, context, centers, activeCenter, locale, onSelect)
                    focusMap(map, centers, activeCenter, animate = true)
                }
            }
        )
        Column(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            MapControlButton("+") { mapRef?.animateCamera(CameraUpdateFactory.zoomIn(), 250) }
            MapControlButton("-") { mapRef?.animateCamera(CameraUpdateFactory.zoomOut(), 250) }
            Button(
                onClick = {
                    onShowAll()
                    mapRef?.let { focusMap(it, centers, null, animate = true) }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KioskColors.Surface, contentColor = KioskColors.Ink),
                border = BorderStroke(1.dp, KioskColors.Line)
            ) {
                Text(if (locale == LocaleMode.AR) "عرض جميع المراكز" else "Tous les centres", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MapControlButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = KioskColors.Surface, contentColor = KioskColors.Green),
        border = BorderStroke(1.dp, KioskColors.Line),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Text(label, fontSize = 25.sp, fontWeight = FontWeight.Black)
    }
}

private fun updateMapMarkers(
    map: MapboxMap,
    context: android.content.Context,
    centers: List<CenterContent>,
    activeCenter: CenterContent?,
    locale: LocaleMode,
    onSelect: (CenterContent) -> Unit
) {
    val normalIcon = buildCenterMarkerIcon(context, selected = false)
    val selectedIcon = buildCenterMarkerIcon(context, selected = true)
    map.clear()
    centers.forEach { center ->
        val position = center.mapPosition() ?: return@forEach
        map.addMarker(
            MarkerOptions()
                .position(position)
                .title(center.name(locale))
                .snippet(center.id)
                .icon(if (center.id == activeCenter?.id) selectedIcon else normalIcon)
        )
    }
    map.setOnMarkerClickListener { marker ->
        centers.firstOrNull { it.id == marker.snippet }?.let { selected ->
            onSelect(selected)
            focusMap(map, centers, selected, animate = true)
        }
        true
    }
}

private fun focusMap(map: MapboxMap, centers: List<CenterContent>, activeCenter: CenterContent?, animate: Boolean) {
    val activePosition = activeCenter?.mapPosition()
    val positions = centers.mapNotNull { it.mapPosition() }
    val cameraUpdate = when {
        activePosition != null -> CameraUpdateFactory.newLatLngZoom(activePosition, 12.5)
        positions.size == 1 -> CameraUpdateFactory.newLatLngZoom(positions.first(), 12.0)
        positions.size > 1 -> {
            val boundsBuilder = LatLngBounds.Builder()
            positions.forEach { boundsBuilder.include(it) }
            CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 92)
        }
        else -> CameraUpdateFactory.newLatLngZoom(LatLng(31.7917, -7.0926), 5.1)
    }
    if (animate) {
        map.animateCamera(cameraUpdate, 700)
    } else {
        map.moveCamera(cameraUpdate)
    }
}

private fun buildCenterMarkerIcon(context: android.content.Context, selected: Boolean) =
    IconFactory.getInstance(context).fromBitmap(buildMarkerBitmap(context, selected))

private fun buildMarkerBitmap(context: android.content.Context, selected: Boolean): Bitmap {
    val density = context.resources.displayMetrics.density
    val width = ((if (selected) 58 else 48) * density).toInt()
    val height = ((if (selected) 70 else 58) * density).toInt()
    val centerX = width / 2f
    val radius = width * 0.34f
    val centerY = radius + 5f * density
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val pinPath = Path().apply {
        moveTo(centerX, height - 3f * density)
        lineTo(centerX - radius * 0.72f, centerY + radius * 0.58f)
        lineTo(centerX + radius * 0.72f, centerY + radius * 0.58f)
        close()
    }
    paint.color = if (selected) AndroidColor.rgb(196, 73, 61) else AndroidColor.rgb(14, 90, 69)
    canvas.drawPath(pinPath, paint)
    canvas.drawCircle(centerX, centerY, radius, paint)
    paint.color = AndroidColor.WHITE
    canvas.drawCircle(centerX, centerY, radius * 0.36f, paint)
    return bitmap
}

private fun CenterContent.mapPosition(): LatLng? {
    val direct = if (isMoroccoCoordinate(latitude, longitude)) LatLng(latitude, longitude) else null
    if (direct != null) return direct
    return if (isMoroccoCoordinate(longitude, latitude)) LatLng(longitude, latitude) else null
}

private fun isMoroccoCoordinate(latitude: Double, longitude: Double): Boolean =
    latitude in 20.0..37.5 && longitude in -18.5..0.5 && !(latitude == 0.0 && longitude == 0.0)

@Composable
private fun CenterPreview(locale: LocaleMode, center: CenterContent, onDetails: (CenterContent) -> Unit, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(KioskColors.Surface), border = BorderStroke(1.dp, KioskColors.Line), modifier = modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(8.dp))) {
        Row(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IconBadge("map", compact = false)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(center.name(locale), color = KioskColors.Ink, fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(listOf(center.centerTypeName(locale), center.cityName(locale), center.provinceName(locale)).filter { it.isNotBlank() }.joinToString(" - "), color = KioskColors.MutedStrong, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(center.phone ?: "", color = KioskColors.Green, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
                if (center.services.isNotEmpty()) {
                    Text(
                        center.services.take(2).joinToString(" - ") { service -> service.title(locale) },
                        color = KioskColors.Muted,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            KioskButton(if (locale == LocaleMode.AR) "عرض التفاصيل" else "Voir details", { onDetails(center) }, Modifier.width(170.dp))
        }
    }
}

@Composable
private fun StatisticsScreen(locale: LocaleMode, snapshot: PublicSnapshot) {
    val stats = snapshot.statistics.ifEmpty { fallbackStats() }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            FeatureIntro(
                title = if (locale == LocaleMode.AR) "الأرقام والمؤشرات" else "Chiffres et indicateurs",
                body = if (locale == LocaleMode.AR) "أبرز مؤشرات التعاون الوطني حسب الدليل الإحصائي 2025." else "Principaux indicateurs de l'Entraide Nationale selon l'annuaire statistique 2025."
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                stats.take(3).forEach { stat ->
                    HeroStatCard(locale, stat, Modifier.weight(1f))
                }
            }
        }
        item {
            Text(if (locale == LocaleMode.AR) "تفاصيل إضافية" else "Details complementaires", color = KioskColors.Ink, fontSize = 25.sp, fontWeight = FontWeight.Black)
        }
        items(stats.drop(3).chunked(2)) { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { stat ->
                    SmallStatCard(locale, stat, Modifier.weight(1f))
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun AssistantScreen(locale: LocaleMode, snapshot: PublicSnapshot, onShowCenters: (CenterContent?) -> Unit, onShowServices: () -> Unit) {
    val question = snapshot.helpQuestions.firstOrNull()
    var step by remember { mutableStateOf(1) }
    var selectedNeed by remember { mutableStateOf<HelpChoiceContent?>(null) }
    var selectedAudience by remember { mutableStateOf<TargetAudienceContent?>(null) }
    var selectedRegion by remember { mutableStateOf<String?>(null) }
    val regions = (snapshot.centers.map { it.regionName(locale) } + moroccoRegions(locale)).filter { it.isNotBlank() }.distinct()
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        WizardHeader(locale, step)
        when (step) {
            1 -> {
                FeatureIntro(
                    title = question?.title(locale) ?: if (locale == LocaleMode.AR) "ما الذي تبحث عنه؟" else "Que recherchez-vous ?",
                    body = question?.description(locale) ?: if (locale == LocaleMode.AR) "اختر المسار الأقرب إلى حاجتك." else "Choisissez le parcours le plus proche de votre besoin."
                )
                LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.weight(1f)) {
                    items(question?.choices ?: fallbackChoices()) { choice ->
                        ChoiceCard(locale, choice) {
                            selectedNeed = choice
                            step = 2
                        }
                    }
                }
            }
            2 -> {
                FeatureIntro(
                    title = if (locale == LocaleMode.AR) "لمن الخدمة؟" else "Pour qui ?",
                    body = if (locale == LocaleMode.AR) "حدد الفئة المستفيدة حتى نوجهك بشكل أفضل." else "Precisez le public concerne pour affiner l'orientation."
                )
                LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.weight(1f)) {
                    items(snapshot.targetAudiences) { audience ->
                        ChoiceSurface(iconFor(audience.code), audience.title(locale), audience.description(locale), locale) {
                            selectedAudience = audience
                            step = 3
                        }
                    }
                }
            }
            3 -> {
                FeatureIntro(
                    title = if (locale == LocaleMode.AR) "أين تقيم؟" else "Ou habitez-vous ?",
                    body = if (locale == LocaleMode.AR) "اختر الجهة أو تابع مباشرة لعرض المراكز المتاحة." else "Choisissez la region ou passez directement aux centres disponibles."
                )
                LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                    items(regions.take(10)) { region ->
                        ChoiceSurface(Icons.Rounded.LocationOn, region, "", locale) {
                            selectedRegion = region
                            step = 4
                        }
                    }
                }
                KioskButton(if (locale == LocaleMode.AR) "تخطي هذه الخطوة" else "Ignorer cette etape", { step = 4 }, Modifier.fillMaxWidth(), quiet = true)
            }
            else -> {
                val filteredCenters = snapshot.centers.filter { selectedRegion == null || it.regionName(locale) == selectedRegion }
                SectionCard(if (locale == LocaleMode.AR) "الخدمة المناسبة لك" else "Service recommande", modifier = Modifier.weight(1f)) {
                    Text(selectedNeed?.title(locale) ?: if (locale == LocaleMode.AR) "توجيه ودعم اجتماعي" else "Orientation et appui social", color = KioskColors.Ink, fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Black)
                    Text(
                        if (locale == LocaleMode.AR) "بناء على اختياراتك، يمكنك التوجه إلى أقرب مركز للحصول على الاستقبال والتوجيه نحو الخدمة المناسبة."
                        else "Selon vos choix, vous pouvez vous orienter vers le centre le plus adapte pour l'accueil et l'information.",
                        color = KioskColors.MutedStrong,
                        fontSize = 19.sp,
                        lineHeight = 28.sp
                    )
                    selectedAudience?.let { ChipWrap(listOf(it.title(locale))) }
                    selectedRegion?.let { ChipWrap(listOf(it)) }
                    Spacer(Modifier.weight(1f))
                    Text(if (locale == LocaleMode.AR) "مراكز مقترحة" else "Centres proposes", color = KioskColors.Ink, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    filteredCenters.take(2).forEach { center ->
                        Text(center.name(locale), color = KioskColors.Green, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    KioskButton(if (locale == LocaleMode.AR) "عرض على الخريطة" else "Afficher sur la carte", { onShowCenters(filteredCenters.firstOrNull()) }, Modifier.fillMaxWidth())
                    KioskButton(if (locale == LocaleMode.AR) "عرض تفاصيل الخدمة" else "Voir les services", onShowServices, Modifier.fillMaxWidth(), quiet = true)
                    KioskButton(
                        if (locale == LocaleMode.AR) "بدء بحث جديد" else "Nouvelle recherche",
                        {
                            step = 1
                            selectedNeed = null
                            selectedAudience = null
                            selectedRegion = null
                        },
                        Modifier.fillMaxWidth(),
                        quiet = true
                    )
                }
            }
        }
    }
}

@Composable
private fun CenterDetailsScreen(locale: LocaleMode, center: CenterContent?) {
    if (center == null) {
        EmptyState(if (locale == LocaleMode.AR) "لم يتم اختيار مركز" else "Aucun centre selectionne")
        return
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            FeatureIntro(center.name(locale), center.description(locale).ifBlank { center.address(locale) })
        }
        item {
            SectionCard(if (locale == LocaleMode.AR) "معلومات عملية" else "Informations pratiques") {
                DetailLine(if (locale == LocaleMode.AR) "النوع" else "Type", center.centerTypeName(locale))
                DetailLine(if (locale == LocaleMode.AR) "الجهة" else "Region", center.regionName(locale))
                DetailLine(if (locale == LocaleMode.AR) "الإقليم" else "Province", center.provinceName(locale))
                DetailLine(if (locale == LocaleMode.AR) "العنوان" else "Adresse", center.address(locale))
                DetailLine(if (locale == LocaleMode.AR) "الهاتف" else "Telephone", center.phone ?: "-")
                DetailLine(if (locale == LocaleMode.AR) "البريد" else "Email", center.email ?: "-")
                DetailLine(if (locale == LocaleMode.AR) "التوقيت" else "Horaires", center.openingHours(locale))
            }
        }
        item {
            Box(modifier = Modifier.height(320.dp)) {
                MoroccoMap(listOf(center), center, locale) {}
            }
        }
    }
}

@Composable
private fun IdleSlideshow(locale: LocaleMode, snapshot: PublicSnapshot, onWake: () -> Unit) {
    val slides = snapshot.attractSlides.ifEmpty {
        listOf(AttractSlideContent("", "مرحبا بكم في التعاون الوطني", "Bienvenue a l'Entraide Nationale", "المعلومة والخدمة في متناول الزائر", "Information et services a votre portee", null, 7))
    }
    var index by remember { mutableStateOf(0) }
    val slide = slides[index % slides.size]
    LaunchedEffect(index, slides.size) {
        delay((slide.displayDurationSeconds.coerceAtLeast(5)) * 1000L)
        index = (index + 1) % slides.size
    }
    Box(modifier = Modifier.fillMaxSize().clickable(onClick = onWake).background(KioskColors.GreenDark)) {
        if (!slide.imageUrl.isNullOrBlank()) {
            AsyncImage(emulatorReachableUrl(slide.imageUrl), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        InstitutionalPattern(Modifier.matchParentSize())
        Box(modifier = Modifier.fillMaxSize().background(KioskColors.GreenDark.copy(alpha = 0.55f)))
        LogoLockup(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 54.dp)
        )
        Column(modifier = Modifier.align(Alignment.Center).padding(44.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text(slide.title(locale), color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
            Text(slide.subtitle(locale), color = Color.White.copy(alpha = 0.88f), fontSize = 23.sp, textAlign = TextAlign.Center, lineHeight = 32.sp)
            Text(if (locale == LocaleMode.AR) "المس الشاشة للبدء" else "Touchez l'ecran pour commencer", color = KioskColors.Gold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FeatureIntro(title: String, body: String) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(KioskColors.GreenSoft), border = BorderStroke(1.dp, KioskColors.Line)) {
        Column(modifier = Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, color = KioskColors.Ink, fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Black)
            Text(body, color = KioskColors.MutedStrong, fontSize = 19.sp, lineHeight = 28.sp)
        }
    }
}

@Composable
private fun SectionCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(8.dp)), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(KioskColors.Surface), border = BorderStroke(1.dp, KioskColors.Line)) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = KioskColors.Ink, fontSize = 25.sp, lineHeight = 31.sp, fontWeight = FontWeight.Black)
            content()
        }
    }
}

@Composable
private fun VisualInfoCard(icon: String, title: String, body: String) {
    PressableCard(modifier = Modifier.height(210.dp), containerColor = KioskColors.Surface) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            IconBadge(icon)
            Text(title, color = KioskColors.Ink, fontSize = 22.sp, lineHeight = 27.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(body, color = KioskColors.MutedStrong, fontSize = 15.sp, lineHeight = 21.sp, textAlign = TextAlign.Center, maxLines = 4, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ServiceCard(locale: LocaleMode, service: ServiceContent, onFindCenters: () -> Unit) {
    PressableCard(modifier = Modifier.height(230.dp), containerColor = KioskColors.Surface) {
        Column(modifier = Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconBadge("service", compact = true)
                Text(service.title(locale), color = KioskColors.Ink, fontSize = 21.sp, lineHeight = 26.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Text(service.shortDescription(locale), color = KioskColors.MutedStrong, fontSize = 16.sp, lineHeight = 23.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            if (service.targetAudience(locale).isNotBlank()) ChipWrap(listOf(service.targetAudience(locale)))
            Spacer(Modifier.weight(1f))
            KioskButton(if (locale == LocaleMode.AR) "أين أجد هذه الخدمة؟" else "Ou trouver ce service ?", onFindCenters, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun LargeServicePanel(locale: LocaleMode, service: ServiceContent, onFindCenters: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(8.dp)), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(KioskColors.Surface), border = BorderStroke(1.dp, KioskColors.Line)) {
        Column(modifier = Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            IconBadge("service")
            Text(service.title(locale), color = KioskColors.Ink, fontSize = 34.sp, lineHeight = 40.sp, fontWeight = FontWeight.Black)
            Text(service.shortDescription(locale), color = KioskColors.MutedStrong, fontSize = 22.sp, lineHeight = 32.sp)
            if (service.targetAudience(locale).isNotBlank()) ChipWrap(listOf(service.targetAudience(locale)))
            Spacer(Modifier.weight(1f))
            KioskButton(if (locale == LocaleMode.AR) "أين أجد هذه الخدمة؟" else "Ou trouver ce service ?", onFindCenters, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun MissionCard(icon: ImageVector, title: String, body: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(150.dp).shadow(4.dp, RoundedCornerShape(8.dp)), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(KioskColors.Surface), border = BorderStroke(1.dp, KioskColors.Line)) {
        Column(modifier = Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(icon, contentDescription = null, tint = KioskColors.Green, modifier = Modifier.size(30.dp))
            Text(title, color = KioskColors.Ink, fontSize = 21.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(body, color = KioskColors.MutedStrong, fontSize = 15.sp, lineHeight = 21.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun HeroStatCard(locale: LocaleMode, stat: StatisticContent, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(176.dp).shadow(7.dp, RoundedCornerShape(8.dp)), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(KioskColors.Surface), border = BorderStroke(1.dp, KioskColors.Line)) {
        Column(modifier = Modifier.fillMaxSize().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            NumericText(stat.number, color = KioskColors.Purple, fontSize = 25)
            Text(stat.label(locale), color = KioskColors.Ink, fontSize = 18.sp, lineHeight = 23.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun SmallStatCard(locale: LocaleMode, stat: StatisticContent, modifier: Modifier = Modifier) {
    Card(modifier = modifier.height(124.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(KioskColors.Surface), border = BorderStroke(1.dp, KioskColors.Line)) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            IconBadge("stats", compact = true)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                NumericText(stat.number, color = KioskColors.Purple, fontSize = 25)
                Text(stat.label(locale), color = KioskColors.Ink, fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun ChoiceCard(locale: LocaleMode, choice: HelpChoiceContent, onClick: () -> Unit) {
    ChoiceSurface(iconFor(choice.icon ?: "help"), choice.title(locale), visitorText(choice.description(locale)), locale, onClick)
}

@Composable
private fun ChoiceSurface(icon: ImageVector, title: String, body: String, locale: LocaleMode, onClick: () -> Unit) {
    PressableCard(modifier = Modifier.height(168.dp), containerColor = KioskColors.Surface, onClick = onClick) {
        Column(modifier = Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = KioskColors.Green, modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(10.dp))
            Text(title, color = KioskColors.Ink, fontSize = 20.sp, lineHeight = 25.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (body.isNotBlank()) {
                Text(body, color = KioskColors.MutedStrong, fontSize = 14.sp, lineHeight = 19.sp, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun PressableCard(modifier: Modifier = Modifier, containerColor: Color = KioskColors.Surface, onClick: () -> Unit = {}, content: @Composable () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.985f else 1f, label = "pressableScale")
    val color by animateColorAsState(if (pressed) KioskColors.GreenSoft else containerColor, label = "pressableColor")
    Card(
        modifier = modifier
            .scale(scale)
            .shadow(if (pressed) 2.dp else 5.dp, RoundedCornerShape(8.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(color),
        border = BorderStroke(1.dp, KioskColors.Line)
    ) {
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipWrap(labels: List<String>) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        labels.filter { it.isNotBlank() }.take(8).forEach { label ->
            Box(modifier = Modifier.background(KioskColors.GoldSoft, RoundedCornerShape(22.dp)).padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(label, color = KioskColors.Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun WizardHeader(locale: LocaleMode, step: Int) {
    val progress = step.coerceIn(1, 4) / 4f
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(if (locale == LocaleMode.AR) "دليل المساعدة" else "Guide d'aide", color = KioskColors.Ink, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Surface(shape = RoundedCornerShape(22.dp), color = KioskColors.GreenSoft, border = BorderStroke(1.dp, KioskColors.Line)) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text("${step.coerceIn(1, 4)} / 4", modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp), color = KioskColors.Green, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(KioskColors.GreenSoft)) {
            Box(modifier = Modifier.fillMaxWidth(progress).height(8.dp).clip(RoundedCornerShape(4.dp)).background(KioskColors.Gold))
        }
    }
}

@Composable
private fun SearchField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        singleLine = true,
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = KioskColors.Green) },
        placeholder = { Text(placeholder, color = KioskColors.Muted, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        shape = RoundedCornerShape(32.dp)
    )
}

@Composable
private fun NumericText(value: String, color: Color, fontSize: Int) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Text(
            value,
            modifier = Modifier.fillMaxWidth(),
            color = color,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InstitutionalPattern(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawCircle(color = KioskColors.Gold.copy(alpha = 0.16f), radius = size.minDimension * 0.32f, center = androidx.compose.ui.geometry.Offset(size.width * 0.14f, size.height * 0.18f))
        drawCircle(color = Color.White.copy(alpha = 0.08f), radius = size.minDimension * 0.22f, center = androidx.compose.ui.geometry.Offset(size.width * 0.82f, size.height * 0.74f))
    }
}

private fun statSource(locale: LocaleMode) = if (locale == LocaleMode.AR) "المصدر: الدليل الإحصائي 2025" else "Source : annuaire statistique 2025"

private fun visitorText(text: String): String {
    val lower = text.lowercase()
    val blocked = listOf(
        "لوحة التحكم",
        "قابل للتعديل",
        "ocr",
        "placeholder",
        "data pending",
        "admin editable",
        "تحقق بصري",
        "يحتاج"
    )
    return if (blocked.any { lower.contains(it.lowercase()) }) "" else text
}

private fun moroccoRegions(locale: LocaleMode) = if (locale == LocaleMode.AR) {
    listOf(
        "الرباط - سلا - القنيطرة",
        "الدار البيضاء - سطات",
        "فاس - مكناس",
        "مراكش - آسفي",
        "طنجة - تطوان - الحسيمة",
        "سوس - ماسة",
        "الشرق",
        "درعة - تافيلالت",
        "بني ملال - خنيفرة",
        "كلميم - واد نون"
    )
} else {
    listOf(
        "Rabat - Sale - Kenitra",
        "Casablanca - Settat",
        "Fes - Meknes",
        "Marrakech - Safi",
        "Tanger - Tetouan - Al Hoceima",
        "Souss - Massa",
        "Oriental",
        "Draa - Tafilalet",
        "Beni Mellal - Khenifra",
        "Guelmim - Oued Noun"
    )
}

private fun iconFor(raw: String): ImageVector {
    val key = raw.lowercase()
    return when {
        key in listOf("in", "institution", "building") -> Icons.Rounded.AccountBalance
        key in listOf("sv", "service", "as", "social_support") -> Icons.Rounded.VolunteerActivism
        key in listOf("mp", "map", "center", "nearby_center") -> Icons.Rounded.LocationOn
        key in listOf("ch", "stats", "statistics") -> Icons.Rounded.BarChart
        key in listOf("ai", "help", "assistant") -> Icons.Rounded.Explore
        key in listOf("ax", "area", "domain") -> Icons.Rounded.Map
        key.contains("women") || key == "woman" -> Icons.Rounded.Woman
        key.contains("child") -> Icons.Rounded.ChildCare
        key.contains("disability") || key.contains("handicap") || key == "ps" -> Icons.Rounded.AccessibilityNew
        key.contains("elder") || key.contains("aged") -> Icons.Rounded.People
        key.contains("family") -> Icons.Rounded.Groups
        key.contains("training") || key == "fr" -> Icons.Rounded.School
        key.contains("orientation") || key.contains("navigation") -> Icons.Rounded.Navigation
        key.contains("search") -> Icons.Rounded.PersonSearch
        else -> Icons.Rounded.Favorite
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterRow(options: List<String>, selected: String?, onSelected: (String?) -> Unit) {
    FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.filter { it.isNotBlank() }.distinct().take(8).forEach { option ->
            OutlinedButton(
                onClick = { onSelected(if (selected == option) null else option) },
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected == option) KioskColors.Green else KioskColors.Surface, contentColor = if (selected == option) Color.White else KioskColors.Ink),
                border = BorderStroke(1.dp, if (selected == option) KioskColors.Green else KioskColors.Line)
            ) {
                Text(option, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TimelineRow(locale: LocaleMode, event: TimelineEventContent) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(modifier = Modifier.width(82.dp).height(70.dp).background(KioskColors.Gold, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Text(event.year, color = KioskColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
        SectionCard(event.title(locale), modifier = Modifier.weight(1f)) {
            Text(event.description(locale), color = KioskColors.Muted, fontSize = 15.sp, lineHeight = 21.sp)
        }
    }
}

@Composable
private fun IconBadge(label: String, compact: Boolean = false) {
    Box(modifier = Modifier.size(if (compact) 44.dp else 52.dp).background(KioskColors.GreenSoft, CircleShape), contentAlignment = Alignment.Center) {
        Icon(iconFor(label), contentDescription = null, tint = KioskColors.Green, modifier = Modifier.size(if (compact) 23.dp else 28.dp))
    }
}

@Composable
private fun KioskButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, quiet: Boolean = false) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (quiet) KioskColors.Surface else KioskColors.Green, contentColor = if (quiet) KioskColors.Ink else Color.White),
        border = if (quiet) BorderStroke(1.dp, KioskColors.Line) else null
    ) {
        Text(label, fontSize = 13.sp, lineHeight = 16.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}

@Composable
private fun KioskSmallButton(label: String, onClick: () -> Unit, accent: Boolean = false) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(42.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (accent) KioskColors.Gold else KioskColors.GreenSoft, contentColor = KioskColors.Ink)
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = KioskColors.MutedStrong, fontSize = 16.sp)
        val isNumeric = value.any { it.isDigit() }
        val currentDirection = LocalLayoutDirection.current
        CompositionLocalProvider(LocalLayoutDirection provides if (isNumeric) LayoutDirection.Ltr else currentDirection) {
            Text(value, color = KioskColors.Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun BulletText(locale: LocaleMode, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
        Box(modifier = Modifier.padding(top = 7.dp).size(8.dp).background(KioskColors.Gold, CircleShape))
        Text(text, color = KioskColors.Muted, fontSize = 16.sp, lineHeight = 23.sp, textAlign = if (locale == LocaleMode.AR) TextAlign.Right else TextAlign.Left)
    }
}

@Composable
private fun SourceNotice(text: String) {
    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(KioskColors.Surface), border = BorderStroke(1.dp, KioskColors.Gold)) {
        Text(text, modifier = Modifier.padding(18.dp), color = KioskColors.Muted, fontSize = 16.sp, lineHeight = 23.sp)
    }
}

@Composable
private fun LoadingScreen(locale: LocaleMode) {
    Box(modifier = Modifier.fillMaxSize().background(KioskColors.Cream), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator(color = KioskColors.Green)
            Text(if (locale == LocaleMode.AR) "جاري تحميل محتوى الطوطم" else "Chargement du contenu", color = KioskColors.Ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun OfflineScreen(locale: LocaleMode, message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(KioskColors.Cream), contentAlignment = Alignment.Center) {
        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(KioskColors.GreenSoft), border = BorderStroke(1.dp, KioskColors.Line)) {
            Column(modifier = Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(if (locale == LocaleMode.AR) "تعذر الاتصال بالخادم" else "Serveur indisponible", color = KioskColors.Ink, fontSize = 30.sp, lineHeight = 36.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                Text(message, color = KioskColors.MutedStrong, fontSize = 19.sp, lineHeight = 28.sp, textAlign = TextAlign.Center)
                KioskButton(if (locale == LocaleMode.AR) "إعادة المحاولة" else "Reessayer", onRetry)
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = KioskColors.Muted, fontSize = 22.sp, textAlign = TextAlign.Center)
    }
}

private fun homeTiles() = listOf(
    NavTile(TotemScreen.Institution, "IN", "المؤسسة", "Institution", "تعرف على المهام والتاريخ والرسالة.", "Missions, histoire et statut."),
    NavTile(TotemScreen.InterventionAreas, "AX", "مجالات التدخل", "Domaines", "أهم محاور العمل الاجتماعي.", "Axes principaux d'intervention."),
    NavTile(TotemScreen.Services, "SV", "الخدمات", "Services", "خدمات وبرامج موجهة للمواطنين.", "Services et prestations."),
    NavTile(TotemScreen.Centers, "MP", "المراكز", "Centres", "خريطة وبحث عن المراكز.", "Carte et recherche de centres."),
    NavTile(TotemScreen.Statistics, "CH", "الأرقام والمؤشرات", "Chiffres", "المصدر: الدليل الإحصائي 2025.", "Indicateurs verifies."),
    NavTile(TotemScreen.Assistant, "AI", "كيف يمكننا مساعدتك؟", "Assistant", "دليل تفاعلي للوصول للمعلومة.", "Guide interactif.")
)

private fun screenTitle(screen: TotemScreen, locale: LocaleMode) = when (screen) {
    TotemScreen.Home -> if (locale == LocaleMode.AR) "الاستقبال" else "Accueil"
    TotemScreen.Institution -> if (locale == LocaleMode.AR) "المؤسسة" else "Institution"
    TotemScreen.InterventionAreas -> if (locale == LocaleMode.AR) "مجالات التدخل" else "Domaines"
    TotemScreen.Services -> if (locale == LocaleMode.AR) "الخدمات" else "Services"
    TotemScreen.Programs -> if (locale == LocaleMode.AR) "البرامج" else "Programmes"
    TotemScreen.Centers -> if (locale == LocaleMode.AR) "المراكز" else "Centres"
    TotemScreen.Statistics -> if (locale == LocaleMode.AR) "الأرقام والمؤشرات" else "Chiffres"
    TotemScreen.Assistant -> if (locale == LocaleMode.AR) "كيف يمكننا مساعدتك؟" else "Assistant"
    TotemScreen.CenterDetails -> if (locale == LocaleMode.AR) "تفاصيل المركز" else "Details du centre"
    TotemScreen.Idle -> ""
}

private fun NavTile.title(locale: LocaleMode) = if (locale == LocaleMode.AR) titleAr else titleFr
private fun NavTile.body(locale: LocaleMode) = if (locale == LocaleMode.AR) bodyAr else bodyFr

private sealed interface SnapshotState {
    data object Loading : SnapshotState
    data class Ready(val snapshot: PublicSnapshot) : SnapshotState
    data class Error(val message: String) : SnapshotState
}

private object PublicSnapshotClient {
    suspend fun load(): PublicSnapshot = withContext(Dispatchers.IO) {
        val connection = (URL(SnapshotUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 5000
            readTimeout = 8000
            requestMethod = "GET"
        }
        connection.inputStream.bufferedReader(Charsets.UTF_8).use { PublicSnapshot.from(JSONObject(it.readText())) }
    }
}

private data class PublicSnapshot(
    val generatedAt: String,
    val settings: Map<String, String>,
    val services: List<ServiceContent>,
    val centers: List<CenterContent>,
    val attractSlides: List<AttractSlideContent>,
    val homeSections: List<HomeSectionContent>,
    val interventionAreas: List<InterventionAreaContent>,
    val programs: List<ProgramContent>,
    val targetAudiences: List<TargetAudienceContent>,
    val statistics: List<StatisticContent>,
    val timelineEvents: List<TimelineEventContent>,
    val helpQuestions: List<HelpQuestionContent>
) {
    companion object {
        fun from(json: JSONObject): PublicSnapshot {
            val settingsJson = json.optJSONObject("settings") ?: JSONObject()
            return PublicSnapshot(
                generatedAt = json.optString("generatedAt"),
                settings = settingsJson.keys().asSequence().associateWith { settingsJson.optString(it) },
                services = json.array("services").map { ServiceContent.from(it) },
                centers = json.array("centers").map { CenterContent.from(it) },
                attractSlides = json.array("attractSlides").map { AttractSlideContent.from(it) },
                homeSections = json.array("homeSections").map { HomeSectionContent.from(it) },
                interventionAreas = json.array("interventionAreas").map { InterventionAreaContent.from(it) },
                programs = json.array("programs").map { ProgramContent.from(it) },
                targetAudiences = json.array("targetAudiences").map { TargetAudienceContent.from(it) },
                statistics = json.array("statistics").map { StatisticContent.from(it) },
                timelineEvents = json.array("timelineEvents").map { TimelineEventContent.from(it) },
                helpQuestions = json.array("helpQuestions").map { HelpQuestionContent.from(it) }
            )
        }
    }
}

private data class HomeSectionContent(val id: String, val key: String, val titleAr: String, val titleFr: String, val subtitleAr: String?, val subtitleFr: String?, val bodyAr: String?, val bodyFr: String?, val imageUrl: String?) {
    fun title(locale: LocaleMode) = if (locale == LocaleMode.AR) titleAr else titleFr
    fun subtitle(locale: LocaleMode) = if (locale == LocaleMode.AR) subtitleAr.orEmpty() else subtitleFr.orEmpty()
    companion object {
        fun from(json: JSONObject) = HomeSectionContent(json.optString("id"), json.optString("key"), json.optString("titleAr"), json.optString("titleFr"), json.nullable("subtitleAr"), json.nullable("subtitleFr"), json.nullable("bodyAr"), json.nullable("bodyFr"), json.nullable("imageUrl"))
    }
}

private data class InterventionAreaContent(val id: String, val titleAr: String, val titleFr: String, val descriptionAr: String, val descriptionFr: String, val icon: String?) {
    fun title(locale: LocaleMode) = if (locale == LocaleMode.AR) titleAr else titleFr
    fun description(locale: LocaleMode) = if (locale == LocaleMode.AR) descriptionAr else descriptionFr
    companion object {
        fun from(json: JSONObject) = InterventionAreaContent(json.optString("id"), json.optString("titleAr"), json.optString("titleFr"), json.optString("descriptionAr"), json.optString("descriptionFr"), json.nullable("icon"))
    }
}

private data class ProgramContent(val id: String, val titleAr: String, val titleFr: String, val descriptionAr: String, val descriptionFr: String, val targetAudienceAr: String?, val targetAudienceFr: String?) {
    fun title(locale: LocaleMode) = if (locale == LocaleMode.AR) titleAr else titleFr
    fun description(locale: LocaleMode) = if (locale == LocaleMode.AR) descriptionAr else descriptionFr
    fun targetAudience(locale: LocaleMode) = if (locale == LocaleMode.AR) targetAudienceAr.orEmpty() else targetAudienceFr.orEmpty()
    companion object {
        fun from(json: JSONObject) = ProgramContent(json.optString("id"), json.optString("titleAr"), json.optString("titleFr"), json.optString("descriptionAr"), json.optString("descriptionFr"), json.nullable("targetAudienceAr"), json.nullable("targetAudienceFr"))
    }
}

private data class TargetAudienceContent(val id: String, val code: String, val titleAr: String, val titleFr: String, val descriptionAr: String?, val descriptionFr: String?) {
    fun title(locale: LocaleMode) = if (locale == LocaleMode.AR) titleAr else titleFr
    fun description(locale: LocaleMode) = if (locale == LocaleMode.AR) descriptionAr.orEmpty() else descriptionFr.orEmpty()
    companion object {
        fun from(json: JSONObject) = TargetAudienceContent(json.optString("id"), json.optString("code"), json.optString("titleAr"), json.optString("titleFr"), json.nullable("descriptionAr"), json.nullable("descriptionFr"))
    }
}

private data class ServiceContent(val id: String, val titleAr: String, val titleFr: String, val shortDescriptionAr: String, val shortDescriptionFr: String, val targetAudienceAr: String?, val targetAudienceFr: String?) {
    fun title(locale: LocaleMode) = if (locale == LocaleMode.AR) titleAr else titleFr
    fun shortDescription(locale: LocaleMode) = if (locale == LocaleMode.AR) shortDescriptionAr else shortDescriptionFr
    fun targetAudience(locale: LocaleMode) = if (locale == LocaleMode.AR) targetAudienceAr.orEmpty() else targetAudienceFr.orEmpty()
    companion object {
        fun from(json: JSONObject) = ServiceContent(json.optString("id"), json.optString("titleAr"), json.optString("titleFr"), json.optString("shortDescriptionAr"), json.optString("shortDescriptionFr"), json.nullable("targetAudienceAr"), json.nullable("targetAudienceFr"))
    }
}

private data class CenterContent(
    val id: String,
    val nameAr: String,
    val nameFr: String,
    val centerTypeNameAr: String?,
    val centerTypeNameFr: String?,
    val regionNameAr: String?,
    val regionNameFr: String?,
    val provinceNameAr: String?,
    val provinceNameFr: String?,
    val cityNameAr: String?,
    val cityNameFr: String?,
    val addressAr: String,
    val addressFr: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String?,
    val email: String?,
    val descriptionAr: String?,
    val descriptionFr: String?,
    val openingHoursAr: String?,
    val openingHoursFr: String?,
    val services: List<ServiceContent>
) {
    fun name(locale: LocaleMode) = if (locale == LocaleMode.AR) nameAr else nameFr
    fun centerTypeName(locale: LocaleMode) = if (locale == LocaleMode.AR) centerTypeNameAr.orEmpty() else centerTypeNameFr.orEmpty()
    fun regionName(locale: LocaleMode) = if (locale == LocaleMode.AR) regionNameAr.orEmpty() else regionNameFr.orEmpty()
    fun provinceName(locale: LocaleMode) = if (locale == LocaleMode.AR) provinceNameAr.orEmpty() else provinceNameFr.orEmpty()
    fun cityName(locale: LocaleMode) = if (locale == LocaleMode.AR) cityNameAr.orEmpty() else cityNameFr.orEmpty()
    fun address(locale: LocaleMode) = if (locale == LocaleMode.AR) addressAr else addressFr
    fun description(locale: LocaleMode) = if (locale == LocaleMode.AR) descriptionAr.orEmpty() else descriptionFr.orEmpty()
    fun openingHours(locale: LocaleMode) = if (locale == LocaleMode.AR) openingHoursAr.orEmpty() else openingHoursFr.orEmpty()
    companion object {
        fun from(json: JSONObject) = CenterContent(
            id = json.optString("id"),
            nameAr = json.optString("nameAr"),
            nameFr = json.optString("nameFr"),
            centerTypeNameAr = json.nullable("centerTypeNameAr"),
            centerTypeNameFr = json.nullable("centerTypeNameFr"),
            regionNameAr = json.nullable("regionNameAr"),
            regionNameFr = json.nullable("regionNameFr"),
            provinceNameAr = json.nullable("provinceNameAr"),
            provinceNameFr = json.nullable("provinceNameFr"),
            cityNameAr = json.nullable("cityNameAr"),
            cityNameFr = json.nullable("cityNameFr"),
            addressAr = json.optString("addressAr"),
            addressFr = json.optString("addressFr"),
            latitude = json.optDouble("latitude"),
            longitude = json.optDouble("longitude"),
            phone = json.nullable("phone"),
            email = json.nullable("email"),
            descriptionAr = json.nullable("descriptionAr"),
            descriptionFr = json.nullable("descriptionFr"),
            openingHoursAr = json.nullable("openingHoursAr"),
            openingHoursFr = json.nullable("openingHoursFr"),
            services = json.array("services").map { ServiceContent.from(it) }
        )
    }
}

private data class AttractSlideContent(val id: String, val titleAr: String, val titleFr: String, val subtitleAr: String?, val subtitleFr: String?, val imageUrl: String?, val displayDurationSeconds: Int) {
    fun title(locale: LocaleMode) = if (locale == LocaleMode.AR) titleAr else titleFr
    fun subtitle(locale: LocaleMode) = if (locale == LocaleMode.AR) subtitleAr.orEmpty() else subtitleFr.orEmpty()
    companion object {
        fun from(json: JSONObject) = AttractSlideContent(json.optString("id"), json.optString("titleAr"), json.optString("titleFr"), json.nullable("subtitleAr"), json.nullable("subtitleFr"), json.nullable("imageUrl"), json.optInt("displayDurationSeconds", 7))
    }
}

private data class StatisticContent(val id: String, val number: String, val labelAr: String, val labelFr: String, val year: String?, val sourceTitle: String?) {
    fun label(locale: LocaleMode) = if (locale == LocaleMode.AR) labelAr else labelFr
    companion object {
        fun from(json: JSONObject) = StatisticContent(json.optString("id"), json.optString("number"), json.optString("labelAr"), json.optString("labelFr"), json.nullable("year"), json.nullable("sourceTitle"))
    }
}

private data class TimelineEventContent(val id: String, val year: String, val titleAr: String, val titleFr: String, val descriptionAr: String?, val descriptionFr: String?) {
    fun title(locale: LocaleMode) = if (locale == LocaleMode.AR) titleAr else titleFr
    fun description(locale: LocaleMode) = if (locale == LocaleMode.AR) descriptionAr.orEmpty() else descriptionFr.orEmpty()
    companion object {
        fun from(json: JSONObject) = TimelineEventContent(json.optString("id"), json.optString("year"), json.optString("titleAr"), json.optString("titleFr"), json.nullable("descriptionAr"), json.nullable("descriptionFr"))
    }
}

private data class HelpQuestionContent(val id: String, val titleAr: String, val titleFr: String, val descriptionAr: String?, val descriptionFr: String?, val choices: List<HelpChoiceContent>) {
    fun title(locale: LocaleMode) = if (locale == LocaleMode.AR) titleAr else titleFr
    fun description(locale: LocaleMode) = if (locale == LocaleMode.AR) descriptionAr.orEmpty() else descriptionFr.orEmpty()
    companion object {
        fun from(json: JSONObject) = HelpQuestionContent(json.optString("id"), json.optString("titleAr"), json.optString("titleFr"), json.nullable("descriptionAr"), json.nullable("descriptionFr"), json.array("choices").map { HelpChoiceContent.from(it) })
    }
}

private data class HelpChoiceContent(val id: String, val titleAr: String, val titleFr: String, val descriptionAr: String?, val descriptionFr: String?, val icon: String?) {
    fun title(locale: LocaleMode) = if (locale == LocaleMode.AR) titleAr else titleFr
    fun description(locale: LocaleMode) = if (locale == LocaleMode.AR) descriptionAr.orEmpty() else descriptionFr.orEmpty()
    companion object {
        fun from(json: JSONObject) = HelpChoiceContent(json.optString("id"), json.optString("titleAr"), json.optString("titleFr"), json.nullable("descriptionAr"), json.nullable("descriptionFr"), json.nullable("icon"))
    }
}

private fun JSONObject.array(key: String): List<JSONObject> {
    val array = optJSONArray(key) ?: return emptyList()
    return List(array.length()) { index -> array.getJSONObject(index) }
}

private fun JSONObject.nullable(key: String): String? = if (isNull(key)) null else optString(key).takeIf { it.isNotBlank() }

private fun fallbackStats() = listOf(
    StatisticContent("beneficiaries", "1 383 516", "إجمالي المستفيدين", "Total beneficiaires", "2025", "Annuaire statistique 2025"),
    StatisticContent("centers", "3 776", "مركز/برنامج", "Centres/programmes", "2025", "Annuaire statistique 2025"),
    StatisticContent("women", "833 413", "عدد المستفيدات", "Beneficiaires feminins", "2025", "Annuaire statistique 2025")
)

private fun fallbackChoices() = listOf(
    HelpChoiceContent("social", "أبحث عن دعم اجتماعي", "Je cherche un appui social", null, null, "AS"),
    HelpChoiceContent("center", "أبحث عن مركز قريب مني", "Je cherche un centre proche", null, null, "MP"),
    HelpChoiceContent("training", "أبحث عن تكوين", "Je cherche une formation", null, null, "FR"),
    HelpChoiceContent("disability", "خدمة لفائدة الأشخاص في وضعية إعاقة", "Service handicap", null, null, "PS")
)

private fun moroccoBaseMapStyleJson() = """
{
  "version": 8,
  "name": "Entraide Morocco base map",
  "sources": {
    "osm": {
      "type": "raster",
      "tiles": [
        "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
      ],
      "tileSize": 256,
      "attribution": "© OpenStreetMap contributors"
    },
    "morocco": {
      "type": "geojson",
      "data": {
        "type": "FeatureCollection",
        "features": [
          {
            "type": "Feature",
            "properties": { "name": "Morocco" },
            "geometry": {
              "type": "Polygon",
              "coordinates": [[
                [-17.10, 21.40],
                [-16.20, 23.50],
                [-15.05, 25.20],
                [-13.20, 27.70],
                [-12.00, 28.60],
                [-10.20, 29.25],
                [-9.80, 31.00],
                [-9.05, 32.25],
                [-8.60, 33.70],
                [-7.30, 34.70],
                [-5.90, 35.80],
                [-4.30, 35.20],
                [-2.10, 35.10],
                [-1.20, 34.20],
                [-1.70, 32.70],
                [-3.00, 31.60],
                [-3.80, 30.30],
                [-5.00, 29.00],
                [-6.10, 27.80],
                [-8.60, 26.10],
                [-10.40, 24.50],
                [-13.10, 22.90],
                [-17.10, 21.40]
              ]]
            }
          }
        ]
      }
    }
  },
  "layers": [
    {
      "id": "background",
      "type": "background",
      "paint": { "background-color": "#E7F1EC" }
    },
    {
      "id": "osm-raster",
      "type": "raster",
      "source": "osm",
      "paint": {
        "raster-opacity": 1.0
      }
    },
    {
      "id": "morocco-fill",
      "type": "fill",
      "source": "morocco",
      "paint": {
        "fill-color": "#0E5A45",
        "fill-opacity": 0.05
      }
    },
    {
      "id": "morocco-line",
      "type": "line",
      "source": "morocco",
      "paint": {
        "line-color": "#0E5A45",
        "line-width": 2,
        "line-opacity": 0.75
      }
    }
  ]
}
""".trimIndent()
