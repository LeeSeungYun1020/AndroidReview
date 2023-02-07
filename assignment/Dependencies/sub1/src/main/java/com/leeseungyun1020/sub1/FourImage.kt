package com.leeseungyun1020.sub1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.leeseungyun1020.sub2.MyImage

@Composable
fun FourImage() {
    Column {
        Row {
            MyImage(Modifier.weight(1f))
            MyImage(Modifier.weight(1f))
        }
        Row {
            MyImage(Modifier.weight(1f))
            MyImage(Modifier.weight(1f))
        }
    }
}