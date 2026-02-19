package com.jvrni.feature.headlines.detail.ui

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jvrni.core.navigation.HeadlineRoute
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.spring
import androidx.compose.ui.tooling.preview.Preview
import com.jvrni.core.ui.R
import com.jvrni.core.ui.theme.NewsAppTheme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HeadlineDetailScreen(
    route: HeadlineRoute.DetailsRoute,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    paddingValues: PaddingValues,
    boundsTransform: BoundsTransform,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            with(sharedTransitionScope) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(route.urlToImage)
                        .allowHardware(false)
                        .build(),
                    contentDescription = route.title,
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.img_placeholder),
                    placeholder = painterResource(R.drawable.img_placeholder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .sharedElement(
                            rememberSharedContentState(key = "headline_image_${route.url}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = boundsTransform
                        )
                )
            }

            with(animatedVisibilityScope) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .animateEnterExit(
                            enter = fadeIn(tween(durationMillis = 300, delayMillis = 200)),
                            exit = fadeOut(tween(durationMillis = 100))
                        )
                ) {
                    Text(
                        text = route.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            lineHeight = 28.sp
                        ),
                        color = Color.Black.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (route.author.isNotEmpty()) {
                            Text(
                                text = route.author,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "·",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black.copy(alpha = 0.35f)
                            )
                        }
                        Text(
                            text = route.publishedAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black.copy(alpha = 0.45f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.Black.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(16.dp))

                    if (route.description.isNotEmpty()) {
                        Text(
                            text = route.description,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                lineHeight = 26.sp
                            ),
                            color = Color.Black.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (route.content.isNotEmpty()) {
                        Text(
                            text = route.content,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                            color = Color.Black.copy(alpha = 0.55f)
                        )
                    }
                }
            }
        }

        with(sharedTransitionScope) {
            with(animatedVisibilityScope) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f)
                        .animateEnterExit(
                            enter = fadeIn(tween(durationMillis = 150)),
                            exit = fadeOut(tween(durationMillis = 150))
                        )
                        .padding(top = paddingValues.calculateTopPadding())
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showSystemUi = true)
@Composable
private fun PreviewHeadlineDetailScreen() {
    NewsAppTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                HeadlineDetailScreen(
                    route = HeadlineRoute.DetailsRoute(
                        author = "Jane Smith",
                        title = "Global markets surge as tech stocks hit record highs worldwide",
                        description = "Stock markets around the world saw significant gains on Thursday, led by a rally in technology stocks that pushed major indices to new all-time highs.",
                        url = "https://example.com/1",
                        urlToImage = "",
                        publishedAt = "19 Feb, 09:00 AM",
                        content = "The surge was driven by strong earnings reports from several major technology companies, along with positive economic data suggesting continued growth in the sector. Analysts say the rally could continue into next week if macroeconomic conditions remain stable."
                    ),
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility,
                    paddingValues = PaddingValues(),
                    boundsTransform = { _, _ -> spring() },
                    onBack = {}
                )
            }
        }
    }
}
