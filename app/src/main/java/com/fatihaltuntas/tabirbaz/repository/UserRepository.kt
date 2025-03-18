package com.fatihaltuntas.tabirbaz.repository

import com.fatihaltuntas.tabirbaz.model.UserProfile
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    // Firebase Authentication işlemleri
    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Giriş başarısız oldu")
    }
    
    suspend fun signUpWithEmailAndPassword(email: String, password: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Kayıt başarısız oldu")
    }
    
    suspend fun signInWithGoogle(account: GoogleSignInAccount): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return result.user ?: throw Exception("Google ile giriş başarısız oldu")
    }
    
    suspend fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().await()
    }
    
    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
    
    fun signOut() {
        auth.signOut()
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    // Firestore kullanıcı profil işlemleri
    suspend fun createUserProfile(user: FirebaseUser) {
        val userProfile = hashMapOf(
            "uid" to user.uid,
            "email" to user.email,
            "displayName" to user.displayName,
            "photoUrl" to user.photoUrl?.toString(),
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore.collection("users").document(user.uid)
            .set(userProfile, SetOptions.merge())
            .await()
    }
    
    suspend fun updateUserProfile(userProfile: UserProfile) {
        val user = auth.currentUser ?: throw Exception("Kullanıcı bulunamadı")
        
        firestore.collection("users")
            .document(user.uid)
            .set(userProfile)
            .await()
    }
    
    suspend fun getUserProfile(): UserProfile? {
        val user = auth.currentUser ?: return null
        
        val document = firestore.collection("users")
            .document(user.uid)
            .get()
            .await()
            
        return if (document.exists()) {
            document.toObject(UserProfile::class.java)
        } else {
            null
        }
    }
} 