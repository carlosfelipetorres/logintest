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

import com.cyxtera.carlostorres.loginapp.controller.local.LoginDatabase;
import com.cyxtera.carlostorres.loginapp.controller.local.LoginRepository;
import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;
import com.cyxtera.carlostorres.loginapp.model.pojo.User;
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
    private LoginCallbackListener loginCallbackListener;
    private ListCallbackListener listCallbackListener;
    private Location currentLocation;
    private Context context;
    private LoginRepository loginRepository;

    public Controller(Context applicationContext, Activity activity) {
        this.context = applicationContext;
        loginRepository = new LoginRepository(LoginDatabase.getDatabase(context).infoLocationDao());

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

    public void setLoginCallbacks(LoginCallbackListener listener) {
        loginCallbackListener = listener;
    }

    public void setListCallbacks(ListCallbackListener listener) {
        listCallbackListener = listener;
    }


    public void login(User user) {
        new VerifyUser().execute(user);
    }

    public void registerUser(User user) {
        new RegisterUser().execute(user);
    }

    public void getListOfInfoLocations(String email){
        new RetrieveSavedData().execute(email);
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

                    Type listType = new TypeToken<InfoLocation>() {}.getType();
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
            if (response == null) {
                loginCallbackListener.onError("");
                return;
            }
            Log.i("INFO", response.toString());
            new SaveData().execute(response);
        }
    }

    private class SaveData extends AsyncTask<InfoLocation, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(InfoLocation... urls) {
            try {
                InfoLocation infoLocation = urls[0];
                loginRepository.insert(infoLocation);
                if (infoLocation.getStatus() == "Exitoso"){
                    loginCallbackListener.onLoginProgress(true);
                } else {
                    loginCallbackListener.onLoginProgress(false);
                }
                return "Saved correctly";
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            Log.d("Finished", response);
            loginCallbackListener.onLoginComplete();
        }
    }

    private class RetrieveSavedData extends AsyncTask<String, Void, List<InfoLocation>> {

        protected void onPreExecute() {
        }

        protected List<InfoLocation> doInBackground(String... urls) {
            try {
                return loginRepository.allLocations(urls[0]);
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(List<InfoLocation> response) {
            listCallbackListener.onListProgress(response);
            listCallbackListener.onListComplete();
        }
    }

    private class VerifyUser extends AsyncTask<User, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(User... urls) {
            try {
                Boolean verified = loginRepository.verifyUser(urls[0].getEmail(), urls[0].getPassword());
                InfoLocation infoLocation = new InfoLocation();
                infoLocation.setLat(currentLocation.getLatitude());
                infoLocation.setLng(currentLocation.getLongitude());
                infoLocation.setEmail(urls[0].getEmail());
                if (verified) {
                    infoLocation.setStatus("Exitoso");
                } else {
                    infoLocation.setStatus("Denegado");
                }
                new RetrieveLocationData().execute(infoLocation);
                return "Done correctly";
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return "with errors";
            }
        }

        protected void onPostExecute(String response) {
            Log.d("Finished", response);
        }
    }

    private class RegisterUser extends AsyncTask<User, Void, String> {

        protected void onPreExecute() {}

        protected String doInBackground(User... urls) {
            try {
                loginCallbackListener.onRegisterProgress(loginRepository.insert(urls[0]));
                return "Saved correctly";
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                loginCallbackListener.onRegisterProgress(false);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            Log.d(" Register user Finished", response);
            loginCallbackListener.onRegisterComplete();
        }
    }



    public interface LoginCallbackListener {
        void onRegisterProgress(Boolean done);

        void onRegisterComplete();

        void onLoginProgress(Boolean verified);

        void onLoginComplete();

        void onError(String message);
    }

    public interface ListCallbackListener {
        void onListProgress(List<InfoLocation> infoLocations);

        void onListComplete();

    }
}
