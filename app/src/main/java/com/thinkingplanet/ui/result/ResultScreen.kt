package com.thinkingplanet.ui.result

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thinkingplanet.ui.theme.*

@Composable
fun ResultScreen(stars: Int, onDone: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val contentScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.7f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "content_scale"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(400),
        label = "content_alpha"
    )

    val (title, emoji, message, bgColors) = when (stars) {
        3    -> ResultData("太完美啦！", "🎉", "全部答对！向导精灵为你骄傲！",
                     listOf(Color(0xFFFFF0F6), Color(0xFFEDE9FE), Color(0xFFFEF9C3)))
        2    -> ResultData("做得很好！", "🌸", "再努力一点点就能全对了！",
                     listOf(Color(0xFFFFF0F6), Color(0xFFF3E8FF), Color(0xFFEFF6FF)))
        1    -> ResultData("继续加油！", "🌟", "勇敢的尝试！下次一定更好！",
                     listOf(Color(0xFFF0FDFA), Color(0xFFEFF6FF), Color(0xFFFFF7ED)))
        else -> ResultData("再试一次吧", "💪", "没关系，看看题目提示再来！",
                     listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9), Color(0xFFEFF6FF)))
    }

    Box(
        Modifier.fillMaxSize()
            .background(Brush.radialGradient(bgColors, radius = 1200f)),
        contentAlignment = Alignment.Center
    ) {
        // 装饰性背景圆
        Box(Modifier.size(400.dp).background(Color.White.copy(alpha = 0.25f), CircleShape).align(Alignment.Center))
        Box(Modifier.size(280.dp).background(Color.White.copy(alpha = 0.15f), CircleShape)
            .align(Alignment.TopStart).offset((-60).dp, (-60).dp))
        Box(Modifier.size(200.dp).background(PurplePrimary.copy(alpha = 0.06f), CircleShape)
            .align(Alignment.BottomEnd).offset(60.dp, 60.dp))

        Column(
            Modifier.scale(contentScale).alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 大表情圆形容器
            Box(
                Modifier.size(110.dp)
                    .shadow(20.dp, CircleShape, spotColor = PinkPrimary.copy(alpha = 0.3f))
                    .background(Brush.linearGradient(listOf(PinkPrimary, PurplePrimary)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 56.sp)
            }

            Text(
                title,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF5B21B6)
            )

            // 星星逐个弹出 - Material Icons + spring 动画
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    StarItem(filled = i < stars, delay = i * 180)
                }
            }

            // 消息卡片
            Box(
                Modifier.shadow(12.dp, RoundedCornerShape(24.dp), spotColor = PurplePrimary.copy(alpha = 0.15f))
                    .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 28.dp, vertical = 16.dp)
            ) {
                Text(
                    message,
                    fontSize = 16.sp,
                    color = Color(0xFF5B21B6),
                    fontWeight = FontWeight.Medium
                )
            }

            // 渐变返回按钮
            Button(
                onClick = onDone,
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier.height(56.dp).width(220.dp)
                    .shadow(10.dp, RoundedCornerShape(20.dp), spotColor = PinkSecondary.copy(alpha = 0.4f))
            ) {
                Box(
                    Modifier.fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(PinkSecondary, PurplePrimary)), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("返回地图 🗺️", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun StarItem(filled: Boolean, delay: Int) {
    var triggered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        triggered = true
    }
    val scale by animateFloatAsState(
        targetValue = if (triggered) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium),
        label = "star_$delay"
    )
    Icon(
        imageVector = if (filled) Icons.Rounded.Star else Icons.Rounded.StarBorder,
        contentDescription = null,
        tint = if (filled) Color(0xFFFFB800) else Color(0xFFCBD5E1),
        modifier = Modifier.size(52.dp).scale(scale)
    )
}

private data class ResultData(val title: String, val emoji: String, val message: String, val bgColors: List<Color>)
