package com.example.airquality
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.requestLocationUpdates

class LocationProvider(val context: Context) {
    // Location : 위도, 경도, 고도와 같이 위치에 관련된 정보를 가지고 있는 데이터 클래스
    private var location: Location? = null
    // Location Manager : 시스템 위치 서비스에 접근을 제공하는 클래스
    private var locationManager: LocationManager? = null

    init {
        // 초기화 시 위치 가져오기
        getLocation();
    }

    private fun getLocation(): Location? {
        try {
            // 위치 시스템 서비스
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            var gpsLocation: Location? = null
            var networkLocation: Location? = null

            // GPS Provider 와 Network Provider가 활성화 되어있는지 확인
            val isGPSEnabled: Boolean =
                locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled: Boolean =
                locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) { // GPS, Network Provider 둘 다 사용 불가능한 상황이면 null을 반환
                return null
            } else {
                val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION // 정밀한 위치 정보 얻기
                )
                val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION // 도시 Block 단위 정밀도의 위치 정보 얻기
                )
                if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) { // 위 2가지 권한이 없는 경우
                    return null
                }

                if (isNetworkEnabled) { // 네트워크로 위치 가져오기
                    networkLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }

                if (isGPSEnabled) { // GPS로 위치 가져오기
                    gpsLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }

                if (gpsLocation != null && networkLocation != null) { // 위치 정보가 2가지인 경우 정확도가 높은 것으로 선택
                    if (gpsLocation.accuracy > networkLocation.accuracy) {
                        location = gpsLocation
                        return gpsLocation
                    } else {
                        location = networkLocation
                        return networkLocation
                    }
                } else { // 위치 정보가 1가지인 경우
                    if (gpsLocation != null) {
                        location = gpsLocation
                    }

                    if (networkLocation != null) {
                        location = networkLocation
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location
    }

    // 위도 정보를 가져오는 함수
    fun getLocationLatitude(): Double {
        return location?.latitude ?: 0.0
    }

    // 경도 정보르 가져오는 함수
    fun getLocationLongitude(): Double {
        return location?.longitude ?: 0.0
    }
}