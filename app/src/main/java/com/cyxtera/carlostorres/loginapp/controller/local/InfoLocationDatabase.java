package com.cyxtera.carlostorres.loginapp.controller.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;


@Database(entities = {InfoLocation.class}, version = 1, exportSchema = false)
public abstract class InfoLocationDatabase extends RoomDatabase {

    public abstract InfoLocationDao infoLocationDao();

    private static InfoLocationDatabase INSTANCE = null;

    public static InfoLocationDatabase getDatabase(Context context) {
        InfoLocationDatabase tempInstance = INSTANCE;
        if (tempInstance != null) {
            return tempInstance;
        }

        synchronized(context) {
            InfoLocationDatabase instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    InfoLocationDatabase.class,
                    "database")
                    .allowMainThreadQueries()
                    .build();
            INSTANCE = instance;
            return instance;
        }
    }
}