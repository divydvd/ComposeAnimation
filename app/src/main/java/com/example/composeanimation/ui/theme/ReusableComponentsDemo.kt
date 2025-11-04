package com.example.composeanimation.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * ========================================
 * REUSABLE COMPONENTS USAGE DEMO
 * ========================================
 *
 * This file demonstrates how to use all the reusable animation components
 * from ReusableAnimationComponents.kt in real-world scenarios.
 *
 * Each example shows:
 * - How to import and use the component
 * - How to pass parameters
 * - How to combine with other UI elements
 * - Common use cases
 */

@Composable
fun ReusableComponentsDemo() {
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        "Shimmer",
        "Pull Refresh",
        "Swipe",
        "Zoom",
        "Stagger",
        "Progress",
        "Sheet",
        "Tabs",
        "FAB",
        "Drag"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab selector
        ScrollableTabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 12.sp) }
                )
            }
        }

        // Content based on selected tab
        when (selectedTab) {
            0 -> ShimmerDemo()
            1 -> PullToRefreshDemo()
            2 -> SwipeToDismissDemo()
            3 -> ZoomableDemo()
            4 -> StaggeredListDemo()
            5 -> ProgressIndicatorDemo()
            6 -> BottomSheetDemo()
            7 -> TabIndicatorDemo()
            8 -> FABDemo()
            9 -> DraggableItemDemo()
        }
    }
}

// ========================================
// 1. SHIMMER EFFECT DEMO
// Use for loading states, skeleton screens
// ========================================

@Composable
fun ShimmerDemo() {
    var isLoading by remember { mutableStateOf(true) }

    DemoCard(
        title = "Shimmer Effect",
        description = "Used for loading placeholders"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Example 1: Loading image
            if (isLoading) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF6200EE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Image Loaded!", color = Color.White, fontSize = 18.sp)
                    }
                }
            }

            // Example 2: Loading list items
            repeat(3) {
                if (isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ShimmerEffect(modifier = Modifier.size(60.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ShimmerEffect(modifier = Modifier.fillMaxWidth().height(16.dp))
                            ShimmerEffect(modifier = Modifier.fillMaxWidth(0.7f).height(12.dp))
                        }
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(Color(0xFF03DAC6), RoundedCornerShape(8.dp))
                            )
                            Column {
                                Text("Item Title", fontWeight = FontWeight.Bold)
                                Text("Item description", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { isLoading = !isLoading },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (isLoading) "Show Content" else "Show Loading")
            }

            UsageCode(
                """
                // Simple usage
                ShimmerEffect(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    durationMillis = 1000 // optional
                )

                // Use case: Loading card
                if (isLoading) {
                    ShimmerEffect(modifier = Modifier.size(100.dp))
                } else {
                    Image(...)
                }
                """.trimIndent()
            )
        }
    }
}

// ========================================
// 2. PULL TO REFRESH DEMO
// Use for refreshable lists
// ========================================

@Composable
fun PullToRefreshDemo() {
    var isRefreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableStateOf(5) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(2000) // Simulate network call
            itemCount += 2
            isRefreshing = false
        }
    }

    DemoCard(
        title = "Pull to Refresh",
        description = "Drag down to refresh content"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "Try pulling down! Items: $itemCount",
                fontWeight = FontWeight.Bold
            )

            PullToRefreshSpring(
                isRefreshing = isRefreshing,
                onRefresh = { isRefreshing = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(itemCount) { index ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Item ${index + 1}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            UsageCode(
                """
                var isRefreshing by remember { mutableStateOf(false) }

                PullToRefreshSpring(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        // Call your API
                        viewModel.refresh()
                    }
                ) {
                    LazyColumn { /* your content */ }
                }

                // In ViewModel or LaunchedEffect:
                LaunchedEffect(isRefreshing) {
                    if (isRefreshing) {
                        // Load data
                        isRefreshing = false
                    }
                }
                """.trimIndent()
            )
        }
    }
}

