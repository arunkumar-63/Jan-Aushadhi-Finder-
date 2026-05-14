package com.janaushadhi.finder.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.janaushadhi.finder.R
import com.janaushadhi.finder.databinding.FragmentMapBinding
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class StoreMapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null
    private var placesClient: PlacesClient? = null
    private lateinit var storeAdapter: KendraStoreAdapter
    private val defaultCenter = LatLng(28.6139, 77.2090)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            enableLocationAndLoadStores()
        } else {
            binding.mapStatusText.text = "Location permission denied. Showing sample Jan Aushadhi Kendras near New Delhi."
            showFallbackStores(defaultCenter)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializePlacesIfPossible()
        storeAdapter = KendraStoreAdapter(::openDirections)
        binding.storeRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.storeRecycler.adapter = storeAdapter
        val mapFragment = childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            setPadding(0, 120, 0, 0)
        }

        if (hasLocationPermission()) {
            enableLocationAndLoadStores()
        } else {
            binding.mapStatusText.text = "Allow location access to show Jan Aushadhi Kendras near you."
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            showFallbackStores(defaultCenter)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationAndLoadStores() {
        val map = googleMap ?: return
        if (!hasLocationPermission()) return

        map.isMyLocationEnabled = true
        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation
            .addOnSuccessListener { location ->
                val center = location?.let { LatLng(it.latitude, it.longitude) } ?: defaultCenter
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 13f))
                loadNearbyJanAushadhiStores(center)
            }
            .addOnFailureListener {
                binding.mapStatusText.text = "Unable to read current location. Showing sample stores near New Delhi."
                showFallbackStores(defaultCenter)
            }
    }

    private fun loadNearbyJanAushadhiStores(center: LatLng) {
        val client = placesClient
        if (client == null) {
            binding.mapStatusText.text = "Add a Google Maps API key to enable live nearby Kendra search. Showing sample stores."
            showFallbackStores(center)
            return
        }

        binding.mapStatusText.text = "Searching live Jan Aushadhi Kendra results nearby..."
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setLocationBias(center.searchBounds())
            .setSessionToken(token)
            .setQuery("Jan Aushadhi Kendra")
            .build()

        client.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions.take(MAX_STORE_RESULTS)
                if (predictions.isEmpty()) {
                    binding.mapStatusText.text = "No live Jan Aushadhi Kendra result found nearby. Showing sample stores."
                    showFallbackStores(center)
                } else {
                    fetchStoreDetails(client, center, predictions)
                }
            }
            .addOnFailureListener {
                binding.mapStatusText.text = "Live store lookup failed. Check key restrictions, billing, and Places API access."
                showFallbackStores(center)
            }
    }

    private fun fetchStoreDetails(
        client: PlacesClient,
        center: LatLng,
        predictions: List<AutocompletePrediction>
    ) {
        val stores = mutableListOf<KendraStore>()
        var pending = predictions.size
        val fields = listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

        predictions.forEach { prediction ->
            val request = FetchPlaceRequest.builder(prediction.placeId, fields).build()
            client.fetchPlace(request).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val place = task.result.place
                    val location = place.latLng
                    if (location != null) {
                        stores += KendraStore(
                            name = place.name ?: prediction.getPrimaryText(null).toString(),
                            location = location,
                            address = place.address ?: prediction.getSecondaryText(null).toString(),
                            isLiveResult = true
                        )
                    }
                }

                pending -= 1
                if (pending == 0) {
                    if (stores.isEmpty()) {
                        binding.mapStatusText.text = "No live Jan Aushadhi Kendra detail found nearby. Showing sample stores."
                        showFallbackStores(center)
                    } else {
                        showStores(center, stores)
                        binding.mapStatusText.text = "Showing ${stores.size} nearby Jan Aushadhi Kendra result${if (stores.size == 1) "" else "s"}."
                    }
                }
            }
        }
    }

    private fun showFallbackStores(center: LatLng) {
        val stores = listOf(
            KendraStore("PMBJK Central Kendra", center.offset(0.018, 0.012), "Sample Jan Aushadhi store", false),
            KendraStore("PMBJK Community Health Centre", center.offset(-0.024, 0.016), "Sample Jan Aushadhi store", false),
            KendraStore("PMBJK Civil Hospital", center.offset(0.011, -0.021), "Sample Jan Aushadhi store", false),
            KendraStore("PMBJK Main Bazaar", center.offset(-0.033, -0.014), "Sample Jan Aushadhi store", false),
            KendraStore("PMBJK Metro Clinic", center.offset(0.041, 0.006), "Sample Jan Aushadhi store", false),
            KendraStore("PMBJK Primary Care Store", center.offset(-0.015, 0.038), "Sample Jan Aushadhi store", false)
        )
        showStores(center, stores)
    }

    private fun showStores(center: LatLng, stores: List<KendraStore>) {
        val map = googleMap ?: return
        val nearbyStores = stores
            .map { it.copy(distanceKm = distanceKm(center, it.location)) }
            .filter { it.distanceKm <= 10.0 }
            .sortedBy { it.distanceKm }

        map.clear()
        nearbyStores.forEach { store ->
            val hue = if (store.isLiveResult) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_AZURE
            map.addMarker(
                MarkerOptions()
                    .position(store.location)
                    .title(store.name)
                    .snippet("${String.format("%.1f", store.distanceKm)} km | ${store.address.ifBlank { "Jan Aushadhi Kendra" }}")
                    .icon(BitmapDescriptorFactory.defaultMarker(hue))
            )
        }
        storeAdapter.submitList(nearbyStores)
        binding.storeListSummaryText.text = if (nearbyStores.isEmpty()) {
            "No Kendras within 10 km"
        } else {
            "${nearbyStores.size} nearby Kendra${if (nearbyStores.size == 1) "" else "s"}"
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 13f))
    }

    private fun openDirections(store: KendraStore) {
        val uri = Uri.parse("google.navigation:q=${store.location.latitude},${store.location.longitude}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:${store.location.latitude},${store.location.longitude}?q=${store.location.latitude},${store.location.longitude}(${Uri.encode(store.name)})")))
        }
    }

    private fun initializePlacesIfPossible() {
        val apiKey = getString(R.string.google_maps_key)
        if (apiKey.isBlank() || apiKey == MAPS_KEY_PLACEHOLDER) return

        if (!Places.isInitialized()) {
            Places.initialize(requireContext().applicationContext, apiKey)
        }
        placesClient = Places.createClient(requireContext())
    }

    private fun LatLng.searchBounds(): RectangularBounds {
        return RectangularBounds.newInstance(
            offset(-SEARCH_BOUNDS_DELTA, -SEARCH_BOUNDS_DELTA),
            offset(SEARCH_BOUNDS_DELTA, SEARCH_BOUNDS_DELTA)
        )
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun distanceKm(start: LatLng, end: LatLng): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)
        val lat1 = Math.toRadians(start.latitude)
        val lat2 = Math.toRadians(end.latitude)
        val a = sin(dLat / 2).pow(2) + sin(dLon / 2).pow(2) * cos(lat1) * cos(lat2)
        return earthRadius * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    private fun LatLng.offset(latitudeDelta: Double, longitudeDelta: Double): LatLng {
        return LatLng(latitude + latitudeDelta, longitude + longitudeDelta)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        googleMap = null
        _binding = null
    }

    private companion object {
        const val MAPS_KEY_PLACEHOLDER = "YOUR_GOOGLE_MAPS_API_KEY"
        const val MAX_STORE_RESULTS = 10
        const val SEARCH_BOUNDS_DELTA = 0.09
    }
}
