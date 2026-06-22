package com.example.trove.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trove.R
import com.example.trove.ui.theme.AutumnBrown

@Composable
fun TripPhotoActionBar(
    onOpenCamera: () -> Unit,
    onImport: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (compact) 0.dp else 16.dp,
                vertical = if (compact) 8.dp else 12.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onOpenCamera,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AutumnBrown,
                contentColor = Color.White
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_camera),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Open Camera",
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = onImport,
            modifier = Modifier.height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE0D5CC)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AutumnBrown
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_image_placeholder),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = AutumnBrown
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Import",
                fontWeight = FontWeight.Medium
            )
        }
    }
}
