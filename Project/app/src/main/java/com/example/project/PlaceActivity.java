package com.example.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class PlaceActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button button;
    private Bitmap other_bitmap;
    private Places place;
    private DBPlaces mDBConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_activity);
        getSupportActionBar().hide();

        mDBConnector = new DBPlaces(this);
        place = mDBConnector.selectPlaces(getIntent().getIntExtra("placeID", 0));

        imageView = findViewById(R.id.imageView);
        try {
            other_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(place.getImage()));
        } catch (IOException e) {
            other_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);
            e.printStackTrace();
        }
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
                            remakeBitmap(bitmap);
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
        });
    }

    protected Bitmap remakeBitmap(Bitmap bitmap) {
        int width = 800;
        int height = (bitmap.getHeight() * 800) / bitmap.getWidth();
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        int x0, y0 = bitmap.getHeight() - (other_bitmap.getHeight() * 2);
        for (int y = 0; y < other_bitmap.getHeight(); ++y) {
            if (y0 >= bitmap.getHeight()) {
                break;
            }
            x0 = 0;
            for (int x = 0; x < other_bitmap.getWidth(); ++x) {
                if (x0 >= 2 * other_bitmap.getWidth()) {
                    break;
                }
                if (other_bitmap.getPixel(x, y) != other_bitmap.getPixel(0, 0)) {
                    bitmap.setPixel(x0, y0, other_bitmap.getPixel(x, y));
                }
                x0 += 2;
            }
            y0 += 2;
        }
        return bitmap;
    }
}
