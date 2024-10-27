package gq.kirmanak.mealient.data.baseurl

import kotlinx.coroutines.flow.Flow

interface ServerInfoStorage {

    val baseUrlFlow: Flow<String?>

    val versionFlow: Flow<Int?>

    suspend fun getBaseURL(): String?

    suspend fun storeBaseURL(baseURL: String?)

    suspend fun getVersion(): Int?

    suspend fun storeVersion(version: Int?)
}