package com.example.firsmadiun.ui.components.section

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.firsmadiun.ui.theme.*

/**
 * Komponen chip pemilih kategori reusable.
 * Menampilkan daftar chip horizontal yang bisa di-wrap.
 *
 * @param kategoriList Daftar nama kategori
 * @param selected Kategori yang sedang dipilih
 * @param onSelected Callback saat kategori dipilih
 * @param modifier Modifier tambahan
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KategoriChipSection(
    kategoriList: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Kategori",
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            kategoriList.forEach { kategori ->
                val isSelected = kategori == selected
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelected(kategori) },
                    label = {
                        Text(
                            text = kategori,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DamkarBlue,
                        selectedLabelColor = White,
                        containerColor = White,
                        labelColor = TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = InputBorder,
                        selectedBorderColor = DamkarBlue,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 1.5.dp
                    )
                )
            }
        }
    }
}