package com.hope.firebase.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseDB<T>(private val subPath: String, private val clazz: Class<T>) {

    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val database: DatabaseReference =
        if (uid != null) {
            FirebaseDatabase.getInstance().getReference("users").child(uid).child(subPath)
        } else {
            throw IllegalStateException("User is not authenticated")
        }

    fun create(id: String, data: T, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        database.child(id).setValue(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
    fun createAuto(data: T, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        database.push().setValue(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
    fun read(id: String, onSuccess: (T?) -> Unit, onError: (Exception) -> Unit) {
        database.child(id).get()
            .addOnSuccessListener { snapshot ->
                val item = snapshot.getValue(clazz)
                onSuccess(item)
            }
            .addOnFailureListener { onError(it) }
    }
    fun readAll(onSuccess: (List<T>) -> Unit, onError: (Exception) -> Unit) {
        database.get()
            .addOnSuccessListener { snapshot ->
                val items = mutableListOf<T>()
                for (child in snapshot.children) {
                    val item = child.getValue(clazz)
                    if (item != null) {
                        items.add(item)
                    }
                }
                onSuccess(items)
            }
            .addOnFailureListener { onError(it) }
    }
    fun update(id: String, updates: Map<String, Any>, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        database.child(id).updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    fun delete(id: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        database.child(id).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }
}
data class UserFirebase(
    val name: String = "",
    val profilePicture: String = "",
    val age: Int = 0
)
