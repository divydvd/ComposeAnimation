package com.example.composeanimation.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * ========================================
 * REUSABLE ANIMATION COMPONENTS LIBRARY
 * ========================================
 *
 * Copy-paste ready components for common animation patterns.
 * Save 30-60 minutes during interviews by using these building blocks.
 *
 * COMPONENTS:
 * 1. ShimmerEffect - Loading placeholder
 * 2. PullToRefreshSpring - Custom pull-to-refresh
 * 3. SwipeToDismissCard - Dismissible card with velocity
 * 4. ZoomableBox - Pinch-to-zoom container
 * 5. StaggeredListAnimation - Animated list entrance
 * 6. CustomProgressIndicator - Segmented progress
 * 7. ExpandableBottomSheet - Draggable bottom sheet
 * 8. AnimatedTabIndicator - Morphing tab indicator
 * 9. CurvedMotionFAB - FAB with curved path
 * 10. DraggableReorderableItem - Drag to reorder
 */

// ========================================
// 1. SHIMMER EFFECT
// Copy-paste for loading states
// ========================================

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer translation"
    )

    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFE0E0E0),
                        Color(0xFFF5F5F5),
                        Color(0xFFE0E0E0)
                    ),
                    start = Offset(shimmerTranslate - 1000f, 0f),
                    end = Offset(shimmerTranslate, 0f)
                )
            )
    )
}

// Usage:
// ShimmerEffect(modifier = Modifier.fillMaxWidth().height(200.dp))

// ========================================
// 2. PULL TO REFRESH WITH SPRING
// Custom implementation with spring physics
// ========================================

@Composable
fun PullToRefreshSpring(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val pullThreshold = 150f
    val maxPull = 300f

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetY.value >= pullThreshold && !isRefreshing) {
                                    onRefresh()
                                    offsetY.animateTo(80f, spring())
                                } else {
                                    offsetY.animateTo(0f, spring())
                                }
                            }
                        },
                        onVerticalDrag = { _, dragAmount ->
                            if (!isRefreshing && dragAmount > 0) {
                                scope.launch {
                                    val newOffset = (offsetY.value + dragAmount * 0.5f)
                                        .coerceIn(0f, maxPull)
                                    offsetY.snapTo(newOffset)
                                }
                            }
                        }
                    )
                }
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
        ) {
            content()
        }

        // Refresh indicator
        if (offsetY.value > 0 || isRefreshing) {
            val infiniteTransition = rememberInfiniteTransition(label = "refresh")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (offsetY.value - 60).dp)
                    .size(40.dp)
                    .background(Color(0xFF6200EE), CircleShape)
                    .then(
                        if (isRefreshing) Modifier.graphicsLayer { rotationZ = rotation }
                        else Modifier.graphicsLayer {
                            rotationZ = (offsetY.value / maxPull) * 180f
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("↻", color = Color.White, fontSize = 20.sp)
            }
        }
    }

    // Reset offset when refresh completes
    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            offsetY.animateTo(0f, spring())
        }
    }
}

// Usage:
// var isRefreshing by remember { mutableStateOf(false) }
// PullToRefreshSpring(isRefreshing, onRefresh = { /* refresh logic */ }) {
//     LazyColumn { ... }
// }

// ========================================
// 3. SWIPE TO DISMISS CARD
// Velocity-based dismissal with animation
// ========================================

@Composable
fun SwipeToDismissCard(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissThreshold: Float = 200f,
    content: @Composable () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val velocityTracker = remember { VelocityTracker() }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            val velocity = velocityTracker.calculateVelocity().x

                            // Dismiss if: swiped beyond threshold OR high velocity
                            if (offsetX.value.absoluteValue > dismissThreshold ||
                                velocity.absoluteValue > 1000f
                            ) {
                                // Animate out
                                offsetX.animateTo(
                                    targetValue = if (offsetX.value > 0) 1000f else -1000f,
                                    animationSpec = tween(300)
                                )
                                onDismiss()
                            } else {
                                // Snap back
                                offsetX.animateTo(0f, spring())
                            }
                        }
                        velocityTracker.resetTracking()
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount)
                            velocityTracker.addPosition(
                                change.uptimeMillis,
                                change.position
                            )
                        }
                    }
                )
            }
            .graphicsLayer {
                alpha = 1f - (offsetX.value.absoluteValue / 500f).coerceIn(0f, 0.5f)
            }
    ) {
        content()
    }
}

// Usage:
// SwipeToDismissCard(onDismiss = { /* remove item */ }) {
//     Card { Text("Swipe me away") }
// }

// ========================================
// 4. ZOOMABLE BOX
// Pinch-to-zoom with boundary detection
// ========================================

@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxScale: Float = 4f,
    content: @Composable BoxScope.() -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    // Update scale
                    scale = (scale * zoom).coerceIn(minScale, maxScale)

                    // Allow panning only when zoomed
                    if (scale > 1f) {
                        offsetX += pan.x
                        offsetY += pan.y

                        // TODO: Add boundary detection based on container size
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offsetX
                translationY = offsetY
            },
        content = content
    )
}

// Usage:
// ZoomableBox {
//     Image(painter = painterResource(R.drawable.image), contentDescription = null)
// }

// ========================================
// 5. STAGGERED LIST ANIMATION
// Entrance animation for lists with delay
// ========================================

