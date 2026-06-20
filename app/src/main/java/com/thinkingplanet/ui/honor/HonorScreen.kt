package com.thinkingplanet.ui.honor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thinkingplanet.domain.model.MedalLevel
import com.thinkingplanet.domain.model.TrophyLevel
import com.thinkingplanet.ui.theme.*

@Composable
fun HonorScreen(onBack: () -> Unit, viewModel: HonorViewModel = hiltViewModel()) {
    val medals by viewModel.medals.collectAsState()
    val trophies by viewModel.trophies.collectAsState()
    val title by viewModel.currentTitle.collectAsState()

    Column(Modifier.fillMaxSize().background(Background)) {
        Row(
            Modifier.fillMaxWidth().background(PurplePrimary).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Text("←", fontSize = 20.sp, color = Color.White) }
            Text("🏆 荣誉殿堂", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Spacer(Modifier.weight(1f))
            Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.2f)) {
                Text("${title.emoji} ${title.label}", Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("维度勋章", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnSurface) }
            items(medals) { medal ->
                Row(
                    Modifier.fillMaxWidth().background(Surface, RoundedCornerShape(14.dp)).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${medal.dimension.emoji} ${medal.packId} · ${medal.dimension.label}", fontSize = 14.sp)
                    Text(
                        when (medal.level) {
                            MedalLevel.GOLD -> "🥇 金牌"; MedalLevel.SILVER -> "🥈 银牌"
                            MedalLevel.BRONZE -> "🥉 铜牌"; MedalLevel.NONE -> "○ 未获得"
                        },
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
                Text("学期奖杯", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnSurface)
            }
            items(trophies) { (packId, level) ->
                Row(
                    Modifier.fillMaxWidth().background(Surface, RoundedCornerShape(14.dp)).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(packId.replace("grade", "").replace("_upper", "年级上").replace("_lower", "年级下"), fontSize = 14.sp)
                    Text(
                        when (level) {
                            TrophyLevel.GOLD -> "🥇 金奖杯"; TrophyLevel.SILVER -> "🥈 银奖杯"
                            TrophyLevel.BRONZE -> "🥉 铜奖杯"; TrophyLevel.NONE -> "○ 未获得"
                        },
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
