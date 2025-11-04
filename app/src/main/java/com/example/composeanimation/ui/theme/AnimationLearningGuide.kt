package com.example.composeanimation.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * COMPOSE ANIMATION LEARNING GUIDE
 *
 * This file contains comprehensive examples of all major Compose animation APIs.
 * Each section demonstrates a specific animation concept with interactive examples.
 *
 * Topics covered:
 * 1. animate*AsState() - Simple state-driven animations
 * 2. updateTransition() - Coordinated multi-property animations
 * 3. AnimatedVisibility & AnimatedContent - Enter/exit animations
 * 4. AnimationSpec types - Different animation curves
 * 5. rememberInfiniteTransition() - Looping animations
 * 6. Animatable - Manual & gesture-driven animations
 * 7. LaunchedEffect with animations - Sequential & parallel patterns
 */

@Composable
fun AnimationLearningGuide(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Compose Animation Learning Guide",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6200EE)
        )

        // Section 1: animate*AsState
        AnimateAsStateSection()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Section 2: updateTransition
        UpdateTransitionSection()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Section 3: AnimatedVisibility
        AnimatedVisibilitySection()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Section 4: AnimatedContent
        AnimatedContentSection()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Section 5: AnimationSpec Types
        AnimationSpecSection()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Section 6: InfiniteTransition
        InfiniteTransitionSection()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Section 7: Animatable
        AnimatableSection()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Section 8: Sequential & Parallel Animations
        SequentialParallelSection()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ============================================
// SECTION 1: animate*AsState()
// Simple state-driven animations - when state changes, value animates
// ============================================

@Composable
fun AnimateAsStateSection() {
    var isExpanded by remember { mutableStateOf(false) }

    // animateFloatAsState - animates Float values
    val size by animateFloatAsState(
        targetValue = if (isExpanded) 200f else 100f,
        label = "size animation"
    )

    // animateDpAsState - animates Dp values
    val cornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 50.dp else 8.dp,
        label = "corner animation"
    )

    // animateColorAsState - animates Color values
    val color by animateColorAsState(
        targetValue = if (isExpanded) Color(0xFF6200EE) else Color(0xFF03DAC6),
        label = "color animation"
    )

    SectionCard(title = "1. animate*AsState()", description = "Simple state-driven animations") {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(size.dp)
                    .background(color, RoundedCornerShape(cornerRadius))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { isExpanded = !isExpanded }) {
                Text(if (isExpanded) "Collapse" else "Expand")
            }

            CommentText(
                """
                • animateFloatAsState: size (100f → 200f)
                • animateDpAsState: corner radius (8dp → 50dp)
                • animateColorAsState: color (cyan → purple)
                • All animate automatically when state changes
                """.trimIndent()
            )
        }
    }
}

// ============================================
// SECTION 2: updateTransition()
// Coordinate multiple property animations together
// ============================================

@Composable
fun UpdateTransitionSection() {
    var currentState by remember { mutableStateOf(BoxState.SMALL) }

    // Create transition - tracks state changes
    val transition = updateTransition(
        targetState = currentState,
        label = "box transition"
    )

    // Animate multiple properties using the same transition
    val size by transition.animateDp(
        label = "size",
        transitionSpec = { tween(500) }
    ) { state ->
        when (state) {
            BoxState.SMALL -> 80.dp
            BoxState.MEDIUM -> 120.dp
            BoxState.LARGE -> 160.dp
        }
    }

    val color by transition.animateColor(
        label = "color",
        transitionSpec = { tween(500) }
    ) { state ->
        when (state) {
            BoxState.SMALL -> Color(0xFF2196F3)
            BoxState.MEDIUM -> Color(0xFFFF9800)
            BoxState.LARGE -> Color(0xFFE91E63)
        }
    }

    val rotation by transition.animateFloat(
        label = "rotation",
        transitionSpec = { tween(500) }
    ) { state ->
        when (state) {
            BoxState.SMALL -> 0f
            BoxState.MEDIUM -> 180f
            BoxState.LARGE -> 360f
        }
    }

    SectionCard(
        title = "2. updateTransition()",
        description = "Coordinate multiple property animations"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(size)
                    .rotate(rotation)
                    .background(color, RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { currentState = BoxState.SMALL }) {
                    Text("Small")
                }
                Button(onClick = { currentState = BoxState.MEDIUM }) {
                    Text("Medium")
                }
                Button(onClick = { currentState = BoxState.LARGE }) {
                    Text("Large")
                }
            }

            CommentText(
                """
                • updateTransition coordinates multiple animations
                • All properties animate together in sync
                • Size, color, and rotation change simultaneously
                • Great for complex state changes
                """.trimIndent()
            )
        }
    }
}

enum class BoxState { SMALL, MEDIUM, LARGE }

// ============================================
// SECTION 3: AnimatedVisibility
// Enter and exit animations for composables
// ============================================

