package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void getPhoto(){
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null){
            try{
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                byte[] bytes= byteArrayOutputStream.toByteArray();
                ParseFile parseFile=new ParseFile("image.png",bytes);
                ParseObject object= new ParseObject("Image");
                object.put("image",parseFile);
                object.put("username",ParseUser.getCurrentUser().getUsername());
                object.saveInBackground(e -> {
                    if(e==null){
                        Toast.makeText(UserListActivity.this, "Photo Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        e.printStackTrace();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.share_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.share){
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else{
                getPhoto();
            }
        }
        else if(item.getItemId()==R.id.logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }
    public void logout(){
        ParseUser.logOut();
        Intent intent= new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Logout", (dialog, which) -> logout());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alert= builder.create();
        alert.show();
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        setTitle("User List");
        ListView listView= findViewById(R.id.listView);
        ArrayList<String> userList= new ArrayList<>();
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,userList);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent= new Intent(getApplicationContext(),UserFeedActivity.class);
            intent.putExtra("username",userList.get(position));
            startActivity(intent);
        });
        ParseQuery<ParseUser> query= ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");
        query.findInBackground((objects, e) -> {
            if(e==null){
                if(objects.size()>0){
                    for (ParseUser user: objects)
                        userList.add(user.getUsername());
                    listView.setAdapter(arrayAdapter);
                }else{
                    Toast.makeText(UserListActivity.this, "No users found other than you :(", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                e.printStackTrace();
            }
        });
    }
}