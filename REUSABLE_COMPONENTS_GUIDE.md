# Reusable Components Quick Reference 🎨

## How to Use This Demo

Run your app - it now shows **ReusableComponentsDemo** with 10 tabs. Each tab demonstrates:
1. Live interactive example
2. How to use the component
3. Code snippet you can copy
4. Common use cases

---

## 10 Components Ready to Copy-Paste

### 1. **ShimmerEffect**
**Use for:** Loading placeholders, skeleton screens

```kotlin
ShimmerEffect(
    modifier = Modifier.fillMaxWidth().height(200.dp),
    durationMillis = 1000 // optional
)
```

**When:** Images loading, list loading, any async content

---

### 2. **PullToRefreshSpring**
**Use for:** Refreshable lists, feeds

```kotlin
var isRefreshing by remember { mutableStateOf(false) }

PullToRefreshSpring(
    isRefreshing = isRefreshing,
    onRefresh = { isRefreshing = true }
) {
    LazyColumn { /* items */ }
}
```

**When:** Social media feeds, news lists, any refreshable content

---

### 3. **SwipeToDismissCard**
**Use for:** Dismissible items, delete actions

```kotlin
SwipeToDismissCard(
    onDismiss = { items = items - item },
    dismissThreshold = 200f
) {
    Card { /* content */ }
}
```

**When:** Email inbox, notifications, chat messages

---

### 4. **ZoomableBox**
**Use for:** Image viewers, maps, zoomable content

```kotlin
ZoomableBox(
    minScale = 1f,
    maxScale = 4f
) {
    Image(...)
}
```

**When:** Photo galleries, PDF viewers, maps

---

### 5. **StaggeredVisibilityItem**
**Use for:** Animated list entrances

```kotlin
LazyColumn {
    itemsIndexed(items) { index, item ->
        StaggeredVisibilityItem(
            visible = isVisible,
            index = index,
            staggerDelayMs = 50
        ) {
            ItemCard(item)
        }
    }
}
```

**When:** Search results, gallery loading, onboarding

---

### 6. **SegmentedProgressIndicator**
**Use for:** Multi-step processes, custom progress

```kotlin
SegmentedProgressIndicator(
    progress = 0.5f, // 0f to 1f
    segments = 8,
    modifier = Modifier.fillMaxWidth()
)
```

**When:** File uploads, multi-step forms, quiz progress

---

### 7. **ExpandableBottomSheet**
**Use for:** Filters, options, additional content

```kotlin
var isExpanded by remember { mutableStateOf(false) }

Box {
    MainContent()

    ExpandableBottomSheet(
        isExpanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        FilterContent()
    }
}
```

**When:** Filters, settings, player controls

---

### 8. **AnimatedTabIndicator**
**Use for:** Custom tab navigation

```kotlin
var selectedTab by remember { mutableStateOf(0) }

AnimatedTabIndicator(
    selectedTabIndex = selectedTab,
    tabCount = 3
)
```

**When:** Custom tab layouts, segmented controls

---

### 9. **CurvedMotionFAB**
**Use for:** Floating action buttons with style

```kotlin
CurvedMotionFAB(
    onClick = { /* action */ },
    isVisible = showFAB,
    modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
)
```

**When:** Add buttons, action menus, scroll-to-top

---

### 10. **DraggableReorderableItem**
**Use for:** Reorderable lists

```kotlin
var isDragging by remember { mutableStateOf(false) }

DraggableReorderableItem(
    isDragging = isDragging,
    onDragStart = { isDragging = true },
    onDragEnd = { offset ->
        isDragging = false
        // Reorder logic
    }
) {
    ItemCard()
}
```

**When:** Todo lists, playlists, priority lists

---

## How to Use in Your Projects

### Step 1: Copy the Component
From `ReusableAnimationComponents.kt`, copy the composable function you need.

### Step 2: Add to Your File
Paste it into your project (or keep it in a shared file).

### Step 3: Use It
Reference the demo in `ReusableComponentsDemo.kt` for exact usage.

---

## Complete Demo Structure

```
ReusableComponentsDemo
├── Tab 1: Shimmer - Loading states
├── Tab 2: Pull Refresh - Refreshable content
├── Tab 3: Swipe - Dismissible cards
├── Tab 4: Zoom - Pinch to zoom
├── Tab 5: Stagger - Animated list entrance
├── Tab 6: Progress - Segmented indicators
├── Tab 7: Sheet - Bottom sheets
├── Tab 8: Tabs - Tab indicators
├── Tab 9: FAB - Floating buttons
└── Tab 10: Drag - Reorderable items
```

