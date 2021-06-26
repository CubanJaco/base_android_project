package cu.jaco.transito.repositories.database.utils

import androidx.room.*

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: T): Long

    @Update
    suspend fun update(obj: T)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(obj: List<T>): List<Long>

    @Update
    suspend fun updateAll(obj: List<T>)

    @Delete
    suspend fun delete(obj: T)

    @Delete
    suspend fun deleteAll(obj: List<T>)

}

@Transaction
suspend inline fun <T> BaseDao<T>.upsertAll(obj: List<T>) {
    val resp = insertAll(obj)

    val update = arrayListOf<T>()

    resp.forEachIndexed { i, it ->
        if (it == -1L)
            update.add(obj[i])
    }

    if (update.isNotEmpty())
        updateAll(update)

}

@Transaction
suspend inline fun <T> BaseDao<T>.upsert(obj: T) {
    val resp = insert(obj)
    if (resp == -1L)
        update(obj)
}