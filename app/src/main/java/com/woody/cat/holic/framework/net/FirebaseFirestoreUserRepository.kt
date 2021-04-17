package com.woody.cat.holic.framework.net

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.woody.cat.holic.data.UserRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.domain.User
import com.woody.cat.holic.framework.COLLECTION_PROFILE_PATH
import com.woody.cat.holic.framework.net.common.DataNotExistException
import com.woody.cat.holic.framework.net.dto.UserDto
import com.woody.cat.holic.framework.net.dto.mapToUser
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreUserRepository(
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {

    companion object {
        private const val USER_PROVIDER = "firebase"
    }

    override suspend fun addUserProfile(user: User): Resource<Unit> {
        return try {
            db.collection(COLLECTION_PROFILE_PATH)
                .document(user.userId)
                .set(user)
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getUserProfile(userId: String): Resource<User> {
        return try {
            val result = db.collection(COLLECTION_PROFILE_PATH)
                .document(userId)
                .get()
                .await()

            val user = result.toObject(UserDto::class.java)?.mapToUser()
            if (user != null) {
                Resource.Success(user)
            } else {
                Resource.Error(DataNotExistException())
            }
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun updateDisplayName(userId: String, displayName: String): Resource<Unit> {
        if (displayName.isEmpty()) return Resource.Error(IllegalStateException())

        return try {
            db.collection(COLLECTION_PROFILE_PATH)
                .document(userId)
                .update(UserDto::displayName.name, displayName)
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun updateUserProfilePhotoUrl(userId: String, userProfilePhotoUrl: String): Resource<Unit> {
        return try {
            db.collection(COLLECTION_PROFILE_PATH)
                .document(userId)
                .update(UserDto::userProfilePhotoUrl.name, userProfilePhotoUrl)
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun updateUserBackgroundPhotoUrl(userId: String, userBackgroundPhotoUrl: String): Resource<Unit> {
        return try {
            db.collection(COLLECTION_PROFILE_PATH)
                .document(userId)
                .update(UserDto::userBackgroundPhotoUrl.name, userBackgroundPhotoUrl)
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun updateGreetings(userId: String, greetings: String): Resource<Unit> {
        return try {
            db.collection(COLLECTION_PROFILE_PATH)
                .document(userId)
                .update(UserDto::greetings.name, greetings)
                .await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override fun isSignedIn(): Boolean {
        return getCurrentUserId() != null
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.providerData?.find { it.providerId == USER_PROVIDER }?.uid
    }
}