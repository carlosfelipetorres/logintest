package com.cyxtera.carlostorres.loginapp.controller.local;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;

import java.util.List;

@Dao
public interface InfoLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(InfoLocation infoLocation);


    @Query("SELECT * from InfoLocation WHERE email = :email")
    List<InfoLocation> getAllLocations(String email);

    @Query("SELECT * from InfoLocation WHERE email = :email AND password = :password")
    List<InfoLocation> verifyUser(String email, String password);
}
