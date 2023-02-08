package com.leeseungyun1020.sub2

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MyImage(modifier: Modifier = Modifier) {
    AsyncImage(
        model = "https://avatars.githubusercontent.com/u/34941061?v=4",
        contentDescription = "My Image",
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.ic_android_24dp),
        error = painterResource(id = R.drawable.ic_android_24dp),
        modifier = modifier.width(100.dp).height(100.dp)
    )
}