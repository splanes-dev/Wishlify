package com.splanes.uoc.wishlify.presentation.common.components.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.splanes.uoc.wishlify.presentation.infrastructure.theme.WishlifyTheme
import kotlinx.coroutines.Dispatchers

@Composable
fun RemoteImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.None,
    colorFilter: ColorFilter? = null,
    loading: @Composable () -> Unit = { RemoteImageLoading() },
    error: @Composable () -> Unit = { RemoteImageError(contentDescription) },
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
) {
    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(url)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(url.cleanUrl())
        .diskCacheKey(url.cleanUrl())
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    SubcomposeAsyncImage(
        modifier = modifier,
        model = imageRequest,
        contentDescription = contentDescription,
        contentScale = contentScale,
        colorFilter = colorFilter,
        onSuccess = onSuccess,
        onError = onError,
        loading = { loading() },
        error = { error() }
    )
}

@Composable
fun RemoteImageLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = WishlifyTheme.colorScheme.surfaceContainerLow),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp
        )
    }
}

@Composable
fun RemoteImageError(contentDescription: String? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = WishlifyTheme.colorScheme.surfaceContainerLow),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.ImageNotSupported,
            contentDescription = contentDescription
        )
    }
}

private fun String?.cleanUrl(): String? =
  this?.replaceAfter("?", "")
      ?.replace("?", "")