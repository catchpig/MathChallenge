package com.thinkingplanet.ui.chapter

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thinkingplanet.domain.model.Dimension
import com.thinkingplanet.ui.theme.*
import com.thinkingplanet.ui.util.icon

private val dimensionColors = mapOf(
    Dimension.MATH     to listOf(Color(0xFFFF6FA8), Color(0xFFFF9BC5)),
    Dimension.LOGIC    to listOf(Color(0xFF818CF8), Color(0xFFA5B4FC)),
    Dimension.SPATIAL  to listOf(Color(0xFF34D399), Color(0xFF6EE7B7)),
    Dimension.PATTERN  to listOf(Color(0xFFFBBF24), Color(0xFFFDE68A)),
    Dimension.CREATIVE to listOf(Color(0xFFBF6EFF), Color(0xFFD8B4FE))
)

@Composable
fun ChapterScreen(
    packId: String,
    onLevelClick: (dimension: String, levelId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: ChapterViewModel = hiltViewModel()
) {
    LaunchedEffect(packId) { viewModel.load(packId) }
    val dimensions by viewModel.dimensionStates.collectAsState()
    var selectedIndex by remember { mutableIntStateOf(0) }

    val safeIndex = if (dimensions.isEmpty()) 0 else selectedIndex.coerceIn(0, dimensions.lastIndex)

    Row(Modifier.fillMaxSize().background(Background)) {

        // ── 左侧：维度选择栏 ──────────────────────────────────────────────────
        Column(
            Modifier.width(260.dp).fillMaxHeight()
                .background(Brush.verticalGradient(listOf(Color(0xFF2D1B4E), Color(0xFF1A0533))))
                .padding(vertical = 20.dp)
        ) {
            // 返回按钮
            Row(
                Modifier.padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onBack)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    Modifier.size(32.dp).background(Color.White.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text("返回", fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "选择维度",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.padding(horizontal = 20.dp),
                letterSpacing = 2.sp
            )
            Text(
                "思维探索",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(24.dp))

            dimensions.forEachIndexed { i, dimState ->
                val isSelected = i == safeIndex
                val colors = dimensionColors[dimState.dimension] ?: listOf(PinkPrimary, PinkSecondary)
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White.copy(alpha = 0.12f) else Color.Transparent,
                    animationSpec = tween(200),
                    label = "dim_bg"
                )

                Row(
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgColor)
                        .clickable { selectedIndex = i }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 维度图标圆圈
                    Box(
                        Modifier.size(40.dp)
                            .background(
                                if (isSelected) Brush.linearGradient(colors)
                                else Brush.linearGradient(colors.map { it.copy(alpha = 0.3f) }),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = dimState.dimension.icon,
                            contentDescription = dimState.dimension.label,
                            tint = if (isSelected) Color.White else colors[0],
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            dimState.dimension.label,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            "${dimState.levels.size} 个关卡",
                            fontSize = 11.sp,
                            color = if (isSelected) colors[0].copy(alpha = 0.9f) else Color.White.copy(alpha = 0.35f)
                        )
                    }
                    if (isSelected) {
                        Spacer(Modifier.weight(1f))
                        Box(Modifier.size(6.dp).background(colors[0], CircleShape))
                    }
                }
            }
        }

        // ── 右侧：关卡列表 ────────────────────────────────────────────────────
        if (dimensions.isNotEmpty()) {
            val dimState = dimensions[safeIndex]
            val colors = dimensionColors[dimState.dimension] ?: listOf(PinkPrimary, PinkSecondary)

            Column(Modifier.weight(1f).fillMaxHeight()) {
                // 维度标题条
                Box(
                    Modifier.fillMaxWidth()
                        .background(Brush.horizontalGradient(colors + listOf(colors.last().copy(alpha = 0.0f))))
                        .padding(horizontal = 28.dp, vertical = 18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            Modifier.size(48.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = dimState.dimension.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Column {
                            Text(
                                "${dimState.dimension.label}思维",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                "共 ${dimState.levels.sumOf { it.totalQuestions }} 道题目",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(dimState.levels) { level ->
                        LevelCard(
                            level = level,
                            accentColors = colors,
                            onClick = { onLevelClick(dimState.dimension.name, level.levelId) }
                        )
                    }
                }
            }
        } else {
            Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PinkPrimary)
            }
        }
    }
}

@Composable
private fun LevelCard(
    level: LevelUiState,
    accentColors: List<Color>,
    onClick: () -> Unit
) {
    val completedFraction = level.bestStars / 3f

    Box(
        Modifier.fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = accentColors[0].copy(alpha = 0.2f))
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        // 左侧彩色竖条
        Box(
            Modifier.width(5.dp).fillMaxHeight()
                .background(Brush.verticalGradient(accentColors))
                .align(Alignment.CenterStart)
        )

        Row(
            Modifier.fillMaxWidth().padding(start = 18.dp, end = 20.dp, top = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 关卡编号圆形
                Box(
                    Modifier.size(44.dp)
                        .background(
                            if (level.bestStars > 0) Brush.linearGradient(accentColors)
                            else Brush.linearGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0))),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (level.bestStars > 0) {
                        Icon(
                            Icons.Rounded.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            level.levelId.takeLast(2),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
                Column {
                    Text(
                        "关卡 ${level.levelId.takeLast(2)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface
                    )
                    Text(
                        "${level.totalQuestions} 道题",
                        fontSize = 12.sp,
                        color = OnSurface.copy(alpha = 0.45f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 星级图标
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    repeat(3) { i ->
                        Icon(
                            imageVector = if (i < level.bestStars) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                            contentDescription = null,
                            tint = if (i < level.bestStars) Color(0xFFFFB800) else Color(0xFFCBD5E1),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                // 进度条
                Box(
                    Modifier.width(80.dp).height(4.dp)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(2.dp))
                ) {
                    if (completedFraction > 0f) {
                        Box(
                            Modifier.fillMaxHeight()
                                .fillMaxWidth(completedFraction)
                                .background(Brush.horizontalGradient(accentColors), RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        }
    }
}
