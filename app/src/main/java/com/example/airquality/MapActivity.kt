package com.example.airquality

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.airquality.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback{

    lateinit var binding: ActivityMapBinding

    private var mMap: GoogleMap? = null
    var currentLat: Double = 37.4166 // MainActivity에서 전달된 위도
    var currentLng: Double = 126.8872 // MainActivity에서 전달된 경도

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MainActivity에서 intent로 전달된 값 가져오기
        currentLat = intent.getDoubleExtra("currentLat", 37.4166)
        currentLng = intent.getDoubleExtra("currentLng", 126.8872)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.btnCheckHere.setOnClickListener {
            mMap?.let {
                val intent = Intent()
                // 버튼이 눌린 시점의 카메라 위치 가져오기
                intent.putExtra("latitude", it.cameraPosition.target.latitude)
                intent.putExtra("longitude", it.cameraPosition.target.longitude)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    // 지도가 준비되었을 때 실행되는 Callback 함수
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap?.let{
            val currentLocation = LatLng(currentLat, currentLng)

            // 줌 설정
            it.setMaxZoomPreference(20.0f)
            it.setMinZoomPreference(12.0f)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16f))
        }

        setMarker()

        binding.fabCurrentLocation.setOnClickListener {
            val locationProvider = LocationProvider(this@MapActivity)
            // 위도와 경도 정보 가져오기
            val latitude = locationProvider.getLocationLatitude()
            val longitude = locationProvider.getLocationLongitude()
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16f))
            setMarker()
        }
    }

    // 마커를 설정하는 함수
    private fun setMarker(){
        mMap?.let{
            it.clear() // 마커 초기화

            // 마커 설정
            val markerOptions = MarkerOptions()
            markerOptions.position(it.cameraPosition.target) // 위치
            markerOptions.title("마커 위치") // 이름

            // 마커 추가
            val marker = it.addMarker(markerOptions)
            it.setOnCameraMoveListener {
                marker?.let { marker ->
                    marker.setPosition(it.cameraPosition.target)
                }
            }
        }
    }

}