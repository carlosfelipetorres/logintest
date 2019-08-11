package com.cyxtera.carlostorres.loginapp.view;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cyxtera.carlostorres.loginapp.R;
import com.cyxtera.carlostorres.loginapp.controller.remote.Controller;
import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;

public class LoginActivity extends AppCompatActivity implements Controller.CallbackListener {

    private Controller controller;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        controller = new Controller(getApplicationContext(), this);
        controller.setCallbacks(LoginActivity.this);

        setViews();
    }

    private void setViews() {
        final EditText email = this.findViewById(R.id.email);
        final EditText password = this.findViewById(R.id.password);

        progressBar = this.findViewById(R.id.progressBar);

        Button login = this.findViewById(R.id.btnLogin);
        Button register = this.findViewById(R.id.btnRegister);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoLocation current = new InfoLocation();
                current.setEmail(email.getText().toString());
                current.setPassword(password.getText().toString());
                controller.startFetching(current);

                progressBar.setVisibility(View.VISIBLE);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.getText().toString();
                password.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onFetchProgress(InfoLocation location) {
        Toast.makeText(this, "Info Location retrieved", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFetchComplete() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1112) {
            controller.setLocationSettings(this);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
