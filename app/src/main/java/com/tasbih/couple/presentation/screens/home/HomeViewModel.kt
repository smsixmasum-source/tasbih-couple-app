package com.tasbih.couple.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tasbih.couple.data.repository.AuthRepository
import com.tasbih.couple.data.repository.ZikrRepository
import com.tasbih.couple.domain.model.Zikr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Default zikr if Firebase empty হলে
val DEFAULT_ZIKR_LIST = listOf(
    Zikr("1", "সুবহানাল্লাহ", "سُبْحَانَ اللَّهِ", "Subhanallah", "আল্লাহ পবিত্র", 33),
    Zikr("2", "আলহামদুলিল্লাহ", "الْحَمْدُ لِلَّهِ", "Alhamdulillah", "সব প্রশংসা আল্লাহর", 33),
    Zikr("3", "আল্লাহু আকবর", "اللَّهُ أَكْبَرُ", "Allahu Akbar", "আল্লাহ সর্বমহান", 34),
    Zikr("4", "লা ইলাহা ইল্লাল্লাহ", "لَا إِلَٰهَ إِلَّا اللَّهُ", "La ilaha illallah", "আল্লাহ ছাড়া কোনো ইলাহ নেই", 100),
    Zikr("5", "আস্তাগফিরুল্লাহ", "أَسْتَغْفِرُ اللَّهَ", "Astaghfirullah", "আমি আল্লাহর কাছে ক্ষমা চাই", 100),
    Zikr("6", "সুবহানাল্লাহি ওয়া বিহামদিহি", "سُبْحَانَ اللهِ وَبِحَمْدِهِ", "Subhanallahi wa bihamdihi", "আল্লাহর প্রশংসায় পবিত্রতা ঘোষণা", 100),
    Zikr("7", "দরূদ শরীফ", "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ", "Allahumma salli ala Muhammad", "নবীর উপর দরূদ", 100),
    Zikr("8", "লা হাওলা ওয়ালা কুওয়াতা", "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ", "La hawla wala quwwata illabillah", "আল্লাহ ছাড়া শক্তি নেই", 100),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val zikrRepo: ZikrRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _defaultZikr = MutableStateFlow<List<Zikr>>(DEFAULT_ZIKR_LIST)
    val defaultZikr: StateFlow<List<Zikr>> = _defaultZikr

    private val _customZikr = MutableStateFlow<List<Zikr>>(emptyList())
    val customZikr: StateFlow<List<Zikr>> = _customZikr

    val userName = authRepo.currentUser?.displayName ?: "বান্দা"

    init {
        loadZikr()
    }

    private fun loadZikr() {
        viewModelScope.launch {
            zikrRepo.getDefaultZikrList().collect { list ->
                if (list.isNotEmpty()) _defaultZikr.value = list
            }
        }
        viewModelScope.launch {
            zikrRepo.getUserZikrList().collect { _customZikr.value = it }
        }
    }

    fun addCustomZikr(zikr: Zikr) {
        viewModelScope.launch { zikrRepo.addCustomZikr(zikr) }
    }

    fun logout() = authRepo.logout()
}
