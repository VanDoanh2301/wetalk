package com.example.wetalk.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.example.wetalk.data.model.objectmodel.Message
import com.example.wetalk.data.model.objectmodel.MessagePaging
import com.example.wetalk.data.remote.ApiChat
import com.example.wetalk.repository.TalkRepository
import kotlinx.coroutines.delay
import java.io.IOException
import javax.inject.Inject

class MessagePagingSource(private val repository: ApiChat, val paginationInfo: MessagePaging) :
    PagingSource<Int, Message>() {
    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1) ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        val nextPage = params.key ?: 1
        paginationInfo.page = nextPage
        try {
            delay(2000)
            val response = repository.getMessagesLimit(paginationInfo)
            val messages = response.body()!! ?: emptyList()

            return LoadResult.Page(
                data = messages,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (messages.isEmpty()) null else nextPage + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}