import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.habitua.data.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class UserPreferencesRepositoryTest {

    @Mock
    private lateinit var mockDataStore: DataStore<Preferences>

    private lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun setup() {MockitoAnnotations.openMocks(this)
        userPreferencesRepository = UserPreferencesRepository(mockDataStore)
    }

    //TODO: Add tests over here - do we need tests over here ?


}