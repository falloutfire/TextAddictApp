package com.textaddict.app.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.textaddict.app.database.converter.Converters
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("userId"),
        childColumns = arrayOf("user_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Article(
    var title: String?,
    var domain: String?,
    var fullPath: String?,
    @TypeConverters(Converters::class)
    var date: Date?,
    var content: String?,
    var archive: Boolean = false,
    @ColumnInfo(name = "user_id")
    var userId: Long
) : ArticleType() {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        Date(parcel.readString()),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readLong()
    ) {
        id = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(domain)
        parcel.writeString(fullPath)
        parcel.writeString(content)
        parcel.writeByte(if (archive) 1 else 0)
        parcel.writeLong(userId)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Article> {
        override fun createFromParcel(parcel: Parcel): Article {
            return Article(parcel)
        }

        override fun newArray(size: Int): Array<Article?> {
            return arrayOfNulls(size)
        }
    }
}

data class ArchiveItem(var listTitles: String?) : ArticleType() {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(listTitles)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ArchiveItem> {
        override fun createFromParcel(parcel: Parcel): ArchiveItem {
            return ArchiveItem(parcel)
        }

        override fun newArray(size: Int): Array<ArchiveItem?> {
            return arrayOfNulls(size)
        }
    }
}

abstract class ArticleType : Parcelable {
    companion object {
        val ARTICLE_TYPE = 0
        val ARCHIEVE_TYPE = 1
        var isActivated = false
    }
}