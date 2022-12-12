package gq.kirmanak.mealient.data.recipes.impl

import com.google.common.truth.Truth.assertThat
import gq.kirmanak.mealient.data.recipes.RecipeRepo
import gq.kirmanak.mealient.data.recipes.db.RecipeStorage
import gq.kirmanak.mealient.data.recipes.network.RecipeDataSource
import gq.kirmanak.mealient.test.BaseUnitTest
import gq.kirmanak.mealient.test.RecipeImplTestData.CAKE_FULL_RECIPE_INFO
import gq.kirmanak.mealient.test.RecipeImplTestData.FULL_CAKE_INFO_ENTITY
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeRepoTest : BaseUnitTest() {

    @MockK(relaxUnitFun = true)
    lateinit var storage: RecipeStorage

    @MockK
    lateinit var dataSource: RecipeDataSource

    @MockK
    lateinit var remoteMediator: RecipesRemoteMediator

    @MockK(relaxUnitFun = true)
    lateinit var pagingSourceFactory: RecipePagingSourceFactory

    lateinit var subject: RecipeRepo

    @Before
    override fun setUp() {
        super.setUp()
        subject = RecipeRepoImpl(remoteMediator, storage, pagingSourceFactory, dataSource, logger)
    }

    @Test
    fun `when loadRecipeInfo expect return value from data source`() = runTest {
        coEvery { storage.queryRecipeInfo(eq("1")) } returns FULL_CAKE_INFO_ENTITY
        val actual = subject.loadRecipeInfo("1")
        assertThat(actual).isEqualTo(FULL_CAKE_INFO_ENTITY)
    }

    @Test
    fun `when refreshRecipeInfo expect call to storage`() = runTest {
        coEvery { dataSource.requestRecipeInfo(eq("cake")) } returns CAKE_FULL_RECIPE_INFO
        subject.refreshRecipeInfo("cake")
        coVerify { storage.saveRecipeInfo(eq(CAKE_FULL_RECIPE_INFO)) }
    }

    @Test
    fun `when clearLocalData expect call to storage`() = runTest {
        subject.clearLocalData()
        coVerify { storage.clearAllLocalData() }
    }

    @Test
    fun `when updateNameQuery expect sets query in paging source factory`() {
        subject.updateNameQuery("query")
        verify { pagingSourceFactory.setQuery("query") }
    }
}