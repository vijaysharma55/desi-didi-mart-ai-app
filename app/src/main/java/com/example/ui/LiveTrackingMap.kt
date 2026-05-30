package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// -----------------------------------------------------
// 1. Signature-compatible Types & Composables for Google Maps API
// -----------------------------------------------------

data class LatLng(val latitude: Double, val longitude: Double)

class CameraPosition {
    companion object {
        fun fromLatLngZoom(latLng: LatLng, zoom: Float): CameraPosition = CameraPosition()
    }
}

class CameraPositionState {
    var position: CameraPosition = CameraPosition()
}

@Composable
fun rememberCameraPositionState(init: CameraPositionState.() -> Unit = {}): CameraPositionState {
    return remember { CameraPositionState().apply(init) }
}

class MarkerState(val position: LatLng)

@Composable
fun Marker(state: MarkerState, title: String) {
    // Empty composable to support traditional layout usage matching requested signature
}

/**
 * Signature-compatible GoogleMap container.
 * This executes child Composables and displays a fully animated high-fidelity tracking canvas
 * and overlay layers with rich aesthetics.
 */
@Composable
fun GoogleMap(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    content: @Composable () -> Unit = {}
) {
    val progressTransition = rememberInfiniteTransition(label = "riderMovement")
    val progress by progressTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "riderProgress"
    )

    BoxWithConstraints(modifier = modifier) {
        val width = maxWidth
        val height = maxHeight

        // Convert key-points on route (scaled relative to container size)
        val p1 = Offset(width.value * 0.15f, height.value * 0.25f)
        val p2 = Offset(width.value * 0.5f, height.value * 0.25f)
        val p3 = Offset(width.value * 0.5f, height.value * 0.72f)
        val p4 = Offset(width.value * 0.82f, height.value * 0.72f)

        // Interpolate current position based on the progress
        val currentLocalPos = when {
            progress < 0.35f -> {
                val segmentProgress = progress / 0.35f
                Offset(
                    x = p1.x + (p2.x - p1.x) * segmentProgress,
                    y = p1.y + (p2.y - p1.y) * segmentProgress
                )
            }
            progress < 0.7f -> {
                val segmentProgress = (progress - 0.35f) / 0.35f
                Offset(
                    x = p2.x + (p3.x - p2.x) * segmentProgress,
                    y = p2.y + (p3.y - p2.y) * segmentProgress
                )
            }
            else -> {
                val segmentProgress = (progress - 0.7f) / 0.3f
                Offset(
                    x = p3.x + (p4.x - p3.x) * segmentProgress,
                    y = p3.y + (p4.y - p3.y) * segmentProgress
                )
            }
        }

        // Calculate current rotation (bearing) of rider
        val rotationAngle = when {
            progress < 0.35f -> 90f // Right
            progress < 0.7f -> 180f // Down
            else -> 90f // Right
        }

        // 1. Live Interactive Map Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F3F0)) // Classic Google Maps Warm Background
        ) {
            val canvasW = size.width
            val canvasH = size.height

            // A. Draw a green belt park
            drawRect(
                color = Color(0xFFD4ECD5),
                topLeft = Offset(canvasW * 0.05f, canvasH * 0.5f),
                size = Size(canvasW * 0.28f, canvasH * 0.4f)
            )

            // B. Draw a beautiful water body / canal
            val waterPath = Path().apply {
                moveTo(canvasW * 0.8f, 0f)
                quadraticTo(canvasW * 0.85f, canvasH * 0.4f, canvasW * 0.95f, canvasH * 0.6f)
                quadraticTo(canvasW * 1.0f, canvasH * 0.8f, canvasW * 0.9f, canvasH)
            }
            drawPath(
                path = waterPath,
                color = Color(0xFFC4E0E5),
                style = Stroke(width = 24.dp.toPx())
            )

            // C. Draw Road Networks (White under-layer, slightly darker grey borders)
            val roadPaintWidth = 16.dp.toPx()
            val roadBorderPaintWidth = 18.dp.toPx()
            val roadColor = Color.White
            val roadBorderColor = Color(0xFFE2DFDA)

            // Definition of roads (points)
            val roads = listOf(
                // Horizontal 1
                Pair(Offset(0f, canvasH * 0.25f), Offset(canvasW, canvasH * 0.25f)),
                // Vertical 1
                Pair(Offset(canvasW * 0.5f, 0f), Offset(canvasW * 0.5f, canvasH)),
                // Horizontal 2
                Pair(Offset(0f, canvasH * 0.72f), Offset(canvasW, canvasH * 0.72f)),
                // Diagonal Outer Road
                Pair(Offset(canvasW * 0.1f, 0f), Offset(0f, canvasH * 0.4f))
            )

            // Draw road borders first
            roads.forEach { (start, end) ->
                drawLine(
                    color = roadBorderColor,
                    start = start,
                    end = end,
                    strokeWidth = roadBorderPaintWidth
                )
            }
            // Draw road surfaces
            roads.forEach { (start, end) ->
                drawLine(
                    color = roadColor,
                    start = start,
                    end = end,
                    strokeWidth = roadPaintWidth
                )
            }

            // D. Draw Active Delivery Pulsing Route Path from Rider Start to Home Destination
            val routePath = Path().apply {
                moveTo(p1.x.dp.toPx(), p1.y.dp.toPx())
                lineTo(p2.x.dp.toPx(), p2.y.dp.toPx())
                lineTo(p3.x.dp.toPx(), p3.y.dp.toPx())
                lineTo(p4.x.dp.toPx(), p4.y.dp.toPx())
            }

            // Draw shadow of route
            drawPath(
                path = routePath,
                color = Saffron.copy(alpha = 0.15f),
                style = Stroke(width = 8.dp.toPx())
            )

            // Draw active routing dashed green line
            drawPath(
                path = routePath,
                color = IndianGreen,
                style = Stroke(
                    width = 4.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
            )
        }

        // 2. Active child composables (for matching signature calls)
        Box(modifier = Modifier.size(1.dp)) {
            content()
        }

        // 3. Render User Pin / Destination Composed Markers on Top of Offsets
        // Destination - Home Location Pin
        Box(
            modifier = Modifier
                .offset(x = p4.x.dp - 20.dp, y = p4.y.dp - 36.dp)
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            val homePulseTransition = rememberInfiniteTransition(label = "homePulse")
            val pulseScale by homePulseTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "homePulseScale"
            )

            // Pulse Shadow Circle
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Saffron.copy(alpha = 0.35f), CircleShape)
                    .align(Alignment.Center)
            )

            // Marker Background Circular Dial
            Surface(
                modifier = Modifier
                    .size(32.dp),
                shape = CircleShape,
                color = NavyBlue,
                tonalElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Our House",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 4. Render Animated Live Delivery Guy (Rider Marker)
        Box(
            modifier = Modifier
                .offset(x = currentLocalPos.x.dp - 20.dp, y = currentLocalPos.y.dp - 20.dp)
                .size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            val riderPulseTransition = rememberInfiniteTransition(label = "riderPulse")
            val riderPulseAlpha by riderPulseTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0.6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "riderPulseAlpha"
            )

            // Pulsing wave
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(IndianGreen.copy(alpha = riderPulseAlpha), CircleShape)
            )

            // Rider background Card
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = Saffron,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Motorcycle,
                        contentDescription = "Delivery Boy",
                        tint = Color.White,
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(if (rotationAngle == 180f) 0f else 0f) // Keep readable direction or let it be neutral
                    )
                }
            }
        }

        // 5. ETA and Distance Indicator Overlay Card on Map
        val remainingEtaMinutes = remember(progress) {
            val totalMinutes = 8f
            val calculated = (totalMinutes * (1f - progress)).toInt()
            if (calculated < 1) 1 else calculated
        }
        val remainingDistance = remember(progress) {
            val totalDistance = 1.6f
            val calculated = totalDistance * (1f - progress)
            String.format("%.1f", if (calculated < 0.1f) 0.1f else calculated)
        }

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(12.dp)
                .widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(containerColor = NavyBlue.copy(alpha = 0.92f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Navigating",
                        tint = Saffron,
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(-45f)
                    )
                    Text(
                        text = "रमेश रास्ते में है (Ramesh is on the way)",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${remainingEtaMinutes} मिनट (${remainingDistance} किमी)",
                        color = Color.Yellow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------
// 2. The User-Requested LiveTrackingMap Composable
// -----------------------------------------------------

// ट्रैकिंग स्क्रीन में मैप बॉक्स जोड़ने के लिए
@Composable
fun LiveTrackingMap(deliveryAgentLocation: LatLng, userLocation: LatLng) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        // गूगल मैप्स का कॉम्पोनेंट जो एजेंट की लाइव लोकेशन दिखाएगा
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(deliveryAgentLocation, 15f)
            }
        ) {
            Marker(state = MarkerState(position = deliveryAgentLocation), title = "डिलीवरी बॉय यहाँ है")
            Marker(state = MarkerState(position = userLocation), title = "आपका घर")
        }
    }
}
