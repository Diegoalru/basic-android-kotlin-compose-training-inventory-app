package com.example.inventory

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventory.data.InventoryDatabase
import com.example.inventory.data.ItemDao
import org.junit.Before
import org.junit.runner.RunWith
import android.content.Context
import com.example.inventory.data.Item
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import kotlin.jvm.Throws


@RunWith(AndroidJUnit4::class)
class ItemDaoTest {
    private lateinit var itemDao: ItemDao
    private lateinit var inventoryDatabase: InventoryDatabase

    @Before
    fun createDatabase() {
        val context: Context = ApplicationProvider.getApplicationContext()
        inventoryDatabase = Room.inMemoryDatabaseBuilder(context, InventoryDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        itemDao = inventoryDatabase.itemDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        inventoryDatabase.close()
    }

    private var apples = Item(1, "Apples", 10.0, 20)
    private var bananas = Item(2, "Bananas", 15.0, 97)

    private suspend fun addOneItemToDb() {
        itemDao.insert(apples)
    }

    private suspend fun addTwoItemsToDb() {
        itemDao.insert(apples)
        itemDao.insert(bananas)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsItemIntoDB() {
        runBlocking {
            addOneItemToDb()
            val allItems = itemDao.getAllItems().first()
            assertEquals(apples, allItems[0])
        }
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()
        val allItems = itemDao.getAllItems().first()
        assertEquals(listOf(apples, bananas), allItems)
    }

    /**
     * This test changes the all properties of the items.
     */
    @Test
    @Throws(Exception::class)
    fun daoUpdateItem_updateItemInDB() {
        runBlocking {
            addOneItemToDb()
            val newApples = Item(1, "Apples", 15.0, 25)
            itemDao.update(newApples)
            val applesFromDB = itemDao.getItem(apples.id).first()
            assertEquals(newApples, applesFromDB)
        }
    }

    @Test
    @Throws(Exception::class)
    fun daoUpdatesItems_updatesItemsInDB() {
        runBlocking {
            addTwoItemsToDb()
            val newApples = Item(1, "Apples", 15.0, 25)
            val newBananas = Item(2, "Bananas", 20.0, 50)
            itemDao.update(newApples)
            itemDao.update(newBananas)
            val applesFromDB = itemDao.getAllItems().first()
            assertEquals(listOf(newApples, newBananas), applesFromDB)
        }
    }

    @Test
    @Throws(Exception::class)
    fun daoDeleteItem_deletesItemFromDB() {
        runBlocking {
            addOneItemToDb()
            itemDao.delete(apples)
            val allItems = itemDao.getAllItems().first()
            assertEquals(emptyList<Item>(), allItems)
        }
    }

    @Test
    @Throws(Exception::class)
    fun daoGetItem_returnsItemFromDB() {
        runBlocking {
            addOneItemToDb()
            val itemFromDB = itemDao.getItem(apples.id).first()
            assertEquals(apples, itemFromDB)
        }
    }
}
