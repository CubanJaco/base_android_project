package cu.jaco.transito.repositories.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "inventory")
data class Inventory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "pending")
    @SerializedName("pending")
    val pending: Int,
    @ColumnInfo(name = "available")
    @SerializedName("available")
    val available: Int,
    @ColumnInfo(name = "not_available")
    @SerializedName("not_available")
    val not_available: Int
)