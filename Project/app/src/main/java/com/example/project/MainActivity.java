package com.example.project;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;


public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button button;
    private Bitmap other_bitmap;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("229b21d7-d218-4f3b-92b7-674b72185dd1");
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);
        mapView = (MapView)findViewById(R.id.mapView);
        mapView.getMap().move(
                new CameraPosition(new Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        /* imageView = findViewById(R.id.imageView);
        other_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        int width = 300;
        int height = (other_bitmap.getHeight() * 300) / other_bitmap.getWidth();
        other_bitmap = Bitmap.createScaledBitmap(other_bitmap, width, height, false);
        imageView.setImageBitmap(other_bitmap);

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                                int width = 700;
                                int height = (bitmap.getHeight() * 700) / bitmap.getWidth();
                                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    }
                });

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                launcher.launch(takePhotoIntent);
            }
        });*/
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }
}