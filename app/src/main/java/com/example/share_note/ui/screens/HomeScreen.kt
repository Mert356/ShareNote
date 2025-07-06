package com.example.share_note.ui.screens

import FilterRight
import Note_stack
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.share_note.ui.components.SelectionCard
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.Color
import com.example.share_note.ui.components.Note
import com.example.share_note.ui.components.NoteItem
import com.example.share_note.ui.theme.Green
import com.example.share_note.ui.theme.White
import com.google.firebase.firestore.FirebaseFirestore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.share_note.ui.components.CustomToastHost
import com.example.share_note.ui.components.ToastManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.*
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.first
import undefined


class HomeViewModel : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    var searchQuery by mutableStateOf("")
    var selectedUniversity by mutableStateOf<String?>(null)
    var selectedDepartment by mutableStateOf<String?>(null)
    var selectedSortOption by mutableStateOf("Tarih: Yeniden Eskiye")
    var showFilterDialog by mutableStateOf(false)

    init { loadNotes() }

    private fun loadNotes() {
        FirebaseFirestore.getInstance().collection("notes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Firestore Error: ${error.message}")
                    _notes.value = emptyList()
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    _notes.value = emptyList()
                    return@addSnapshotListener
                }

                _notes.value = snapshot.documents.mapNotNull { doc ->
                    val note = doc.toObject(Note::class.java)?.copy(id = doc.id)

                    val ratedByRaw = doc.get("ratedBy")

                    val ratedByMap: Map<String, Double> = when (ratedByRaw) {
                        is Map<*, *> -> {
                            ratedByRaw.mapNotNull { (key, value) ->
                                val validKey = key as? String
                                val validValue = value as? Double
                                if (validKey != null && validValue != null) validKey to validValue else null
                            }.toMap()
                        }
                        is List<*> -> emptyMap()
                        else -> emptyMap()
                    }

                    note?.copy(ratedBy = ratedByMap)
                }
            }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    context: Context,
    toastManager: ToastManager,
    viewModel: HomeViewModel = viewModel(),

) {
    val scaffoldState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val universities = loadUniversities(context)

    val notes by viewModel.notes.collectAsState()
    val searchQuery by viewModel::searchQuery
    val selectedSortOption by remember { mutableStateOf(viewModel.selectedSortOption) }

    val filteredNotes by remember(notes, searchQuery, viewModel.selectedUniversity, viewModel.selectedDepartment, selectedSortOption) {
        derivedStateOf {
            notes.filter { note ->
                (searchQuery.isBlank() || note.topic.contains(searchQuery, ignoreCase = true)) &&
                        (viewModel.selectedUniversity == null || note.university == viewModel.selectedUniversity) &&
                        (viewModel.selectedDepartment == null || note.department == viewModel.selectedDepartment)
            }.sortedWith(
                when (selectedSortOption) {
                    "Tarih: Eskiden Yeniye" -> compareBy { it.timestamp?.seconds ?: 0 }
                    "Tarih: Yeniden Eskiye" -> compareByDescending { it.timestamp?.seconds ?: 0 }
                    "Puan: Azdan Çoka" -> compareBy { it.rating }
                    "Puan: Çoktan Aza" -> compareByDescending { it.rating }
                    else -> compareByDescending { it.timestamp?.seconds ?: 0 }
                }
            )
        }
    }
    val KEY_AD_FREE_UNTIL = longPreferencesKey("ad_free_until")

    suspend fun saveAdFreeUntil(context: Context) {
        val adFreeUntil = System.currentTimeMillis() + 24 * 60 * 60 * 1000 // 24 saat sonrası
        context.dataStore.edit { prefs ->
            prefs[KEY_AD_FREE_UNTIL] = adFreeUntil
        }
    }

    suspend fun isAdFree(context: Context): Boolean {
        val prefs = context.dataStore.data.first()
        val adFreeUntil = prefs[KEY_AD_FREE_UNTIL] ?: 0
        return System.currentTimeMillis() < adFreeUntil
    }


    val coroutineScope = rememberCoroutineScope()

    val showAd = rewardedAdHandler(context) {
        coroutineScope.launch {
            saveAdFreeUntil(context)
        }
    }


    // 🟢 **Ana UI'yi ve Toast'u üst katmana yerleştiriyoruz**
    Box(modifier = Modifier.fillMaxSize()) {

        // **Ana UI İçeriği**
        ModalNavigationDrawer(
            drawerState = scaffoldState,
            drawerContent = { DrawerContent(navController) },
            gesturesEnabled = true
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Ders Notları", color = White) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { scaffoldState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menü", tint = White)
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                scope.launch {
                                    if (!isAdFree(context)) {
                                        showAd()
                                    } else {
                                        toastManager.showToast("Reklam izleme hakkınızı zaten kullandınız")
                                        showAd()
                                    }
                                }
                            }) {
                                Icon(undefined, contentDescription = "Reklam İzle", tint = White)
                            }

                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Green)
                    )

                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    SearchBar(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.searchQuery = it },
                        onFilterClick = {
                            viewModel.showFilterDialog = true
                        }
                    )

                    if (viewModel.showFilterDialog) {
                        FilterDialog(
                            universities = universities.mapValues { it.value.map { it.first } },
                            selectedUniversity = viewModel.selectedUniversity,
                            selectedDepartment = viewModel.selectedDepartment,
                            selectedSortOption = viewModel.selectedSortOption,
                            onSortOptionSelected = { viewModel.selectedSortOption = it },
                            onUniversitySelected = { viewModel.selectedUniversity = it; viewModel.selectedDepartment = null },
                            onDepartmentSelected = { viewModel.selectedDepartment = it },
                            onDismiss = { viewModel.showFilterDialog = false }
                        )
                    }

                    ContentScreen(
                        notes = filteredNotes,
                        searchQuery = viewModel.searchQuery,
                        selectedUniversity = viewModel.selectedUniversity,
                        selectedDepartment = viewModel.selectedDepartment,
                        selectedSortOption = viewModel.selectedSortOption,
                        navController = navController
                    )
                }
            }
            CustomToastHost(toastManager)
        }
    }
}

