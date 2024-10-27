package gq.kirmanak.mealient.data.baseurl

import gq.kirmanak.mealient.datasource.models.VersionResponse
import kotlinx.coroutines.flow.Flow

interface ServerInfoRepo {

    val baseUrlFlow: Flow<String?>

    val versionFlow: Flow<Int?>

    suspend fun getUrl(): String?

    suspend fun getVersion(): Int?

    suspend fun tryBaseURL(baseURL: String): Result<VersionResponse>

}

