package com.example.project;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class CustomDialog extends AppCompatActivity {
    private Button yes, no;
    private Button browse;
    private EditText address_input, description_input;
    private ImageView imageView;
    private String address, description;
    private Uri image;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dialog);
        this.setFinishOnTouchOutside(true);

        yes = findViewById(R.id.confirm);
        no = findViewById(R.id.cancel);
        browse = findViewById(R.id.browse);
        address_input = findViewById(R.id.address_input);
        description_input = findViewById(R.id.description_input);
        imageView = findViewById(R.id.imageView);

        no.setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v)
                {
                    finish();
                }
            });

        yes.setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v)
                {
                    address = address_input.getText().toString();
                    description = description_input.getText().toString();
                    if (address == null || description == null) {
                        finish();
                    }
                    Intent i = new Intent();
                    i.putExtra("address", address);
                    i.putExtra("description", description);
                    i.putExtra("image", image);
                    setResult(RESULT_OK, i);
                    finish();
                }
            });

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                image = data.getData();
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                                    int width = 200;
                                    int height = (bitmap.getHeight() * 200) / bitmap.getWidth();
                                    bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

                                    imageView.setVisibility(View.VISIBLE);
                                    imageView.setImageBitmap(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

        browse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);
            }
        });
    }
}