@Composable
fun StaggeredVisibilityItem(
    visible: Boolean,
    index: Int,
    staggerDelayMs: Int = 50,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = index * staggerDelayMs
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = index * staggerDelayMs
            ),
            initialOffsetY = { it / 3 }
        ),
        exit = fadeOut() + shrinkVertically()
    ) {
        content()
    }
}

// Usage in LazyColumn:
// var isVisible by remember { mutableStateOf(false) }
// LaunchedEffect(Unit) { isVisible = true }
//
// itemsIndexed(items) { index, item ->
//     StaggeredVisibilityItem(visible = isVisible, index = index) {
//         ListItem(item)
//     }
// }

// ========================================
// 6. CUSTOM PROGRESS INDICATOR
// Segmented circular progress with animation
// ========================================

@Composable
fun SegmentedProgressIndicator(
    progress: Float, // 0f to 1f
    segments: Int = 8,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(segments) { index ->
            val segmentProgress = ((animatedProgress * segments) - index).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(segmentProgress)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC6))
                            )
                        )
                )
            }
        }
    }
}

// Usage:
// var progress by remember { mutableStateOf(0f) }
// SegmentedProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())

// ========================================
// 7. EXPANDABLE BOTTOM SHEET
// Draggable bottom sheet with snap points
// ========================================

@Composable
fun ExpandableBottomSheet(
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val heightPx = remember { Animatable(200f) }
    val collapsedHeight = 200f
    val expandedHeight = 600f
    val scope = rememberCoroutineScope()

    LaunchedEffect(isExpanded) {
        heightPx.animateTo(
            targetValue = if (isExpanded) expandedHeight else collapsedHeight,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(heightPx.value.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            val shouldExpand = heightPx.value > (collapsedHeight + expandedHeight) / 2
                            onExpandedChange(shouldExpand)
                        }
                    },
                    onVerticalDrag = { _, dragAmount ->
                        scope.launch {
                            val newHeight = (heightPx.value - dragAmount)
                                .coerceIn(collapsedHeight, expandedHeight)
                            heightPx.snapTo(newHeight)
                        }
                    }
                )
            },
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Drag handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.Gray, RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

// Usage:
// var isExpanded by remember { mutableStateOf(false) }
// Box(modifier = Modifier.fillMaxSize()) {
//     ExpandableBottomSheet(isExpanded, onExpandedChange = { isExpanded = it }) {
//         Text("Bottom sheet content")
//     }
// }

// ========================================
// 8. ANIMATED TAB INDICATOR
// Morphing indicator that follows tabs
// ========================================

@Composable
fun AnimatedTabIndicator(
    selectedTabIndex: Int,
    tabCount: Int,
    modifier: Modifier = Modifier
) {
    val indicatorOffset by animateDpAsState(
        targetValue = (selectedTabIndex * (360 / tabCount)).dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "indicator offset"
    )

    val indicatorWidth by animateDpAsState(
        targetValue = (360 / tabCount).dp,
        animationSpec = spring(),
        label = "indicator width"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        // Background tabs (placeholder)
        Row(modifier = Modifier.fillMaxWidth()) {
            repeat(tabCount) {
                Box(modifier = Modifier.weight(1f))
            }
        }

        // Animated indicator
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(indicatorWidth)
                .height(4.dp)
                .align(Alignment.BottomStart)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC6))
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

// Usage:
// var selectedTab by remember { mutableStateOf(0) }
// AnimatedTabIndicator(selectedTabIndex = selectedTab, tabCount = 3)

// ========================================
// 9. FAB WITH CURVED MOTION PATH
// Floating button with curved animation path
// ========================================

@Composable
fun CurvedMotionFAB(
    onClick: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    // Animate along a curved path using keyframes
    val offsetX by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 200.dp,
        animationSpec = keyframes {
            durationMillis = 600
            0.dp at 0
            -50.dp at 200 // Curve left
            50.dp at 400 // Curve right
            0.dp at 600
        },
        label = "offsetX"
    )

    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 200.dp,
        animationSpec = tween(600),
        label = "offsetY"
    )

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "scale"
    )

    if (isVisible || scale > 0f) {
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier
                .offset(x = offsetX, y = offsetY)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            Text("+", fontSize = 24.sp)
        }
    }
}

// Usage:
// var showFAB by remember { mutableStateOf(true) }
// CurvedMotionFAB(onClick = { }, isVisible = showFAB)

// ========================================
// 10. DRAGGABLE REORDERABLE ITEM
// List item with drag-to-reorder capability
// ========================================

@Composable
fun DraggableReorderableItem(
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDragEnd: (Float) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val offsetY = remember { Animatable(0f) }
    val elevation by animateDpAsState(
        targetValue = if (isDragging) 8.dp else 2.dp,
        label = "elevation"
    )
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        label = "scale"
    )
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDragEnd = {
                        scope.launch {
                            val finalOffset = offsetY.value
                            offsetY.animateTo(0f, spring())
                            onDragEnd(finalOffset)
                        }
                    },
                    onDrag = { _, dragAmount ->
                        scope.launch {
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                        }
                    }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        content()
    }
}

// Usage:
// var isDragging by remember { mutableStateOf(false) }
// DraggableReorderableItem(
//     isDragging = isDragging,
//     onDragStart = { isDragging = true },
//     onDragEnd = { offset ->
//         isDragging = false
//         // Handle reordering based on offset
//     }
// ) {
//     Text("Drag me to reorder")
// }
