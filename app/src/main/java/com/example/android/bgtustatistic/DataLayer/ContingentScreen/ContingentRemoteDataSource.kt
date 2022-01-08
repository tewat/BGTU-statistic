package com.example.android.bgtustatistic.DataLayer.ContingentScreen

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ContingentRemoteDataSource(
    private val contingentApi: ContingentApi,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getContingent(token: String) =
        withContext(ioDispatcher){
            contingentApi.getContingent(token)
        }
}