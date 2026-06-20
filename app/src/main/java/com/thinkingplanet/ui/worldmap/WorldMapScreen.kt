package com.thinkingplanet.ui.worldmap

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thinkingplanet.domain.model.TrophyLevel
import com.thinkingplanet.ui.theme.*

@Composable
fun WorldMapScreen(
    onChapterClick: (packId: String) -> Unit,
    onHonorClick: () -> Unit,
    viewModel: WorldMapViewModel = hiltViewModel()
) {
    val worlds by viewModel.worldStates.collectAsState()
    val totalStars by viewModel.totalStars.collectAsState()

    Box(Modifier.fillMaxSize()) {
        // 径向渐变背景
        Box(
            Modifier.fillMaxSize().background(
                Brush.radialGradient(
                    listOf(Color(0xFFFFF0F9), Color(0xFFF5E9FF), Color(0xFFEBF0FF)),
                    radius = 1200f
                )
            )
        )
        DecoStars()

        Column(Modifier.fillMaxSize()) {
            AppHeader(totalStars = totalStars, onHonorClick = onHonorClick)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(worlds) { world ->
                    WorldCard(world = world, onClick = {
                        if (world.isUnlocked) onChapterClick(world.upperPackId)
                    })
                }
            }
        }
    }
}

@Composable
private fun DecoStars() {
    Box(Modifier.fillMaxSize()) {
        listOf(
            Triple("✦", 0.06f, Alignment.TopStart),
            Triple("✧", 0.05f, Alignment.TopEnd),
            Triple("✦", 0.04f, Alignment.BottomStart),
            Triple("✧", 0.07f, Alignment.BottomEnd),
            Triple("✦", 0.03f, Alignment.CenterEnd),
        ).forEach { (emoji, alpha, align) ->
            Text(
                emoji, fontSize = 40.sp,
                color = PurplePrimary.copy(alpha = alpha),
                modifier = Modifier.align(align).padding(32.dp)
            )
        }
    }
}

@Composable
private fun AppHeader(totalStars: Int, onHonorClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(
                Brush.linearGradient(listOf(Color(0xFFFF6FA8), Color(0xFFBF6EFF), Color(0xFF7B8CFF))),
                RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .padding(horizontal = 28.dp, vertical = 16.dp)
    ) {
        Box(
            Modifier.size(80.dp).background(Color.White.copy(alpha = 0.08f), CircleShape)
                .align(Alignment.CenterEnd).offset(x = 20.dp)
        )
        Box(
            Modifier.size(50.dp).background(Color.White.copy(alpha = 0.06f), CircleShape)
                .align(Alignment.TopStart).offset(x = (-10).dp, y = (-10).dp)
        )

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Box(
                    Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧚", fontSize = 26.sp)
                }
                Column {
                    Text(
                        "思维星球",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "✨ 小探险家的奇幻之旅",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 星星计数胶囊 - 使用 Material Star 图标
                Row(
                    Modifier.background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFE066),
                        modifier = Modifier.size(18.dp)
                    )
                    Text("$totalStars", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                // 荣誉殿堂按钮 - 使用 EmojiEvents 奖杯图标
                TextButton(
                    onClick = onHonorClick,
                    colors = ButtonDefaults.textButtonColors(containerColor = Color.White.copy(alpha = 0.18f)),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFE066),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("荣誉殿堂", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun WorldCard(world: WorldUiState, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && world.isUnlocked) 0.96f else 1f,
        animationSpec = tween(100),
        label = "card_scale"
    )

    val primaryColor = Color(world.theme.primaryColor)
    val secondaryColor = Color(world.theme.secondaryColor)

    Box(
        Modifier.scale(scale)
            .alpha(if (world.isUnlocked) 1f else 0.45f)
            .shadow(
                elevation = if (world.isUnlocked) 12.dp else 2.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = primaryColor.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = world.isUnlocked,
                onClick = onClick
            )
    ) {
        Column {
            // 渐变图片区域
            Box(
                Modifier.fillMaxWidth().height(130.dp)
                    .background(Brush.verticalGradient(listOf(primaryColor, secondaryColor))),
                contentAlignment = Alignment.Center
            ) {
                Box(Modifier.size(90.dp).background(Color.White.copy(alpha = 0.12f), CircleShape))
                Text(world.theme.emoji, fontSize = 52.sp)

                if (!world.isUnlocked) {
                    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.15f)))
                    // 使用 Material Lock 图标替换 emoji
                    Icon(
                        Icons.Rounded.Lock,
                        contentDescription = "已锁定",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(32.dp).align(Alignment.Center)
                    )
                }

                // 年级标签
                Box(
                    Modifier.align(Alignment.TopStart).padding(10.dp)
                        .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("${world.theme.grade}年级", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            // 信息区（白底）
            Column(
                Modifier.background(Color.White).padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    world.theme.worldName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TrophyChip(level = world.upperTrophy, label = "上册", primary = primaryColor)
                    TrophyChip(level = world.lowerTrophy, label = "下册", primary = primaryColor)
                }
            }
        }
    }
}

@Composable
private fun TrophyChip(level: TrophyLevel, label: String, primary: Color) {
    val (trophyEmoji, bg) = when (level) {
        TrophyLevel.GOLD   -> "🥇" to Color(0xFFFEF9C3)
        TrophyLevel.SILVER -> "🥈" to Color(0xFFF1F5F9)
        TrophyLevel.BRONZE -> "🥉" to Color(0xFFFFF7ED)
        TrophyLevel.NONE   -> "○" to primary.copy(alpha = 0.06f)
    }
    Row(
        Modifier.background(bg, RoundedCornerShape(8.dp)).padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(trophyEmoji, fontSize = 11.sp)
        Text(label, fontSize = 10.sp, color = OnSurface.copy(alpha = 0.7f))
    }
}
