package dev.kjdz.flashcardapp.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomOutlineTextField(
    placeholder: String? = null,
    value: String,
    label: String? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    // parameters below will be passed to BasicTextField for correct behavior of the text field,
    // and to the decoration box for proper styling and sizing

    val colors = TextFieldDefaults.colors()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth(),
    ) { innerTextField ->
        Column(
            modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 1.dp)
                    .indicatorLine(
                        enabled = enabled,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = colors
                    )
            ) {
                Box() {
                    innerTextField()
                    if (value.isEmpty() && placeholder != null) {
                        Text(text = placeholder, style = textStyle)
                    }
                }
                Spacer(
                    Modifier
                        .height(6.dp)
                )
            }
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}