package com.example.android.bgtustatistic.DataLayer.PerformanceScreen

import com.example.android.bgtustatistic.DataLayer.PerformanceScreen.DebtApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DebtRemoteDataSource(
    private val debtApi: DebtApi,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun getDebts(token: String) =
        withContext(ioDispatcher){
            debtApi.getDebts(token)
        }

    suspend fun getDebtsById(token: String, id: Int) =
        withContext(ioDispatcher){
            debtApi.getDebtsById(token, id)
        }
}