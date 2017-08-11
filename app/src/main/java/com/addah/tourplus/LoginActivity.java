package com.addah.tourplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * Created by choxmi on 6/13/17.
 */

public class LoginActivity extends AppCompatActivity{

    Button loginBtn;
    RadioButton user,admin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = (Button)findViewById(R.id.login_btn);
        user = (RadioButton)findViewById(R.id.userRadio);
        admin = (RadioButton)findViewById(R.id.adminRadio);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.isChecked()){
                    Intent intent = new Intent(LoginActivity.this,PlaceSelectActivity.class);
                    startActivity(intent);
                }if(admin.isChecked()){
                    Intent intent = new Intent(LoginActivity.this,NewPlacesActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
