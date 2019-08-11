package com.cyxtera.carlostorres.loginapp.controller.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;
import com.cyxtera.carlostorres.loginapp.model.pojo.User;


@Database(entities = {InfoLocation.class, User.class}, version = 1, exportSchema = false)
public abstract class LoginDatabase extends RoomDatabase {

    public abstract LoginDao infoLocationDao();

    private static LoginDatabase INSTANCE = null;

    public static LoginDatabase getDatabase(Context context) {
        LoginDatabase tempInstance = INSTANCE;
        if (tempInstance != null) {
            return tempInstance;
        }

        synchronized(context) {
            LoginDatabase instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    LoginDatabase.class,
                    "database")
                    .allowMainThreadQueries()
                    .build();
            INSTANCE = instance;
            return instance;
        }
    }
}