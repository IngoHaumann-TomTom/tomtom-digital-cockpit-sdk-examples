/*
 * Copyright © 2022 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */

package com.example.ivi.example.media.custompolicies

import com.tomtom.ivi.appsuite.media.api.common.core.IviMediaItem
import com.tomtom.ivi.appsuite.media.api.common.frontend.MediaItemData
import com.tomtom.ivi.appsuite.media.api.common.frontend.policies.MediaItemDataExtractionPolicy

/***
 * [MediaItemDataExtractionPolicy] helps change how the information from a source is displayed to
 * the user.
 * In this case, we want all media to always show a title and a subtitle even if the source doesn't
 * make any available.
 */
internal class ExampleMediaSourceItemDataExtractionPolicy : MediaItemDataExtractionPolicy {
    override fun invoke(item: IviMediaItem): MediaItemData {
        return MediaItemData(
            title = item.title ?: "No Title",
            subtitle = item.subtitle ?: "No Artist",
            artworkUri = item.artUri,
            isPlayable = item.isPlayable,
            isBrowsable = item.isBrowsable,
            duration = item.duration
        )
    }
}
