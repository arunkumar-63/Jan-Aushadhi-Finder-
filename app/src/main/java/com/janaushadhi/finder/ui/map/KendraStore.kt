package com.janaushadhi.finder.ui.map

import com.google.android.gms.maps.model.LatLng

data class KendraStore(
    val name: String,
    val location: LatLng,
    val address: String,
    val isLiveResult: Boolean,
    val storeCode: String = "",
    val phone: String = "",
    val state: String = "",
    val district: String = "",
    val distanceKm: Double = 0.0
)
