package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChanged: ((Float) -> Unit)? = null,
    maxStars: Int = 5,
    starSize: Dp = 24.dp,
    activeColor: Color = Color(0xFFE67E22),
    inactiveColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.testTag("star_rating_bar"),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val starValue = i.toFloat()
            val isFull = rating >= starValue
            val isHalf = !isFull && rating >= (starValue - 0.5f)

            val animatedScale by animateFloatAsState(
                targetValue = if (isFull || isHalf) 1.08f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "star_anim"
            )

            val interactionSource = remember { MutableInteractionSource() }

            Icon(
                imageVector = when {
                    isFull -> Icons.Filled.Star
                    isHalf -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Filled.StarBorder
                },
                contentDescription = "Star $i",
                tint = if (isFull || isHalf) activeColor else inactiveColor,
                modifier = Modifier
                    .size(starSize)
                    .scale(animatedScale)
                    .then(
                        if (onRatingChanged != null) {
                            Modifier.clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                // Toggle full star or half star if tapped again
                                val newRating = if (rating == starValue) {
                                    starValue - 0.5f
                                } else if (rating == (starValue - 0.5f)) {
                                    starValue - 1.0f
                                } else {
                                    starValue
                                }
                                onRatingChanged(newRating.coerceAtLeast(0.5f))
                            }
                        } else Modifier
                    )
            )
        }
    }
}
