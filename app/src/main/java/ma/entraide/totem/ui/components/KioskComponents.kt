package ma.entraide.totem.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ma.entraide.totem.ui.theme.TotemColors
import ma.entraide.totem.ui.theme.TotemSpace
import ma.entraide.totem.ui.theme.TotemType

internal enum class ButtonTone { Primary, Secondary, Quiet, Danger }
internal enum class StatusTone { Good, Warning, Neutral }

@Composable
internal fun InfoPanel(
    modifier: Modifier = Modifier,
    containerColor: Color = TotemColors.Surface,
    borderColor: Color = TotemColors.Line,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(TotemSpace.Radius),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(TotemSpace.GapSm),
            content = content
        )
    }
}

@Composable
internal fun KioskButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    tone: ButtonTone = ButtonTone.Primary
) {
    val container = when {
        selected -> TotemColors.GreenDark
        tone == ButtonTone.Primary -> TotemColors.Green
        tone == ButtonTone.Secondary -> TotemColors.Gold
        tone == ButtonTone.Danger -> TotemColors.Red
        else -> TotemColors.Surface
    }
    val content = if (tone == ButtonTone.Secondary || tone == ButtonTone.Quiet) TotemColors.Ink else Color.White
    val border = if (tone == ButtonTone.Quiet) BorderStroke(1.dp, TotemColors.Line) else null

    if (tone == ButtonTone.Quiet) {
        OutlinedButton(
            modifier = modifier.height(TotemSpace.ButtonHeight),
            onClick = onClick,
            shape = RoundedCornerShape(TotemSpace.Radius),
            border = border,
            colors = ButtonDefaults.outlinedButtonColors(containerColor = container, contentColor = content)
        ) {
            Text(label, fontSize = TotemType.Button, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    } else {
        Button(
            modifier = modifier.height(TotemSpace.ButtonHeight),
            onClick = onClick,
            shape = RoundedCornerShape(TotemSpace.Radius),
            colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content)
        ) {
            Text(label, fontSize = TotemType.Button, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
internal fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(title, color = TotemColors.Ink, fontSize = TotemType.Brand, fontWeight = FontWeight.Bold)
        Text(subtitle, color = TotemColors.Muted, fontSize = TotemType.Eyebrow)
    }
}

@Composable
internal fun StatusBadge(label: String, tone: StatusTone, modifier: Modifier = Modifier) {
    val color = when (tone) {
        StatusTone.Good -> Color(0xFF217A56)
        StatusTone.Warning -> Color(0xFFB54708)
        StatusTone.Neutral -> TotemColors.Muted
    }
    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(TotemSpace.Radius))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Box(modifier = Modifier.size(11.dp).background(color, CircleShape))
        Text(label, color = color, fontSize = TotemType.Small, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun StatisticTile(label: String, value: String, modifier: Modifier = Modifier, unit: String? = null) {
    InfoPanel(modifier = modifier.height(132.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(value, color = TotemColors.Purple, fontSize = TotemType.Number, fontWeight = FontWeight.Bold)
                if (!unit.isNullOrBlank()) {
                    Text(unit, color = TotemColors.Muted, fontSize = TotemType.Small, modifier = Modifier.padding(bottom = 7.dp))
                }
            }
            Text(label, color = TotemColors.Muted, fontSize = TotemType.Body, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
internal fun Pill(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(TotemSpace.Radius),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) TotemColors.Green else TotemColors.Surface,
            contentColor = if (selected) Color.White else TotemColors.Ink
        ),
        border = BorderStroke(1.dp, if (selected) TotemColors.Green else TotemColors.Line)
    ) {
        Text(label, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
