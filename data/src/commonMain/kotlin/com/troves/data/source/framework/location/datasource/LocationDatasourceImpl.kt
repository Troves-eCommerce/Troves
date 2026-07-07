package com.troves.data.source.framework.location.datasource

import com.troves.data.source.framework.location.service.LocationAddress
import com.troves.data.source.framework.location.service.LocationCoordinates
import com.troves.data.source.framework.location.service.LocationService
import com.troves.data.source.remote.service.ktor.getResults
import com.troves.domain.utils.Result
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.http.path

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
    private val locationClient: HttpClient
) : LocationDatasource {
    override suspend fun getCurrentLocationCoordinates(): LocationCoordinates =
        locationService.getCurrentLocationCoordinates()

    override suspend fun reverseGeocode(coordinates: LocationCoordinates): Result<LocationAddress> {
        return locationClient.getResults {
            method = HttpMethod.Get
            url {
                path("reverce/")
            }
        }
    }
}