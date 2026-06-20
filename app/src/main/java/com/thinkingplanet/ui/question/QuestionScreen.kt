package com.thinkingplanet.ui.question

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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

private val dimensionGradients = mapOf(
    "MATH"     to listOf(Color(0xFFFF6FA8), Color(0xFFFF9BC5)),
    "LOGIC"    to listOf(Color(0xFF818CF8), Color(0xFFA5B4FC)),
    "SPATIAL"  to listOf(Color(0xFF34D399), Color(0xFF6EE7B7)),
    "PATTERN"  to listOf(Color(0xFFFBBF24), Color(0xFFFDE68A)),
    "CREATIVE" to listOf(Color(0xFFBF6EFF), Color(0xFFD8B4FE))
)

@Composable
fun QuestionScreen(
    packId: String,
    dimension: String,
    levelId: String,
    onFinish: (stars: Int) -> Unit,
    viewModel: QuestionViewModel = hiltViewModel()
) {
    LaunchedEffect(packId, dimension, levelId) { viewModel.load(packId, dimension, levelId) }
    val s by viewModel.state.collectAsState()

    LaunchedEffect(s.isFinished) {
        if (s.isFinished) onFinish(s.finalStars)
    }

    val q = s.question ?: return
    val accentColors = dimensionGradients[dimension] ?: listOf(PinkPrimary, PinkSecondary)
    val dimObj = runCatching { Dimension.valueOf(dimension) }.getOrNull()

    Row(Modifier.fillMaxSize().background(Color(0xFFF8F0FD))) {

        // ── 左侧：题目面板 ──────────────────────────────────────────────────
        Column(
            Modifier.weight(1.05f).fillMaxHeight()
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF5FB), Color(0xFFF5EEFF))))
        ) {
            // 顶部维度色彩条
            Box(
                Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(accentColors))
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 维度图标 + 标签
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            Modifier.size(32.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (dimObj != null) {
                                Icon(
                                    imageVector = dimObj.icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text("🧠", fontSize = 16.sp)
                            }
                        }
                        Text(
                            "${dimObj?.label ?: ""}思维",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    // 点状进度指示器
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(s.total.coerceAtMost(8)) { i ->
                            val isDone = i < s.currentIndex
                            val isCurrent = i == s.currentIndex
                            Box(
                                Modifier.size(if (isCurrent) 10.dp else 7.dp)
                                    .background(
                                        when {
                                            isDone    -> Color.White
                                            isCurrent -> Color.White.copy(alpha = 0.9f)
                                            else      -> Color.White.copy(alpha = 0.35f)
                                        },
                                        CircleShape
                                    )
                            )
                        }
                        Text(
                            "${s.currentIndex + 1}/${s.total}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Column(
                Modifier.weight(1f).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // 吉祥物 + 对话气泡
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        Modifier.size(56.dp)
                            .shadow(8.dp, CircleShape, spotColor = accentColors[0].copy(alpha = 0.4f))
                            .background(Brush.linearGradient(accentColors), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧚", fontSize = 30.sp)
                    }
                    Box(
                        Modifier.shadow(4.dp, RoundedCornerShape(6.dp, 20.dp, 20.dp, 20.dp))
                            .background(Color.White, RoundedCornerShape(6.dp, 20.dp, 20.dp, 20.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = if (s.showHint) "💡 ${q.hint}" else "仔细想想，你一定可以的 ✨",
                            fontSize = 13.sp,
                            color = Color(0xFF5B21B6),
                            lineHeight = 20.sp
                        )
                    }
                }

                // 题目卡片
                Box(
                    Modifier.weight(1f)
                        .shadow(10.dp, RoundedCornerShape(24.dp), spotColor = accentColors[0].copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                ) {
                    Box(Modifier.width(5.dp).fillMaxHeight()
                        .background(Brush.verticalGradient(accentColors)))
                    Box(
                        Modifier.fillMaxSize().padding(start = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            q.text,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnSurface,
                            lineHeight = 32.sp
                        )
                    }
                }
            }
        }

        // ── 分隔线 ──────────────────────────────────────────────────────────
        Box(Modifier.width(1.dp).fillMaxHeight().background(PinkPrimary.copy(alpha = 0.15f)))

        // ── 右侧：选项面板 ──────────────────────────────────────────────────
        Column(
            Modifier.weight(0.95f).fillMaxHeight()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            Text(
                "选择正确答案",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface.copy(alpha = 0.4f),
                letterSpacing = 1.5.sp
            )

            val labels = listOf("A", "B", "C", "D")
            q.options.forEachIndexed { i, option ->
                OptionButton(
                    label = labels[i],
                    text = option,
                    isSelected = s.selectedIndex == i,
                    isAnswered = s.isAnswered,
                    isCorrect = s.isAnswered && i == q.answerIndex,
                    isWrong = s.isAnswered && s.selectedIndex == i && i != q.answerIndex,
                    accentColors = accentColors,
                    onClick = { viewModel.selectAnswer(i) }
                )
            }

            Spacer(Modifier.height(4.dp))

            if (s.isAnswered) {
                Button(
                    onClick = { viewModel.next() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        Modifier.fillMaxSize()
                            .background(Brush.horizontalGradient(accentColors), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (s.currentIndex + 1 >= s.total) "✨ 查看结果" else "下一题 →",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = { viewModel.showHint() },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColors[0])
                ) {
                    Icon(
                        Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("需要提示？", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun OptionButton(
    label: String,
    text: String,
    isSelected: Boolean,
    isAnswered: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    accentColors: List<Color>,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !isAnswered) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "opt_scale"
    )

    val bgColor by animateColorAsState(
        targetValue = when {
            isCorrect  -> Color(0xFF34D399)
            isWrong    -> Color(0xFFFB7185)
            isSelected -> accentColors[0]
            else       -> Color.White
        },
        animationSpec = tween(200),
        label = "opt_bg"
    )

    val textColor = if (isSelected || isCorrect || isWrong) Color.White else OnSurface
    val borderColor = if (!isSelected && !isCorrect && !isWrong)
        accentColors[0].copy(alpha = 0.25f) else Color.Transparent

    Row(
        Modifier.scale(scale).fillMaxWidth()
            .shadow(
                if (isSelected || isCorrect || isWrong) 8.dp else 3.dp,
                RoundedCornerShape(16.dp),
                spotColor = bgColor.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isAnswered,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 选项标签圆
        Box(
            Modifier.size(34.dp)
                .background(
                    if (isSelected || isCorrect || isWrong) Color.White.copy(alpha = 0.22f)
                    else accentColors[0].copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCorrect -> Icon(
                    Icons.Rounded.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                isWrong -> Icon(
                    Icons.Rounded.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                else -> Text(
                    label,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) Color.White else accentColors[0],
                    fontSize = 14.sp
                )
            }
        }
        Text(
            text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}