@Composable
fun AnimatedVisibilitySection() {
    var isVisible by remember { mutableStateOf(true) }

    SectionCard(
        title = "3. AnimatedVisibility",
        description = "Enter/exit animations for composables"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(Color(0xFF4CAF50), RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { isVisible = !isVisible }) {
                Text(if (isVisible) "Hide" else "Show")
            }

            CommentText(
                """
                • AnimatedVisibility handles show/hide animations
                • enter: fadeIn + slideInVertically
                • exit: fadeOut + slideOutVertically
                • Composable is added/removed from composition
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Multiple enter/exit combinations
            Text("Other enter/exit combinations:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            CommentText(
                """
                • fadeIn/fadeOut
                • slideIn/slideOut (Vertically, Horizontally)
                • expandIn/shrinkOut
                • scaleIn/scaleOut
                • Can combine multiple with + operator
                """.trimIndent()
            )
        }
    }
}

// ============================================
// SECTION 4: AnimatedContent
// Animates content changes (like switching between composables)
// ============================================

@Composable
fun AnimatedContentSection() {
    var count by remember { mutableStateOf(0) }

    SectionCard(
        title = "4. AnimatedContent",
        description = "Animate content changes"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    // Slide in from right + fade in, slide out to left + fade out
                    (slideInHorizontally { width -> width } + fadeIn())
                        .togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                },
                label = "count animation"
            ) { targetCount ->
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color(0xFFFF6E40), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$targetCount",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { count-- }) {
                    Text("-")
                }
                Button(onClick = { count++ }) {
                    Text("+")
                }
            }

            CommentText(
                """
                • AnimatedContent animates between different content
                • Old content slides out left + fades
                • New content slides in from right + fades
                • Perfect for switching between screens/states
                """.trimIndent()
            )
        }
    }
}

// ============================================
// SECTION 5: AnimationSpec Types
// Different animation curves and behaviors
// ============================================

@Composable
fun AnimationSpecSection() {
    var animationType by remember { mutableStateOf(AnimationType.TWEEN) }
    var trigger by remember { mutableStateOf(false) }

    val offset by animateDpAsState(
        targetValue = if (trigger) 200.dp else 0.dp,
        animationSpec = when (animationType) {
            AnimationType.TWEEN -> tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            AnimationType.SPRING -> spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            AnimationType.KEYFRAMES -> keyframes {
                durationMillis = 1000
                0.dp at 0
                100.dp at 500 using LinearEasing
                200.dp at 1000
            }
            AnimationType.SNAP -> snap(delayMillis = 200)
            AnimationType.REPEATABLE -> repeatable(
                iterations = 3,
                animation = tween(500),
                repeatMode = RepeatMode.Reverse
            )
        },
        label = "spec animation"
    )

    SectionCard(
        title = "5. AnimationSpec Types",
        description = "Different animation curves"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = offset)
                        .size(60.dp)
                        .background(Color(0xFF9C27B0), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animation type selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { animationType = AnimationType.TWEEN }, modifier = Modifier.weight(1f)) {
                        Text("Tween", fontSize = 10.sp)
                    }
                    Button(onClick = { animationType = AnimationType.SPRING }, modifier = Modifier.weight(1f)) {
                        Text("Spring", fontSize = 10.sp)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { animationType = AnimationType.KEYFRAMES }, modifier = Modifier.weight(1f)) {
                        Text("Keyframes", fontSize = 10.sp)
                    }
                    Button(onClick = { animationType = AnimationType.SNAP }, modifier = Modifier.weight(1f)) {
                        Text("Snap", fontSize = 10.sp)
                    }
                }
                Button(onClick = { animationType = AnimationType.REPEATABLE }, modifier = Modifier.fillMaxWidth()) {
                    Text("Repeatable", fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { trigger = !trigger }) {
                Text("Animate")
            }

            CommentText(
                """
                Current: ${animationType.name}

                • TWEEN: Linear interpolation with easing
                • SPRING: Physics-based bouncy animation
                • KEYFRAMES: Multi-step custom animation
                • SNAP: Instant change with optional delay
                • REPEATABLE: Repeat animation N times
                • INFINITE: Use rememberInfiniteTransition (next section)
                """.trimIndent()
            )
        }
    }
}

enum class AnimationType { TWEEN, SPRING, KEYFRAMES, SNAP, REPEATABLE }

// ============================================
// SECTION 6: rememberInfiniteTransition()
// Continuously looping animations
// ============================================

@Composable
fun InfiniteTransitionSection() {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    // Rotating animation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulsing scale animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Color animation
    val color by infiniteTransition.animateColor(
        initialValue = Color(0xFF6200EE),
        targetValue = Color(0xFF03DAC6),
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )

    SectionCard(
        title = "6. rememberInfiniteTransition()",
        description = "Continuously looping animations"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .rotate(rotation)
                    .background(color, RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            CommentText(
                """
                • rememberInfiniteTransition for continuous loops
                • Rotation: 0° → 360° (RestartMode)
                • Scale: 1.0 → 1.3 (ReverseMode)
                • Color: Purple → Cyan (ReverseMode)
                • All animate infinitely without stopping
                • Great for loading indicators, attention grabbers
                """.trimIndent()
            )
        }
    }
}

// ============================================
// SECTION 7: Animatable
// Low-level API for manual control and gesture-driven animations
// ============================================

@Composable
fun AnimatableSection() {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    SectionCard(
        title = "7. Animatable",
        description = "Manual & gesture-driven animations"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                // Snap back to center when released
                                scope.launch {
                                    offsetX.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                // Update position immediately during drag
                                scope.launch {
                                    offsetX.snapTo(offsetX.value + dragAmount)
                                }
                            }
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                        .size(80.dp)
                        .background(Color(0xFFFF5722), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    scope.launch {
                        offsetX.animateTo(200f, animationSpec = tween(500))
                    }
                }) {
                    Text("Move Right")
                }
                Button(onClick = {
                    scope.launch {
                        offsetX.animateTo(0f, animationSpec = spring())
                    }
                }) {
                    Text("Reset")
                }
            }

            CommentText(
                """
                • Animatable: Low-level animation control
                • animateTo(): Animate to target value
                • snapTo(): Jump immediately without animation
                • Great for gesture-driven animations
                • Try dragging the circle - it snaps back!
                • Provides full control over animation lifecycle
                """.trimIndent()
            )
        }
    }
}

// ============================================
// SECTION 8: Sequential & Parallel Animations
// Using LaunchedEffect to orchestrate complex animations
// ============================================

@Composable
fun SequentialParallelSection() {
    var runSequential by remember { mutableStateOf(false) }
    var runParallel by remember { mutableStateOf(false) }

    val alpha1 = remember { Animatable(1f) }
    val alpha2 = remember { Animatable(1f) }
    val alpha3 = remember { Animatable(1f) }

    val scale1 = remember { Animatable(1f) }
    val scale2 = remember { Animatable(1f) }
    val scale3 = remember { Animatable(1f) }

    // Sequential Animation - one after another
    LaunchedEffect(runSequential) {
        if (runSequential) {
            // Reset
            alpha1.snapTo(1f)
            alpha2.snapTo(1f)
            alpha3.snapTo(1f)

            // Animate sequentially
            alpha1.animateTo(0.3f, animationSpec = tween(500))
            delay(100)
            alpha2.animateTo(0.3f, animationSpec = tween(500))
            delay(100)
            alpha3.animateTo(0.3f, animationSpec = tween(500))
            delay(300)

            // Fade back in
            alpha1.animateTo(1f, animationSpec = tween(500))
            delay(100)
            alpha2.animateTo(1f, animationSpec = tween(500))
            delay(100)
            alpha3.animateTo(1f, animationSpec = tween(500))

            runSequential = false
        }
    }

    // Parallel Animation - all at once
    LaunchedEffect(runParallel) {
        if (runParallel) {
            // Reset
            scale1.snapTo(1f)
            scale2.snapTo(1f)
            scale3.snapTo(1f)

            // Launch all animations in parallel and wait for all to complete
            val job1 = launch {
                scale1.animateTo(
                    targetValue = 1.5f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
                scale1.animateTo(1f, animationSpec = spring())
            }
            val job2 = launch {
                scale2.animateTo(
                    targetValue = 1.5f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
                scale2.animateTo(1f, animationSpec = spring())
            }
            val job3 = launch {
                scale3.animateTo(
                    targetValue = 1.5f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
                scale3.animateTo(1f, animationSpec = spring())
            }

            // Wait for all animations to complete
            job1.join()
            job2.join()
            job3.join()

            runParallel = false
        }
    }

    SectionCard(
        title = "8. Sequential & Parallel Animations",
        description = "Orchestrate complex animation patterns"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Sequential demo
            Text("Sequential Animation:", fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .alpha(alpha1.value)
                        .background(Color(0xFF2196F3), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .alpha(alpha2.value)
                        .background(Color(0xFF4CAF50), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .alpha(alpha3.value)
                        .background(Color(0xFFFF9800), CircleShape)
                )
            }

            Button(
                onClick = { runSequential = true },
                enabled = !runSequential
            ) {
                Text("Run Sequential")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Parallel demo
            Text("Parallel Animation:", fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .scale(scale1.value)
                        .background(Color(0xFFE91E63), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .scale(scale2.value)
                        .background(Color(0xFF9C27B0), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .scale(scale3.value)
                        .background(Color(0xFF673AB7), CircleShape)
                )
            }

            Button(
                onClick = { runParallel = true },
                enabled = !runParallel
            ) {
                Text("Run Parallel")
            }

            CommentText(
                """
                SEQUENTIAL:
                • Use LaunchedEffect with sequential calls
                • Each animation waits for previous to finish
                • Use delay() between animations
                • Pattern: animate → delay → animate → delay

                PARALLEL:
                • Launch multiple coroutines with launch { }
                • All animations start simultaneously
                • Each runs independently
                • Pattern: launch { animate } | launch { animate }

                INTERVIEW TIP: Sequential = one after another,
                Parallel = all at the same time using launch{}
                """.trimIndent()
            )
        }
    }
}

// ============================================
// HELPER COMPOSABLES
// ============================================

@Composable
fun SectionCard(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE)
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
fun CommentText(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color(0xFF555555),
            modifier = Modifier.padding(12.dp),
            lineHeight = 16.sp
        )
    }
}