@Composable
fun rewardedAdHandler(context: Context, onRewardEarned: () -> Unit): () -> Unit {
    val rewardedAd = remember { mutableStateOf<RewardedAd?>(null) }

    // Load Ad
    LaunchedEffect(Unit) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            "ca-app-pub-3940256099942544/5224354917", // Test Ad ID
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd.value = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d("Ad", "Ad failed to load: ${error.message}")
                }
            }
        )
    }

    // Return function to show ad
    return {
        rewardedAd.value?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd.value = null
            }
        }

        rewardedAd.value?.show(context as Activity) {
            onRewardEarned()
        } ?: Log.d("Ad", "Rewarded Ad not ready")
    }
}


@Composable
fun SearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onFilterClick:  () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { onQueryChange(it) },
            placeholder = { Text("Ara...") },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            onClick = {
                onFilterClick() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Green),
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = FilterRight,
                    contentDescription = "Filtrele",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}


@Composable
fun DrawerContent(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxHeight().background(Green).padding(16.dp).width(250.dp)
    ) {
        Text("Menü", color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        DrawerButton("Profil", Icons.Filled.Person) { navController.navigate("profile") }
        DrawerButton("Ayarlar", Icons.Filled.Settings) { navController.navigate("settings") }
        DrawerButton("Notlarım", Note_stack) { navController.navigate("myNotes") }
        DrawerButton("Not Ekle", Icons.Filled.Add) { navController.navigate("addNote") }
        DrawerButton("Çıkış Yap", Icons.AutoMirrored.Filled.ExitToApp) { logOut(navController) }
    }
}

fun logOut(navController: NavController) {
    FirebaseAuth.getInstance().signOut()
    navController.navigate("login") { popUpTo("home") { inclusive = true } }
}

@Composable
fun DrawerButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(12.dp)) {
        Icon(icon, contentDescription = text, tint = White)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = White, fontSize = 18.sp)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    universities: Map<String, List<String>>,
    selectedUniversity: String?,
    selectedDepartment: String?,
    selectedSortOption: String,
    onSortOptionSelected: (String) -> Unit,
    onUniversitySelected: (String?) -> Unit,
    onDepartmentSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Filtreleme", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            SelectionCard(
                title = "Sıralama Seç",
                items = listOf(
                    "Tarih: Yeniden Eskiye",
                    "Tarih: Eskiden Yeniye",
                    "Puan: Azdan Çoka",
                    "Puan: Çoktan Aza"
                ),
                selectedItem = selectedSortOption,
                onItemSelected = onSortOptionSelected,
                isSearchBarActive = false
            )

            SelectionCard(
                title = "Üniversite Seç",
                items = listOf("Seçiniz") + universities.keys.toList(),
                selectedItem = selectedUniversity ?: "Seçiniz",
                onItemSelected = { newValue ->
                    if (newValue == "Seçiniz") {
                        onUniversitySelected(null)
                    } else {
                        onUniversitySelected(newValue)
                    }
                }
            )

            selectedUniversity?.let {
                SelectionCard(
                    title = "Bölüm Seç",
                    items = listOf("Seçiniz") + (universities[it] ?: emptyList()),
                    selectedItem = selectedDepartment ?: "Seçiniz",
                    onItemSelected = { newValue ->
                        if (newValue == "Seçiniz") {
                            onDepartmentSelected(null)
                        } else {
                            onDepartmentSelected(newValue)
                        }
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        onUniversitySelected(null)
                        onDepartmentSelected(null)
                        onSortOptionSelected("Tarih: Yeniden Eskiye")
                    }
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Sıfırla")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sıfırla")
                }
                Button(
                    onClick = onDismiss
                ) {
                    Text("Uygula")
                }
            }
        }
    }
}


@Composable
fun ContentScreen(
    notes: List<Note>,
    searchQuery: String,
    selectedUniversity: String?,
    selectedDepartment: String?,
    selectedSortOption: String,
    navController: NavController
) {
    val filteredNotes = notes.filter { note ->
        (searchQuery.isBlank() || note.topic.contains(searchQuery, ignoreCase = true)) &&
                (selectedUniversity == null || note.university == selectedUniversity) &&
                (selectedDepartment == null || note.department == selectedDepartment)
    }.sortedWith(
        when (selectedSortOption) {
            "Tarih: Eskiden Yeniye" -> compareBy { it.timestamp?.seconds ?: 0 }
            "Tarih: Yeniden Eskiye" -> compareByDescending { it.timestamp?.seconds ?: 0 }
            "Puan: Azdan Çoka" -> compareBy { it.rating }
            "Puan: Çoktan Aza" -> compareByDescending { it.rating }
            else -> compareByDescending { it.timestamp?.seconds ?: 0 }
        }
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(filteredNotes) { note ->
            NoteItem(note = note, onClick = {
                navController.navigate("noteDetail/${note.id}")
            })
        }
    }
}

