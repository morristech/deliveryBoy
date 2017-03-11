package com.yoscholar.deliveryboy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.retrofitPojo.login.Login;
import com.yoscholar.deliveryboy.utils.AppPreference;
import com.yoscholar.deliveryboy.utils.RetrofitApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {

    private ProgressDialog progressDialog;

    @NotEmpty(message = "username cannot be empty.")
    private EditText userNameEditText;
    @NotEmpty(message = "password cannot be empty.")
    private EditText passwordEditText;
    private Button loginButton;

    private Validator validator;

    private String status;
    private String id;
    private String name;
    private String city;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {

        validator = new Validator(this);
        validator.setValidationListener(this);

        userNameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);

        //dev
        //userNameEditText.setText("bang1");
        //passwordEditText.setText("bang1$23");

        //prod
        //userNameEditText.setText("Sandeep");
        //passwordEditText.setText("bang1$23");

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

    }

    @Override
    public void onValidationSucceeded() {

        progressDialog.show();

        makeNetworkRequest();
    }


    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }


    private void makeNetworkRequest() {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<Login> loginCall = apiInterface.login(
                userNameEditText.getText().toString(),
                passwordEditText.getText().toString()
        );

        loginCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    status = response.body().getStatus();
                    id = response.body().getUser().getId();
                    name = response.body().getUser().getName();
                    city = response.body().getUser().getCity();
                    token = response.body().getToken();

                    login(status, id, name, city, token);

                } else {
                    Toast.makeText(LoginActivity.this, "Some Error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Network problem", Toast.LENGTH_SHORT).show();
                //Log.d("VICKY", call.request().url().toString());
            }
        });
    }

    private void login(String status, String id, String name, String city, String token) {

        if (status.equalsIgnoreCase("success")) {

            saveUserDetails(id, name, city, token);

            startActivity(new Intent(LoginActivity.this, OptionsActivity.class));

            finish();

        } else if (status.equalsIgnoreCase("failure")) {

            Toast.makeText(this, "Invalid Credentials.", Toast.LENGTH_SHORT).show();

        }
    }

    private void saveUserDetails(String id, String name, String city, String token) {

        AppPreference.saveBoolean(this, AppPreference.IS_LOGGED_IN, true);
        AppPreference.saveString(this, AppPreference.ID, id);
        AppPreference.saveString(this, AppPreference.NAME, name);
        AppPreference.saveString(this, AppPreference.CITY, city);
        AppPreference.saveString(this, AppPreference.TOKEN, token);

    }

}
