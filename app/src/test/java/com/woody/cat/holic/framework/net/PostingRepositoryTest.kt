package com.woody.cat.holic.framework.net

import com.google.firebase.firestore.*
import com.woody.cat.holic.data.common.PostingOrder
import com.woody.cat.holic.data.common.PostingType
import com.woody.cat.holic.data.common.Resource
import com.woody.cat.holic.framework.COLLECTION_POSTING_PATH
import com.woody.cat.holic.framework.net.dto.PostingDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.*

@ExperimentalCoroutinesApi
class PostingRepositoryTest {

    companion object {
        const val FAKE_USER_ID = "1234"
        const val FAKE_POSTING_ID = "5678"
        const val FAKE_PAGE_SIZE = 10
        const val FAKE_PAGE_KEY = "ABCD"
    }

    private val db = mock(FirebaseFirestore::class.java)
    private val postingRepository = FirebaseFirestorePostingRepository(db)

    @Before
    fun clearRepository() {
        reset(db)
    }

    @Test
    fun testGetPostingOrder() {
        val postingOrder1 = postingRepository.getPostingOrder(PostingType.GALLERY)
        assert(postingOrder1 == postingRepository.currentGalleryPostingOrder)

        val postingOrder2 = postingRepository.getPostingOrder(PostingType.LIKED)
        assert(postingOrder2 == postingRepository.currentLikePostingOrder)

        val postingOrder3 = postingRepository.getPostingOrder(PostingType.USER)
        assert(postingOrder3 == PostingOrder.LIKED)
    }

    @Test
    fun testChangeToNextGalleryPostingOrder() {
        postingRepository.currentGalleryPostingOrder = PostingOrder.LIKED

        postingRepository.changeToNextPostingOrder(PostingType.GALLERY)
        assert(postingRepository.currentGalleryPostingOrder == PostingOrder.CREATED)

        postingRepository.changeToNextPostingOrder(PostingType.GALLERY)
        assert(postingRepository.currentGalleryPostingOrder == PostingOrder.RANDOM)

        postingRepository.changeToNextPostingOrder(PostingType.GALLERY)
        assert(postingRepository.currentGalleryPostingOrder == PostingOrder.LIKED)
    }

    @Test
    fun testChangeToNextLikePostingOrder() {
        postingRepository.currentLikePostingOrder = PostingOrder.CREATED

        postingRepository.changeToNextPostingOrder(PostingType.LIKED)
        assert(postingRepository.currentLikePostingOrder == PostingOrder.RANDOM)

        postingRepository.changeToNextPostingOrder(PostingType.LIKED)
        assert(postingRepository.currentLikePostingOrder == PostingOrder.LIKED)

        postingRepository.changeToNextPostingOrder(PostingType.LIKED)
        assert(postingRepository.currentLikePostingOrder == PostingOrder.CREATED)
    }

    @Test
    fun testChangeToNextUserPostingOrder() {
        postingRepository.changeToNextPostingOrder(PostingType.USER)
    }

    @Test
    fun testAddPostingSuccess() = runBlockingTest {

        given(db.runTransaction(any<Transaction.Function<Any>>())).willReturn(FakeSuccessTask(Any()))

        val result = postingRepository.addPosting(FAKE_USER_ID, emptyList())

        assert(result is Resource.Success)
    }

    @Test
    fun testAddPostingFail() = runBlockingTest {

        given(db.runTransaction(any<Transaction.Function<Any>>())).willReturn(FakeFailTask(Any()))

        val result = postingRepository.addPosting(FAKE_USER_ID, emptyList())

        assert(result is Resource.Error)
    }

    @Test
    fun testRemovePostingSuccess() = runBlockingTest {
        given(db.runTransaction(any<Transaction.Function<Any>>())).willReturn(FakeSuccessTask(Any()))

        val result = postingRepository.removePosting(FAKE_USER_ID, FAKE_POSTING_ID)

        assert(result is Resource.Success)
    }

    @Test
    fun testRemovePostingFail() = runBlockingTest {
        given(db.runTransaction(any<Transaction.Function<Any>>())).willReturn(FakeFailTask(Any()))

        val result = postingRepository.removePosting(FAKE_USER_ID, FAKE_POSTING_ID)

        assert(result is Resource.Error)
    }

    @Test
    fun testGetGalleryPostingsWithKeyDocNotFound() = runBlockingTest {
        given(db.collection(COLLECTION_POSTING_PATH)).willThrow(RuntimeException())
        val key = FAKE_PAGE_KEY
        val pageSize = FAKE_PAGE_SIZE

        val result = postingRepository.getGalleryPostings(key, pageSize)

        assert(result is Resource.Error && result.exception is IllegalStateException)
    }

    @Test
    fun testGetGalleryPostingsWithQueryError() = runBlockingTest {
        given(db.collection(COLLECTION_POSTING_PATH)).willThrow(RuntimeException())
        val key = null
        val pageSize = FAKE_PAGE_SIZE

        val result = postingRepository.getGalleryPostings(key, pageSize)

        assert(result is Resource.Error && result.exception is RuntimeException)
    }

