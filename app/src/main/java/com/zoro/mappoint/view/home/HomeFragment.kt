package com.zoro.mappoint.view.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.tomtom.quantity.Distance
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProviderConfig
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.image.ImageFactory
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.marker.Marker
import com.tomtom.sdk.map.display.marker.MarkerOptions
import com.tomtom.sdk.map.display.style.StandardStyles
import com.tomtom.sdk.map.display.ui.MapFragment
import com.zoro.mappoint.BuildConfig
import com.zoro.mappoint.R
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class HomeFragment : Fragment(R.layout.home_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //map setup
        val mapOptions =
            MapOptions(
                mapKey = BuildConfig.TOMTOM_API_KEY,
                mapStyle = StandardStyles.BROWSING,
                cameraOptions = CameraOptions(zoom = 12.0)
            )
        val mapFragment = MapFragment.newInstance(mapOptions)

        val androidLocationProviderConfig =
            AndroidLocationProviderConfig(
                minTimeInterval = 250L.milliseconds,
                minDistance = Distance.meters(20.0),
            )
        val androidLocationProvider: LocationProvider =
            AndroidLocationProvider(
                context = requireContext(),
                config = androidLocationProviderConfig,
            )


        //begin map render
        mapFragment.getMapAsync { tomTomMap: TomTomMap ->
            tomTomMap.setLocationProvider(androidLocationProvider)
            androidLocationProvider.enable()

            val locationMarkerOptions =
                LocationMarkerOptions(
                    type = LocationMarkerOptions.Type.Pointer,
                )
            tomTomMap.enableLocationMarker(locationMarkerOptions)

            //add dummmy data
            val ourLocation = androidLocationProvider.lastKnownLocation
            val listOfMarkers = arrayListOf<MarkerOptions>()
            val listofMarkerIcons = arrayOf(
                ImageFactory.fromResource(R.drawable.ic_lane_fix),
                ImageFactory.fromResource(R.drawable.ic_tree),
                ImageFactory.fromResource(R.drawable.ic_crash),
            )

            for (i in 1..15) {
                listOfMarkers.add(
                    MarkerOptions(
                        coordinate = GeoPoint(
                            ourLocation!!.position.latitude + (Random.nextInt(0, 3) * 0.10),
                            ourLocation.position.longitude - (Random.nextInt(0, 3) * 0.75)
                        ), pinImage = listofMarkerIcons[Random.nextInt(0, 2)]
                    )
                )
            }
            tomTomMap.addMarkers(listOfMarkers)
            tomTomMap.moveCamera(
                CameraOptions(
                    position = GeoPoint(
                        ourLocation!!.position.latitude,
                        ourLocation.position.longitude
                    ),
                    zoom = 12.0
                )
            )

            tomTomMap.addMarkerClickListener { marker: Marker ->
                MaterialDialog(requireContext(), BottomSheet()).show {
                    cornerRadius(16f)
                    customView(R.layout.layout_dialog)
                }
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.map_fragment, mapFragment)
            .commit()


    }
}