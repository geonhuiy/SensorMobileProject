package com.example.funplus

import android.graphics.drawable.Drawable

class Prize (val type: String, val img : Drawable, val count: Int)


/*
* @Entity(foreignKeys = [(ForeignKey(
     entity = UserInfo::class,
    parentColumns = ["uid"],
    childColumns = ["user"]))])
data class ContactInfo(val user: Int,
                       val type: String,
                       @PrimaryKey
                       val value: String
                       )


@Dao
interface ContactInfoDao {
    @Query("SELECT * FROM contactinfo")
    fun getAll(): LiveData<List<ContactInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contactInfo: ContactInfo): Long

    @Update
    fun update(contactInfo: ContactInfo)

    @Query("SELECT * FROM contactinfo WHERE contactinfo.user = :userid")
    fun getUserContacts(userid: Int): LiveData<List<ContactInfo>>

    @Query("SELECT * FROM contactinfo WHERE contactinfo.type = :type AND contactinfo.user = :userid")
    fun getUserContact(userid: Int, type: String): ContactInfo
}
* */