    @Test
    fun testGetGalleryPostingsWithAllCase() = runBlockingTest {
        val postingOrders = PostingOrder.values()
        val keys = listOf(null, FAKE_PAGE_KEY)
        postingOrders.forEach { postingOrder ->
            keys.forEach { key ->
                val pageSize = FAKE_PAGE_SIZE
                postingRepository.currentGalleryPostingOrder = postingOrder
                val collectionReference = mock(CollectionReference::class.java)
                val querySnapshot = mock(QuerySnapshot::class.java)
                val documentSnapshot = mock(DocumentSnapshot::class.java)
                val task = FakeSuccessTask(querySnapshot)
                val query = mock(Query::class.java)

                given(db.collection(COLLECTION_POSTING_PATH)).willReturn(collectionReference)
                given(collectionReference.whereEqualTo(eq(PostingDto::deleted.name), any())).willReturn(query)
                if (postingOrder == PostingOrder.RANDOM) {
                    given(query.orderBy(any(String::class.java), any())).willThrow(RuntimeException())
                } else {
                    given(query.orderBy(any(String::class.java), any())).willReturn(query)
                }
                if (key == null) {
                    given(query.startAfter(any())).willThrow(RuntimeException())
                } else {
                    val documentReference = mock(DocumentReference::class.java)
                    given(collectionReference.document(any())).willReturn(documentReference)
                    given(documentReference.get()).willReturn(FakeSuccessTask(documentSnapshot))
                    given(query.startAfter(any())).willReturn(query)
                }
                given(query.limit(any(Long::class.java))).willReturn(query)
                given(query.get()).willReturn(task)

                given(querySnapshot.documents).willReturn(listOf(documentSnapshot))

                val result = postingRepository.getGalleryPostings(key, pageSize)

                assert(result is Resource.Success)

                reset(db)
            }
        }
    }

    @Test
    fun testGetUserLikePostingsWithKeyDocNotFound() = runBlockingTest {
        given(db.collection(COLLECTION_POSTING_PATH)).willThrow(RuntimeException())
        val key = FAKE_PAGE_KEY
        val userId = FAKE_USER_ID
        val pageSize = FAKE_PAGE_SIZE

        val result = postingRepository.getUserLikePostings(key, userId, pageSize)

        assert(result is Resource.Error && result.exception is IllegalStateException)
    }

    @Test
    fun testGetUserLikePostingsWithQueryError() = runBlockingTest {
        given(db.collection(COLLECTION_POSTING_PATH)).willThrow(RuntimeException())
        val key = null
        val userId = FAKE_USER_ID
        val pageSize = FAKE_PAGE_SIZE

        val result = postingRepository.getUserLikePostings(key, userId, pageSize)

        assert(result is Resource.Error && result.exception is RuntimeException)
    }

    @Test
    fun testGetUserLikePostingsWithAllCase() = runBlockingTest {
        val postingOrders = PostingOrder.values()
        val keys = listOf(null, FAKE_PAGE_KEY)
        val userId = FAKE_USER_ID
        postingOrders.forEach { postingOrder ->
            keys.forEach { key ->
                val pageSize = FAKE_PAGE_SIZE
                postingRepository.currentLikePostingOrder = postingOrder
                val collectionReference = mock(CollectionReference::class.java)
                val querySnapshot = mock(QuerySnapshot::class.java)
                val documentSnapshot = mock(DocumentSnapshot::class.java)
                val task = FakeSuccessTask(querySnapshot)
                val query = mock(Query::class.java)

                given(db.collection(COLLECTION_POSTING_PATH)).willReturn(collectionReference)
                given(collectionReference.whereEqualTo(eq(PostingDto::deleted.name), any())).willReturn(query)
                given(query.whereArrayContains(eq(PostingDto::likedUserIds.name), any())).willReturn(query)
                if (postingOrder == PostingOrder.RANDOM) {
                    given(query.orderBy(any(String::class.java), any())).willThrow(RuntimeException())
                } else {
                    given(query.orderBy(any(String::class.java), any())).willReturn(query)
                }
                if (key == null) {
                    given(query.startAfter(any())).willThrow(RuntimeException())
                } else {
                    val documentReference = mock(DocumentReference::class.java)
                    given(collectionReference.document(any())).willReturn(documentReference)
                    given(documentReference.get()).willReturn(FakeSuccessTask(documentSnapshot))
                    given(query.startAfter(any())).willReturn(query)
                }
                given(query.limit(any(Long::class.java))).willReturn(query)
                given(query.get()).willReturn(task)

                given(querySnapshot.documents).willReturn(listOf(documentSnapshot))

                val result = postingRepository.getUserLikePostings(key, userId, pageSize)

                assert(result is Resource.Success)

                reset(db)
            }
        }
    }

