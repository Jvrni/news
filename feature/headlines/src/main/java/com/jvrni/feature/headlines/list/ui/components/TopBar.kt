package com.jvrni.feature.headlines.list.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jvrni.core.ui.R
import com.jvrni.core.ui.theme.NewsAppTheme

@Composable
fun TopBar(
    title: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(bottom = 6.dp)
            .padding(horizontal = 16.dp)
        ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            Text(
                text = stringResource(com.jvrni.feature.headlines.R.string.headlines_top_bar_title, title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center,
                color = Color.Black.copy(alpha = 0.8f),
            )
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            value = searchQuery,
            label = {
                Text(stringResource(com.jvrni.feature.headlines.R.string.headlines_top_bar_label_search), color = MaterialTheme.colorScheme.onSurface.copy(0.7f))
            },
            onValueChange = { onSearchQueryChange(it) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = ""
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary.copy(0.2f),
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.7f),
                cursorColor = MaterialTheme.colorScheme.primary.copy(0.7f),
                focusedSuffixColor = MaterialTheme.colorScheme.outline.copy(0.05f),
                unfocusedContainerColor = MaterialTheme.colorScheme.outline.copy(0.05f),
                focusedContainerColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.outline.copy(0.05f)
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PrevTopBar() {
    NewsAppTheme {
        TopBar(
            "BBC",
            ""
        ) {

        }
    }
}