// ========================================
// 3. SWIPE TO DISMISS DEMO
// Use for dismissible cards, delete actions
// ========================================

@Composable
fun SwipeToDismissDemo() {
    var items by remember { mutableStateOf(listOf("Item 1", "Item 2", "Item 3", "Item 4")) }

    DemoCard(
        title = "Swipe to Dismiss",
        description = "Swipe cards left or right to remove"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items.forEach { item ->
                SwipeToDismissCard(
                    onDismiss = {
                        items = items - item
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item, fontWeight = FontWeight.Medium)
                            Text("← Swipe", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            if (items.isEmpty()) {
                Text(
                    "All items dismissed!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Button(
                onClick = { items = listOf("Item 1", "Item 2", "Item 3", "Item 4") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset Items")
            }

            UsageCode(
                """
                SwipeToDismissCard(
                    onDismiss = {
                        // Remove item from list
                        items = items.filter { it.id != currentItem.id }
                    },
                    dismissThreshold = 200f // optional, default 200
                ) {
                    Card { /* your card content */ }
                }

                // Use case: Email inbox, chat messages
                messages.forEach { message ->
                    SwipeToDismissCard(
                        onDismiss = { viewModel.deleteMessage(message) }
                    ) {
                        MessageCard(message)
                    }
                }
                """.trimIndent()
            )
        }
    }
}

// ========================================
// 4. ZOOMABLE BOX DEMO
// Use for image viewers, maps
// ========================================

@Composable
fun ZoomableDemo() {
    DemoCard(
        title = "Zoomable Box",
        description = "Pinch to zoom, drag when zoomed"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Pinch to zoom the box below", fontWeight = FontWeight.Bold)

            ZoomableBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                minScale = 1f,
                maxScale = 4f
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC6))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.White
                        )
                        Text(
                            "Zoom Me!",
                            fontSize = 24.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            UsageCode(
                """
                ZoomableBox(
                    minScale = 1f,
                    maxScale = 4f
                ) {
                    Image(
                        painter = painterResource(R.drawable.photo),
                        contentDescription = "Photo"
                    )
                }

                // Use case: Photo viewer
                ZoomableBox {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                """.trimIndent()
            )
        }
    }
}

// ========================================
// 5. STAGGERED LIST DEMO
// Use for animated list entrances
// ========================================

@Composable
fun StaggeredListDemo() {
    var isVisible by remember { mutableStateOf(false) }
    val items = remember { listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5") }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(100)
            isVisible = true
        }
    }

    DemoCard(
        title = "Staggered List Animation",
        description = "Items appear one by one with delay"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items.forEachIndexed { index, item ->
                StaggeredVisibilityItem(
                    visible = isVisible,
                    index = index,
                    staggerDelayMs = 100
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = getColorForListIndex(index)
                        )
                    ) {
                        Text(
                            item,
                            modifier = Modifier.padding(16.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Button(
                onClick = {
                    isVisible = false
                    // Will re-trigger via LaunchedEffect
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Replay Animation")
            }

            UsageCode(
                """
                var isVisible by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    delay(100)
                    isVisible = true
                }

                LazyColumn {
                    itemsIndexed(items) { index, item ->
                        StaggeredVisibilityItem(
                            visible = isVisible,
                            index = index,
                            staggerDelayMs = 50 // delay between items
                        ) {
                            ItemCard(item)
                        }
                    }
                }

                // Perfect for: Onboarding, search results, gallery
                """.trimIndent()
            )
        }
    }
}

// ========================================
// 6. SEGMENTED PROGRESS INDICATOR DEMO
// Use for multi-step processes
// ========================================

@Composable
fun ProgressIndicatorDemo() {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(100)
            progress = (progress + 0.01f) % 1f
        }
    }

    DemoCard(
        title = "Segmented Progress",
        description = "Custom progress with animated segments"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            // Auto-animating progress
            Text("Auto-animating:", fontWeight = FontWeight.Bold)
            SegmentedProgressIndicator(
                progress = progress,
                segments = 8,
                modifier = Modifier.fillMaxWidth()
            )

            // Manual control
            Text("Manual control: ${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold)
            SegmentedProgressIndicator(
                progress = progress,
                segments = 5,
                modifier = Modifier.fillMaxWidth()
            )

            // Step indicator
            var currentStep by remember { mutableStateOf(0) }
            Text("Step ${currentStep + 1} of 4", fontWeight = FontWeight.Bold)
            SegmentedProgressIndicator(
                progress = (currentStep + 1) / 4f,
                segments = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { if (currentStep > 0) currentStep-- },
                    enabled = currentStep > 0
                ) {
                    Text("Previous")
                }
                Button(
                    onClick = { if (currentStep < 3) currentStep++ },
                    enabled = currentStep < 3
                ) {
                    Text("Next")
                }
            }

            UsageCode(
                """
                var progress by remember { mutableStateOf(0f) }

                SegmentedProgressIndicator(
                    progress = progress, // 0f to 1f
                    segments = 8, // number of segments
                    modifier = Modifier.fillMaxWidth()
                )

                // Use cases:
                // 1. File upload: progress = uploadProgress
                // 2. Onboarding: progress = currentStep / totalSteps
                // 3. Quiz: progress = answeredQuestions / totalQuestions
                """.trimIndent()
            )
        }
    }
}

