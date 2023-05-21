package com.example.project;

import android.content.ContentResolver;
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
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.internal.BaseImplementation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PlaceActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView description;
    private Button button;
    private Bitmap other_bitmap;
    private Places place;
    private Users user;
    private Visiting visit;
    private DBPlaces mDBConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_activity);
        getSupportActionBar().hide();

        mDBConnector = new DBPlaces(this);
        place = mDBConnector.selectPlaces(getIntent().getLongExtra("placeID", 0));
        user = mDBConnector.selectUsers(getIntent().getLongExtra("userID", 0));
        visit = mDBConnector.selectVisiting(user);

        imageView = findViewById(R.id.imageView);
        description = findViewById(R.id.textView);
        description.setText(place.getDescription());

        other_bitmap = BitmapFactory.decodeFile(place.getImage());
        int width = 1100;
        int height = (other_bitmap.getHeight() * 1100) / other_bitmap.getWidth();
        other_bitmap = Bitmap.createScaledBitmap(other_bitmap, width, height, false);

        if (visit == null) {
            imageView.setImageBitmap(other_bitmap);
        } else {
            imageView.setImageBitmap(BitmapFactory.decodeFile(visit.getImages()));
        }

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                            bitmap = remakeBitmap(bitmap);
                            try {
                                File file = saveFile(bitmap);
                                mDBConnector.insertVisiting(user.getId(), place.getId(), file.getPath());
                                visit = mDBConnector.selectVisiting(user);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
        int width = 2200;
        int height = other_bitmap.getHeight() * 2;
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        int x0, y0 = 0;
        for (int y = 0; y < other_bitmap.getHeight(); ++y) {
            if (y0 >= bitmap.getHeight()) {
                break;
            }
            x0 = 0;
            for (int x = 0; x < other_bitmap.getWidth(); ++x) {
                if (x0 >= 2 * other_bitmap.getWidth()) {
                    break;
                }
                bitmap.setPixel(x0, y0, other_bitmap.getPixel(x, y));
                x0 += 2;
            }
            y0 += 2;
        }
        return bitmap;
    }

    private File saveFile(Bitmap bitmap) throws IOException {
        File file = new File(getCacheDir(), "userImage" + user.getId());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return file;
    }
}
