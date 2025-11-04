package com.example.composeanimation.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * COMPLEX ANIMATION EXAMPLES
 *
 * This file demonstrates how to combine multiple animation APIs
 * to create sophisticated, real-world animations.
 *
 * Examples:
 * 1. Loading State Animation - Multiple infinite animations + state changes
 * 2. Card Flip Animation - 3D rotation + updateTransition + content switching
 * 3. Pull to Refresh - Animatable + gesture + sequential animations
 * 4. Staggered List Animation - LaunchedEffect + sequential + parallel
 * 5. Complex Button Animation - Multi-property transition with ripple effects
 * 6. Morphing Shapes - Keyframes + multiple properties + coordinated timing
 * 7. Swipe to Delete - Gesture + Animatable + threshold detection
 * 8. Onboarding Flow - AnimatedContent + updateTransition + complex choreography
 */

@Composable
fun ComplexAnimationExamples(
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
            text = "Complex Animation Examples",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6200EE)
        )

        Text(
            text = "Combining multiple animation APIs for real-world effects",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Example 1: Loading State
        LoadingStateExample()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Example 2: Card Flip
        CardFlipExample()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Example 3: Pull to Refresh
        PullToRefreshExample()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Example 4: Staggered List
        StaggeredListExample()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Example 5: Complex Button
        ComplexButtonExample()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Example 6: Morphing Shapes
        MorphingShapesExample()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Example 7: Swipe to Delete
        SwipeToDeleteExample()

        Divider(thickness = 2.dp, color = Color.Gray.copy(alpha = 0.3f))

        // Example 8: Onboarding Flow
        OnboardingFlowExample()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ============================================
// EXAMPLE 1: Loading State Animation
// Combines: InfiniteTransition + AnimatedVisibility + AnimatedContent
// ============================================

@Composable
fun LoadingStateExample() {
    var loadingState by remember { mutableStateOf(LoadingState.LOADING) }

    // Infinite rotation for loading spinner
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulsing scale for loading dots
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    ComplexExampleCard(
        title = "1. Loading State Animation",
        description = "InfiniteTransition + AnimatedVisibility + AnimatedContent"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Animated content switches between loading, success, error
            AnimatedContent(
                targetState = loadingState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith
                            fadeOut(animationSpec = tween(500))
                },
                label = "loading content"
            ) { state ->
                when (state) {
                    LoadingState.LOADING -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .rotate(rotation)
                                    .border(4.dp, Color(0xFF6200EE), CircleShape)
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                repeat(3) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .scale(if (index == 1) scale else 1f)
                                            .background(Color(0xFF6200EE), CircleShape)
                                    )
                                }
                            }
                        }
                    }
                    LoadingState.SUCCESS -> {
                        // Animated checkmark with scale
                        var showCheck by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { showCheck = true }

                        val checkScale by animateFloatAsState(
                            targetValue = if (showCheck) 1f else 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "check scale"
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .scale(checkScale)
                                    .background(Color(0xFF4CAF50), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Success!", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        }
                    }
                    LoadingState.ERROR -> {
                        // Shake animation for error
                        var shake by remember { mutableStateOf(false) }
                        val offsetX by animateFloatAsState(
                            targetValue = if (shake) 0f else 10f,
                            animationSpec = repeatable(
                                iterations = 4,
                                animation = tween(100),
                                repeatMode = RepeatMode.Reverse
                            ),
                            finishedListener = { shake = false },
                            label = "shake"
                        )

                        LaunchedEffect(Unit) { shake = true }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(32.dp)
                                .offset(x = offsetX.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color(0xFFE91E63), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("!", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Error!", color = Color(0xFFE91E63), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { loadingState = LoadingState.LOADING }) {
                    Text("Loading", fontSize = 10.sp)
                }
                Button(onClick = { loadingState = LoadingState.SUCCESS }) {
                    Text("Success", fontSize = 10.sp)
                }
                Button(onClick = { loadingState = LoadingState.ERROR }) {
                    Text("Error", fontSize = 10.sp)
                }
            }

            ExplanationText(
                """
                COMBINATION:
                • InfiniteTransition: Rotating spinner + pulsing dots
                • AnimatedContent: Switch between states with fade
                • animateFloatAsState: Success bounce, error shake
                • LaunchedEffect: Trigger animations on state entry

                PATTERN: Multiple animation types working together
                to create a cohesive loading experience
                """.trimIndent()
            )
        }
    }
}

