# Compose Animation Cheat Sheet 🎨
**Quick Reference for Interview Coding Challenges**

---

## 📋 Table of Contents
1. [Animation APIs Quick Reference](#animation-apis)
2. [Gesture Detection Patterns](#gestures)
3. [Performance Optimization](#performance)
4. [Common Patterns & Snippets](#patterns)
5. [Debugging & Troubleshooting](#debugging)

---

## 🎬 Animation APIs Quick Reference {#animation-apis}

### 1. `animate*AsState()` - Simple State-Driven

**When to use:** Single property animation based on state
**Time cost:** 5-10 mins

```kotlin
// Float, Dp, Color, Int, Size, Offset, etc.
val size by animateDpAsState(
    targetValue = if (isExpanded) 200.dp else 100.dp,
    animationSpec = spring(), // or tween(500)
    label = "size"
)

// With callback
val alpha by animateFloatAsState(
    targetValue = if (visible) 1f else 0f,
    finishedListener = { finalValue ->
        // Called when animation completes
    },
    label = "alpha"
)
```

### 2. `updateTransition()` - Multiple Properties Together

**When to use:** Coordinate 2+ properties animating together
**Time cost:** 15-20 mins

```kotlin
val transition = updateTransition(targetState = currentState, label = "state")

val size by transition.animateDp(label = "size") { state ->
    when(state) {
        State.SMALL -> 50.dp
        State.LARGE -> 150.dp
    }
}

val color by transition.animateColor(label = "color") { state ->
    when(state) {
        State.SMALL -> Color.Blue
        State.LARGE -> Color.Red
    }
}
```

### 3. `AnimatedVisibility` - Show/Hide with Animation

**When to use:** Composable appearing/disappearing
**Time cost:** 10-15 mins

```kotlin
AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn() + slideInVertically(),
    exit = fadeOut() + slideOutVertically()
) {
    Content()
}

// Common enter/exit combinations:
// fadeIn/fadeOut
// slideInVertically/slideOutVertically
// slideInHorizontally/slideOutHorizontally
// expandIn/shrinkOut
// expandVertically/shrinkVertically
// scaleIn/scaleOut

// Combine with +
enter = fadeIn() + expandVertically() + slideInHorizontally()
```

### 4. `AnimatedContent` - Content Switching

**When to use:** Swapping between different composables
**Time cost:** 10-15 mins

```kotlin
AnimatedContent(
    targetState = currentPage,
    transitionSpec = {
        if (targetState > initialState) {
            // Forward: slide left
            slideInHorizontally { it } togetherWith
            slideOutHorizontally { -it }
        } else {
            // Backward: slide right
            slideInHorizontally { -it } togetherWith
            slideOutHorizontally { it }
        }
    },
    label = "content"
) { page ->
    when(page) {
        0 -> Page1()
        1 -> Page2()
    }
}
```

### 5. `Animatable` - Manual Control

**When to use:** Gesture-driven or complex custom animations
**Time cost:** 20-30 mins

```kotlin
val offset = remember { Animatable(0f) }
val scope = rememberCoroutineScope()

// Animate to target
scope.launch {
    offset.animateTo(
        targetValue = 200f,
        animationSpec = spring()
    )
}

// Snap immediately (no animation)
scope.launch {
    offset.snapTo(100f)
}

// Animate by delta
scope.launch {
    offset.animateDecay(
        initialVelocity = velocity,
        animationSpec = exponentialDecay()
    )
}

// Usage with gesture
Modifier.pointerInput(Unit) {
    detectDragGestures { _, dragAmount ->
        scope.launch {
            offset.snapTo(offset.value + dragAmount.x)
        }
    }
}
```

### 6. `rememberInfiniteTransition()` - Looping Animations

**When to use:** Continuous animations (loading, pulsing)
**Time cost:** 10 mins

```kotlin
val infiniteTransition = rememberInfiniteTransition(label = "infinite")

val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
        animation = tween(2000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart // or Reverse
    ),
    label = "rotation"
)

// Apply
Modifier.rotate(rotation)
```

### 7. `AnimationSpec` Types

```kotlin
// 1. TWEEN - Linear interpolation with easing
tween(
    durationMillis = 500,
    delayMillis = 100,
    easing = FastOutSlowInEasing // or LinearEasing, etc.
)

// 2. SPRING - Physics-based bouncy
spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

// 3. KEYFRAMES - Custom milestones
keyframes {
    durationMillis = 1000
    0.dp at 0
    100.dp at 500 with LinearEasing
    50.dp at 1000
}

// 4. SNAP - Instant change
snap(delayMillis = 200)

// 5. REPEATABLE - Repeat N times
repeatable(
    iterations = 3,
    animation = tween(500),
    repeatMode = RepeatMode.Reverse
)

// 6. INFINITE REPEATABLE - Never stops
infiniteRepeatable(
    animation = tween(1000),
    repeatMode = RepeatMode.Restart
)
```

---

## 👆 Gesture Detection Patterns {#gestures}

### Tap & Click

```kotlin
// Simple click
Modifier.clickable { /* action */ }

// Pointer input for custom
Modifier.pointerInput(Unit) {
    detectTapGestures(
        onTap = { offset -> },
        onDoubleTap = { offset -> },
        onLongPress = { offset -> },
        onPress = { offset -> }
    )
}
```

### Drag Gestures

```kotlin
// Horizontal drag
Modifier.pointerInput(Unit) {
    detectHorizontalDragGestures(
        onDragStart = { offset -> },
        onDragEnd = { },
        onDragCancel = { },
        onHorizontalDrag = { change, dragAmount ->
            // dragAmount is Float (px moved)
        }
    )
}

// Vertical drag
detectVerticalDragGestures(/* same callbacks */)

// Any direction drag
detectDragGestures(
    onDragStart = { },
    onDragEnd = { },
    onDrag = { change, dragAmount ->
        // dragAmount is Offset(x, y)
    }
)
```

### Transform Gestures (Pinch, Zoom, Rotate)

```kotlin
Modifier.pointerInput(Unit) {
    detectTransformGestures { centroid, pan, zoom, rotation ->
        // centroid: Offset - center of gesture
        // pan: Offset - translation amount
        // zoom: Float - scale factor (1f = no change)
        // rotation: Float - rotation in degrees

        scale *= zoom
        offsetX += pan.x
        offsetY += pan.y
        angle += rotation
    }
}
```

### Velocity Tracking (for fling)

```kotlin
val velocityTracker = remember { VelocityTracker() }

Modifier.pointerInput(Unit) {
    detectDragGestures(
        onDragEnd = {
            val velocity = velocityTracker.calculateVelocity()
            // velocity.x, velocity.y in px/sec

            // Fling if high velocity
            if (velocity.x.absoluteValue > 1000f) {
                // Trigger dismiss/fling animation
            }

            velocityTracker.resetTracking()
        },
        onDrag = { change, _ ->
            velocityTracker.addPosition(
                change.uptimeMillis,
                change.position
            )
        }
    )
}
```

---

## ⚡ Performance Optimization {#performance}

### 1. Minimize Recomposition

```kotlin
// ❌ BAD - Recomposes entire composable
@Composable
fun BadExample(items: List<Item>) {
    val total = items.sumOf { it.price } // Recalculated every recomposition
    Text("Total: $total")
}

// ✅ GOOD - Use remember
@Composable
fun GoodExample(items: List<Item>) {
    val total = remember(items) {
        items.sumOf { it.price }
    }
    Text("Total: $total")
}

// ✅ BETTER - Use derivedStateOf for computed values
@Composable
fun BetterExample(items: List<Item>) {
    val total by remember {
        derivedStateOf { items.sumOf { it.price } }
    }
    Text("Total: $total")
}
```

### 2. LazyList Optimization

```kotlin
LazyColumn {
    items(
        items = itemsList,
        key = { item -> item.id } // ⭐ CRITICAL: Stable keys
    ) { item ->
        ItemRow(item)
    }
}

// Use itemsIndexed when you need index
itemsIndexed(
    items = itemsList,
    key = { index, item -> item.id }
) { index, item ->
    ItemRow(item, index)
}
```

### 3. Avoid Heavy Operations in Composition

```kotlin
// ❌ BAD - Expensive operation in composition
@Composable
fun BadImageLoader(url: String) {
    val bitmap = loadBitmapFromUrl(url) // Blocks UI!
    Image(bitmap)
}

// ✅ GOOD - Use LaunchedEffect
@Composable
fun GoodImageLoader(url: String) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(url) {
        bitmap = withContext(Dispatchers.IO) {
            loadBitmapFromUrl(url)
        }
    }

    bitmap?.let { Image(it) }
}
```

### 4. Reduce Layer Complexity

```kotlin
// ❌ BAD - Multiple graphicsLayer calls
Modifier
    .graphicsLayer { alpha = 0.5f }
    .graphicsLayer { scaleX = 1.2f }

// ✅ GOOD - Combine into one
Modifier.graphicsLayer {
    alpha = 0.5f
    scaleX = 1.2f
}
```

### 5. Use Stable Parameters

```kotlin
// ❌ BAD - Lambda recreated every composition
@Composable
fun Parent() {
    Child(onClick = { doSomething() })
}

// ✅ GOOD - Remember lambda
@Composable
fun Parent() {
    val onClick = remember { { doSomething() } }
    Child(onClick = onClick)
}

// Or use remember with key
val onClick = remember(dependency) {
    { doSomething(dependency) }
}
```

---

## 🔧 Common Patterns & Snippets {#patterns}

### Staggered List Animation

```kotlin
var isVisible by remember { mutableStateOf(false) }

LaunchedEffect(Unit) {
    delay(100)
    isVisible = true
}

LazyColumn {
    itemsIndexed(items) { index, item ->
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 400,
                    delayMillis = index * 50 // Stagger
                )
            ) + slideInVertically(
                animationSpec = tween(400, delayMillis = index * 50)
            )
        ) {
            ItemCard(item)
        }
    }
}
```

### Sequential vs Parallel Animations

```kotlin
// SEQUENTIAL - one after another
LaunchedEffect(trigger) {
    alpha1.animateTo(0.5f)
    delay(100)
    alpha2.animateTo(0.5f)
    delay(100)
    alpha3.animateTo(0.5f)
}

// PARALLEL - all at once
LaunchedEffect(trigger) {
    launch { alpha1.animateTo(0.5f) }
    launch { alpha2.animateTo(0.5f) }
    launch { alpha3.animateTo(0.5f) }

    // Wait for all
    // (jobs automatically waited in LaunchedEffect)
}

// PARALLEL with explicit waiting
LaunchedEffect(trigger) {
    val job1 = launch { alpha1.animateTo(0.5f) }
    val job2 = launch { alpha2.animateTo(0.5f) }
    val job3 = launch { alpha3.animateTo(0.5f) }

    job1.join()
    job2.join()
    job3.join()

    // Now all are complete
}
```

### Shimmer Loading Effect

```kotlin
@Composable
fun Shimmer() {
    val infiniteTransition = rememberInfiniteTransition()
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
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
```

### 3D Rotation (Card Flip)

```kotlin
var isFlipped by remember { mutableStateOf(false) }

val rotation by animateFloatAsState(
    targetValue = if (isFlipped) 180f else 0f,
    animationSpec = tween(600)
)

Card(
    modifier = Modifier
        .graphicsLayer {
            rotationY = rotation
            cameraDistance = 12f * density // Important for 3D effect
        }
        .clickable { isFlipped = !isFlipped }
) {
    if (rotation <= 90f) {
        FrontContent()
    } else {
        // Flip back content
        Box(modifier = Modifier.graphicsLayer { this.rotationY = 180f }) {
            BackContent()
        }
    }
}
```

### Parallax Scroll Effect

```kotlin
val scrollState = rememberScrollState()

Column(modifier = Modifier.verticalScroll(scrollState)) {
    // Header with parallax
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .graphicsLayer {
                translationY = scrollState.value * 0.5f // Scroll slower
            }
    ) {
        Image(...)
    }

    // Content scrolls normally
    Content()
}
```

---

## 🐛 Debugging & Troubleshooting {#debugging}

### Common Issues

#### 1. "Val cannot be reassigned"
```kotlin
// ❌ Problem
val rotation by animateFloatAsState(...)
Modifier.graphicsLayer { rotationY = 180f } // Error!

// ✅ Solution
Modifier.graphicsLayer { this.rotationY = 180f }
```

#### 2. Animation not triggering
```kotlin
// ❌ Problem - Same value
var state by remember { mutableStateOf(false) }
state = false // Already false!

// ✅ Solution - Toggle
state = !state

// Or reset properly
LaunchedEffect(trigger) {
    state = false
    delay(50)
    state = true
}
```

#### 3. Animations cancel early
```kotlin
// ❌ Problem - Sets false immediately
LaunchedEffect(runAnimation) {
    if (runAnimation) {
        launch { animate1() }
        launch { animate2() }
        runAnimation = false // ⚠️ Cancels coroutines!
    }
}

// ✅ Solution - Wait for completion
LaunchedEffect(runAnimation) {
    if (runAnimation) {
        val job1 = launch { animate1() }
        val job2 = launch { animate2() }
        job1.join()
        job2.join()
        runAnimation = false // Now safe
    }
}
```

#### 4. Gesture conflicts
```kotlin
// ❌ Problem - Both try to handle
Modifier
    .pointerInput(Unit) { detectVerticalDragGestures {...} }
    .verticalScroll(scrollState)

// ✅ Solution - Use nestedScroll or choose one
```

#### 5. Performance issues
```kotlin
// Check recomposition count
Modifier.drawBehind {
    println("Recomposing!") // See how often this runs
}

// Use Layout Inspector in Android Studio
// Enable "Show Recomposition Counts"
```

---

## 🎯 Interview Time Management

### 3-Hour Challenge Breakdown

**0:00 - 0:10 (10 mins)** - Setup
- Copy blueprint template
- Adjust package names
- Set up navigation structure
- Define data models

**0:10 - 1:30 (80 mins)** - Core Features (Priority 1)
- Staggered grid with fade-in (20 mins)
- Basic detail screen transition (20 mins)
- One gesture: pinch-to-zoom OR swipe (25 mins)
- Shimmer loading effect (15 mins)

**1:30 - 2:15 (45 mins)** - Secondary Features (Priority 2)
- Pull-to-refresh OR bottom sheet (20 mins)
- One custom component (15 mins)
- Polish existing features (10 mins)

**2:15 - 2:45 (30 mins)** - Documentation & Cleanup
- Add comments explaining approach
- Create README with priorities
- Clean code, remove TODOs
- Test on device/emulator

**2:45 - 3:00 (15 mins)** - Buffer
- Fix unexpected issues
- Final review
- Prepare presentation

---

## 🚀 Quick Copy-Paste Imports

```kotlin
// Core Compose
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Navigation
import androidx.navigation.compose.*

// Coroutines
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Image loading (if using Coil)
import coil.compose.AsyncImage
```

---

## ✅ Pre-Interview Checklist

- [ ] Review all animation APIs (30 mins)
- [ ] Practice 2-3 gestures (pinch, swipe, drag)
- [ ] Build one complete feature end-to-end (1 hour)
- [ ] Memorize time estimates for each feature
- [ ] Prepare prioritization strategy
- [ ] Test copy-paste speed from cheat sheet
- [ ] Have blueprint template ready
- [ ] Know your development environment well
- [ ] Prepare to explain trade-offs

---

**Good luck! Remember: They want to see your thinking process, not perfection.** 🎉
