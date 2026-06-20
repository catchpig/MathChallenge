package com.thinkingplanet.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.ui.graphics.vector.ImageVector
import com.thinkingplanet.domain.model.Dimension

val Dimension.icon: ImageVector
    get() = when (this) {
        Dimension.MATH     -> Icons.Outlined.Calculate
        Dimension.LOGIC    -> Icons.Outlined.Extension
        Dimension.SPATIAL  -> Icons.Outlined.ViewInAr
        Dimension.PATTERN  -> Icons.Outlined.AutoAwesome
        Dimension.CREATIVE -> Icons.Outlined.Palette
    }
