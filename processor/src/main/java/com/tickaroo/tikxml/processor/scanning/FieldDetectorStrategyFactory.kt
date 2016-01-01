/*
 * Copyright (C) 2015 Hannes Dorfmann
 * Copyright (C) 2015 Tickaroo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tickaroo.tikxml.processor.scanning

import com.tickaroo.tikxml.annotation.ScanMode
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Detects and instantiates the concrete [FieldScanner]
 * @author Hannes Dorfmann
 */
class FieldDetectorStrategyFactory(private val elementUtils: Elements, private val typeUtils: Types, private val requiredDetector: RequiredDetector) {

    // Visible for testing
    private val commonCaseScanStrategy = CommonCaseFieldDetectorStrategy(elementUtils, typeUtils, requiredDetector)
    private val annotationOnlyScanStrategy = AnnotationOnlyFieldDetectorStrategy(elementUtils, typeUtils, requiredDetector)

    /**
     * Get the strategy or use the default one
     */
    fun getStrategy(scanMode: ScanMode, defaultScanMode: ScanMode = ScanMode.COMMON_CASE) =
            if (scanMode == ScanMode.DEFAULT)
                strategyForScanMode(defaultScanMode)
            else
                strategyForScanMode(scanMode)

    /**
     * Maps the [ScanMode] to a [FieldScanner]
     */
    private fun strategyForScanMode(scanMode: ScanMode) = when (scanMode) {
        ScanMode.COMMON_CASE -> commonCaseScanStrategy
        ScanMode.ANNOTATIONS_ONLY -> annotationOnlyScanStrategy
        ScanMode.DEFAULT -> commonCaseScanStrategy
    }
}