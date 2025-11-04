/**
 * ========================================
 * INTERVIEW BLUEPRINT TEMPLATE
 * ========================================
 *
 * This is a complete project structure blueprint for the Media Gallery interview problem.
 * Copy this structure to start quickly and save 15-20 minutes of setup time.
 *
 * TIME BUDGET: 3 hours total
 * - Setup (10 mins): Copy this template, adjust package names
 * - Core Features (120 mins): Implement prioritized features
 * - Polish & Documentation (30 mins): Clean code, add comments
 * - Buffer (20 mins): Handle unexpected issues
 */

package com.example.mediagallery

// ========================================
// 1. DATA MODELS (5 mins)
// ========================================

data class MediaItem(
    val id: String,
    val imageUrl: String,
    val title: String,
    val description: String,
    val author: String,
    val timestamp: Long,
    val aspectRatio: Float = 1f // For staggered grid
)

// Sample data generator
object SampleData {
    fun generateMediaItems(count: Int = 20): List<MediaItem> {
        return (1..count).map { index ->
            MediaItem(
                id = "item_$index",
                imageUrl = "https://picsum.photos/400/600?random=$index",
                title = "Media Item $index",
                description = "Description for media item $index with some details about the content",
                author = "Author ${index % 5}",
                timestamp = System.currentTimeMillis() - (index * 100000),
                aspectRatio = if (index % 3 == 0) 1.5f else 1f
            )
        }
    }
}

// ========================================
// 2. NAVIGATION SETUP (10 mins)
// ========================================

import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Gallery : Screen("gallery")
    object Detail : Screen("detail/{itemId}") {
        fun createRoute(itemId: String) = "detail/$itemId"
    }
}

@Composable
fun MediaGalleryApp() {
    val navController = rememberNavController()

    // SharedTransitionLayout for shared element transitions
    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = Screen.Gallery.route) {
            composable(Screen.Gallery.route) {
                GalleryScreen(
                    animatedVisibilityScope = this,
                    onItemClick = { itemId ->
                        navController.navigate(Screen.Detail.createRoute(itemId))
                    }
                )
            }

            composable(Screen.Detail.route) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")
                DetailScreen(
                    animatedVisibilityScope = this,
                    itemId = itemId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// ========================================
// 3. GALLERY SCREEN SCAFFOLD (30 mins)
// ========================================

@Composable
fun GalleryScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemClick: (String) -> Unit
) {
    var isRefreshing by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf(SampleData.generateMediaItems()) }
    var isGridVisible by remember { mutableStateOf(false) }

    // Trigger staggered animation on launch
    LaunchedEffect(Unit) {
        delay(100)
        isGridVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Media Gallery") },
                actions = {
                    IconButton(onClick = { /* Edit mode */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshContainer(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                // Simulate refresh
                // In real app: viewModel.refresh()
                // For now: delay and reset
            }
        ) {
            StaggeredGridGallery(
                items = items,
                isVisible = isGridVisible,
                animatedVisibilityScope = animatedVisibilityScope,
                onItemClick = onItemClick,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

// ========================================
// 4. STAGGERED GRID WITH ANIMATIONS (40 mins)
// ========================================

@Composable
fun StaggeredGridGallery(
    items: List<MediaItem>,
    isVisible: Boolean,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            // Staggered fade-in animation
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 400,
                        delayMillis = index * 50 // Stagger by 50ms
                    )
                ) + slideInVertically(
                    animationSpec = tween(
                        durationMillis = 400,
                        delayMillis = index * 50
                    ),
                    initialOffsetY = { it / 4 }
                )
            ) {
                MediaGridItem(
                    item = item,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onClick = { onItemClick(item.id) }
                )
            }
        }
    }
}

// ========================================
// 5. GRID ITEM WITH SHIMMER (20 mins)
// ========================================

@Composable
fun MediaGridItem(
    item: MediaItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(item.aspectRatio)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            // Shimmer loading effect
            if (isLoading) {
                ShimmerEffect(modifier = Modifier.fillMaxSize())
            }

            // Shared element for transition
            with(animatedVisibilityScope) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedElement(
                            rememberSharedContentState(key = "image-${item.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop,
                    onSuccess = { isLoading = false }
                )
            }

            // Overlay with title
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ========================================
// 6. DETAIL SCREEN WITH GESTURES (50 mins)
// ========================================

@Composable
fun DetailScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    itemId: String?,
    onBack: () -> Unit
) {
    // In real app: val item = viewModel.getItemById(itemId)
    val item = remember { SampleData.generateMediaItems().firstOrNull { it.id == itemId } }

    if (item == null) {
        // Handle error
        return
    }

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Zoomable Image with gestures
            ZoomableImage(
                item = item,
                animatedVisibilityScope = animatedVisibilityScope,
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                onScaleChange = { scale = it },
                onOffsetChange = { x, y -> offsetX = x; offsetY = y },
                onDismiss = onBack
            )

            // Parallax content
            val parallaxOffset = scrollState.value * 0.5f
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = parallaxOffset.dp)
                    .padding(16.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "By ${item.author}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = item.description,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        }

        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White.copy(alpha = 0.8f), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
    }
}

