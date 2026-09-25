package com.laurentvrevin.wheris.core.ui.category

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
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
        else -> R.string.category_other
    }

@Composable
fun categoryLabel(categoryId: CategoryId): String = stringResource(categoryLabelRes(categoryId))

fun categoryIcon(categoryId: CategoryId): ImageVector =
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
        else -> Icons.Default.Landscape
    }
