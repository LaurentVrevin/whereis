package com.laurentvrevin.wheris.core.ui.category

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.laurentvrevin.wheris.core.designsystem.theme.LocalWherisDarkTheme
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.core.ui.R

@StringRes
fun categoryLabelRes(categoryId: CategoryId): Int =
    when (categoryId) {
        SystemCategoryIds.CAR -> R.string.category_car
        SystemCategoryIds.TENT -> R.string.category_tent
        SystemCategoryIds.BIVOUAC -> R.string.category_bivouac
        SystemCategoryIds.RESTAURANT -> R.string.category_restaurant
        SystemCategoryIds.PHOTO_SPOT -> R.string.category_photo_spot
        SystemCategoryIds.VIEWPOINT -> R.string.category_viewpoint
        SystemCategoryIds.BIKE -> R.string.category_bike
        SystemCategoryIds.PARKING -> R.string.category_parking
        SystemCategoryIds.BEACH -> R.string.category_beach
        SystemCategoryIds.FISHING -> R.string.category_fishing
        SystemCategoryIds.HIKING -> R.string.category_hiking
        SystemCategoryIds.MEETING -> R.string.category_meeting
        SystemCategoryIds.OTHER -> R.string.category_other
        else -> R.string.category_unavailable
    }

@Composable
fun categoryLabel(
    categoryId: CategoryId,
    category: Category? = null,
): String = if (category != null) categoryLabel(category) else stringResource(categoryLabelRes(categoryId))

@Composable
fun categoryLabel(category: Category): String =
    if (category.isSystem) stringResource(categoryLabelRes(category.id)) else requireNotNull(category.name)

fun categoryIcon(
    categoryId: CategoryId,
    category: Category? = null,
): ImageVector =
    if (category != null) {
        categoryIcon(category)
    } else {
        when (categoryId) {
            SystemCategoryIds.CAR -> Icons.Default.DirectionsCar
            SystemCategoryIds.TENT -> Icons.Default.Home
            SystemCategoryIds.BIVOUAC -> Icons.Default.Park
            SystemCategoryIds.RESTAURANT -> Icons.Default.Restaurant
            SystemCategoryIds.PHOTO_SPOT -> Icons.Default.PhotoCamera
            SystemCategoryIds.VIEWPOINT -> Icons.Default.Visibility
            SystemCategoryIds.BIKE -> Icons.AutoMirrored.Filled.DirectionsBike
            SystemCategoryIds.PARKING -> Icons.Default.LocalParking
            SystemCategoryIds.BEACH -> Icons.Default.BeachAccess
            SystemCategoryIds.FISHING -> Icons.Default.Place
            SystemCategoryIds.HIKING -> Icons.AutoMirrored.Filled.DirectionsWalk
            SystemCategoryIds.MEETING -> Icons.Default.Event
            SystemCategoryIds.OTHER -> Icons.Default.MoreHoriz
            else -> Icons.Default.Place
        }
    }

fun categoryIcon(category: Category): ImageVector =
    if (category.isSystem) categoryIcon(category.id) else categoryIcon(requireNotNull(category.iconKey))

fun categoryIcon(key: CategoryIconKey): ImageVector =
    when (key) {
        CategoryIconKey.PLACE -> Icons.Default.Place
        CategoryIconKey.PHOTO_CAMERA -> Icons.Default.PhotoCamera
        CategoryIconKey.PARK -> Icons.Default.Park
        CategoryIconKey.RESTAURANT -> Icons.Default.Restaurant
    }

/** Approved Light/Dark accents from docs/design/03_DESIGN_COMPONENTS.md, section 32. */
fun categoryAccent(
    key: CategoryColorKey,
    darkTheme: Boolean,
): Color {
    val colors =
        when (key) {
            CategoryColorKey.ORANGE -> 0xFFF97316 to 0xFFFB923C
            CategoryColorKey.AMBER -> 0xFFD97706 to 0xFFF59E0B
            CategoryColorKey.YELLOW -> 0xFFCA8A04 to 0xFFEAB308
            CategoryColorKey.GREEN -> 0xFF16A34A to 0xFF4ADE80
            CategoryColorKey.TEAL -> 0xFF0F766E to 0xFF2DD4BF
            CategoryColorKey.BLUE -> 0xFF2563EB to 0xFF60A5FA
            CategoryColorKey.INDIGO -> 0xFF4F46E5 to 0xFF818CF8
            CategoryColorKey.PURPLE -> 0xFF7E22CE to 0xFFC084FC
            CategoryColorKey.PINK -> 0xFFDB2777 to 0xFFF472B6
            CategoryColorKey.RED -> 0xFFDC2626 to 0xFFF87171
            CategoryColorKey.BROWN -> 0xFF92400E to 0xFFD6A36A
            CategoryColorKey.SLATE -> 0xFF475569 to 0xFF94A3B8
        }
    return Color(if (darkTheme) colors.second else colors.first)
}

@Composable
fun categoryColor(category: Category?): Color =
    category?.colorKey?.let { categoryAccent(it, LocalWherisDarkTheme.current) } ?: MaterialTheme.colorScheme.primary

@Composable
fun categoryColor(key: CategoryColorKey): Color = categoryAccent(key, LocalWherisDarkTheme.current)

@StringRes
fun categoryIconLabelRes(key: CategoryIconKey): Int =
    when (key) {
        CategoryIconKey.PLACE -> R.string.category_icon_place
        CategoryIconKey.PHOTO_CAMERA -> R.string.category_icon_photo
        CategoryIconKey.PARK -> R.string.category_icon_park
        CategoryIconKey.RESTAURANT -> R.string.category_icon_restaurant
    }

@StringRes
fun categoryColorLabelRes(key: CategoryColorKey): Int =
    when (key) {
        CategoryColorKey.ORANGE -> R.string.category_color_orange
        CategoryColorKey.AMBER -> R.string.category_color_amber
        CategoryColorKey.YELLOW -> R.string.category_color_yellow
        CategoryColorKey.GREEN -> R.string.category_color_green
        CategoryColorKey.TEAL -> R.string.category_color_teal
        CategoryColorKey.BLUE -> R.string.category_color_blue
        CategoryColorKey.INDIGO -> R.string.category_color_indigo
        CategoryColorKey.PURPLE -> R.string.category_color_purple
        CategoryColorKey.PINK -> R.string.category_color_pink
        CategoryColorKey.RED -> R.string.category_color_red
        CategoryColorKey.BROWN -> R.string.category_color_brown
        CategoryColorKey.SLATE -> R.string.category_color_slate
    }
