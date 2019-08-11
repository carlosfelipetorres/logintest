package com.cyxtera.carlostorres.loginapp.controller.local;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;
import com.cyxtera.carlostorres.loginapp.model.pojo.User;

import java.util.List;

@Dao
public interface LoginDao {

    //InfoLocation

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(InfoLocation infoLocation);

    @Query("SELECT * from InfoLocation WHERE email = :email ORDER BY id DESC")
    List<InfoLocation> getAllLocations(String email);

    //User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(User user);

    @Query("SELECT * from User WHERE email = :email AND password = :password")
    List<User> verifyUser(String email, String password);

    @Query("SELECT * from User WHERE email = :email")
    List<User> getUsersByEmail(String email);
}