enum class LoadingState { LOADING, SUCCESS, ERROR }

// ============================================
// EXAMPLE 2: Card Flip Animation
// Combines: updateTransition + graphicsLayer (3D) + AnimatedContent
// ============================================

@Composable
fun CardFlipExample() {
    var isFlipped by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = isFlipped, label = "flip")

    val rotationY by transition.animateFloat(
        label = "rotation",
        transitionSpec = { tween(600) }
    ) { flipped ->
        if (flipped) 180f else 0f
    }

    val scale by transition.animateFloat(
        label = "scale",
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy) }
    ) { flipped ->
        if (flipped) 1.1f else 1f
    }

    ComplexExampleCard(
        title = "2. Card Flip Animation (3D)",
        description = "updateTransition + graphicsLayer + AnimatedContent"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Card(
                modifier = Modifier
                    .size(width = 200.dp, height = 120.dp)
                    .scale(scale)
                    .graphicsLayer {
                        this.rotationY = rotationY
                        cameraDistance = 12f * density
                    }
                    .clickable { isFlipped = !isFlipped },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (rotationY <= 90f) Color(0xFF6200EE) else Color(0xFF03DAC6)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Show different content based on rotation
                    if (rotationY <= 90f) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "FRONT",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.graphicsLayer { this.rotationY = 180f }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "BACK",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { isFlipped = !isFlipped }) {
                Text("Flip Card")
            }

            ExplanationText(
                """
                COMBINATION:
                • updateTransition: Coordinate rotation + scale
                • graphicsLayer: 3D rotation (rotationY)
                • Conditional content: Show front/back at 90°
                • spring spec: Bouncy scale on flip

                INTERVIEW TIP: graphicsLayer enables 3D transforms.
                cameraDistance controls perspective depth.
                """.trimIndent()
            )
        }
    }
}

// ============================================
// EXAMPLE 3: Pull to Refresh
// Combines: Animatable + gesture + sequential animations
// ============================================

@Composable
fun PullToRefreshExample() {
    val offsetY = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    val maxPullDistance = 200f
    val refreshThreshold = 150f

    ComplexExampleCard(
        title = "3. Pull to Refresh",
        description = "Animatable + gesture + sequential animations"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    if (offsetY.value >= refreshThreshold && !isRefreshing) {
                                        // Trigger refresh
                                        isRefreshing = true

                                        // Animate to refresh position
                                        launch { offsetY.animateTo(100f, tween(300)) }

                                        // Spin rotation - loop while refreshing
                                        launch {
                                            while (isRefreshing) {
                                                rotation.animateTo(
                                                    targetValue = rotation.value + 360f,
                                                    animationSpec = tween(1000, easing = LinearEasing)
                                                )
                                            }
                                        }

                                        // Simulate loading
                                        delay(2000)

                                        // Stop and reset
                                        isRefreshing = false
                                        rotation.snapTo(0f)
                                        launch { offsetY.animateTo(0f, spring()) }
                                    } else {
                                        // Snap back
                                        offsetY.animateTo(0f, spring())
                                    }
                                }
                            },
                            onDrag = { _, dragAmount ->
                                if (!isRefreshing) {
                                    scope.launch {
                                        val newOffset =
                                            (offsetY.value + dragAmount.y).coerceIn(0f, maxPullDistance)
                                        offsetY.snapTo(newOffset)

                                        // Rotate based on pull distance
                                        rotation.snapTo((newOffset / maxPullDistance) * 180f)
                                    }
                                }
                            }
                        )
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Refresh indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .offset(y = (offsetY.value - 60).dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .rotate(rotation.value)
                                .background(
                                    if (offsetY.value >= refreshThreshold) Color(0xFF4CAF50)
                                    else Color(0xFF6200EE),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isRefreshing) "..." else "↻",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Drag down to refresh",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content list
                    repeat(3) { index ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "Item ${index + 1}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            ExplanationText(
                """
                COMBINATION:
                • Animatable: Manual control of offset + rotation
                • detectDragGestures: Track user pull
                • snapTo(): Update values during drag
                • animateTo(): Smooth return animation
                • Sequential: Pull → Threshold check → Spin → Reset
                • Threshold detection: Changes color at 150px

                PATTERN: Gesture → Immediate feedback → Animation
                """.trimIndent()
            )
        }
    }
}

