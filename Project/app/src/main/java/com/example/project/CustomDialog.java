package com.example.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CustomDialog extends AlertDialog {
    public MainActivity c;
    private Button yes, no;
    private EditText address_input, description_input, image_input;
    private String address, description, image;

    public CustomDialog(MainActivity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dialog);
        yes = findViewById(R.id.confirm);
        no = findViewById(R.id.cancel);
        address_input = findViewById(R.id.address_input);
        description_input = findViewById(R.id.description_input);
        image_input = findViewById(R.id.image_input);

        no.setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v)
                {
                    dismiss();
                }
            });

        yes.setOnClickListener(
            new View.OnClickListener() {
                @Override public void onClick(View v)
                {
                    address = address_input.getText().toString();
                    description = description_input.getText().toString();
                    image = image_input.getText().toString();
                    if (address == null || description == null || image == null) {
                        dismiss();
                    }
                    c.sendInput(address, description, image);
                    dismiss();
                }
            });
    }
}
