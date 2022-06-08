package com.example.m8uf2recu;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m8uf2recu.Retrofit.ApiCall;
import com.example.m8uf2recu.Retrofit.ApiURL;
import com.example.m8uf2recu.Retrofit.Model.ModelApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.m8uf2recu.databinding.ActivityMapsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FloatingActionButton btnRecord;
    ImageButton btnSearch;
    EditText txt;
    TextView displayName, displayMain2, displayWeather, displayMain;
    private SpeechRecognizer speechRecognizer;
    private int recordAudioRequestCode = 1;
    LatLng barcelona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check for permission
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO}, recordAudioRequestCode);
            }
        }

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnRecord = findViewById(R.id.btnRecord);
        btnSearch = findViewById(R.id.btnSearch1);
        txt = findViewById(R.id.idName);
        displayName = findViewById(R.id.txtDisplayName);
        displayWeather = findViewById(R.id.txtDisplayWeather);
        displayMain = findViewById(R.id.txtDisplayMain);
        displayMain2 = findViewById(R.id.txtDisplayMain2);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        btnSearch.setOnClickListener(e->{ getData(); });


    }

    public void getData(){
        ApiURL apiURL = new ApiURL();
        Retrofit retrofit = apiURL.getClient();
        ApiCall apiCall = retrofit.create(ApiCall.class);
        Call<ModelApi> call = apiCall.getData(txt.getText().toString(), "835babac7f4d7e37f8f51a1abac4fe63");

        call.enqueue(new Callback<ModelApi>(){
            @Override
            public void onResponse(Call<ModelApi> call, Response<ModelApi> response) {
                if(response.code()!=200){
                    Log.i("testApi", "checkConnection");
                    return;
                }

                Log.i("testApi", response.body().getName() + " - " + response.body().getCoord().getLat());

                if(response.isSuccessful()){
                    displayName.setText(response.body().getName());
                    displayMain.setText(String.valueOf(response.body().getCoord().getLat()));
                    displayMain2.setText(String.valueOf(response.body().getCoord().getAlt()));
                    displayWeather.setText(String.valueOf(response.body().getWeather().get(0)));

                    barcelona = new LatLng(response.body().getCoord().getLat(), response.body().getCoord().getAlt());
                    onChangePosition(mMap, barcelona);
                    mMap.addMarker(new MarkerOptions().position(barcelona).title("Marker in Barcelona"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(barcelona));

                }
            }

            @Override
            public void onFailure(Call<ModelApi> call, Throwable t) {

            }
        });
    }

    public void onChangePosition(GoogleMap mMap, LatLng latLng){
        double angle = 130.0; // rotation angle


        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(latLng);
        // Clears the previously touched position
        mMap.clear();
        // Animating to the touched position
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);


    }

    //voice recognition
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == recordAudioRequestCode && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void voiceSearchOption(){
        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hola, digues quelcom!");

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                displayName.setText(data.get(0));
                Log.i("voice", "voice captured: " + data.get(0));
                getData();
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }

        });

        btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) speechRecognizer.stopListening();

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });
    }


}