// ============================================
// EXAMPLE 4: Staggered List Animation
// Combines: LaunchedEffect + sequential delays + parallel items
// ============================================

@Composable
fun StaggeredListExample() {
    var trigger by remember { mutableStateOf(false) }
    val itemStates = remember { mutableStateListOf<Boolean>().apply { addAll(List(5) { false }) } }

    LaunchedEffect(trigger) {
        if (trigger) {
            // Reset all
            itemStates.replaceAll { false }
            delay(100)

            // Animate items with stagger
            itemStates.indices.forEach { index ->
                itemStates[index] = true
                delay(100) // Stagger delay
            }
        }
    }

    ComplexExampleCard(
        title = "4. Staggered List Animation",
        description = "LaunchedEffect + sequential delays + AnimatedVisibility"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemStates.forEachIndexed { index, isVisible ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = slideInHorizontally(
                            initialOffsetX = { -it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) + fadeIn(),
                        exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = getColorForIndexComplex(index)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Staggered Item ${index + 1}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { trigger = !trigger }) {
                Text("Animate List")
            }

            ExplanationText(
                """
                COMBINATION:
                • LaunchedEffect: Sequential triggering
                • forEach with delay: Stagger effect (100ms apart)
                • AnimatedVisibility: Enter animation for each item
                • slideInHorizontally + fadeIn: Combined enter
                • spring: Bouncy effect on each item

                INTERVIEW TIP: Staggered animations create visual
                hierarchy. Each item animates after the previous one.
                """.trimIndent()
            )
        }
    }
}

// ============================================
// EXAMPLE 5: Complex Button Animation
// Combines: updateTransition + multiple properties + ripple
// ============================================

