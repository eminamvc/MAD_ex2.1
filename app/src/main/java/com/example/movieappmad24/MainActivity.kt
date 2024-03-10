package com.example.movieappmad24
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.movieappmad24.models.Movie
import com.example.movieappmad24.models.getMovies
import com.example.movieappmad24.ui.theme.MovieAppMAD24Theme
import com.example.movieappmad24.ui.theme.LightColorScheme
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieAppMAD24Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = LightColorScheme.primary
                ) {
                    TopAppNavigationBar()
                }
            }
        }
    }
}

//TopBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppNavigationBar() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    //Farbe von der TopBar
                    containerColor = LightColorScheme.primaryContainer,
                    titleContentColor = LightColorScheme.primary,
                ),
                title = {
                    //mit textalignt den Text zentrieren
                    Text("Movie App", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                },
            )
        },
        bottomBar = {
            BottomAppNavigationBar()
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MovieList(movies = getMovies()) // Ihr Inhalt, der die MovieList enthält
        }
    }
}

@Composable
fun BottomAppNavigationBar() {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Watchlist") },
            label = { Text("Watchlist") },
            selected = false,
            onClick = { }
        )
    }
}

@Composable
fun MovieList(movies: List<Movie> = getMovies()){
    LazyColumn {
        items(movies) { movie ->
            MovieRow(movie)
        }
    }
}

@Composable
fun MovieRow(movie: Movie) {
    var isExpanded by remember { mutableStateOf(false) }

    MovieCardLayout(isExpanded = isExpanded) {
        MovieImageHeader(movie.images)
        MovieTitleRow(movie.title, isExpanded) { isExpanded = !isExpanded }
        ExpandableMovieDetails(isExpanded, movie)
    }
}

@Composable
fun MovieCardLayout(isExpanded: Boolean, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .animateContentSize(),
        shape = ShapeDefaults.Large,
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun MovieImageHeader(images: List<String>) {
    Box(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        images.firstOrNull()?.let { imageUrl ->
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "Movie image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        FavoriteIcon()
    }
}

@Composable
fun FavoriteIcon() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Icon(
            tint = MaterialTheme.colorScheme.secondary,
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = "Add to favorites"
        )
    }
}

@Composable
fun MovieTitleRow(title: String, isExpanded: Boolean, onExpandChange: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        ExpandCollapseIcon(isExpanded, onExpandChange)
    }
}

@Composable
fun ExpandCollapseIcon(isExpanded: Boolean, onClick: () -> Unit) {
    Icon(
        modifier = Modifier.clickable(onClick = onClick),
        imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
        contentDescription = if (isExpanded) "Show less" else "Show more"
    )
}

@Composable
fun ExpandableMovieDetails(isExpanded: Boolean, movie: Movie) {
    AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(12.dp)) {
            MovieDetailsText("Director: ${movie.director}")
            MovieDetailsText("Released: ${movie.year}")
            MovieDetailsText("Genre: ${movie.genre}")
            MovieDetailsText("Actors: ${movie.actors}")
            MovieRatingRow(movie.rating)
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth())
            MovieDetailsText("Plot: ${movie.plot}")
        }
    }
}

@Composable
fun MovieDetailsText(detail: String) {
    Text(text = detail, style = MaterialTheme.typography.bodySmall)
}

@Composable
fun MovieRatingRow(rating: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MovieDetailsText("Rating: $rating")
    }
}

@Preview
@Composable
fun DefaultPreview(){
    MovieAppMAD24Theme {
        MovieList(movies = getMovies())
    }
}