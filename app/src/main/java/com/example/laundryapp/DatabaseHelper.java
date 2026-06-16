package com.example.laundryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LaundryApp.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CUSTOMER_NAME = "customer_name";
    private static final String COLUMN_SERVICE_TYPE = "service_type";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CUSTOMER_NAME + " TEXT, " +
                COLUMN_SERVICE_TYPE + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_STATUS + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    public long addOrder(String name, String service, double amount, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NAME, name);
        values.put(COLUMN_SERVICE_TYPE, service);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_STATUS, status);
        return db.insert(TABLE_ORDERS, null, values);
    }

    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS, null);

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getString(4)
                );
                orderList.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderList;
    }

    public int updateOrder(int id, String name, String service, double amount, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NAME, name);
        values.put(COLUMN_SERVICE_TYPE, service);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_STATUS, status);
        return db.update(TABLE_ORDERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteOrder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ORDERS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<Order> searchOrders(String query) {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String searchQuery = "SELECT * FROM " + TABLE_ORDERS + " WHERE " +
                COLUMN_CUSTOMER_NAME + " LIKE ? OR " + COLUMN_ID + " LIKE ?";
        Cursor cursor = db.rawQuery(searchQuery, new String[]{"%" + query + "%", "%" + query + "%"});

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getString(4)
                );
                orderList.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orderList;
    }
}
