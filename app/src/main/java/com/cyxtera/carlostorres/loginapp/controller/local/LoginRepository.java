package com.cyxtera.carlostorres.loginapp.controller.local;


import androidx.annotation.WorkerThread;

import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;
import com.cyxtera.carlostorres.loginapp.model.pojo.User;

import java.util.ArrayList;
import java.util.List;

public class LoginRepository {

    LoginDao loginDao;

    public LoginRepository(LoginDao loginDao) {
        this.loginDao = loginDao;
    }

    // Info Location

    public List<InfoLocation> allLocations(String email) {
        if (this.loginDao != null) return this.loginDao.getAllLocations(email);
        else return new ArrayList<>();
    }

    @WorkerThread
    public void insert(InfoLocation infoLocation) {
        loginDao.insert(infoLocation);
    }

    //User

    @WorkerThread
    public Boolean insert(User user) {
        if (getUsersByEmail(user.getEmail()).size() == 0) {
            loginDao.insert(user);
            return true;
        } else {
            return false;
        }
    }

    public Boolean verifyUser(String email, String password) {
        return this.loginDao.verifyUser(email, password).size() != 0;
    }

    public List<User> getUsersByEmail(String email) {
        return this.loginDao.getUsersByEmail(email);
    }
}
