package com.example.firsmadiun.ui.components.img

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.firsmadiun.ui.theme.DamkarBlue
import java.io.File

@Composable
fun ImageInputField(
    label: String,
    value: Uri?,
    onValueChange: (Uri?) -> Unit,
    isError: Boolean,
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Uri sementara untuk menyimpan foto kamera sebelum ke ViewModel
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // 1. Launcher untuk GALERI
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) onValueChange(uri)
    }

    // 2. Launcher untuk KAMERA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            onValueChange(tempCameraUri) // Berhasil ambil foto, kirim URI ke ViewModel
        }
    }

    // Fungsi pembantu membuat file URI kosong khusus kamera
    fun createImageUri(context: Context): Uri {
        val directory = File(context.cacheDir, "camera_photos").apply { mkdirs() }
        val file = File.createTempFile("snapshot_", ".jpg", directory)
        // Pastikan com.example.firsmadiun.fileprovider cocok dengan AndroidManifest.xml kamu
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val borderColor = if (isError) MaterialTheme.colorScheme.error else Color.Gray

    Column(modifier = modifier.fillMaxWidth()) {
        // Label Input
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Kotak Area Klik / Preview Gambar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                .clickable { showDialog = true }, // Memunculkan dialog pilihan saat diklik
            contentAlignment = Alignment.Center
        ) {
            if (value != null) {
                // Menampilkan Preview jika gambar sudah dipilih (Menggunakan Coil Library)
                AsyncImage(
                    model = value,
                    contentDescription = "Preview Gambar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Tampilan default jika belum ada gambar yang dipilih (Placeholder)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Icon Tambah Foto",
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Pilih Gambar / Ambil Foto", color = Color.Gray)
                }
            }
        }

        // Menampilkan Pesan Error jika terjadi Error
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text("Pilih Sumber Foto") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Opsi Kamera
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDialog = false
                                    val uri = createImageUri(context)
                                    tempCameraUri = uri
                                    cameraLauncher.launch(uri)
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Kamera", tint = DamkarBlue)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Ambil dari Kamera")
                        }

                        // Opsi Galeri
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showDialog = false
                                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Galeri", tint = DamkarBlue)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Pilih dari Galeri")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}