package com.laurentvrevin.wheris.core.model

/**
 * Stable, non-localized identifiers for Wheris system categories.
 */
object SystemCategoryIds {
    val CAR = CategoryId("car")
    val TENT = CategoryId("tent")
    val BIVOUAC = CategoryId("bivouac")
    val RESTAURANT = CategoryId("restaurant")
    val PHOTO_SPOT = CategoryId("photo_spot")
    val VIEWPOINT = CategoryId("viewpoint")
    val BIKE = CategoryId("bike")
    val PARKING = CategoryId("parking")
    val BEACH = CategoryId("beach")
    val FISHING = CategoryId("fishing")
    val HIKING = CategoryId("hiking")
    val MEETING = CategoryId("meeting")
    val OTHER = CategoryId("other")

    val ALL: List<CategoryId> =
        listOf(
            CAR,
            TENT,
            BIVOUAC,
            RESTAURANT,
            PHOTO_SPOT,
            VIEWPOINT,
            BIKE,
            PARKING,
            BEACH,
            FISHING,
            HIKING,
            MEETING,
            OTHER,
        )
}