@Composable
fun ComplexButtonExample() {
    var buttonState by remember { mutableStateOf(ButtonAnimState.NORMAL) }

    val transition = updateTransition(targetState = buttonState, label = "button")

    val scale by transition.animateFloat(label = "scale") { state ->
        when (state) {
            ButtonAnimState.NORMAL -> 1f
            ButtonAnimState.PRESSED -> 0.95f
            ButtonAnimState.SUCCESS -> 1.1f
        }
    }

    val width by transition.animateDp(label = "width") { state ->
        when (state) {
            ButtonAnimState.NORMAL -> 200.dp
            ButtonAnimState.PRESSED -> 200.dp
            ButtonAnimState.SUCCESS -> 60.dp
        }
    }

    val cornerRadius by transition.animateDp(label = "corner") { state ->
        when (state) {
            ButtonAnimState.NORMAL -> 8.dp
            ButtonAnimState.PRESSED -> 8.dp
            ButtonAnimState.SUCCESS -> 30.dp
        }
    }

    val backgroundColor by transition.animateColor(label = "color") { state ->
        when (state) {
            ButtonAnimState.NORMAL -> Color(0xFF6200EE)
            ButtonAnimState.PRESSED -> Color(0xFF3700B3)
            ButtonAnimState.SUCCESS -> Color(0xFF4CAF50)
        }
    }

    LaunchedEffect(buttonState) {
        if (buttonState == ButtonAnimState.SUCCESS) {
            delay(1500)
            buttonState = ButtonAnimState.NORMAL
        }
    }

    ComplexExampleCard(
        title = "5. Complex Button Animation",
        description = "updateTransition + multi-property coordination"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .width(width)
                    .height(60.dp)
                    .scale(scale)
                    .background(backgroundColor, RoundedCornerShape(cornerRadius))
                    .clickable {
                        if (buttonState == ButtonAnimState.NORMAL) {
                            buttonState = ButtonAnimState.PRESSED
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = buttonState,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "button content"
                ) { state ->
                    when (state) {
                        ButtonAnimState.NORMAL -> Text(
                            "Submit",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        ButtonAnimState.PRESSED -> Text(
                            "Processing...",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        ButtonAnimState.SUCCESS -> Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LaunchedEffect(buttonState) {
                if (buttonState == ButtonAnimState.PRESSED) {
                    delay(1000)
                    buttonState = ButtonAnimState.SUCCESS
                }
            }

            ExplanationText(
                """
                COMBINATION:
                • updateTransition: Coordinates 4 properties
                • scale, width, cornerRadius, color all animate together
                • AnimatedContent: Switch between text and icon
                • LaunchedEffect: Auto-progress through states
                • Pattern: Normal → Press → Processing → Success → Reset

                REAL-WORLD USE: Submit buttons, action confirmations
                """.trimIndent()
            )
        }
    }
}

enum class ButtonAnimState { NORMAL, PRESSED, SUCCESS }

// ============================================
// EXAMPLE 6: Morphing Shapes
// Combines: keyframes + multiple properties + coordinated timing
// ============================================

@Composable
fun MorphingShapesExample() {
    var animate by remember { mutableStateOf(false) }

    val size by animateDpAsState(
        targetValue = if (animate) 140.dp else 80.dp,
        animationSpec = keyframes {
            durationMillis = 2000
            80.dp at 0
            120.dp at 500
            60.dp at 1000
            180.dp at 1500
            140.dp at 2000
        },
        label = "size"
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (animate) 16.dp else 40.dp,
        animationSpec = keyframes {
            durationMillis = 2000
            40.dp at 0
            8.dp at 500
            70.dp at 1000
            4.dp at 1500
            16.dp at 2000
        },
        label = "corner"
    )

    val rotation by animateFloatAsState(
        targetValue = if (animate) 360f else 0f,
        animationSpec = keyframes {
            durationMillis = 2000
            0f at 0
            90f at 500
            180f at 1000
            270f at 1500
            360f at 2000
        },
        label = "rotation"
    )

    val color by animateColorAsState(
        targetValue = if (animate) Color(0xFFE91E63) else Color(0xFF2196F3),
        animationSpec = tween(2000),
        label = "color"
    )

    ComplexExampleCard(
        title = "6. Morphing Shapes",
        description = "keyframes + multiple properties + coordinated timing"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(size)
                    .rotate(rotation)
                    .background(color, RoundedCornerShape(cornerRadius))
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { animate = !animate }) {
                Text("Morph Shape")
            }

            ExplanationText(
                """
                COMBINATION:
                • keyframes: Custom animation curves
                • 4 properties morph simultaneously:
                  - Size: 80→120→60→180→140
                  - Corner: Circle→Square→Circle→Square→Rounded
                  - Rotation: 0→90→180→270→360
                  - Color: Blue→Pink (smooth transition)
                • Each keyframe at different timing
                • Creates complex morphing effect

                INTERVIEW TIP: keyframes allow fine control
                over animation timing at specific milestones
                """.trimIndent()
            )
        }
    }
}

// ============================================
// EXAMPLE 7: Swipe to Delete
// Combines: Animatable + gesture + threshold + remove animation
// ============================================

@Composable
fun SwipeToDeleteExample() {
    var items by remember { mutableStateOf(listOf("Item 1", "Item 2", "Item 3")) }

    ComplexExampleCard(
        title = "7. Swipe to Delete",
        description = "Animatable + gesture + threshold detection"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                SwipeToDeleteItem(
                    text = item,
                    onDelete = { items = items - item }
                )
            }

            if (items.isEmpty()) {
                Text(
                    "All items deleted! (Reload to reset)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = { items = listOf("Item 1", "Item 2", "Item 3") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset Items")
            }

            ExplanationText(
                """
                COMBINATION:
                • Animatable: Control swipe offset
                • detectDragGestures: Track horizontal swipe
                • Threshold: Delete at -200px swipe
                • animateTo: Smooth slide-out on delete
                • AnimatedVisibility: Remove from composition
                • State update: Remove item from list

                PATTERN: Gesture → Threshold → Animation → State change
                """.trimIndent()
            )
        }
    }
}

