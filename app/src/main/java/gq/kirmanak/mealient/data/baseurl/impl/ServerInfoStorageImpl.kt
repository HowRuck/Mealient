package gq.kirmanak.mealient.data.baseurl.impl

import androidx.datastore.preferences.core.Preferences
import gq.kirmanak.mealient.data.baseurl.ServerInfoStorage
import gq.kirmanak.mealient.data.storage.PreferencesStorage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ServerInfoStorageImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage,
) : ServerInfoStorage {

    private val baseUrlKey: Preferences.Key<String>
        get() = preferencesStorage.baseUrlKey

    private val versionKey: Preferences.Key<Int>
        get() = preferencesStorage.versionKey

    override val baseUrlFlow: Flow<String?>
        get() = preferencesStorage.valueUpdates(baseUrlKey)

    override val versionFlow: Flow<Int?>
        get() = preferencesStorage.valueUpdates(versionKey)

    override suspend fun getBaseURL(): String? = getValue(baseUrlKey)

    override suspend fun storeBaseURL(baseURL: String?) {
        if (baseURL == null) {
            preferencesStorage.removeValues(baseUrlKey)
        } else {
            preferencesStorage.storeValues(Pair(baseUrlKey, baseURL))
        }
    }

    override suspend fun getVersion(): Int? = getValue(versionKey)

    override suspend fun storeVersion(version: Int?) {
        if (version == null) {
            preferencesStorage.removeValues(versionKey)
        } else {
            preferencesStorage.storeValues(Pair(versionKey, version))
        }
    }

    private suspend fun <T> getValue(key: Preferences.Key<T>): T? = preferencesStorage.getValue(key)
}