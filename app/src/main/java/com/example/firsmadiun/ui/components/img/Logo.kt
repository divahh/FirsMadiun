package com.example.firsmadiun.ui.components.img

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.firsmadiun.R

/**
 * Komponen Logo DAMKAR yang reusable.
 *
 * @param size Ukuran logo (default 100.dp)
 * @param modifier Modifier tambahan
 */
@Composable
fun Logo(
    size: Dp = 100.dp,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon_damkar),
            contentDescription = "Logo DAMKAR",
            modifier = Modifier.size(size)
        )
    }
}