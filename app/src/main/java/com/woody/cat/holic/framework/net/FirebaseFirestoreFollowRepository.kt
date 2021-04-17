package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.woody.cat.holic.data.FollowRepository
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.framework.COLLECTION_PROFILE_PATH
import com.woody.cat.holic.framework.net.dto.UserDto
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreFollowRepository(private val db: FirebaseFirestore) : FollowRepository {

    override suspend fun followUser(myUserId: String, targetUserId: String): Resource<Unit> {
        return try {
            db.runTransaction {
                val myUserDto = it.get(db.collection(COLLECTION_PROFILE_PATH).document(myUserId)).toObject(UserDto::class.java)
                val targetUserDto = it.get(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId)).toObject(UserDto::class.java)

                val isMyUserAlreadyFollowing = myUserDto?.followingUserIds?.contains(targetUserId) == true
                val isTargetUserAlreadyFollowed = targetUserDto?.followerUserIds?.contains(myUserId) == true

                if (isMyUserAlreadyFollowing || isTargetUserAlreadyFollowed) {
                    throw IllegalStateException()
                }

                it.update(db.collection(COLLECTION_PROFILE_PATH).document(myUserId), UserDto::followingUserIds.name, FieldValue.arrayUnion(targetUserId))
                it.update(db.collection(COLLECTION_PROFILE_PATH).document(myUserId), UserDto::followingCount.name, FieldValue.increment(1))
                it.update(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId), UserDto::followerUserIds.name, FieldValue.arrayUnion(myUserId))
                it.update(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId), UserDto::followerCount.name, FieldValue.increment(1))
            }.await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun unfollowUser(myUserId: String, targetUserId: String): Resource<Unit> {
        return try {
            db.runTransaction {
                val myUserDto = it.get(db.collection(COLLECTION_PROFILE_PATH).document(myUserId)).toObject(UserDto::class.java)
                val targetUserDto = it.get(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId)).toObject(UserDto::class.java)

                val isMyUserAlreadyFollowing = myUserDto?.followingUserIds?.contains(targetUserId) == true
                val isTargetUserAlreadyFollowed = targetUserDto?.followerUserIds?.contains(myUserId) == true

                if (!isMyUserAlreadyFollowing || !isTargetUserAlreadyFollowed) {
                    throw IllegalStateException()
                }

                it.update(db.collection(COLLECTION_PROFILE_PATH).document(myUserId), UserDto::followingUserIds.name, FieldValue.arrayRemove(targetUserId))
                it.update(db.collection(COLLECTION_PROFILE_PATH).document(myUserId), UserDto::followingCount.name, FieldValue.increment(-1))
                it.update(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId), UserDto::followerUserIds.name, FieldValue.arrayRemove(myUserId))
                it.update(db.collection(COLLECTION_PROFILE_PATH).document(targetUserId), UserDto::followerCount.name, FieldValue.increment(-1))
            }.await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getFollowers(userId: String): Resource<List<String>> {
        return try {
            val result = db.collection(COLLECTION_PROFILE_PATH)
                .document(userId)
                .get()
                .await()

            val userDto = result.toObject(UserDto::class.java)
            Resource.Success(userDto?.followerUserIds ?: emptyList())
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun getFollowings(userId: String): Resource<List<String>> {
        return try {
            val result = db.collection(COLLECTION_PROFILE_PATH)
                .document(userId)
                .get()
                .await()

            val userDto = result.toObject(UserDto::class.java)
            Resource.Success(userDto?.followingUserIds ?: emptyList())
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}