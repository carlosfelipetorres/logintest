package com.cyxtera.carlostorres.loginapp.controller.remote;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cyxtera.carlostorres.loginapp.controller.local.InfoLocationDatabase;
import com.cyxtera.carlostorres.loginapp.controller.local.InfoLocationRepository;
import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Controller {
    private CallbackListener callbackListener;
    private Location currentLocation;
    private Context context;
    private InfoLocationRepository infoLocationRepository;

    public Controller(Context applicationContext, Activity activity) {
        this.context = applicationContext;
        infoLocationRepository = new InfoLocationRepository(InfoLocationDatabase.getDatabase(context).infoLocationDao());

        setLocationSettings(activity);
    }

    public void setLocationSettings(Activity activity) {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every 1 seconds

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLastLocation();
            }
        };

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                }
            });
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1112
            );
        }
    }

    public void setCallbacks(CallbackListener listener) {
        callbackListener = listener;
    }


    public void startFetching(InfoLocation infoLocation) {
        infoLocation.setLat(currentLocation.getLatitude());
        infoLocation.setLng(currentLocation.getLongitude());
        new RetrieveLocationData().execute(infoLocation);
    }


public interface CallbackListener {
    void onFetchProgress(InfoLocation location);

    void onFetchComplete();
}

private class RetrieveLocationData extends AsyncTask<InfoLocation, Void, InfoLocation> {

    private Exception exception;
    private String API_URL = "http://api.geonames.org/timezoneJSON?formatted=true&username=qa_mobile_easy&style=full";

    protected void onPreExecute() {
    }

    protected InfoLocation doInBackground(InfoLocation... urls) {
        InfoLocation currentInfo = urls[0];
        try {
            URL url = new URL(API_URL + "&lat=" + currentInfo.getLat() + "&lng=" + currentInfo.getLng() + "");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();

                Type listType = new TypeToken<InfoLocation>() {
                }.getType();
                InfoLocation data = new Gson().fromJson(stringBuilder.toString(), listType);

                currentInfo.setTime(data.getTime());

                return currentInfo;
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(InfoLocation response) {
        Log.i("INFO", response.toString());


        new SaveData().execute(response);
    }
}

private class SaveData extends AsyncTask<InfoLocation, Void, String> {

    protected void onPreExecute() {
    }

    protected String doInBackground(InfoLocation... urls) {
        try {
            infoLocationRepository.insert(urls[0]);
            return "Saved correctly";
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(String response) {
        Log.d("Finished", response);
    }
}

private class RetrieveSavedData extends AsyncTask<InfoLocation, Void, List<InfoLocation>> {

    protected void onPreExecute() {
    }

    protected List<InfoLocation> doInBackground(InfoLocation... urls) {
        try {
            return infoLocationRepository.allLocations(urls[0].getEmail());
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(List<InfoLocation> response) {
        callbackListener.onFetchProgress(response.get(0));
        callbackListener.onFetchComplete();
    }
}

private class VerifyUser extends AsyncTask<InfoLocation, Void, String> {

    protected void onPreExecute() {
    }

    protected String doInBackground(InfoLocation... urls) {
        try {
            infoLocationRepository.verifyUser(urls[0].getEmail(), urls[0].getPassword());
            return "Saved correctly";
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(String response) {
        Log.d("Finished", response);
    }
}
}
