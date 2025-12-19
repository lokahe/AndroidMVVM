package com.lokahe.androidmvvm.ui.preference

import android.annotation.SuppressLint
import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.lokahe.androidmvvm.toInt

@Composable
inline fun ListPreference(
    modifier: Modifier = Modifier,
    title: String,
    value: Any,
    summary: (Any) -> String = { "" },
    @ArrayRes names: Int,
    @ArrayRes values: Int,
    @ArrayRes icons: Int? = null,
    @DrawableRes iconsMap: HashMap<Int, Int>? = null,
    noinline enabled: @Composable () -> Boolean = { true },
    contentPaddingValues: PaddingValues = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
    crossinline onChange: (value: Any) -> Unit,
) {
    var openSelector by rememberSaveable { mutableStateOf(false) }
    if (openSelector) {
        var currentValue by rememberSaveable { mutableStateOf(value) }
        val items = toItems(currentValue, names, values, icons, iconsMap)
        Dialog(
            title = title,
            contentPaddingValues = contentPaddingValues,
            onDismissRequest = { openSelector = false },
            onPositiveButtonClick = { onChange(currentValue) }
        ) {
            val lazyListState = rememberLazyListState()
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = lazyListState,
            ) {
                items(items = items) { item ->
                    RadioButtonItem(currentValue, item) {
                        currentValue = item.value
                    }
                }
            }
        }
    }
    Preference(
        title = title,
        modifier = modifier,
        enabled = enabled,
        summary = summary(value)
    ) {
        openSelector = true
    }
}

fun getCurrentItem(
    value: Any,
    items: List<Item>,
): Item? {
    for (item in items) {
        if (item.value == value) {
            return item
        }
    }
    return null
}

data class SecondConfirmDialogInfo(
    val title: String,
    @DrawableRes val titleIcon: Int? = null,
    val message: String? = null,
    val positiveButtonText: String? = null,
    val negativeButtonText: String? = null,
)

@SuppressLint("LocalContextResourcesRead")
@Composable
fun toItems(
    value: Any,
    @ArrayRes namesRes: Int,
    @ArrayRes valuesRes: Int,
    @ArrayRes iconsRes: Int? = null,
    @DrawableRes iconsMap: HashMap<Int, Int>? = null
): List<Item> {
    val context = LocalContext.current
    val names = context.resources.getStringArray(namesRes)
    val values: Array<out Any> = when (value) {
        is Int -> arrayOf(context.resources.getIntArray(valuesRes))
        is String -> context.resources.getStringArray(valuesRes)
        else -> throw IllegalArgumentException("Unsupported type for value $value")
    }
    return toItems(names, values, iconsRes, iconsMap)
}

@SuppressLint("LocalContextResourcesRead")
@Composable
fun toItems(
    names: Array<String>,
    values: Array<out Any>,
    @ArrayRes iconsRes: Int? = null,
    @DrawableRes iconsMap: HashMap<Int, Int>? = null
): List<Item> {
    val context = LocalContext.current
    val icons = iconsRes?.let { context.resources.obtainTypedArray(it) }
    val items = mutableListOf<Item>()
    for (i in 0..<names.size) {
        items.add(
            Item(
                names[i],
                values[i],
                icons?.getResourceId(i, 0) ?: iconsMap?.get(values[i].toInt())
            )
        )
    }
    icons?.recycle()
    return items
}

data class Item(
    val name: String,
    val value: Any,
    @DrawableRes val icon: Int?
) {
    override fun equals(other: Any?): Boolean =
        when (other) {
            is Set<*> -> other.contains(value)
            is Array<*> -> other.contains(value)
            is Item -> other.value == value
            else -> value == other
        }

    override fun hashCode(): Int {
        var result = icon ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}

@Composable
fun RadioButtonItem(
    currentValue: Any,
    item: Item,
    onClick: () -> Unit,
) {
    val selected = item.value == currentValue
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .selectable(
                    selected = selected,
                    enabled = true,
                    role = Role.RadioButton,
                    onClick = onClick
                )
                .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(modifier = Modifier.width(12.dp))
        if (item.icon != null) {
            Image(
                painter = painterResource(id = item.icon),
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.name,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