    @Test
    fun testGetUserUploadedPostingsWithKeyDocNotFound() = runBlockingTest {
        given(db.collection(COLLECTION_POSTING_PATH)).willThrow(RuntimeException())
        val key = FAKE_PAGE_KEY
        val userId = FAKE_USER_ID
        val pageSize = FAKE_PAGE_SIZE

        val result = postingRepository.getUserUploadedPostings(key, userId, pageSize)

        assert(result is Resource.Error && result.exception is IllegalStateException)
    }

    @Test
    fun testGetUserUploadedPostingsWithQueryError() = runBlockingTest {
        given(db.collection(COLLECTION_POSTING_PATH)).willThrow(RuntimeException())
        val key = null
        val userId = FAKE_USER_ID
        val pageSize = FAKE_PAGE_SIZE

        val result = postingRepository.getUserUploadedPostings(key, userId, pageSize)

        assert(result is Resource.Error && result.exception is RuntimeException)
    }

    @Test
    fun testGetUserUploadedPostingsWithAllCase() = runBlockingTest {
        val keys = listOf(null, FAKE_PAGE_KEY)
        val userId = FAKE_USER_ID
        keys.forEach { key ->
            val pageSize = FAKE_PAGE_SIZE
            val collectionReference = mock(CollectionReference::class.java)
            val querySnapshot = mock(QuerySnapshot::class.java)
            val documentSnapshot = mock(DocumentSnapshot::class.java)
            val task = FakeSuccessTask(querySnapshot)
            val query = mock(Query::class.java)

            given(db.collection(COLLECTION_POSTING_PATH)).willReturn(collectionReference)
            given(collectionReference.whereEqualTo(eq(PostingDto::deleted.name), any())).willReturn(query)
            given(query.whereEqualTo(eq(PostingDto::userId.name), any())).willReturn(query)
            given(query.orderBy(any(String::class.java), any())).willReturn(query)
            if (key == null) {
                given(query.startAfter(any())).willThrow(RuntimeException())
            } else {
                val documentReference = mock(DocumentReference::class.java)
                given(collectionReference.document(any())).willReturn(documentReference)
                given(documentReference.get()).willReturn(FakeSuccessTask(documentSnapshot))
                given(query.startAfter(any())).willReturn(query)
            }
            given(query.limit(any(Long::class.java))).willReturn(query)
            given(query.get()).willReturn(task)

            given(querySnapshot.documents).willReturn(listOf(documentSnapshot))

            val result = postingRepository.getUserUploadedPostings(key, userId, pageSize)

            assert(result is Resource.Success)

            reset(db)
        }
    }

    @Test
    fun testGetUserPostingsWithKeyDocNotFound() = runBlockingTest {
        given(db.collection(COLLECTION_POSTING_PATH)).willThrow(RuntimeException())
        val key = FAKE_PAGE_KEY
        val userId = FAKE_USER_ID
        val pageSize = FAKE_PAGE_SIZE

        val result = postingRepository.getUserPostings(key, userId, pageSize)

        assert(result is Resource.Error && result.exception is IllegalStateException)
    }

    @Test
    fun testGetUserPostingsWithQueryError() = runBlockingTest {
        given(db.collection(COLLECTION_POSTING_PATH)).willThrow(RuntimeException())
        val key = null
        val userId = FAKE_USER_ID
        val pageSize = FAKE_PAGE_SIZE

        val result = postingRepository.getUserPostings(key, userId, pageSize)

        assert(result is Resource.Error && result.exception is RuntimeException)
    }

    @Test
    fun testGetUserPostingsWithAllCase() = runBlockingTest {
        val keys = listOf(null, FAKE_PAGE_KEY)
        val userId = FAKE_USER_ID
        keys.forEach { key ->
            val pageSize = FAKE_PAGE_SIZE
            val collectionReference = mock(CollectionReference::class.java)
            val querySnapshot = mock(QuerySnapshot::class.java)
            val documentSnapshot = mock(DocumentSnapshot::class.java)
            val task = FakeSuccessTask(querySnapshot)
            val query = mock(Query::class.java)

            given(db.collection(COLLECTION_POSTING_PATH)).willReturn(collectionReference)
            given(collectionReference.whereEqualTo(eq(PostingDto::deleted.name), any())).willReturn(query)
            given(query.whereEqualTo(eq(PostingDto::userId.name), any())).willReturn(query)
            given(query.orderBy(any(String::class.java), any())).willReturn(query)
            if (key == null) {
                given(query.startAfter(any())).willThrow(RuntimeException())
            } else {
                val documentReference = mock(DocumentReference::class.java)
                given(collectionReference.document(any())).willReturn(documentReference)
                given(documentReference.get()).willReturn(FakeSuccessTask(documentSnapshot))
                given(query.startAfter(any())).willReturn(query)
            }
            given(query.limit(any(Long::class.java))).willReturn(query)
            given(query.get()).willReturn(task)

            given(querySnapshot.documents).willReturn(listOf(documentSnapshot))

            val result = postingRepository.getUserPostings(key, userId, pageSize)

            assert(result is Resource.Success)

            reset(db)
        }
    }
}