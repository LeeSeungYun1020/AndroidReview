package com.leeseungyun1020.sub2

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage

@Composable
fun MyImage(modifier: Modifier = Modifier) {
    AsyncImage(
        model = "https://avatars.githubusercontent.com/u/34941061?v=4",
        contentDescription = "My Image",
        modifier = modifier
    )
}