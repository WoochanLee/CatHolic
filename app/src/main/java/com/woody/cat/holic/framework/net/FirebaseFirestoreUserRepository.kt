package com.woody.cat.holic.framework.net

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.woody.cat.holic.data.UserRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.User
import com.woody.cat.holic.framework.net.common.DataNotExistException
import com.woody.cat.holic.framework.net.dto.UserDto
import com.woody.cat.holic.framework.net.dto.mapToUser
import kotlinx.coroutines.CompletableDeferred

class FirebaseFirestoreUserRepository(private val db: FirebaseFirestore, private val firebaseAuth: FirebaseAuth) : UserRepository {

    companion object {
        const val COLLECTION_PROFILE_PATH = "profile"
        private const val USER_PROVIDER = "firebase"
    }

    override suspend fun addUserProfile(user: User): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        db.collection(COLLECTION_PROFILE_PATH)
            .document(user.userId)
            .set(user)
            .addOnSuccessListener {
                dataDeferred.complete(Resource.Success(Unit))
            }.addOnFailureListener {
                dataDeferred.complete(Resource.Error(it))
            }

        return dataDeferred.await()
    }

    override suspend fun getUserProfile(userId: String): Resource<User> {
        val dataDeferred = CompletableDeferred<Resource<User>>()

        db.collection(COLLECTION_PROFILE_PATH)
            .document(userId)
            .get()
            .addOnSuccessListener {
                val userDto = it.toObject(UserDto::class.java)?.mapToUser()
                if (userDto != null) {
                    dataDeferred.complete(Resource.Success(userDto))
                } else {
                    dataDeferred.complete(Resource.Error(DataNotExistException()))
                }
            }.addOnFailureListener {
                dataDeferred.complete(Resource.Error(it))
            }

        return dataDeferred.await()
    }

    override fun isSignedIn(): Boolean {
        return getCurrentUserId() != null
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.providerData?.find { it.providerId == USER_PROVIDER }?.uid
    }
}