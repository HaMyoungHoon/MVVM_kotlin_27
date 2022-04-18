package com.mvvm_kotlin_27.base.module.dbSQL

import android.database.Cursor

class FSqliteDataType
{
    companion object
    {
        //region Person
        object PERSON_OBJ
        {
            const val TABLE_NAME    = "T_PERSON_INFO"
            const val C_ID          = "C_ID"
            const val C_NAME        = "C_NAME"
            const val C_PW          = "C_PW"
            const val C_PART        = "C_PART"
            const val C_LEVEL       = "C_LEVEL"

            const val DELETE_TABLE  = "DROP TABLE IF EXISTS $TABLE_NAME"
            const val CREATE_TABLE  = "CREATE TABLE $TABLE_NAME ($C_ID VARCHAR(50), $C_NAME VARCHAR(50), $C_PW VARCHAR(50), $C_PART VARCHAR(50), $C_LEVEL VARCHAR(50))"
            const val SELECT_TABLE  = "SELECT * FROM $TABLE_NAME "
            const val UPDATE_TABLE  = "UPDATE $TABLE_NAME SET"

            const val COLUMN_CNT    : Int = 5
        }
        class PERSON_INFO
        {
            //region variable
            var _id : String
            var _name : String
            var _pw : String
            var _part : String
            var _level : String
            //endregion

            //region ctor
            constructor()
            {
                _id    = ""
                _name  = ""
                _pw    = ""
                _part  = ""
                _level = ""
            }
            constructor(data : PERSON_INFO)
            {
                _id    = data._id
                _name  = data._name
                _pw    = data._pw
                _part  = data._part
                _level = data._level
            }
            constructor(cursor : Cursor)
            {
                if (cursor.columnCount < PERSON_OBJ.COLUMN_CNT)
                {
                    _id    = ""
                    _name  = ""
                    _pw    = ""
                    _part  = ""
                    _level = ""
                }
                else
                {
                    _id    = cursor.getString(0)
                    _name  = cursor.getString(1)
                    _pw    = cursor.getString(2)
                    _part  = cursor.getString(3)
                    _level = cursor.getString(4)
                }
            }
            //endregion

            //region sql method
            fun getSelectString(compare : Boolean) : String
            {
                var ret = PERSON_OBJ.SELECT_TABLE
                if (compare)
                {
                    ret += "WHERE ${PERSON_OBJ.C_ID} = '$_id' "
                }

                ret += "ORDER BY ${PERSON_OBJ.C_ID} ASC"

                return ret
            }
            //endregion

            //region ex method
            fun clear()
            {
                _id = ""
                _name = ""
                _pw = ""
                _part = ""
                _level = ""
            }
            fun getString() : String
            {
                return "$_id$SPLIT$_name$SPLIT$_pw$SPLIT$_part$SPLIT$_level"
            }
            fun setString(data : String) : Boolean
            {
                val array = data.split(SPLIT)
                if (array.count() >= PERSON_OBJ.COLUMN_CNT)
                {
                    _id = array[0]
                    _name = array[1]
                    _pw = array[2]
                    _part = array[3]
                    _level = array[4]

                    return true
                }

                return false
            }
            fun isEmpty() : Boolean
            {
                if (_id.isBlank() || _name.isBlank() || _pw.isBlank() || _part.isBlank())
                {
                    return true
                }

                return false
            }
            //endregion
        }
        //endregion

        //region RecyclerView Sample
        object SAMPLE_OBJ
        {
            const val TABLE_NAME = "T_SAMPLE_OBJ"
            const val C_DATE_TIME = "C_DATE_TIME"
            const val C_NAME = "C_NAME"
            const val C_NUMBER = "C_NUMBER"
            const val C_OX = "C_OX"

            const val DELETE_TABLE  = "DROP TABLE IF EXISTS $TABLE_NAME"
            const val CREATE_TABLE  = "CREATE TABLE $TABLE_NAME ($C_DATE_TIME VARCHAR(50), $C_NAME VARCHAR(50), $C_NUMBER VARCHAR(50), $C_OX VARCHAR(50))"
            const val SELECT_TABLE  = "SELECT * FROM $TABLE_NAME "

            const val COLUMN_CNT : Int = 3
        }
        open class SAMPLE_INFO
        {
            //region variable
            var _dateTime : String
            var _name : String
            var _number : String
            var _ox : String
            //endregion

            //region ctor
            constructor()
            {
                _dateTime = ""
                _name = ""
                _number = "0"
                _ox = ""
            }
            constructor(data : SAMPLE_INFO)
            {
                _dateTime = data._dateTime
                _name = data._name
                _number = data._number
                _ox = data._ox
            }
            constructor(cursor : Cursor)
            {
                if (cursor.columnCount < SAMPLE_OBJ.COLUMN_CNT)
                {
                    _dateTime = ""
                    _name = ""
                    _number = "0"
                    _ox = ""
                }
                else
                {
                    _dateTime = cursor.getString(0)
                    _name = cursor.getString(1)
                    _number = cursor.getString(2)
                    _ox = cursor.getString(3)
                }
            }
            //endregion

            //region sql method
            fun getSelectString(compare : Boolean) : String
            {
                return if (compare)
                {
                    "${SAMPLE_OBJ.SELECT_TABLE} WHERE ${SAMPLE_OBJ.C_NAME} = '$_name' ORDER BY ${SAMPLE_OBJ.C_NAME} ASC"
                }
                else
                {
                    "${SAMPLE_OBJ.SELECT_TABLE} ORDER BY ${SAMPLE_OBJ.C_NAME} ASC"
                }
            }
            //endregion

            //region ex method
            fun clear()
            {
                setDateTime("")
                setName("")
                setNumber(0)
                setOX("")
            }
            fun getString() : String = "$_dateTime$SPLIT$_name$SPLIT$_number$SPLIT$_ox"
            fun setString(data : String) : Boolean
            {
                data.split(SPLIT).let {
                    if (it.count() >= SAMPLE_OBJ.COLUMN_CNT)
                    {
                        setDateTime(it[0])
                        setName(it[1])
                        setNumber(it[2])
                        setOX(it[3])

                        return true
                    }
                }

                return false
            }
            fun isEmpty() : Boolean
            {
                if (_dateTime.isBlank() || _name.isBlank() || _ox.isBlank())
                {
                    return true
                }

                return false
            }
            fun setDateTime(data : String)
            {
                _dateTime = dateRegex.find(data)?.value ?: ""
            }
            fun setName(data : String)
            {
                _name = data
            }
            fun setNumber(data : String)
            {
                if (!numRegex.matches(data))
                {
                    return
                }

                _number = data
            }
            fun setNumber(data : Double)
            {
                _number = data.toString()
            }
            fun setNumber(data : Int)
            {
                _number = data.toString()
            }
            fun setOX(data : String)
            {
                _ox = data
            }
            //endregion
        }
        //endregion

        //region ETC
        const val DATETIME_PATTERN = "(^\\d{4}(-)\\d{2}(-)\\d{2}( )\\d{2}(:)\\d{2}(:)\\d{2})"
        val dateRegex = Regex(DATETIME_PATTERN)
        const val NUMBER_PATTERN = "^(\\+\\d|\\d)+(\\.?\\d*)\$"
        val numRegex = Regex(NUMBER_PATTERN)
        const val DATE_TO_NUM_PATTERN = "[^0-9]";
        val dateToNumRegex = Regex(DATE_TO_NUM_PATTERN)
        const val DB_NAME = "MVVM_KOTLIN.db"
        const val DB_VER = 1
        const val SELECT_TABLE = "SELECT name FROM SQLITE_MASTER WHERE TYPE='table'"
        val SPLIT : Char = (0x08).toChar()
        //endregion
    }
}