package com.mvvm_kotlin_27.base.module.dbSQL

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import java.lang.Exception
import com.mvvm_kotlin_27.base.module.dbSQL.FSqliteDataType.Companion.PERSON_OBJ
import com.mvvm_kotlin_27.base.module.dbSQL.FSqliteDataType.Companion.PERSON_INFO

class FSqlite(context: Context) : SQLiteOpenHelper(context, FSqliteDataType.DB_NAME, null, FSqliteDataType.DB_VER)
{
    private var _dbW : SQLiteDatabase
    private var _dbR : SQLiteDatabase

    init
    {
        _dbW = this.writableDatabase
        _dbR = this.readableDatabase
        createTable()
    }

    override fun onCreate(db: SQLiteDatabase)
    {
        var tableIndex = 0
        db.rawQuery(FSqliteDataType.SELECT_TABLE, null)?.let {
            with(it) {
                while (moveToNext())
                {
                    if (it.count > 0)
                    {
                        if (it.getString(0) == PERSON_OBJ.TABLE_NAME)
                        {
                            tableIndex = tableIndex or 0x01
                        }
                    }
                }
            }
        }

        if ((tableIndex and 0x01) == 0)     db.execSQL(PERSON_OBJ.CREATE_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        db.execSQL(PERSON_OBJ.DELETE_TABLE)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        onUpgrade(db, oldVersion, newVersion)
    }
    fun createTable()
    {
        var tableIndex = 0
        _dbW.rawQuery(FSqliteDataType.SELECT_TABLE, null)?.let {
            with(it) {
                while (moveToNext())
                {
                    if (it.count > 0)
                    {
                        if (it.getString(0) == PERSON_OBJ.TABLE_NAME)
                        {
                            tableIndex = tableIndex or 0x01
                        }
                    }
                }
            }
        }

        if ((tableIndex and 0x01) == 0)     _dbW.execSQL(PERSON_OBJ.CREATE_TABLE)
    }
    fun reCallDB()
    {
        _dbW = this.writableDatabase
        _dbR = this.readableDatabase
    }
    fun getPath() : String = _dbR.path

    //region Person
    fun getPerson() = _dbR.rawQuery(PERSON_INFO().getSelectString(false), null)
    fun getPerson(data : PERSON_INFO): Cursor = _dbR.rawQuery(data.getSelectString(true), null)
    fun insertPerson(data : PERSON_INFO, isBTData : Boolean = false)
    {
        if (getPerson(data).count > 0)
        {
            if (isBTData)
            {
                updatePerson(data)
            }
            return
        }

        contentValuesOf().apply {
            put(PERSON_OBJ.C_ID,    data._id)
            put(PERSON_OBJ.C_NAME,  data._name)
            put(PERSON_OBJ.C_PW,    data._pw)
            put(PERSON_OBJ.C_PART,  data._part)
            put(PERSON_OBJ.C_LEVEL, data._level)
        }.let {
            _dbW.insert(PERSON_OBJ.TABLE_NAME, null, it)
        }
    }
    private fun updatePerson(data : PERSON_INFO) : Boolean
    {
        try
        {
            val selection = "${PERSON_OBJ.C_ID} LIKE ?"
            val selArgs = arrayOf(data._id)
            ContentValues().apply {
                put(PERSON_OBJ.C_NAME,  data._name)
                put(PERSON_OBJ.C_PW,    data._pw)
                put(PERSON_OBJ.C_PART,  data._part)
                put(PERSON_OBJ.C_LEVEL, data._level)
            }.let {
                _dbW.update(PERSON_OBJ.TABLE_NAME, it, selection, selArgs)
            }
        }
        catch (ex : Exception)
        {
            return false
        }

        return true
    }
    //endregion
}