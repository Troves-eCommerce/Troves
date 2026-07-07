package com.troves.data.source.framework.location.datasource

import com.troves.data.source.framework.location.service.LocationAddress
import com.troves.data.source.framework.location.service.LocationCoordinates
import com.troves.data.source.framework.location.service.LocationService

/**
 * Copyright (c) 2026 Wahid Ali Wahid Hussien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Author: Wahid Ali Wahid Hussien
 * Created: 07/07/2026
 */
class LocationDatasourceImpl(
    private val locationService: LocationService,
) : LocationDatasource {
    override suspend fun requestPermission(): Boolean = locationService.requestPermission()
    override suspend fun getCurrentLocationCoordinates(): LocationCoordinates =
        locationService.getCurrentLocationCoordinates()

    override suspend fun reverseGeocode(coordinates: LocationCoordinates): LocationAddress =
        locationService.reverseGeocode(coordinates)
}