package com.example.notenxt.repository

import com.example.notenxt.models.Notes
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val NOTES_COLLECTION_REF = "notes"

class StorageRepository() {
    val user = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val noteRef: CollectionReference = Firebase.firestore.collection(NOTES_COLLECTION_REF)

    fun getUserNotes(
        userId: String
    ): Flow<Resources<List<Notes>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null
        try {
            snapshotStateListener = noteRef
                .orderBy("timeStamp")
                .whereEqualTo("usrId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val notes = snapshot.toObjects(Notes::class.java)
                        Resources.Success(data = notes)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)
                }
        } catch (e: Exception) {
            trySend(Resources.Error(e?.cause))
            e.printStackTrace()
        }
        awaitClose { snapshotStateListener?.remove() }
    }

    fun getNote(
        noteId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Notes?) -> Unit
    ) {
        noteRef.document(noteId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Notes::class.java))
            }
            .addOnFailureListener { result ->
                onError.invoke(result.cause)
            }
    }

    fun addNote(
        userId: String,
        title: String,
        description: String,
        timeStamp: Timestamp,
        color: Int = 0,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = noteRef.document().id
        val note = Notes(
            userId,
            title,
            description,
            timeStamp,
            colorIndex = color
        )
        noteRef.document(documentId).set(note).addOnCompleteListener { result ->
            onComplete.invoke(result.isSuccessful)
        }
    }

    fun deleteNote(
        noteId: String,
        onComplete: (Boolean) -> Unit
    ){
        noteRef.document(noteId).delete().addOnCompleteListener {
            onComplete.invoke(it.isSuccessful)
        }
    }

    fun updateNote(
        noteId: String,
        title: String,
        note: String,
        color: Int,
        onResult:(Boolean)-> Unit
    ){
        val updateData = hashMapOf<String,Any>(
            "colorIndex" to color,
            "description" to note,
            "title" to title
        )
        noteRef.document(noteId).update(updateData).addOnCompleteListener {
            onResult(it.isSuccessful)
        }
    }
}

sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)

}