// ========================================
// 7. EXPANDABLE BOTTOM SHEET DEMO
// Use for additional content, filters
// ========================================

@Composable
fun BottomSheetDemo() {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        DemoCard(
            title = "Expandable Bottom Sheet",
            description = "Drag or tap to expand/collapse"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("The bottom sheet is below this content")
                Text("Try dragging it up or down!", color = Color.Gray)

                Button(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (isExpanded) "Collapse Sheet" else "Expand Sheet")
                }

                UsageCode(
                    """
                    var isExpanded by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxSize()) {
                        // Your main content
                        MainContent()

                        // Bottom sheet
                        ExpandableBottomSheet(
                            isExpanded = isExpanded,
                            onExpandedChange = { isExpanded = it },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            // Sheet content
                            Text("Filters")
                            FilterOptions()
                        }
                    }
                    """.trimIndent()
                )
            }
        }

        ExpandableBottomSheet(
            isExpanded = isExpanded,
            onExpandedChange = { isExpanded = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Bottom Sheet Content", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("This sheet is draggable!", color = Color.Gray)

                repeat(3) { index ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Option ${index + 1}",
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

// ========================================
// 8. ANIMATED TAB INDICATOR DEMO
// Use for custom tab layouts
// ========================================

@Composable
fun TabIndicatorDemo() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Home", "Search", "Profile")

    DemoCard(
        title = "Animated Tab Indicator",
        description = "Indicator smoothly follows selected tab"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Tab buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tabTitles.forEachIndexed { index, title ->
                    TextButton(onClick = { selectedTab = index }) {
                        Text(
                            title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color(0xFF6200EE) else Color.Gray
                        )
                    }
                }
            }

            // Animated indicator
            AnimatedTabIndicator(
                selectedTabIndex = selectedTab,
                tabCount = tabTitles.size,
                modifier = Modifier.fillMaxWidth()
            )

            // Content based on tab
            Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${tabTitles[selectedTab]} Content",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            UsageCode(
                """
                var selectedTab by remember { mutableStateOf(0) }

                Column {
                    // Your tab buttons
                    Row {
                        tabs.forEachIndexed { index, tab ->
                            TextButton(onClick = { selectedTab = index }) {
                                Text(tab.title)
                            }
                        }
                    }

                    // Animated indicator
                    AnimatedTabIndicator(
                        selectedTabIndex = selectedTab,
                        tabCount = tabs.size
                    )

                    // Tab content
                    when(selectedTab) {
                        0 -> HomeScreen()
                        1 -> SearchScreen()
                        2 -> ProfileScreen()
                    }
                }
                """.trimIndent()
            )
        }
    }
}

