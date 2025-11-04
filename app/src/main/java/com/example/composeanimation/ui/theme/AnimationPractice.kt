package com.example.composeanimation.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AnimationTesting(modifier: Modifier = Modifier) {
    var showHeader by remember { mutableStateOf(false) }
    var showStatsCards by remember { mutableStateOf(false) }
    var showMainCard by remember { mutableStateOf(false) }
    var showListItems by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showHeader = true

        delay(800)
        showStatsCards = true

        delay(800)
        showMainCard = true

    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AnimatedVisibility(
                visible = showHeader,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                HeaderSection()
            }
        }

        // Stats Cards Row
        item {
            AnimatedVisibility(
                visible = showStatsCards,
            ) {
                StatsCardsRow()
            }
        }

        // Main Content Card
        item {
            AnimatedVisibility(
                visible = showMainCard,
            ) {
                MainContentCard()
            }
        }

        // List Items
        items(10) { index ->
            AnimatedVisibility(
                visible = showListItems,
            ) {

                ListItemCard(index = index)
            }
        }
    }
}

@Composable
fun HeaderSection() {

    var triggerBounce by remember { mutableStateOf(false) }
    var triggerAlpha by remember { mutableStateOf(false) }

    val textScale by animateFloatAsState(
        targetValue = if (triggerBounce) 1.5f else 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        finishedListener = {
            if (it == 1.5f) {
                triggerAlpha = true
                triggerBounce = false
            }
        }
    )

    val alphaScale by animateFloatAsState(
        targetValue = if (triggerAlpha) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
    )

    LaunchedEffect(Unit) {
        delay(600)
        triggerBounce = true
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF6200EE),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp)
    ) {
        Text(
            text = "Animation Playground",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.scale(textScale)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Test your animations here",
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.alpha(alphaScale)
        )
    }
}

@Composable
fun StatsCardsRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(5) { index ->
            StatsCard(
                title = "Stat ${index + 1}",
                value = "${(index + 1) * 100}",
                color = getColorForIndex(index)
            )
        }
    }
}

@Composable
fun StatsCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun MainContentCard() {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Featured Content",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This is a sample card with expandable content. You can add animations to expand/collapse, fade in/out, or slide elements.",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { isExpanded = !isExpanded },
            ) {
                Text(text = if (isExpanded) "Show Less" else "Show More")
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Additional content appears here! You can animate this section when it expands or collapses.",
                    fontSize = 14.sp,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

@Composable
fun ListItemCard(index: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "List Item ${index + 1}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Description for item ${index + 1}",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = getColorForIndex(index % 5),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

fun getColorForIndex(index: Int): Color {
    return when (index % 5) {
        0 -> Color(0xFF6200EE)
        1 -> Color(0xFF03DAC6)
        2 -> Color(0xFFFF6E40)
        3 -> Color(0xFF2196F3)
        else -> Color(0xFF4CAF50)
    }
}

@Preview(showBackground = true)
@Composable
fun AnimationTestingPreview(modifier: Modifier = Modifier) {
    AnimationTesting()
}
