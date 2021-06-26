package cu.jaco.transito.repositories.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import cu.jaco.transito.repositories.database.models.Inventory
import cu.jaco.transito.repositories.database.utils.BaseDao

@Dao
interface InventoryDao : BaseDao<Inventory> {

    @Query("SELECT * FROM inventory LIMIT 1")
    fun getInventory(): LiveData<List<Inventory>>

}