Each tab shows:
- ✅ Live example you can interact with
- ✅ Working code
- ✅ Usage snippet
- ✅ Common use cases

---

## Interview Strategy with Components

### Time Saved Per Component
- **Shimmer:** ~10 minutes
- **Pull-to-refresh:** ~20 minutes
- **Swipe-to-dismiss:** ~15 minutes
- **Pinch-to-zoom:** ~25 minutes
- **Staggered list:** ~15 minutes
- **Progress indicator:** ~15 minutes
- **Bottom sheet:** ~20 minutes
- **Tab indicator:** ~10 minutes
- **FAB animation:** ~10 minutes
- **Drag-to-reorder:** ~25 minutes

**Total potential time saved: 2+ hours!**

### During Interview:

1. **Copy Component** (30 seconds)
   - Keep `ReusableAnimationComponents.kt` open
   - Copy entire function

2. **Paste & Adjust** (2-3 minutes)
   - Paste into your project
   - Adjust colors/sizes to match

3. **Use It** (1-2 minutes)
   - Reference demo for exact usage
   - Add to your screen

4. **Explain** (important!)
   - "I have pre-built components for common patterns"
   - "This demonstrates architectural thinking"
   - "In production, I'd use a shared component library"

---

## Combining Components (Examples)

### Example 1: Photo Gallery
```kotlin
// Staggered grid with shimmer loading
LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)) {
    itemsIndexed(photos) { index, photo ->
        StaggeredVisibilityItem(visible = isLoaded, index = index) {
            ZoomableBox {
                if (photo.isLoading) {
                    ShimmerEffect(modifier = Modifier.aspectRatio(1f))
                } else {
                    AsyncImage(model = photo.url, ...)
                }
            }
        }
    }
}
```

### Example 2: Messaging App
```kotlin
// Swipeable messages with pull-to-refresh
PullToRefreshSpring(isRefreshing, onRefresh = { loadMessages() }) {
    LazyColumn {
        items(messages) { message ->
            SwipeToDismissCard(onDismiss = { deleteMessage(message) }) {
                MessageCard(message)
            }
        }
    }
}
```

### Example 3: Onboarding Flow
```kotlin
Column {
    // Progress
    SegmentedProgressIndicator(
        progress = currentStep / totalSteps.toFloat(),
        segments = totalSteps
    )

    // Content with staggered animation
    StaggeredVisibilityItem(visible = showContent, index = 0) {
        OnboardingContent(currentStep)
    }

    // FAB for next step
    CurvedMotionFAB(
        onClick = { nextStep() },
        isVisible = currentStep < totalSteps
    )
}
```

---

## Your Complete Learning Path

### You Now Have:

1. ✅ **AnimationLearningGuide.kt** - Learn animation basics
2. ✅ **ComplexAnimationExamples.kt** - See combinations
3. ✅ **ReusableAnimationComponents.kt** - Ready-to-use components
4. ✅ **ReusableComponentsDemo.kt** - How to use components ⭐ NEW
5. ✅ **INTERVIEW_BLUEPRINT.kt** - Quick project template
6. ✅ **ANIMATION_CHEAT_SHEET.md** - Quick reference
7. ✅ **INTERVIEW_STRATEGY_GUIDE.md** - Complete prep plan

### Recommended Study Order:

**Day 1:** AnimationLearningGuide → understand APIs
**Day 2:** ComplexAnimationExamples → see combinations
**Day 3:** ReusableComponentsDemo → learn patterns ⭐
**Day 4:** Practice with components in new projects
**Day 5:** Timed practice with blueprint + components
**Day 6:** Performance optimization & polish
**Day 7:** Review cheat sheet, final prep

---

## Tips for Using Components

### DO:
✅ Copy entire component function
✅ Customize colors/sizes to your needs
✅ Combine multiple components
✅ Explain why you're using pre-built patterns

### DON'T:
❌ Just paste without understanding
❌ Use all 10 in one interview (overkill!)
❌ Forget to explain your choices
❌ Skip the demo - interact with it first!

---

## Next Steps

1. **Run the app** - See ReusableComponentsDemo
2. **Play with each tab** - Understand behavior
3. **Try building something** - Use 2-3 components
4. **Time yourself** - See how fast you can integrate
5. **Practice explaining** - Why this component, not custom code

---

You're now equipped with production-ready components that can save you hours during interviews! 🚀
