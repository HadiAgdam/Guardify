package ir.the_code.guardify.components.field

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ir.the_code.guardify.R

@Composable
fun CustomField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        maxLines = 1,
        singleLine = true,
        decorationBox = { content ->
            Row(
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.background)
                    .height(48.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    content()
                    if (value.isEmpty()) {
                        Text(
                            placeholder,
                            modifier = Modifier.alpha(.7f)
                        )
                    }
                }
            }
        },
        textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onBackground)
    )
}