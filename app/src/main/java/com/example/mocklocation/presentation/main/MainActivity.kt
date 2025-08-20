package com.example.mocklocation.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.viewinterop.AndroidView
import com.skt.tmap.vsm.map.MapEngine
import com.skt.tmap.vsm.map.VSMMapView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var mMapView: VSMMapView? = null
    private var mMapEngine: MapEngine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)

        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                MapContainer(
                    modifier = Modifier.fillMaxSize(),
                    onCreated = ::onMapCreated,
                    onDispose = ::cleanupMap
                )
            }
        }
    }

    private fun onMapCreated(mapView: VSMMapView) {
        mMapView = mapView
        mMapEngine = mapView.mapEngine()
        Timber.d("Map and Helper 초기화")
    }

    private fun cleanupMap() {
        Timber.d("VSMMapView destroyed")
        mMapEngine?.onDestroy()
        val parent = mMapView?.parent as? android.view.ViewGroup
        parent?.removeView(mMapView)
    }

    override fun onStart() {
        Timber.d("onStart")
        super.onStart()
        mMapView?.onResume()
    }

    override fun onResume() {
        Timber.d("onResume")
        super.onResume()
        mMapView?.onResume()
    }

    override fun onPause() {
        Timber.d("onPause")
        super.onPause()
        mMapView?.onPause()
    }

    override fun onStop() {
        Timber.d("onStop")
        super.onStop()
        mMapView?.onPause()
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
        cleanupMap()
    }
}

@Composable
fun MapContainer(
    modifier: Modifier = Modifier,
    onCreated: (VSMMapView) -> Unit,
    onDispose: () -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context -> VSMMapView(context).also(onCreated) }
    )
    DisposableEffect(Unit) {
        onDispose { onDispose() }
    }
}