package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.woody.cat.holic.data.FollowRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.framework.COLLECTION_PROFILE_PATH
import com.woody.cat.holic.framework.net.dto.UserDto
import kotlinx.coroutines.CompletableDeferred

class FirebaseFirestoreFollowRepository(private val db: FirebaseFirestore) : FollowRepository {

    override suspend fun followUser(myUserId: String, targetUserId: String): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        db.runTransaction {
            it.update(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId), UserDto::followerUserIds.name, FieldValue.arrayUnion(myUserId))
            it.update(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId), UserDto::followerCount.name, FieldValue.increment(1))
        }.addOnSuccessListener {
            dataDeferred.complete(Resource.Success(Unit))
        }.addOnFailureListener {
            dataDeferred.complete(Resource.Error(it))
        }

        return dataDeferred.await()
    }

    override suspend fun unfollowUser(myUserId: String, targetUserId: String): Resource<Unit> {
        val dataDeferred = CompletableDeferred<Resource<Unit>>()

        db.runTransaction {
            it.update(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId), UserDto::followerUserIds.name, FieldValue.arrayRemove(myUserId))
            it.update(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId), UserDto::followerCount.name, FieldValue.increment(-1))
        }.addOnSuccessListener {
            dataDeferred.complete(Resource.Success(Unit))
        }.addOnFailureListener {
            dataDeferred.complete(Resource.Error(it))
        }

        return dataDeferred.await()
    }

    override suspend fun getFollowers(userId: String): Resource<List<String>> {
        val dataDeferred = CompletableDeferred<Resource<List<String>>>()

        db.collection(COLLECTION_PROFILE_PATH)
            .document(userId)
            .get()
            .addOnSuccessListener {
                val userDto = it.toObject(UserDto::class.java)
                dataDeferred.complete(Resource.Success(userDto?.followerUserIds ?: emptyList()))
            }.addOnFailureListener {
                dataDeferred.complete(Resource.Error(it))
            }

        return dataDeferred.await()
    }

    override suspend fun getFollowings(userId: String): Resource<List<String>> {
        val dataDeferred = CompletableDeferred<Resource<List<String>>>()

        db.collection(COLLECTION_PROFILE_PATH)
            .document(userId)
            .get()
            .addOnSuccessListener {
                val userDto = it.toObject(UserDto::class.java)
                dataDeferred.complete(Resource.Success(userDto?.followingUserIds ?: emptyList()))
            }.addOnFailureListener {
                dataDeferred.complete(Resource.Error(it))
            }

        return dataDeferred.await()
    }
}