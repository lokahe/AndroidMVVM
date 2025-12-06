package com.lokahe.androidmvvm.ui.widget

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.annotation.StringRes
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.lokahe.androidmvvm.LocalNavController
import com.lokahe.androidmvvm.LocalPreferenceTheme
import com.lokahe.androidmvvm.copy
import jp.co.omronsoft.iwnnime.mlbeta.compose.widget.drawVerticalScrollbar


fun LazyListScope.settingsCard(
    @StringRes titleRes: Int? = null,
    content: @Composable () -> Unit
) {
    item(contentType = "SettingsCard") {
        SettingsCard(
            title = if (titleRes != null) stringResource(titleRes) else null,
            content = content
        )
    }
}

fun LazyListScope.text(
    modifier: Modifier = Modifier,
    @StringRes textRes: Int,
    styled: TextStyle? = null
) {
    item(contentType = "Text") {
        Text(
            modifier = modifier,
            text = stringResource(textRes),
            style = styled ?: LocalTextStyle.current
        )
    }
}

fun LazyListScope.settingsDivider(color: @Composable (() -> Color)? = null) {
    item(contentType = "SettingsDivider") {
        SettingsItemDivider(color = color?.invoke() ?: MaterialTheme.colorScheme.surfaceContainer)
    }
}

@Composable
fun SettingsItemDivider(color: Color = MaterialTheme.colorScheme.surfaceContainer) {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        color = color
    )
}

@Composable
fun SettingsCard(
    title: String? = null,
    content: @Composable () -> Unit
) {
    val theme = LocalPreferenceTheme.current
    if (title != null) {
        Text(
            text = title,
            modifier = Modifier.Companion
                .padding(theme.padding.copy(horizontal = 0.dp))
                .wrapContentHeight(),
            style = MaterialTheme.typography.titleMedium
        )
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright,
        ),
    ) {
        content()
    }
}

@Composable
fun TextBtn(
    @StringRes textRes: Int,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text = stringResource(textRes))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScaffold(
    @StringRes titleRes: Int,
    useNav: Boolean = true,
    collapsingTopBar: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    header: @Composable (() -> Unit)? = null,
    dialog: @Composable (() -> Unit)? = null,
    content: LazyListScope.() -> Unit
) {
    val navController = if (useNav) LocalNavController.current else null
    val activity = LocalActivity.current as ComponentActivity?
    val theme = LocalPreferenceTheme.current
    val scrollBehavior =
        if (collapsingTopBar)
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
        else TopAppBarDefaults.pinnedScrollBehavior()
    val windowInsets = WindowInsets.safeDrawing
    val scrollState = rememberLazyListState()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .drawVerticalScrollbar(scrollState),
        topBar = {
            MyAppBar(
                title = stringResource(titleRes),
                collapsingTopBar = collapsingTopBar,
                onBackClick = {
                    if (!(navController?.popBackStack() ?: false))
                        activity?.onBackPressedDispatcher?.onBackPressed()
                },
                actions = actions,
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
//        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = windowInsets
    ) { contentPadding ->
        dialog?.invoke()
        header?.let {
            Column(modifier = Modifier.padding(contentPadding.copy(bottom = 0.dp))) {
                it()
                LazyColumn(
                    modifier = Modifier
                        .padding(theme.padding.copy(vertical = 0.dp))
                        .fillMaxHeight(),
                    contentPadding = contentPadding.copy(top = 0.dp),
                    state = scrollState,
                    content = content
                )
            }
        } ?: LazyColumn(
            modifier = Modifier
                .padding(theme.padding.copy(vertical = 0.dp))
                .fillMaxHeight(),
            state = scrollState,
            contentPadding = contentPadding,
            content = content
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    title: String,
    collapsingTopBar: Boolean,
    onBackClick: () -> Unit, // Callback for when the back arrow is clicked
    windowInsets: WindowInsets = WindowInsets.safeDrawing,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    actions: @Composable RowScope.() -> Unit = {}
) {
    if (collapsingTopBar) {
        LargeTopAppBar(
            title = { Text(title) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Recommended for LTR/RTL
                        contentDescription = "Back" // Provide a content description for accessibility
                    )
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer // Example: Use primary color from theme
            ),
            scrollBehavior = scrollBehavior,
            windowInsets = windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        )
    } else {
        TopAppBar(
            title = { Text(title) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Recommended for LTR/RTL
                        contentDescription = "Back" // Provide a content description for accessibility
                    )
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer, // Example: Use primary color from theme
            ),
            scrollBehavior = scrollBehavior,
            windowInsets = windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
        )
    }
}

fun AppBarMenu(
    itemNames: List<String>,
    onEnable: (String) -> Boolean = { true },
    onItemClick: (String) -> Unit = {}
): @Composable RowScope.() -> Unit = {
    var showMenu by remember { mutableStateOf(false) }
    IconButton(onClick = { showMenu = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
    }
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        for (itemName in itemNames) {
            DropdownMenuItem(
                text = { Text(itemName) },
                enabled = onEnable(itemName),
                onClick = {
                    onItemClick(itemName)
                    showMenu = false
                }
            )
        }
    }
}

@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    options: List<String>,
    tag: String? = null,
    selectedState: () -> State<Int>,
    onOptionSelected: (Int) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val selectedIndex by selectedState()
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            easing = CubicBezierEasing(0.0f, 0.5f, 0.0f, 1.0f)
        )
    )
    val density = LocalDensity.current
    val scrollState = rememberScrollState()
    LaunchedEffect(expanded) {
        scrollState.animateScrollTo(with(density) { (48.dp * selectedIndex).roundToPx() })
    }
    val onExpandChange: (Boolean) -> Unit = {
//        if (tag == null || !tappedIn(tag = tag, inMillis = 250, updateOnlyOutOf = true))
        expanded = it
    }
    Box(modifier = modifier) {
        TextButton(onClick = { onExpandChange(!expanded) }) {
            Text(options[selectedIndex])
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.rotate(rotation),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        DropdownMenu(
            expanded = expanded,
            modifier = Modifier.wrapContentHeight(),
            scrollState = scrollState,
            onDismissRequest = { onExpandChange(false) },
            properties = PopupProperties(focusable = false)
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    modifier = Modifier.height(48.dp),
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(options.indexOf(selectionOption))
                        onExpandChange(false)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditText(
    modifier: Modifier,
    value: String,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = TextFieldDefaults.colors(),
    shape: Shape = TextFieldDefaults.shape,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        modifier = modifier,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        decorationBox = { innerTextField ->
            Box(
                modifier = modifier.padding(8.dp) // 这里是内部 padding
            ) {
                TextFieldDefaults.DecorationBox(
                    value = value,
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    placeholder = placeholder,
                    label = label,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    shape = shape,
                    singleLine = singleLine,
                    enabled = enabled,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors
                )
            }
        }
    )
}
