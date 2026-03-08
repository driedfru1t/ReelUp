package com.nikol.search_impl.presentation.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.nikol.detail_api.DetailScreen
import com.nikol.detail_api.ContentType
import com.nikol.di.scope.directViewModel
import com.nikol.search_impl.domain.models.SearchResultDomain
import com.nikol.search_impl.presentation.mvi.intent.SearchIntent
import com.nikol.search_impl.presentation.viewModel.SearchRouter
import com.nikol.search_impl.presentation.viewModel.SearchScreenViewModel

@Composable
fun SearchScreen(
    onDetail: (DetailScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = directViewModel<SearchScreenViewModel, SearchRouter> {
        object : SearchRouter {
            override fun toDetail(contentType: ContentType, id: Int) {
                onDetail(DetailScreen(contentType = contentType, id = id))
            }

            override fun toFilter() { /* TODO */ }
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagedItems = viewModel.pagedContent.collectAsLazyPagingItems()

    val isLoading = pagedItems.loadState.refresh is LoadState.Loading
            && state.searchQuery.isNotBlank()

    // 1. Получаем FocusManager на уровне экрана
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            // 2. Снимаем фокус и прячем клавиатуру при тапе в любую пустую область
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SearchTopBar(
                query = state.inputText,
                isLoading = isLoading,
                onQueryChange = { viewModel.setIntent(SearchIntent.Search(it)) },
                onClear = { viewModel.setIntent(SearchIntent.ClearText) }
            )
        }
    ) { padding ->
        SearchContent(
            modifier = Modifier.padding(top = padding.calculateTopPadding()),
            pagedItems = pagedItems,
            searchQuery = state.searchQuery,
            onItemClick = { item ->
                // Перед переходом на другой экран тоже полезно убрать фокус
                focusManager.clearFocus()
                viewModel.setIntent(
                    SearchIntent.GoToDetail(contentType = item.type, id = item.id)
                )
            }
        )
    }
}

@Composable
fun SearchTopBar(
    query: String,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Вернул Box вместо Column, чтобы индикатор загрузки лежал прямо на поле
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Фильмы, сериалы...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = {
                            onClear()
                            keyboardController?.show()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Очистить")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                })
            )

//            // Восстановил красивую анимацию загрузки поверх нижней границы
//            AnimatedVisibility(
//                visible = isLoading,
//                enter = fadeIn(),
//                exit = fadeOut(),
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(horizontal = 2.dp)
//                    .padding(bottom = 2.dp)
//            ) {
//                LinearProgressIndicator(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(3.dp)
//                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
//                    color = MaterialTheme.colorScheme.primary,
//                    trackColor = Color.Transparent
//                )
//            }
        }
    }
}

@Composable
fun SearchContent(
    modifier: Modifier = Modifier,
    pagedItems: LazyPagingItems<SearchResultDomain>,
    searchQuery: String,
    onItemClick: (SearchResultDomain) -> Unit
) {
    val isRefreshLoading = pagedItems.loadState.refresh is LoadState.Loading
    val isRefreshError = pagedItems.loadState.refresh is LoadState.Error

    // Для скрытия клавиатуры при скролле
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()

    // 3. Супер-фича: скрываем клавиатуру, как только пользователь начал листать список
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            focusManager.clearFocus()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState, // Привязываем состояние списка
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = pagedItems.itemCount,
                key = pagedItems.itemKey { it.id }
            ) { index ->
                pagedItems[index]?.let { item ->
                    SearchResultItem(item = item, onClick = { onItemClick(item) })
                }
            }

            if (pagedItems.loadState.append is LoadState.Loading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(Modifier.size(32.dp))
                    }
                }
            }

            if (pagedItems.loadState.append is LoadState.Error) {
                item {
                    OutlinedButton(
                        onClick = { pagedItems.retry() },
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) { Text("Не удалось загрузить больше. Повторить?") }
                }
            }
        }

        // --- СОСТОЯНИЯ ПУСТОГО ЭКРАНА ---
        if (pagedItems.itemCount == 0) {
            if (searchQuery.isBlank()) {
                EmptyStateMessage("Начните вводить текст для поиска...")
            } else if (isRefreshError) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Произошла ошибка при поиске", textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { pagedItems.retry() }) { Text("Повторить") }
                }
            } else if (!isRefreshLoading) {
                EmptyStateMessage("По запросу «$searchQuery» ничего не найдено \uD83D\uDE14")
            }
        }
    }
}

@Composable
private fun EmptyStateMessage(text: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// SearchResultItem остается без изменений
@Composable
fun SearchResultItem(
    item: SearchResultDomain,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(76.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.type.name, // MOVIE / TV
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}