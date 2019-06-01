package com.oit.lotspot.database


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.oit.lotspot.retrofit.response.VehicleDetailResponseModel


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var TAG = DatabaseHelper::class.java.simpleName

    companion object {
        /**
         * Database information
         */
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "VehicleRecords"

        /**
         * Table information
         */
        const val TABLE_VEHICLE = "table_vehicle_detail"

        /**
         * Keys used in Table
         */
        const val KEY_YEAR = "year"
        const val KEY_MAKE = "make"
        const val KEY_MODEL = "model"
        const val KEY_ENGINE = "engine"
        const val KEY_IMAGE = "image"
        const val KEY_VIN = "vin"

        /**
         * Create Table
         */
        private val CREATE_TABLE_VEHICLE_DETAILS =
            "CREATE TABLE ${TABLE_VEHICLE}($KEY_YEAR INTEGER,$KEY_MAKE TEXT,$KEY_MODEL TEXT," +
                    "$KEY_ENGINE TEXT,$KEY_IMAGE TEXT,$KEY_VIN TEXT)"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(CREATE_TABLE_VEHICLE_DETAILS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS" + TABLE_VEHICLE)
        onCreate(db)
    }

    /**
     * This method will accept VehicleDetailModel and save data into database
     */
    fun saveVehicleRecords(model: VehicleDetailResponseModel.VehicleDetailDataResponseModel) {
        val db: SQLiteDatabase = this.writableDatabase
        val values = ContentValues()

        values.put(KEY_YEAR, model.year)
        values.put(KEY_MAKE, model.make)
        values.put(KEY_MODEL, model.model)
        values.put(KEY_ENGINE, model.engine)
        values.put(KEY_IMAGE, model.image)
        values.put(KEY_VIN, model.vin)

        db.insert(TABLE_VEHICLE, null, values)
        Log.d(TAG, "data Saved ----> ")
    }

    /**
     * This method will provide details of vehicle
     */
    fun getVehicleDetail(): ArrayList<VehicleDetailResponseModel.VehicleDetailDataResponseModel> {

        val arrayList: ArrayList<VehicleDetailResponseModel.VehicleDetailDataResponseModel> = arrayListOf()

        val db = this.writableDatabase

        val selectQuery = "SELECT * FROM $TABLE_VEHICLE"

        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                arrayList.add(VehicleDetailResponseModel.VehicleDetailDataResponseModel().apply {
                    this.id = cursor.getInt(0)
                    this.year = cursor.getInt(1)
                    this.make = cursor.getString(2)
                    this.model = cursor.getString(3)
                    this.engine = cursor.getString(4)
                    this.image = cursor.getString(5)
                    this.vin = cursor.getString(6)
                    this.created_at = cursor.getString(7)
                    this.updated_at = cursor.getString(8)
                    this.lat = cursor.getDouble(9)
                    this.lng = cursor.getDouble(10)
                    this.address = cursor.getString(11)
                })

            } while (cursor.moveToNext())
        }
        cursor.close()

        return arrayList
    }

    /**
     * This method will remove all data from table
     */
    fun removeData() {
        val dbase = this.writableDatabase
        dbase.execSQL("delete from $TABLE_VEHICLE")
    }

    /**
     * This method will remove only single record from table
     */
//    fun removeParticularItemFromTable(id: Int) {
//        val dbase = this.writableDatabase
//        val deleteQuery = ("delete from $TABLE_VEHICLE WHERE $KEY_ID = $id")
//        dbase.execSQL(deleteQuery)
//    }

}