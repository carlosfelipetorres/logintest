package com.cyxtera.carlostorres.loginapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.cyxtera.carlostorres.loginapp.R;
import com.cyxtera.carlostorres.loginapp.controller.remote.Controller;
import com.cyxtera.carlostorres.loginapp.model.pojo.User;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements Controller.LoginCallbackListener {

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.btnLogin)
    Button login;

    @BindView(R.id.btnRegister)
    Button register;

    private Controller controller;
    private User currentUser = new User();

    private final String regexComplete = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private Boolean validPass = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        controller = new Controller(getApplicationContext(), this);
        controller.setLoginCallbacks(LoginActivity.this);

        setViews();
    }

    private void setViews() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validPass) {
                    currentUser.setEmail(email.getText().toString());
                    currentUser.setPassword(password.getText().toString());
                    controller.login(currentUser);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    onError("El password debe tener mínimo 8 caracteres donde incluya \nmayúsculas, \nminúsculas, \nnúmeros y\n" +
                            "caracteres especiales.");
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.setEmail(email.getText().toString());
                currentUser.setPassword(password.getText().toString());
                controller.registerUser(currentUser);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Pattern.compile(regexComplete).matcher(password.getText()).matches()) {
                    validPass = true;
                    password.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.white));
                } else {
                    validPass= false;
                    password.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.red));
                }
            }
        });
    }

    @Override
    public void onRegisterProgress(final Boolean done) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (done) {
                    Toast.makeText(LoginActivity.this, "Usuario registrado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "El usuario ya existe, Inicia Sesion para empezar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onRegisterComplete() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoginProgress(Boolean verified) {
        if (verified) {
            Intent i = new Intent(this, ListActivity.class);
            i.putExtra("email", currentUser.getEmail());
            startActivity(i);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "Usuario o password invalidos, si no tienes cuenta registrate primero", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onLoginComplete() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(String message) {
        if (message.equals("")) message = "Ha ocurrido un error, intente mas tarde";
        final String finalMessage = message;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, finalMessage, Toast.LENGTH_LONG).show();
            }
        });
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
