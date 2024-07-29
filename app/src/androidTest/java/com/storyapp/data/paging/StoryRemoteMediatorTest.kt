package com.storyapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.storyapp.data.local.StoryDatabase
import com.storyapp.data.local.model.Story
import com.storyapp.data.remote.retrofit.ApiService
import com.storyapp.di.testModule
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.get

@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest: KoinTest {
    private lateinit var mockApi: ApiService
    private lateinit var mockDb: StoryDatabase

    @Before
    fun setup() {
        loadKoinModules(testModule)
        mockApi = get()
        mockDb = get()
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }

    @Test
    fun refreshLoadReturnSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi
        )

        val pagingState = PagingState<Int, Story>(listOf(), null, PagingConfig(5), 10)
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}