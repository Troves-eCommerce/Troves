package com.troves.domain.entity

import kotlinx.serialization.SerialName

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
data class LocationAddress(
    val city: String,
    val country: String,
    val countryCode: String,
    val houseNumber: String,
    val neighbourhood: String,
    val postcode: String,
    val road: String,
    val state: String,
    val suburb: String
) {
    /** True when the geocoder returned no usable address fields. */
    val isEmpty: Boolean
        get() = city.isBlank() && country.isBlank() && road.isBlank() &&
                state.isBlank() && postcode.isBlank() && neighbourhood.isBlank() &&
                suburb.isBlank() && houseNumber.isBlank()

    companion object {
        val EMPTY = LocationAddress("", "", "", "", "", "", "", "", "")
    }
}
