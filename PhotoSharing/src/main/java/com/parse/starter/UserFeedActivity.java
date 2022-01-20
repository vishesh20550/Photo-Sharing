package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class UserFeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        LinearLayout linearLayout= findViewById(R.id.linlayout);
        Intent intent=getIntent();
        String username= intent.getStringExtra("username");
        setTitle(username+"'s Feed");
        ParseQuery<ParseObject> query= new ParseQuery<>("Image");
        query.whereEqualTo("username",username);
        query.orderByDescending("createdAt");
        query.findInBackground((objects, e) -> {
            if(e==null){
                if(objects.size()>0){
                    for(ParseObject object: objects){
                        ParseFile file= object.getParseFile("image");
                        file.getDataInBackground((data, e1) -> {
                            if(e1 ==null){
                                if(data!=null){
                                    Bitmap bitmap= BitmapFactory.decodeByteArray(data,0,data.length);
                                    ImageView imageView= new ImageView(getApplicationContext());
                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    imageView.setImageBitmap(bitmap);
                                    linearLayout.addView(imageView);
                                }else{
                                    Toast.makeText(UserFeedActivity.this, "No Image Found", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                e1.printStackTrace();
                            }
                        });
                    }
                }else{
                    Toast.makeText(UserFeedActivity.this, "No Image Found", Toast.LENGTH_SHORT).show();
                }
            }else{
                e.printStackTrace();
            }
        });
    }
}