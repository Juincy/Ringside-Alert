package com.juincy.ringsidealert

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Startup animation: ropes draw in, the bell pops up and rings, the name fades in, then it fades away. Tap to skip. */
@Composable
fun Splash(onDone: () -> Unit) {
    val enter = remember { Animatable(0f) }
    val swing = remember { Animatable(0f) }
    val pulse = remember { Animatable(0f) }
    val fade = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        enter.animateTo(1f, tween(550, easing = FastOutSlowInEasing))
        launch { pulse.animateTo(1f, tween(900, easing = LinearEasing)) }
        swing.animateTo(14f, tween(110))
        swing.animateTo(-12f, tween(170))
        swing.animateTo(9f, tween(150))
        swing.animateTo(-6f, tween(140))
        swing.animateTo(3f, tween(120))
        swing.animateTo(0f, tween(120))
        delay(500)
        fade.animateTo(0f, tween(350))
        onDone()
    }

    Box(
        Modifier.fillMaxSize()
            .graphicsLayer { alpha = fade.value }
            .background(Color(0xFF0B0C10))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDone() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.fillMaxWidth().offset(y = (-65).dp),
            verticalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            repeat(3) {
                Box(
                    Modifier.fillMaxWidth().height(4.dp)
                        .graphicsLayer { scaleX = enter.value; alpha = 0.9f * enter.value }
                        .background(Color(0xFF2B2C38)),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(260.dp), contentAlignment = Alignment.Center) {
                Image(
                    painterResource(R.drawable.logo_waves), null,
                    Modifier.fillMaxSize().graphicsLayer {
                        val s = 0.9f + 0.18f * pulse.value
                        scaleX = s
                        scaleY = s
                        alpha = enter.value * (1f - 0.4f * pulse.value)
                    },
                )
                Image(
                    painterResource(R.drawable.logo_bell), null,
                    Modifier.fillMaxSize().graphicsLayer {
                        val s = 0.55f + 0.45f * enter.value
                        scaleX = s
                        scaleY = s
                        alpha = enter.value
                        rotationZ = swing.value
                        transformOrigin = TransformOrigin(0.5f, 0.3f)
                    },
                )
            }
            Text(
                "RINGSIDE", color = Color(0xFFF4F3EF), fontFamily = Dsp, fontSize = 46.sp, letterSpacing = 5.sp,
                modifier = Modifier.graphicsLayer { alpha = enter.value; translationY = (1f - enter.value) * 40f },
            )
            Text(
                "ALERT", color = Color(0xFFF0463F), fontFamily = Dsp, fontSize = 46.sp, letterSpacing = 9.sp,
                modifier = Modifier.graphicsLayer { alpha = enter.value; translationY = (1f - enter.value) * 40f },
            )
            Text(
                "Never miss a wrestling show", color = Color(0xFF9A98A0), fontSize = 13.sp,
                modifier = Modifier.graphicsLayer { alpha = enter.value },
            )
        }
    }
}
