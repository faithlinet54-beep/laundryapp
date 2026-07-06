package com.example.laundryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LaundryDB";
    private static final int DATABASE_VERSION = 3;

    // Users Table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Orders Table
    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ORDER_ID = "id";
    private static final String COLUMN_CUSTOMER_NAME = "customer_name";
    private static final String COLUMN_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_SERVICE_TYPE = "service_type";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    private static final String COLUMN_ORDER_STATUS = "order_status";
    private static final String COLUMN_IS_FAVORITE = "is_favorite";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(createUsersTable);

        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + "("
                + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CUSTOMER_NAME + " TEXT,"
                + COLUMN_PHONE_NUMBER + " TEXT,"
                + COLUMN_SERVICE_TYPE + " TEXT,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_TOTAL_AMOUNT + " REAL,"
                + COLUMN_ORDER_STATUS + " TEXT,"
                + COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0" + ")";
        db.execSQL(createOrdersTable);

        // Insert default admin user
        insertUserInternal(db, "admin", "admin123");
        
        // Insert some sample orders
        insertSampleOrders(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    // --- USER METHODS ---

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, hashPassword(password));
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, hashedPassword};
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // --- ORDER METHODS ---

    public void addOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NAME, order.getCustomerName());
        values.put(COLUMN_PHONE_NUMBER, order.getPhoneNumber());
        values.put(COLUMN_SERVICE_TYPE, order.getServiceType());
        values.put(COLUMN_QUANTITY, order.getQuantity());
        values.put(COLUMN_TOTAL_AMOUNT, order.getTotalAmount());
        values.put(COLUMN_ORDER_STATUS, order.getOrderStatus());
        values.put(COLUMN_IS_FAVORITE, order.isFavorite() ? 1 : 0);
        db.insert(TABLE_ORDERS, null, values);
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS, null);

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORDER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE_NUMBER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS))
                );
                order.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_FAVORITE)) == 1);
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return orders;
    }

    public void updateOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NAME, order.getCustomerName());
        values.put(COLUMN_PHONE_NUMBER, order.getPhoneNumber());
        values.put(COLUMN_SERVICE_TYPE, order.getServiceType());
        values.put(COLUMN_QUANTITY, order.getQuantity());
        values.put(COLUMN_TOTAL_AMOUNT, order.getTotalAmount());
        values.put(COLUMN_ORDER_STATUS, order.getOrderStatus());
        values.put(COLUMN_IS_FAVORITE, order.isFavorite() ? 1 : 0);

        db.update(TABLE_ORDERS, values, COLUMN_ORDER_ID + " = ?", new String[]{String.valueOf(order.getId())});
    }

    public void deleteOrder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ORDERS, COLUMN_ORDER_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // --- HELPER METHODS ---

    private void insertUserInternal(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, hashPassword(password));
        db.insert(TABLE_USERS, null, values);
    }

    private void insertSampleOrders(SQLiteDatabase db) {
        ContentValues v1 = new ContentValues();
        v1.put(COLUMN_CUSTOMER_NAME, "John Smith");
        v1.put(COLUMN_PHONE_NUMBER, "0711223344");
        v1.put(COLUMN_SERVICE_TYPE, "Full Wash");
        v1.put(COLUMN_QUANTITY, 5);
        v1.put(COLUMN_TOTAL_AMOUNT, 500.0);
        v1.put(COLUMN_ORDER_STATUS, "Washing");
        db.insert(TABLE_ORDERS, null, v1);

        ContentValues v2 = new ContentValues();
        v2.put(COLUMN_CUSTOMER_NAME, "Jane Doe");
        v2.put(COLUMN_PHONE_NUMBER, "0722334455");
        v2.put(COLUMN_SERVICE_TYPE, "Ironing");
        v2.put(COLUMN_QUANTITY, 10);
        v2.put(COLUMN_TOTAL_AMOUNT, 300.0);
        v2.put(COLUMN_ORDER_STATUS, "Ready for Pickup");
        db.insert(TABLE_ORDERS, null, v2);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }
}
