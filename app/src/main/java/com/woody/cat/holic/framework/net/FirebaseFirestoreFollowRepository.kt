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
            val myUserDto = it.get(db.collection(COLLECTION_PROFILE_PATH).document(myUserId)).toObject(UserDto::class.java)
            val targetUserDto = it.get(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId)).toObject(UserDto::class.java)

            val isMyUserAlreadyFollowing = myUserDto?.followingUserIds?.contains(targetUserId) == true
            val isTargetUserAlreadyFollowed = targetUserDto?.followerUserIds?.contains(myUserId) == true

            if (isMyUserAlreadyFollowing || isTargetUserAlreadyFollowed) {
                dataDeferred.complete(Resource.Error(IllegalStateException()))
                return@runTransaction
            }

            it.update(db.collection(COLLECTION_PROFILE_PATH).document(myUserId), UserDto::followingUserIds.name, FieldValue.arrayUnion(targetUserId))
            it.update(db.collection(COLLECTION_PROFILE_PATH).document(myUserId), UserDto::followingCount.name, FieldValue.increment(1))
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
            val myUserDto = it.get(db.collection(COLLECTION_PROFILE_PATH).document(myUserId)).toObject(UserDto::class.java)
            val targetUserDto = it.get(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId)).toObject(UserDto::class.java)

            val isMyUserAlreadyFollowing = myUserDto?.followingUserIds?.contains(targetUserId) == true
            val isTargetUserAlreadyFollowed = targetUserDto?.followerUserIds?.contains(myUserId) == true

            if (!isMyUserAlreadyFollowing || !isTargetUserAlreadyFollowed) {
                dataDeferred.complete(Resource.Error(IllegalStateException()))
                return@runTransaction
            }

            it.update(db.collection(COLLECTION_PROFILE_PATH).document(myUserId), UserDto::followingUserIds.name, FieldValue.arrayRemove(targetUserId))
            it.update(db.collection(COLLECTION_PROFILE_PATH).document(myUserId), UserDto::followingCount.name, FieldValue.increment(-1))
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