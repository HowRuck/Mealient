package gq.kirmanak.mealient.data.baseurl

import gq.kirmanak.mealient.datasource.ServerUrlProvider
import gq.kirmanak.mealient.datasource.models.VersionResponse
import gq.kirmanak.mealient.logging.Logger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ServerInfoRepoImpl @Inject constructor(
    private val serverInfoStorage: ServerInfoStorage,
    private val versionDataSource: VersionDataSource,
    private val logger: Logger,
) : ServerInfoRepo, ServerUrlProvider {

    override val baseUrlFlow: Flow<String?>
        get() = serverInfoStorage.baseUrlFlow

    override val versionFlow: Flow<Int?>
        get() = serverInfoStorage.versionFlow

    override suspend fun getUrl(): String? {
        val result = serverInfoStorage.getBaseURL()
        logger.v { "getUrl() returned: $result" }
        return result
    }

    override suspend fun getVersion(): Int {
        var result: Int? = serverInfoStorage.getVersion()
        logger.v { "getVersion() returned: $result" }

        if (result == null) {
            // If the version number is initially not available, try to get it from the server
            // This is done, so that the version number is fetched on apps not running without
            // cleared data, as `tryBaseURL` is called upon url change/input
            tryBaseURL(getUrl() ?: return -1)
            result = serverInfoStorage.getVersion()
            // If the version number is still not available, throw an exception
            if (result == null) {
                throw IllegalStateException("Version number is not available")
            }
        }

        return result
    }

    override suspend fun tryBaseURL(baseURL: String): Result<VersionResponse> {
        return versionDataSource.runCatching {
            requestVersion(baseURL)
        }.onSuccess {
            serverInfoStorage.storeBaseURL(baseURL)

            // Store the major version of mealie
            val majorVersion: Int? = it.version
                .removePrefix("v").split(".").firstOrNull()?.toIntOrNull()

            serverInfoStorage.storeVersion(majorVersion)
        }
    }
}