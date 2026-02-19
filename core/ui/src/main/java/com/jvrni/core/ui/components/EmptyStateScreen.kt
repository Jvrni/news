package com.jvrni.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jvrni.core.ui.R
import com.jvrni.core.ui.theme.NewsAppTheme

@Composable
fun EmptyStateScreen(
    modifier: Modifier = Modifier,
    icon: Painter,
    iconTint: Color,
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center,
            color = Color.Black.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            textAlign = TextAlign.Center,
            color = Color.Black.copy(alpha = 0.45f)
        )

        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun EmptyResultsState(modifier: Modifier = Modifier) {
    EmptyStateScreen(
        modifier = modifier,
        icon = painterResource(R.drawable.ic_search),
        iconTint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
        title = stringResource(R.string.empty_results_title),
        subtitle = stringResource(R.string.empty_results_subtitle),
    )
}

@Composable
fun ErrorState(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    EmptyStateScreen(
        modifier = modifier,
        icon = painterResource(R.drawable.ic_info),
        iconTint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
        title = stringResource(R.string.error_title),
        subtitle = stringResource(R.string.error_subtitle),
        actionLabel = stringResource(R.string.error_action),
        onAction = onRetry
    )
}

@Preview(showBackground = true)
@Composable
private fun PrevEmptyResults() {
    NewsAppTheme { EmptyResultsState() }
}

@Preview(showBackground = true)
@Composable
private fun PrevError() {
    NewsAppTheme { ErrorState(onRetry = {}) }
}
