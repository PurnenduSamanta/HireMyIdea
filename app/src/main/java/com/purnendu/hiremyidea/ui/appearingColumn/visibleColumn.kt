package com.purnendu.hiremyidea.ui.appearingColumn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun VisibleColumn(
    modifier: Modifier = Modifier,
    onCloseClick:()->Unit,
    isVisible: Boolean
) {

    AnimatedVisibility(
        modifier = modifier.fillMaxHeight(),
        visible = isVisible) {

        Column(
            modifier = Modifier.padding(10.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(space = 15.dp, alignment = Alignment.Bottom),
            horizontalAlignment = Alignment.End
        )
        {

            SingleIconRow(
                iconName="Icon1",
                icon= Icons.Default.AccountCircle,
                contentDescription="icon1",
                onClick = {}
            )

            SingleIconRow(
                iconName="Icon2",
                icon= Icons.Default.AccountBox,
                contentDescription="icon2",
                onClick = {}
            )

            SingleIconRow(
                iconName="Icon3",
                icon= Icons.Default.Call,
                contentDescription="icon3",
                onClick = {}
            )

            SingleIconRow(
                iconName="Icon4",
                icon= Icons.Default.Home,
                contentDescription="icon4",
                onClick = {}
            )

            SingleIconRow(
                isCircularShape = false,
                iconName="",
                icon= Icons.Default.Close,
                contentDescription="closeIcon",
                onClick = {onCloseClick()}
            )

        }

    }

}


@Composable
fun SingleIconRow(modifier: Modifier = Modifier,isCircularShape:Boolean=true,iconName:String,icon: ImageVector,contentDescription:String,onClick:()->Unit) {

    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically)
    {

        if(iconName.isNotEmpty())
        {
            Text(iconName, color = Color.White)

            Spacer(modifier = Modifier.width(10.dp))
        }


        if(isCircularShape)
        {
            Box(modifier = Modifier.clip(CircleShape).background(Color.White).padding(10.dp), contentAlignment = Alignment.Center)
            {
                Icon(modifier = Modifier.size(30.dp) .clickable(onClick = onClick), imageVector = icon, contentDescription = contentDescription)
            }
        }

        else
        {
            Icon(modifier = Modifier.size(30.dp) .clickable(onClick = onClick), tint = Color.White, imageVector = icon, contentDescription = contentDescription)
        }


    }


}