package com.example.myday2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.VideoView;
import java.io.File;
import java.io.InputStream;



public class WorkActivity extends AppCompatActivity {

    private Button back_btn;
    private Button file_btn;
    private Button save_btn;
    private ImageView file_img;
    private VideoView file_video;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        file_img = findViewById(R.id.file_img);
        file_video = findViewById(R.id.file_video);
        file_video.setVisibility(View.GONE);  //비디오뷰 감춰주기


        //뒤로가기 버튼 동작
        back_btn = findViewById(R.id.back_draw_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        //파일버튼 동작
        file_btn = findViewById(R.id.fileBtn);
        file_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                PopupMenu popup = new PopupMenu(WorkActivity.this, file_btn);
                popup.getMenuInflater().inflate(R.menu.file_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.gallery: //사진촬영기능
                                checkSelfPermission();
                                Intent imageintent = new Intent(); //기기 기본 갤러리 접근
                                imageintent.setType(MediaStore.Images.Media.CONTENT_TYPE); //구글 갤러리 접근
                                imageintent.setType("image/*");
                                imageintent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(imageintent, 101);
                                break;

                            case R.id.camera: //사진촬영기능
                                Intent takepictureintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takepictureintent, 0);
                                break;

                            case R.id.movie: //동영상첨부기능
                                checkSelfPermission();
                                Intent movieintent = new Intent(); //기기 기본 갤러리 접근
                                movieintent.setType(MediaStore.Images.Media.CONTENT_TYPE); //구글 갤러리 접근
                                movieintent.setType("video/*");
                                movieintent.setAction(Intent.ACTION_GET_CONTENT);
                                movieintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivityForResult(movieintent, 102);
                                break;

                            case R.id.draw: //그림그리기 기능
                                Toast.makeText(getApplication(), "그리기모드", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(WorkActivity.this, OnDraw.class);
                                startActivityForResult(intent, 1234);

                                break;
                        }
                        return false;

                    }
                });

                popup.show();
            }
        });

//        저장버튼 구현
//        save_btn =  findViewById(R.id.save_btn);
//        save_btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //권한을 허용 했을 경우
        if(requestCode == 1){
            int length = permissions.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // 동의
                    Log.d("WorkActivity","권한 허용 : " + permissions[i]); }
            }
        }
    }

    public void checkSelfPermission() {
        String temp = "";

        //파일 읽기 권한 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        //파일 쓰기 권한 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }
        if (TextUtils.isEmpty(temp) == false) {
            // 권한 요청
            ActivityCompat.requestPermissions(this, temp.trim().split(" "),1);
        }
        else {
            // 모두 허용 상태
            Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show();
        }
    }


    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //사진띄우기
        if(requestCode == 101 && resultCode == RESULT_OK){
            try{
                InputStream is = getContentResolver().openInputStream(data.getData());
                Bitmap bm = BitmapFactory.decodeStream(is);
                is.close();
                file_img.setImageBitmap(bm);
            }
            catch (Exception e){ e.printStackTrace();
            }
        }

        //동영상띄우기
        else if(requestCode == 102 && resultCode == RESULT_OK ){

            Uri fileUri = data.getData();

           file_video.setVideoURI(fileUri);
           file_video.setVisibility(View.VISIBLE); //비디오뷰 출력하기
           file_video.start();

        }

        else if(requestCode == 101 && resultCode == RESULT_CANCELED){
            Toast.makeText(this,"취소", Toast.LENGTH_SHORT).show();
        }

        //사진촬영 이미지 띄우기
        else if(requestCode == 0  && data.hasExtra("data")){
            Bitmap bm = Bitmap.createScaledBitmap((Bitmap) data.getExtras().get("data"),600, 600, false);
            file_img.setImageBitmap(bm);
        }

        //그림판 띄우기
        else if(requestCode == 1234 && resultCode == RESULT_OK){

            try{

                File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                Bitmap bm = BitmapFactory.decodeFile(file+"/my.png");
                file_img.setImageBitmap(bm);
                Toast.makeText(getApplicationContext(), "load ok", Toast.LENGTH_SHORT).show();
            }
            catch(Exception e){
                Toast.makeText(getApplicationContext(), "load error", Toast.LENGTH_SHORT).show();
            }


        }
    }

}








