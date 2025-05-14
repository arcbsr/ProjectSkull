//package com.hope.firebase.firestore
//
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.SetOptions
//import com.google.android.gms.tasks.Task
//import com.google.firebase.firestore.DocumentSnapshot
//import com.google.firebase.firestore.QuerySnapshot
//
//object FirestoreHelper {
//
//    private val db = FirebaseFirestore.getInstance()
//
//    // CREATE or UPDATE
//    fun setDocument(
//        collection: String,
//        documentId: String,
//        data: Map<String, Any>
//    ): Task<Void> {
//        return db.collection(collection)
//            .document(documentId)
//            .set(data, SetOptions.merge())
//    }
//
//    // READ single document
//    fun getDocument(
//        collection: String,
//        documentId: String
//    ): Task<DocumentSnapshot> {
//        return db.collection(collection)
//            .document(documentId)
//            .get()
//    }
//
//    // READ all documents in a collection
//    fun getAllDocuments(
//        collection: String
//    ): Task<QuerySnapshot> {
//        return db.collection(collection).get()
//    }
//
//    // UPDATE (specific fields only)
//    fun updateDocument(
//        collection: String,
//        documentId: String,
//        updates: Map<String, Any>
//    ): Task<Void> {
//        return db.collection(collection)
//            .document(documentId)
//            .update(updates)
//    }
//
//    // DELETE
//    fun deleteDocument(
//        collection: String,
//        documentId: String
//    ): Task<Void> {
//        return db.collection(collection)
//            .document(documentId)
//            .delete()
//    }
//}