// ========================================
// 9. CURVED MOTION FAB DEMO
// Use for floating action buttons
// ========================================

@Composable
fun FABDemo() {
    var showFAB by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        DemoCard(
            title = "Curved Motion FAB",
            description = "FAB animates with curved path"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Watch the FAB animate with a curved path")

                Button(
                    onClick = { showFAB = !showFAB },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (showFAB) "Hide FAB" else "Show FAB")
                }

                Spacer(modifier = Modifier.height(200.dp))

                UsageCode(
                    """
                    var showFAB by remember { mutableStateOf(true) }

                    Box(modifier = Modifier.fillMaxSize()) {
                        // Your content

                        CurvedMotionFAB(
                            onClick = { /* action */ },
                            isVisible = showFAB,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        )
                    }

                    // Hide/show based on scroll:
                    val listState = rememberLazyListState()
                    val showFAB = remember {
                        derivedStateOf { listState.firstVisibleItemIndex == 0 }
                    }
                    """.trimIndent()
                )
            }
        }

        CurvedMotionFAB(
            onClick = { /* Add action */ },
            isVisible = showFAB,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

// ========================================
// 10. DRAGGABLE REORDERABLE ITEM DEMO
// Use for sortable lists
// ========================================

@Composable
fun DraggableItemDemo() {
    var items by remember { mutableStateOf(listOf("Item 1", "Item 2", "Item 3", "Item 4")) }
    var draggingIndex by remember { mutableStateOf<Int?>(null) }

    DemoCard(
        title = "Draggable Reorderable Item",
        description = "Long press and drag to reorder"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Long press and drag items to reorder", fontWeight = FontWeight.Bold)

            items.forEachIndexed { index, item ->
                DraggableReorderableItem(
                    isDragging = draggingIndex == index,
                    onDragStart = { draggingIndex = index },
                    onDragEnd = { offset ->
                        draggingIndex = null
                        // Simple reorder logic (you'd implement proper logic)
                        if (offset > 50 && index < items.size - 1) {
                            // Move down
                            val newItems = items.toMutableList()
                            newItems.add(index + 2, newItems.removeAt(index))
                            items = newItems
                        } else if (offset < -50 && index > 0) {
                            // Move up
                            val newItems = items.toMutableList()
                            newItems.add(index - 1, newItems.removeAt(index))
                            items = newItems
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item, fontWeight = FontWeight.Medium)
                        Text("☰", fontSize = 20.sp, color = Color.Gray)
                    }
                }
            }

            Button(
                onClick = { items = listOf("Item 1", "Item 2", "Item 3", "Item 4") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset Order")
            }

            UsageCode(
                """
                var items by remember { mutableStateOf(listOf(...)) }
                var draggingIndex by remember { mutableStateOf<Int?>(null) }

                items.forEachIndexed { index, item ->
                    DraggableReorderableItem(
                        isDragging = draggingIndex == index,
                        onDragStart = { draggingIndex = index },
                        onDragEnd = { offset ->
                            draggingIndex = null
                            // Reorder logic based on offset
                            viewModel.reorderItem(index, offset)
                        }
                    ) {
                        ItemCard(item)
                    }
                }

                // Use case: Todo lists, playlists, priority lists
                """.trimIndent()
            )
        }
    }
}

// ========================================
// HELPER COMPOSABLES
// ========================================

@Composable
fun DemoCard(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6200EE)
        )
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.Gray
        )
        HorizontalDivider()
        content()
    }
}

@Composable
fun UsageCode(code: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                "Usage Example:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                code,
                fontSize = 11.sp,
                color = Color(0xFFE0E0E0),
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

private fun getColorForListIndex(index: Int): Color {
    return when (index % 5) {
        0 -> Color(0xFF6200EE)
        1 -> Color(0xFF03DAC6)
        2 -> Color(0xFFFF6E40)
        3 -> Color(0xFF2196F3)
        else -> Color(0xFF4CAF50)
    }
}
