package com.example.skynet.ui.ejercicios

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skynet.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarEjercicioScreen(
    viewModel: AgregarEjercicioViewModel,
    onCancel: () -> Unit,
    onCreateNew: () -> Unit
) {
    val uiState by viewModel.uiState.observeAsState(AgregarEjercicioUiState.Loading())
    val searchQuery by viewModel.searchQuery.observeAsState("")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Agregar Ejercicio",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111111)
                        )
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onCancel) {
                        Text("Cancelar", color = Color(0xFF0088CC))
                    }
                },
                actions = {
                    TextButton(onClick = onCreateNew) {
                        Text("Crear", color = Color(0xFF0088CC), fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            FilterChipsRow(
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "Ejercicios populares",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            )

            when (val state = uiState) {
                is AgregarEjercicioUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF0088CC))
                    }
                }
                is AgregarEjercicioUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(state.ejercicios) { ejercicio ->
                            EjercicioItem(ejercicio = ejercicio)
                        }
                    }
                }
                is AgregarEjercicioUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = { Text("Buscar ejercicio", color = Color.Gray) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
        },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF4F6F9),
            unfocusedContainerColor = Color(0xFFF4F6F9),
            disabledContainerColor = Color(0xFFF4F6F9),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun FilterChipsRow(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(label = "Todo Equipamiento")
        }
        item {
            FilterChip(label = "Todos Músculos")
        }
    }
}

@Composable
fun FilterChip(label: String) {
    Surface(
        color = Color(0xFFF4F6F9),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.height(44.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF111111)
                )
            )
        }
    }
}

@Composable
fun EjercicioItem(ejercicio: EntrenamientoDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Seleccionar ejercicio */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = if (ejercicio.imagenUrl.isNullOrEmpty()) "file:///android_asset/login.jpg" else ejercicio.imagenUrl,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_workout),
            error = painterResource(R.drawable.ic_workout),
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFF4F6F9)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = ejercicio.nombre,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111111)
                )
            )
            Text(
                text = ejercicio.musculo,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                )
            )
        }

        IconButton(
            onClick = { /* Acción de selección */ },
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFF4F6F9), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF0088CC),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
