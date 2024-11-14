package com.example.fitshare.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fitshare.models.Photo;
import com.example.fitshare.models.User;
import com.example.fitshare.models.WorkoutLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FitShare.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_WORKOUTS = "workouts";
    public static final String TABLE_PHOTOS = "photos";
    public static final String TABLE_FRIENDS = "friends";

    // Common Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";

    // User Table Columns
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PROFILE_IMAGE = "profile_image";

    // Workout Table Columns
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_DATE = "date";

    // Photo Table Columns
    public static final String COLUMN_IMAGE_PATH = "image_path";
    public static final String COLUMN_CAPTION = "caption";
    public static final String COLUMN_UPLOAD_DATE = "upload_date";

    // Friends Table Columns
    public static final String COLUMN_FRIEND_ID = "friend_id";
    public static final String COLUMN_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PROFILE_IMAGE + " TEXT"
                + ")";

        // Create Workouts table
        String CREATE_WORKOUTS_TABLE = "CREATE TABLE " + TABLE_WORKOUTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DURATION + " INTEGER,"
                + COLUMN_DATE + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_ID + ")"
                + ")";

        // Create Photos table
        String CREATE_PHOTOS_TABLE = "CREATE TABLE " + TABLE_PHOTOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_IMAGE_PATH + " TEXT,"
                + COLUMN_CAPTION + " TEXT,"
                + COLUMN_UPLOAD_DATE + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_ID + ")"
                + ")";

        // Create Friends table
        String CREATE_FRIENDS_TABLE = "CREATE TABLE " + TABLE_FRIENDS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_FRIEND_ID + " INTEGER,"
                + COLUMN_STATUS + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + COLUMN_FRIEND_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COLUMN_ID + ")"
                + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_WORKOUTS_TABLE);
        db.execSQL(CREATE_PHOTOS_TABLE);
        db.execSQL(CREATE_FRIENDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // User operations
    public long addUser(String email, String password, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_NAME, name);

        return db.insert(TABLE_USERS, null, values);
    }

    // Get user ID by email
    @SuppressLint("Range")
    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
        }
        cursor.close();
        return userId;
    }

    // Photo operations
    public long addPhoto(int userId, String imagePath, String caption) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_IMAGE_PATH, imagePath);
        values.put(COLUMN_CAPTION, caption);
        values.put(COLUMN_UPLOAD_DATE, System.currentTimeMillis());
        return db.insert(TABLE_PHOTOS, null, values);
    }

    public List<Photo> getUserPhotos(int userId) {
        List<Photo> photos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {
                COLUMN_ID,
                COLUMN_USER_ID,
                COLUMN_IMAGE_PATH,
                COLUMN_CAPTION,
                COLUMN_UPLOAD_DATE
        };

        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String orderBy = COLUMN_UPLOAD_DATE + " DESC";

        Cursor cursor = db.query(
                TABLE_PHOTOS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );

        try {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    @SuppressLint("Range") String imagePath = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
                    @SuppressLint("Range") String caption = cursor.getString(cursor.getColumnIndex(COLUMN_CAPTION));
                    @SuppressLint("Range") long uploadDate = cursor.getLong(cursor.getColumnIndex(COLUMN_UPLOAD_DATE));

                    Photo photo = new Photo(id, userId, imagePath, caption, uploadDate);
                    photos.add(photo);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return photos;
    }

    // Friend operations
    public void addFriend(int userId, int friendId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_FRIEND_ID, friendId);
        values.put(COLUMN_STATUS, "accepted"); // For simplicity, auto-accept

        db.insert(TABLE_FRIENDS, null, values);
    }

    @SuppressLint("Range")
    public List<User> getUserFriends(int userId) {
        List<User> friends = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u.* FROM " + TABLE_USERS + " u " +
                "INNER JOIN " + TABLE_FRIENDS + " f ON u." + COLUMN_ID +
                " = f." + COLUMN_FRIEND_ID +
                " WHERE f." + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                user.setProfileImage(cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_IMAGE)));
                friends.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return friends;
    }

    @SuppressLint("Range")
    public List<User> searchUsers(String query, int currentUserId) {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String searchQuery = "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_ID + " != ? AND (" +
                COLUMN_NAME + " LIKE ? OR " +
                COLUMN_EMAIL + " LIKE ?)";

        String searchPattern = "%" + query + "%";

        Cursor cursor = db.rawQuery(searchQuery,
                new String[]{String.valueOf(currentUserId), searchPattern, searchPattern});

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                user.setProfileImage(cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_IMAGE)));
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    public boolean isFriend(int userId, int friendId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_FRIENDS +
                " WHERE " + COLUMN_USER_ID + " = ? AND " +
                COLUMN_FRIEND_ID + " = ?";

        Cursor cursor = db.rawQuery(query,
                new String[]{String.valueOf(userId), String.valueOf(friendId)});

        boolean isFriend = cursor.getCount() > 0;
        cursor.close();
        return isFriend;
    }

    public void removeFriend(int userId, int friendId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_USER_ID + " = ? AND " + COLUMN_FRIEND_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId), String.valueOf(friendId)};
        db.delete(TABLE_FRIENDS, whereClause, whereArgs);
    }

    public List<WorkoutLog> getAllFriendsWorkouts(int userId) {
        List<WorkoutLog> workouts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query to get workouts from friends and the current user
        String query = "SELECT w.*, u.name as user_name FROM " + TABLE_WORKOUTS + " w " +
                "INNER JOIN " + TABLE_USERS + " u ON w." + COLUMN_USER_ID + " = u." + COLUMN_ID + " " +
                "WHERE w." + COLUMN_USER_ID + " IN " +
                "(SELECT " + COLUMN_FRIEND_ID + " FROM " + TABLE_FRIENDS +
                " WHERE " + COLUMN_USER_ID + " = ?) " +
                "OR w." + COLUMN_USER_ID + " = ? " +
                "ORDER BY w." + COLUMN_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int workoutId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") int workoutUserId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
                @SuppressLint("Range") String userName = cursor.getString(cursor.getColumnIndex("user_name"));
                @SuppressLint("Range") String workoutType = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION));
                @SuppressLint("Range") long dateMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE));

                // Create WorkoutLog object
                WorkoutLog workout = new WorkoutLog(
                        workoutId,
                        workoutUserId,
                        userName,
                        workoutType,
                        description,
                        duration,
                        new Date(dateMillis)
                );
                workouts.add(workout);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return workouts;
    }

    // Helper method to get workouts for a specific user
    public List<WorkoutLog> getUserWorkouts(int userId) {
        List<WorkoutLog> workouts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT w.*, u.name as user_name FROM " + TABLE_WORKOUTS + " w " +
                "INNER JOIN " + TABLE_USERS + " u ON w." + COLUMN_USER_ID + " = u." + COLUMN_ID + " " +
                "WHERE w." + COLUMN_USER_ID + " = ? " +
                "ORDER BY w." + COLUMN_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int workoutId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String userName = cursor.getString(cursor.getColumnIndex("user_name"));
                @SuppressLint("Range") String workoutType = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                @SuppressLint("Range") int duration = cursor.getInt(cursor.getColumnIndex(COLUMN_DURATION));
                @SuppressLint("Range") long dateMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE));

                WorkoutLog workout = new WorkoutLog(
                        workoutId,
                        userId,
                        userName,
                        workoutType,
                        description,
                        duration,
                        new Date(dateMillis)
                );
                workouts.add(workout);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return workouts;
    }

    // Add workout to database
    public long addWorkout(int userId, String type, String description, int duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_DURATION, duration);
        values.put(COLUMN_DATE, System.currentTimeMillis());

        return db.insert(TABLE_WORKOUTS, null, values);
    }

    @SuppressLint("Range")
    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = null;

        String[] columns = {COLUMN_NAME};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);

        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
        }
        cursor.close();
        return name;
    }

    // Additional helper methods for user management
    @SuppressLint("Range")
    public User getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PROFILE_IMAGE};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            user.setProfileImage(cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_IMAGE)));
        }
        cursor.close();
        return user;
    }

    @SuppressLint("Range")
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        String[] columns = {COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PROFILE_IMAGE};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            user.setProfileImage(cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_IMAGE)));
        }
        cursor.close();
        return user;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID};
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    @SuppressLint("Range")
    public List<Photo> getUserAndFriendsPhotos(int userId) {
        List<Photo> photos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get photos from user and their friends
        String query =
                "SELECT p.*, u.name as user_name FROM " + TABLE_PHOTOS + " p " +
                        "INNER JOIN " + TABLE_USERS + " u ON p." + COLUMN_USER_ID + " = u." + COLUMN_ID + " " +
                        "WHERE p." + COLUMN_USER_ID + " = ? " + // User's photos
                        "OR p." + COLUMN_USER_ID + " IN " + // Friends' photos
                        "(SELECT " + COLUMN_FRIEND_ID + " FROM " + TABLE_FRIENDS +
                        " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_STATUS + " = 'accepted') " +
                        "ORDER BY p." + COLUMN_UPLOAD_DATE + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(userId),
                String.valueOf(userId)
        });

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Photo photo = new Photo(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CAPTION)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_UPLOAD_DATE))
                );
                photo.setUserName(cursor.getString(cursor.getColumnIndex("user_name")));
                photos.add(photo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return photos;
    }

    public boolean deletePhoto(int photoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(photoId)};
        int result = db.delete(TABLE_PHOTOS, whereClause, whereArgs);
        return result > 0;
    }

    public int getUserWorkoutCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_WORKOUTS +
                " WHERE " + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int getUserFriendsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_FRIENDS +
                " WHERE " + COLUMN_USER_ID + " = ? AND " +
                COLUMN_STATUS + " = 'accepted'";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
}

