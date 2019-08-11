package com.cyxtera.carlostorres.loginapp.controller.local;


import androidx.annotation.WorkerThread;

import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;

import java.util.ArrayList;
import java.util.List;

public class InfoLocationRepository {

    InfoLocationDao infoLocationDao;

    public InfoLocationRepository(InfoLocationDao infoLocationDao) {
        this.infoLocationDao = infoLocationDao;
    }

    public List<InfoLocation> allLocations(String email) {
        if (this.infoLocationDao != null) return this.infoLocationDao.getAllLocations(email);
        else return new ArrayList<>();
    }

    public List<InfoLocation> verifyUser(String email, String password) {
        if (this.infoLocationDao != null) return this.infoLocationDao.verifyUser(email, password);
        else return new ArrayList<>();
    }

    @WorkerThread
    public void insert(InfoLocation infoLocation) {
        infoLocationDao.insert(infoLocation);
    }

}
