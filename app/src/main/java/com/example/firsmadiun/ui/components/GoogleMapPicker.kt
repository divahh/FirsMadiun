package com.example.firsmadiun.ui.components

import android.content.Context
import android.location.Geocoder
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.firsmadiun.data.models.LokasiPeta
import com.example.firsmadiun.ui.theme.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

/**
 * Komponen peta menggunakan SDK Google Maps Resmi untuk Jetpack Compose.
 * User bisa tap di peta untuk memilih titik lokasi kejadian darurat.
 */
@Composable
fun GoogleMapPicker(
    isVisible: Boolean,
    initialLat: Double = -7.6298, // Mengubah default awal ke Madiun
    initialLng: Double = 111.5240,
    onLokasiDipilih: (LokasiPeta) -> Unit,
    onToggle: () -> Unit,
    lokasiTerpilih: LokasiPeta? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Mengatur posisi kamera Google Maps
    val cameraPositionState = rememberCameraPositionState {
        val targetLat = lokasiTerpilih?.latitude ?: initialLat
        val targetLng = lokasiTerpilih?.longitude ?: initialLng
        position = CameraPosition.fromLatLngZoom(LatLng(targetLat, targetLng), 15f)
    }

    // Singkronisasi posisi kamera jika lokasiTerpilih berubah secara eksternal
    LaunchedEffect(lokasiTerpilih) {
        if (lokasiTerpilih != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(lokasiTerpilih.latitude, lokasiTerpilih.longitude),
                16f
            )
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Label + Tombol buka/tutup peta
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Titik Lokasi di Peta",
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = onToggle,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DamkarBlue),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, DamkarBlue),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Map,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isVisible) "Tutup Peta" else "Buka Peta",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // Info lokasi terpilih di bawah tombol
        if (lokasiTerpilih != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DamkarBlue.copy(alpha = 0.07f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = DamkarBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Koordinat: ${"%.5f".format(lokasiTerpilih.latitude)}, ${"%.5f".format(lokasiTerpilih.longitude)}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = DamkarBlue
                    )
                    if (lokasiTerpilih.alamat.isNotBlank()) {
                        Text(
                            text = lokasiTerpilih.alamat,
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Box Area Peta Google Maps Native
        AnimatedVisibility(
            visible = isVisible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.5.dp, InputBorder, RoundedCornerShape(14.dp))
            ) {
                // Menjalankan Google Map asli Android
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInteropFilter { motionEvent ->
                            when (motionEvent.action) {
                                android.view.MotionEvent.ACTION_DOWN -> {
                                    // Beritahu parent scroll untuk tidak mengintersep geseran jari
                                    // sehingga maps bisa digeser dengan sangat mulus
                                    cameraPositionState.isMoving // mentrigger internal state maps
                                    false
                                }
                            }
                            // Kembalikan false agar event diteruskan ke fungsi internal Google Maps SDK
                            false
                        },
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true // Memunculkan lingkaran biru lokasi user saat ini ketika izin benar-benar sudah diberikan
                    ),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true, // Tombol GPS bawaan Google Maps
                        zoomControlsEnabled = true     // Tombol + - bawaan
                    ),
                    onMapClick = { latLng ->
                        // Saat peta ditap, cari alamat jalan aslinya
                        val alamatJalan = getAlamatDariLatLng(context, latLng.latitude, latLng.longitude)

                        // Kembalikan objek data LokasiPeta ke komponen induk (ViewModel)
                        onLokasiDipilih(
                            LokasiPeta(
                                latitude = latLng.latitude,
                                longitude = latLng.longitude,
                                alamat = alamatJalan
                            )
                        )
                    }
                ) {
                    // Menampilkan Pin Merah (Marker) bawaan Google Maps jika koordinat sudah ada
                    lokasiTerpilih?.let { lokasi ->
                        Marker(
                            state = MarkerState(position = LatLng(lokasi.latitude, lokasi.longitude)),
                            title = "Lokasi Kejadian Darurat",
                            snippet = lokasi.alamat.ifBlank { "Lokasi ditandai" }
                        )
                    }
                }

                // Hint Overlap text
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.55f)
                    ) {
                        Text(
                            text = "Tap pada peta untuk menandai lokasi",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getAlamatDariLatLng(context: Context, lat: Double, lng: Double): String {
    return try {
        val geocoder = Geocoder(context, Locale("id", "ID"))
        val listAlamat = geocoder.getFromLocation(lat, lng, 1)
        if (!listAlamat.isNullOrEmpty()) {
            listAlamat[0].getAddressLine(0) ?: ""
        } else {
            ""
        }
    } catch (e: Exception) {
        "" // Mengembalikan string kosong jika offline atau error
    }
}