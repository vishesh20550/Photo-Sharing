/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
  boolean signUpModeActive=true;
  ConstraintLayout constraintLayout;
  ImageView imageView;
  TextView textView;
  EditText username;
  EditText password;
  Button button;

  public void launchUserList(){
    Intent intent= new Intent(getApplicationContext(),UserListActivity.class);
    startActivity(intent);  
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    imageView = findViewById(R.id.imageView);
    constraintLayout= findViewById(R.id.ConstraintLayout);
    username= findViewById(R.id.usernameEditText);
    password= findViewById(R.id.passwordEditText);
    button= findViewById(R.id.signupButton);
    textView= findViewById(R.id.loginTextView);
    textView.setOnClickListener(this);
    imageView.setOnClickListener(this);
    constraintLayout.setOnClickListener(this);
    password.setOnKeyListener(this);
    if(ParseUser.getCurrentUser()!=null){
      launchUserList();
    }
    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }
  public void signupClicked(View view){
    if(username.getText().toString().matches("")||password.getText().toString().matches("")){
      Toast.makeText(MainActivity.this, "Username and Password are required", Toast.LENGTH_SHORT).show();
    }
    else{
      if(signUpModeActive){
        ParseUser user = new ParseUser();
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());
        user.signUpInBackground(e -> {
          if (e == null) {
            //ok
            Toast.makeText(MainActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
            launchUserList();
          } else {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
      }
      else{
        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), (user, e) -> {
          if(user!=null){
            if(e==null){
              Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
              launchUserList();
            }
            else{
              Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          } else{
            Toast.makeText(MainActivity.this, "Invalid Username or password", Toast.LENGTH_SHORT).show();
          }
        });
      }
    }
  }
  @SuppressLint("SetTextI18n")
  @Override
  public void onClick(View v) {
    if(v.getId()==R.id.loginTextView){
      if (signUpModeActive) {
        button.setText("Login");
        textView.setText("or, Sign Up");
        signUpModeActive = false;
      } else {
        button.setText("Sign Up");
        textView.setText("or, Login");
        signUpModeActive = true;
      }
    }
    else if(v.getId()==constraintLayout.getId()||v.getId()==imageView.getId()){
      InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
  }

  @Override
  public boolean onKey(View v, int keyCode, KeyEvent event) {
    if(keyCode==KeyEvent.KEYCODE_ENTER&& event.getAction()==KeyEvent.ACTION_DOWN){
      InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
      signupClicked(v);
    }
    return false;
  }
}