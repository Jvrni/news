package com.jvrni.feature.headlines.list.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jvrni.core.ui.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.spring
import androidx.compose.ui.tooling.preview.Preview
import com.jvrni.core.ui.theme.NewsAppTheme
import com.jvrni.domain.models.Headline
import com.jvrni.domain.models.Source

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Card(
    article: Headline,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    boundsTransform: BoundsTransform,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            with(sharedTransitionScope) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(article.urlToImage)
                        .allowHardware(false)
                        .build(),
                    contentDescription = article.title,
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.img_placeholder),
                    placeholder = painterResource(R.drawable.img_placeholder),
                    modifier = Modifier
                        .size(80.dp)
                        .sharedElement(
                            rememberSharedContentState(key = "headline_image_${article.url}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = boundsTransform
                        )
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Column {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    textAlign = TextAlign.Start,
                    color = Color.Black.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (article.author.isNotEmpty()) {
                        Text(
                            text = article.author,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp
                            ),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }

                    Text(
                        text = article.publishedAt,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Light,
                            fontSize = 10.sp
                        ),
                        textAlign = TextAlign.Center,
                        color = Color.Black.copy(alpha = 0.8f),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
private fun PreviewHeadlineCard() {
    NewsAppTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                Card(
                    article = Headline(
                        source = Source(id = "bbc-news", name = "BBC News", category = null),
                        author = "John Doe",
                        title = "Breaking: Major event unfolds in downtown area this weekend",
                        description = "Details emerging about a significant development.",
                        url = "https://example.com/1",
                        urlToImage = "",
                        publishedAt = "19 Feb, 10:30 AM",
                        content = ""
                    ),
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedVisibility,
                    boundsTransform = { _, _ -> spring() },
                    onClick = {}
                )
            }
        }
    }
}
