package com.tasbih.couple.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tasbih.couple.domain.model.Zikr
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZikrRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    fun getDefaultZikrList(): Flow<List<Zikr>> = callbackFlow {
        val listener = firestore.collection("zikr_library")
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.map { doc ->
                    Zikr(id = doc.id, name = doc.getString("name") ?: "",
                        arabicText = doc.getString("arabicText") ?: "",
                        transliteration = doc.getString("transliteration") ?: "",
                        meaning = doc.getString("meaning") ?: "",
                        targetCount = doc.getLong("defaultTarget")?.toInt() ?: 33)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getUserZikrList(): Flow<List<Zikr>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: run { trySend(emptyList()); return@callbackFlow }
        val listener = firestore.collection("user_zikr").document(uid).collection("items")
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.map { doc ->
                    Zikr(id = doc.id, name = doc.getString("name") ?: "",
                        arabicText = doc.getString("arabicText") ?: "",
                        transliteration = doc.getString("transliteration") ?: "",
                        meaning = doc.getString("meaning") ?: "",
                        targetCount = doc.getLong("targetCount")?.toInt() ?: 33,
                        isDefault = false)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addCustomZikr(zikr: Zikr) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("user_zikr").document(uid).collection("items")
            .add(mapOf("name" to zikr.name, "arabicText" to zikr.arabicText,
                "transliteration" to zikr.transliteration, "meaning" to zikr.meaning,
                "targetCount" to zikr.targetCount)).await()
    }
}
