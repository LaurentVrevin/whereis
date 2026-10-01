package com.laurentvrevin.wheris.data.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laurentvrevin.wheris.core.database.WherisDatabase
import com.laurentvrevin.wheris.core.database.entity.PinEntity
import com.laurentvrevin.wheris.core.model.Category
import com.laurentvrevin.wheris.core.model.CategoryColorKey
import com.laurentvrevin.wheris.core.model.CategoryIconKey
import com.laurentvrevin.wheris.core.model.CategoryId
import com.laurentvrevin.wheris.core.model.SystemCategoryIds
import com.laurentvrevin.wheris.domain.repository.CategoryMutationResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoryMutationPersistenceTest {
    private lateinit var database: WherisDatabase
    private lateinit var repository: CategoryRepositoryImpl
    private lateinit var source: Category
    private val sourceId: CategoryId get() = source.id
    private val operationTime = 9000L

    @Before
    fun setUp() =
        runBlocking {
            database =
                Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext<Context>(), WherisDatabase::class.java)
                    .addCallback(WherisDatabase.getCallback()).build()
            repository = CategoryRepositoryImpl(database.categoryDao(), clock = { operationTime })
            source = repository.createCustomCategory("Source", CategoryIconKey.PLACE, CategoryColorKey.ORANGE)
        }

    @After
    fun tearDown() = database.close()

    private suspend fun addPlaces(count: Int): List<PinEntity> =
        (1..count).map { index ->
            PinEntity("pin-$index", 10.0, 20.0, sourceId.value, 7f, 35.0, index.toLong(), 2000L, "Nom $index", "Note $index")
                .also { database.pinDao().insertPin(it) }
        }

    private suspend fun assertPreserved(original: List<PinEntity>) {
        assertEquals(original.sortedBy { it.id }, database.pinDao().observePins().first().sortedBy { it.id })
        assertEquals(sourceId.value, database.categoryDao().getCategoryById(sourceId.value)?.id)
        database.openHelper.writableDatabase.query("PRAGMA foreign_key_check").use { assertEquals(0, it.count) }
    }

    private suspend fun assertReassigned(
        original: List<PinEntity>,
        target: CategoryId,
    ) {
        assertNull(database.categoryDao().getCategoryById(sourceId.value))
        assertEquals(
            original.map { it.copy(categoryId = target.value, updatedAtEpochMillis = operationTime) }.sortedBy { it.id },
            database.pinDao().observePins().first().sortedBy { it.id },
        )
        database.openHelper.writableDatabase.query("PRAGMA foreign_key_check").use { assertEquals(0, it.count) }
    }

    @Test
    fun unusedDeletionPreservesOtherCategoriesAndPlaces() =
        runBlocking {
            val unrelated = PinEntity("unrelated", 10.0, 20.0, SystemCategoryIds.CAR.value, null, null, 1000L, 2000L)
            database.pinDao().insertPin(unrelated)
            val before = repository.observeCategories().first()
            assertEquals(CategoryMutationResult.SUCCESS, repository.deleteCustomCategory(sourceId))
            assertEquals(before.filterNot { it.id == sourceId }, repository.observeCategories().first())
            assertEquals(listOf(unrelated), database.pinDao().observePins().first())
        }

    @Test
    fun onePlaceCanBeReassignedToOtherWithoutLosingAnyFields() =
        runBlocking {
            val original = addPlaces(1)
            assertEquals(CategoryMutationResult.SUCCESS, repository.reassignAndDeleteCustomCategory(sourceId, SystemCategoryIds.OTHER))
            assertReassigned(original, SystemCategoryIds.OTHER)
        }

    @Test
    fun allPlacesAreReassignedWithOneTimestamp() =
        runBlocking {
            val original = addPlaces(4)
            assertEquals(CategoryMutationResult.SUCCESS, repository.reassignAndDeleteCustomCategory(sourceId, SystemCategoryIds.OTHER))
            assertReassigned(original, SystemCategoryIds.OTHER)
        }

    @Test
    fun customReplacementIsValidAndRemainsUnchanged() =
        runBlocking {
            val original = addPlaces(2)
            val target = repository.createCustomCategory("Target", CategoryIconKey.PARK, CategoryColorKey.GREEN)
            assertEquals(CategoryMutationResult.SUCCESS, repository.reassignAndDeleteCustomCategory(sourceId, target.id))
            assertReassigned(original, target.id)
            assertEquals(target, repository.observeCategories().first().last())
        }

    @Test
    fun missingReplacementLeavesSourceAndPlacesUnchanged() =
        runBlocking {
            val original = addPlaces(2)
            val categories = repository.observeCategories().first()
            assertEquals(
                CategoryMutationResult.REPLACEMENT_NOT_FOUND,
                repository.reassignAndDeleteCustomCategory(sourceId, CategoryId("missing")),
            )
            assertPreserved(original)
            assertEquals(categories, repository.observeCategories().first())
        }

    @Test
    fun everySystemCategoryRejectsUpdateDeletionAndReassignment() =
        runBlocking {
            val original = addPlaces(1)
            val categories = repository.observeCategories().first()
            SystemCategoryIds.ALL.forEach { id ->
                assertEquals(CategoryMutationResult.SYSTEM_PROTECTED, repository.deleteCustomCategory(id))
                assertEquals(CategoryMutationResult.SYSTEM_PROTECTED, repository.reassignAndDeleteCustomCategory(id, sourceId))
                assertEquals(
                    CategoryMutationResult.SYSTEM_PROTECTED,
                    repository.updateCustomCategory(id, "Changed", CategoryIconKey.PARK, CategoryColorKey.PURPLE),
                )
            }
            assertPreserved(original)
            assertEquals(categories, repository.observeCategories().first())
        }

    @Test
    fun identicalReplacementLeavesSourceAndPlacesUnchanged() =
        runBlocking {
            val original = addPlaces(2)
            assertEquals(CategoryMutationResult.SAME_CATEGORY, repository.reassignAndDeleteCustomCategory(sourceId, sourceId))
            assertPreserved(original)
        }

    @Test
    fun usedDeletionIsRefusedAndForeignKeyStillRestrictsRawDeletion() =
        runBlocking {
            val original = addPlaces(1)
            assertEquals(CategoryMutationResult.IN_USE, repository.deleteCustomCategory(sourceId))
            assertThrows(SQLiteConstraintException::class.java) {
                database.openHelper.writableDatabase.execSQL("DELETE FROM categories WHERE id = ?", arrayOf(sourceId.value))
            }
            assertPreserved(original)
        }

    @Test
    fun otherIsSeededAndProtectedEvenWhenUnused() =
        runBlocking {
            assertEquals(true, database.categoryDao().getCategoryById(SystemCategoryIds.OTHER.value)?.isSystem)
            assertEquals(0, repository.getCategoryUsageCount(SystemCategoryIds.OTHER))
            assertEquals(CategoryMutationResult.SYSTEM_PROTECTED, repository.deleteCustomCategory(SystemCategoryIds.OTHER))
        }

    @Test
    fun failureAfterReassignmentRollsBackCategoryPlacesAndTimestamps() =
        runBlocking {
            val original = addPlaces(3)
            val categories = repository.observeCategories().first()
            // This trigger aborts deletion only AFTER reassignment has actually removed every source reference.
            database.openHelper.writableDatabase.execSQL(
                "CREATE TRIGGER fail_category_delete BEFORE DELETE ON categories " +
                    "WHEN OLD.id = '${sourceId.value}' AND NOT EXISTS (SELECT 1 FROM pins WHERE categoryId = OLD.id) " +
                    "BEGIN SELECT RAISE(ABORT, 'synthetic transaction failure'); END",
            )
            assertEquals(
                CategoryMutationResult.TECHNICAL_FAILURE,
                repository.reassignAndDeleteCustomCategory(sourceId, SystemCategoryIds.OTHER),
            )
            assertPreserved(original)
            assertEquals(categories, repository.observeCategories().first())
            database.openHelper.writableDatabase.execSQL("DROP TRIGGER fail_category_delete")
            assertEquals(CategoryMutationResult.SUCCESS, repository.reassignAndDeleteCustomCategory(sourceId, SystemCategoryIds.OTHER))
            assertReassigned(original, SystemCategoryIds.OTHER)
        }

    @Test
    fun missingSourceReturnsNotFoundWithoutChangingAnyData() =
        runBlocking {
            val original = addPlaces(1)
            val missing = CategoryId("missing")
            assertEquals(CategoryMutationResult.NOT_FOUND, repository.deleteCustomCategory(missing))
            assertEquals(CategoryMutationResult.NOT_FOUND, repository.reassignAndDeleteCustomCategory(missing, SystemCategoryIds.OTHER))
            assertEquals(
                CategoryMutationResult.NOT_FOUND,
                repository.updateCustomCategory(missing, "Name", CategoryIconKey.PARK, CategoryColorKey.GREEN),
            )
            assertEquals(0, repository.getCategoryUsageCount(missing))
            assertPreserved(original)
        }

    @Test
    fun updatingPresentationPreservesIdentityCreationTimeAndPlacesAndEmitsNewCategory() =
        runBlocking {
            val original = addPlaces(2)
            val category = repository.observeCategories().first().last()
            assertEquals(
                CategoryMutationResult.SUCCESS,
                repository.updateCustomCategory(sourceId, "  散歩 — Promenades  ", CategoryIconKey.PARK, CategoryColorKey.PURPLE),
            )
            assertEquals(
                category.copy(name = "散歩 — Promenades", iconKey = CategoryIconKey.PARK, colorKey = CategoryColorKey.PURPLE),
                repository.observeCategories().first().last(),
            )
            assertPreserved(original)
            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.updateCustomCategory(sourceId, " \t\n", CategoryIconKey.PLACE, CategoryColorKey.ORANGE) }
            }
            assertPreserved(original)
        }

    @Test
    fun usageCountIsExactForZeroOneAndManyPlaces() =
        runBlocking {
            assertEquals(0, repository.getCategoryUsageCount(sourceId))
            addPlaces(1)
            assertEquals(1, repository.getCategoryUsageCount(sourceId))
            database.pinDao().insertPin(database.pinDao().observePins().first().single().copy(id = "second"))
            database.pinDao().insertPin(database.pinDao().observePins().first().first().copy(id = "third"))
            assertEquals(3, repository.getCategoryUsageCount(sourceId))
            assertEquals(0, repository.getCategoryUsageCount(SystemCategoryIds.OTHER))
        }
}