// ========================================
// 7. ZOOMABLE IMAGE WITH GESTURES (40 mins)
// ========================================

@Composable
fun ZoomableImage(
    item: MediaItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    onScaleChange: (Float) -> Unit,
    onOffsetChange: (Float, Float) -> Unit,
    onDismiss: () -> Unit
) {
    val scaleAnimatable = remember { Animatable(1f) }
    val offsetXAnimatable = remember { Animatable(0f) }
    val offsetYAnimatable = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scope.launch {
                        // Update scale
                        val newScale = (scaleAnimatable.value * zoom).coerceIn(1f, 4f)
                        scaleAnimatable.snapTo(newScale)
                        onScaleChange(newScale)

                        // Update offset only when zoomed
                        if (newScale > 1f) {
                            offsetXAnimatable.snapTo(offsetXAnimatable.value + pan.x)
                            offsetYAnimatable.snapTo(offsetYAnimatable.value + pan.y)
                            onOffsetChange(offsetXAnimatable.value, offsetYAnimatable.value)
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                // Swipe to dismiss when not zoomed
                detectVerticalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            if (scaleAnimatable.value <= 1f && offsetYAnimatable.value.absoluteValue > 200f) {
                                onDismiss()
                            } else {
                                offsetYAnimatable.animateTo(0f, spring())
                                onOffsetChange(offsetXAnimatable.value, 0f)
                            }
                        }
                    },
                    onVerticalDrag = { _, dragAmount ->
                        if (scaleAnimatable.value <= 1f) {
                            scope.launch {
                                offsetYAnimatable.snapTo(offsetYAnimatable.value + dragAmount)
                                onOffsetChange(offsetXAnimatable.value, offsetYAnimatable.value)
                            }
                        }
                    }
                )
            }
    ) {
        with(animatedVisibilityScope) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxSize()
                    .sharedElement(
                        rememberSharedContentState(key = "image-${item.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .graphicsLayer {
                        scaleX = scaleAnimatable.value
                        scaleY = scaleAnimatable.value
                        translationX = offsetXAnimatable.value
                        translationY = offsetYAnimatable.value
                    },
                contentScale = ContentScale.Fit
            )
        }
    }
}

// ========================================
// 8. HELPER COMPOSABLES (Placeholders)
// ========================================

@Composable
fun PullToRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    // TODO: Implement pull-to-refresh
    // For now, just show content
    Box {
        content()
        if (isRefreshing) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            )
        }
    }
}

// ========================================
// 9. ADDITIONAL FEATURES TO ADD IF TIME
// ========================================

/**
 * PRIORITY 1 (Must Have - 90 mins):
 * ✅ Grid with staggered animation
 * ✅ Basic detail view transition
 * ✅ Shimmer loading effect
 * ✅ One gesture (pinch-to-zoom OR swipe)
 *
 * PRIORITY 2 (Should Have - 60 mins):
 * - Pull-to-refresh with spring
 * - Bottom sheet with content
 * - Custom progress indicator
 * - Parallax scroll effect
 *
 * PRIORITY 3 (Nice to Have - 30 mins):
 * - Drag to reorder in edit mode
 * - Double-tap to zoom
 * - Haptic feedback
 * - Tab indicator animation
 *
 * PRIORITY 4 (If Time Left):
 * - FAB with curved motion
 * - Accessibility features
 * - Orientation handling
 * - Performance optimizations
 */

// ========================================
// 10. REQUIRED IMPORTS
// ========================================

/*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
*/

// ========================================
// 11. GRADLE DEPENDENCIES NEEDED
// ========================================

/*
dependencies {
    // Compose
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-extended:1.6.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Staggered grid (included in compose since 1.6)
    // implementation("androidx.compose.foundation:foundation:1.6.0")
}
*/
