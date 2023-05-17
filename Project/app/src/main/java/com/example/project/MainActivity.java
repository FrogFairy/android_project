package com.example.project;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObject;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;


import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {
    private Button add;
    private Button update;
    private MapView mapView;
    private TextView textView;
    private Button place;
    private DBPlaces mDBConnector;
    private CustomDialog dialog;

    private FusedLocationProviderClient fusedLocationClient;
    private double latitude = 55.751574;
    private double longitude = 37.573856;
    private PlacemarkMapObject curPosition;
    private PlacemarkMapObject position;
    private MapObjectTapListener markerListener;

    private String address;
    private double lat;
    private double lon;
    private Scanner pos;
    private GeocoderResponse.Geocoder map;
    private String description;
    private String image;

    private int height;
    private int width;

    private AsyncTask<String , Void ,String> get;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("229b21d7-d218-4f3b-92b7-674b72185dd1");
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        mDBConnector = new DBPlaces(this);

        dialog = new CustomDialog(this);

        textView = findViewById(R.id.textView);

        // создание карты
        mapView = (MapView) findViewById(R.id.mapView);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getMap();
        setPoints();

        // нажатия на кнопки
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.update:
                        // определение местоположения и добавление метки на карту
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                        get_location();
                        break;
                    case R.id.place:
                        startActivity(new Intent(MainActivity.this, PlaceActivity.class));
                        break;
                    case R.id.add:
                        dialog.show();
                        View view = getLayoutInflater().inflate(R.layout.add_dialog, null);
                        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.addDialogLayout);
                        dialog.getWindow().setLayout(width, layout.getMeasuredHeight());
                        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        break;
                }

            }
        };
        add = findViewById(R.id.add);
        update = findViewById(R.id.update);
        place = findViewById(R.id.place);
        add.setOnClickListener(listener);
        update.setOnClickListener(listener);
        place.setOnClickListener(listener);
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

    protected void getMap() {
        mapView.getMap().move(
                new CameraPosition(new Point(latitude, longitude), 16.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        if (curPosition == null) {
            curPosition = mapView.getMap().getMapObjects().addPlacemark(new Point(latitude, longitude));
            curPosition.setUserData("Ваше местоположение");
            markerListener = new MapObjectTapListener() {
                @Override
                public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                    textView.setText(mapObject.getUserData().toString());
                    if (!textView.getText().toString().equals("Ваше местоположение")) {
                        place.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
            };
            curPosition.addTapListener(markerListener);
            curPosition.setIcon(ImageProvider.fromResource(MainActivity.this, R.drawable.cur_marker));
        } else {
            curPosition.setGeometry(new Point(latitude, longitude));
        }
    }

    protected void setPoints() {
        ArrayList<Places> md = mDBConnector.selectAllPlaces();
        for (Places p: md) {
            position = mapView.getMap().getMapObjects().addPlacemark(new Point(p.getLatitude(), p.getLongitude()));
            position.setUserData(p.getAddress());
            position.addTapListener(markerListener);
            position.setIcon(ImageProvider.fromResource(MainActivity.this, R.drawable.marker));
        }
    }

    protected void get_location() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
                getMap();
            }
        });
    }

    public void sendInput(String address, String description, String image) {
        this.address = address;
        this.description = description;
        this.image = image;

        // изменение адреса в соответствии с форматом
        this.address = address.replace(" ", "+");

        try {
            // определение широты и долготы по адресу
            get = new GetMethod();
            get.execute("https://geocode-maps.yandex.ru/1.x", this.address,
                    "76d3a208-8282-4af2-b4f1-f749cdb7c2ad", "json");
            parse(get.get());
            pos = new Scanner(this.map.response.GeoObjectCollection.featureMember.get(0).GeoObject.Point.pos
                    .replace(".", ","));
            lon = pos.nextDouble();
            lat = pos.nextDouble();

            mDBConnector.insertPlaces(address, (float) lat, (float) lon, this.description, this.image);
            setPoints();
        } catch (Exception e) {
            Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public class GetMethod extends AsyncTask<String , Void ,String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url;
            URLConnection myConnection;
            StringBuilder result = new StringBuilder("");
            try {
                String myData = "apikey=" + strings[2] + "&geocode=" + strings[1] +
                        "&format=" + strings[3];
                url = new URL(strings[0] + "?" + myData);
                myConnection = url.openConnection();

                InputStream in = myConnection.getInputStream();
                InputStreamReader isw = new InputStreamReader(in);
                int data = isw.read();

                while (data != -1) {
                    char current = (char) data;
                    data = isw.read();
                    result.append(current);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                Toast.makeText(MainActivity.this, "Неверный адрес", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Неверный адрес", Toast.LENGTH_SHORT).show();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    public void parse(String result) {
        /*int maxLogSize = 1000;
        for(int i = 0; i <= result.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > result.length() ? result.length() : end;
            Log.d("MyApp", result.substring(start, end));
        }*/
        this.map = new Gson().fromJson(result, GeocoderResponse.Geocoder.class);
    }
}