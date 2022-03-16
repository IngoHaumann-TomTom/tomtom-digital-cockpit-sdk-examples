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

package com.example.ivi.example.media.panelpolicy

import com.tomtom.ivi.appsuite.media.api.common.core.IviMediaItem
import com.tomtom.ivi.appsuite.media.api.common.core.SourceId
import com.tomtom.ivi.appsuite.media.api.common.frontend.MediaFrontendContext
import com.tomtom.ivi.appsuite.media.api.common.frontend.policies.MediaTaskPanelPolicy

/**
 * The task panel policy is responsible for creating panels.
 */
internal class ExampleMediaSourceTaskPanelPolicy : MediaTaskPanelPolicy {

    /**
     * This method is called to create the initial root panel. The root panel is the first panel
     * that is opened when browsing a source.
     */
    override fun createMediaRootPanel(
        mediaContext: MediaFrontendContext,
        sourceId: SourceId,
        mediaItem: IviMediaItem?
    ) = ExampleMediaSourcePanel(mediaContext)

    /** This method is called when the user selects search. */
    override fun createMediaSearchPanel(
        mediaContext: MediaFrontendContext,
        sourceId: SourceId,
        mediaItem: IviMediaItem?
    ) = throw UnsupportedOperationException("Search is not supported by the example media source.")

    /**
     * This method is called when restoring the panels when the frontend is reopened. If there are
     * other panels, this has to be able to create more than just the root panel.
     */
    override fun createMediaTaskPanel(
        mediaContext: MediaFrontendContext,
        sourceId: SourceId,
        mediaItem: IviMediaItem?
    ) = ExampleMediaSourcePanel(mediaContext)
}