@Composable
fun SwipeToDeleteItem(text: String, onDelete: () -> Unit) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isDeleted by remember { mutableStateOf(false) }

    val deleteThreshold = -200f
    val alpha = 1f - (offsetX.value / deleteThreshold).coerceIn(0f, 1f)

    AnimatedVisibility(
        visible = !isDeleted,
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value <= deleteThreshold) {
                                    // Delete animation
                                    offsetX.animateTo(-1000f, tween(300))
                                    isDeleted = true
                                    onDelete()
                                } else {
                                    // Snap back
                                    offsetX.animateTo(0f, spring())
                                }
                            }
                        },
                        onDrag = { _, dragAmount ->
                            scope.launch {
                                val newOffset = (offsetX.value + dragAmount.x).coerceAtMost(0f)
                                offsetX.snapTo(newOffset)
                            }
                        }
                    )
                }
        ) {
            // Delete background
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFFE91E63), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    "Delete",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.alpha(alpha)
                )
            }

            // Item content
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 16.sp
                )
            }
        }
    }
}

// ============================================
// EXAMPLE 8: Onboarding Flow
// Combines: AnimatedContent + updateTransition + complex choreography
// ============================================

@Composable
fun OnboardingFlowExample() {
    var currentPage by remember { mutableStateOf(0) }
    val pages = listOf(
        OnboardingPage("Welcome", "👋", Color(0xFF6200EE)),
        OnboardingPage("Explore", "🚀", Color(0xFF03DAC6)),
        OnboardingPage("Enjoy", "🎉", Color(0xFF4CAF50))
    )

    val transition = updateTransition(targetState = currentPage, label = "onboarding")

    val indicatorOffset by transition.animateDp(
        label = "indicator",
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy) }
    ) { page ->
        (page * 40).dp
    }

    ComplexExampleCard(
        title = "8. Onboarding Flow",
        description = "AnimatedContent + updateTransition + choreography"
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Page content with slide animation
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }.using(SizeTransform(clip = false))
                },
                label = "page content"
            ) { page ->
                val currentPageData = pages[page]

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            currentPageData.color.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    var showContent by remember { mutableStateOf(false) }
                    LaunchedEffect(page) {
                        showContent = false
                        delay(200)
                        showContent = true
                    }

                    val scale by animateFloatAsState(
                        targetValue = if (showContent) 1f else 0.5f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "scale"
                    )

                    Text(
                        text = currentPageData.emoji,
                        fontSize = 64.sp,
                        modifier = Modifier.scale(scale)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = currentPageData.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentPageData.color,
                        modifier = Modifier.scale(scale)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Page indicators
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color.Gray.copy(alpha = 0.3f), CircleShape)
                        )
                        if (index < pages.size - 1) Spacer(modifier = Modifier.width(28.dp))
                    }
                }

                // Animated indicator
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset + 6.dp)
                        .size(12.dp)
                        .background(Color(0xFF6200EE), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { if (currentPage > 0) currentPage-- },
                    enabled = currentPage > 0
                ) {
                    Text("Previous")
                }
                Button(
                    onClick = { if (currentPage < pages.size - 1) currentPage++ },
                    enabled = currentPage < pages.size - 1
                ) {
                    Text("Next")
                }
            }

            ExplanationText(
                """
                COMBINATION:
                • AnimatedContent: Slide pages left/right
                • Different transitions based on direction
                • updateTransition: Animated page indicator
                • LaunchedEffect: Trigger scale on page enter
                • animateFloatAsState: Bounce in content
                • SizeTransform: Smooth size changes

                PATTERN: Multi-layer coordination for smooth UX
                Perfect example for interview demonstration!
                """.trimIndent()
            )
        }
    }
}

data class OnboardingPage(val title: String, val emoji: String, val color: Color)

// ============================================
// HELPER COMPOSABLES
// ============================================

@Composable
fun ComplexExampleCard(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE)
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
fun ExplanationText(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color(0xFF555555),
            modifier = Modifier.padding(12.dp),
            lineHeight = 14.sp
        )
    }
}

private fun getColorForIndexComplex(index: Int): Color {
    return when (index % 5) {
        0 -> Color(0xFF6200EE)
        1 -> Color(0xFF03DAC6)
        2 -> Color(0xFFFF6E40)
        3 -> Color(0xFF2196F3)
        else -> Color(0xFF4CAF50)
    }
}
