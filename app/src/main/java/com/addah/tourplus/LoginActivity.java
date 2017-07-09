package com.addah.tourplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by choxmi on 6/13/17.
 */

public class LoginActivity extends AppCompatActivity{

    Button loginBtn;
    EditText userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtn = (Button)findViewById(R.id.login_btn);
        userName = (EditText)findViewById(R.id.user_edit);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText().toString().equals("Admin")){
                    Intent intent = new Intent(LoginActivity.this,NewPlacesActivity.class);
                    startActivity(intent);
                }if(userName.getText().toString().equals("User")){
                    Intent intent = new Intent(LoginActivity.this,PlaceSelectActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
