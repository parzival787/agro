package com.example.agro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class activity_crop_management extends BaseActivity {

    Spinner cropSpinner;
    EditText etLandSize;
    Button btnCalculate;
    TextView tvResult;
    CardView cardDisease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_management);

        // 1. Initialize Views
        cropSpinner = findViewById(R.id.cropSpinner);
        etLandSize = findViewById(R.id.etLandSize);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvResult = findViewById(R.id.tvResult);
        cardDisease = findViewById(R.id.cardDisease);

        // 2. Setup Spinner using string-array from resources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.crop_list_mgmt,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cropSpinner.setAdapter(adapter);

        // 3. Button for Calculator
        btnCalculate.setOnClickListener(v -> calculateFertilizer());

        // 4. Link to Disease Detector
        cardDisease.setOnClickListener(v -> {
            Intent intent = new Intent(activity_crop_management.this, DiseaseDetectorActivity.class);
            startActivity(intent);
        });
    }

    private void calculateFertilizer() {
        String sizeStr = etLandSize.getText().toString();
        if (sizeStr.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_land_size), Toast.LENGTH_SHORT).show();
            return;
        }

        double acres = Double.parseDouble(sizeStr);

        // GET SPINNER INDEX AND TEXT
        int cropIndex = cropSpinner.getSelectedItemPosition();
        String cropNameForDisplay = cropSpinner.getSelectedItem().toString();

        double reqN = 0, reqP = 0, reqK = 0;

        // SWITCH BASED ON INDEX, NOT STRING NAME (Works in any language!)
        switch (cropIndex) {
            case 0:  reqN = 40; reqP = 24; reqK = 24; break; // Rice
            case 1:  reqN = 48; reqP = 24; reqK = 16; break; // Wheat
            case 2:  reqN = 48; reqP = 24; reqK = 20; break; // Maize
            case 3:  reqN = 32; reqP = 16; reqK = 0; break;  // Bajra
            case 4:  reqN = 32; reqP = 16; reqK = 12; break; // Jowar
            case 5:  reqN = 24; reqP = 12; reqK = 12; break; // Ragi
            case 6:  reqN = 8;  reqP = 16; reqK = 0; break;  // Chickpea
            case 7:  reqN = 8;  reqP = 20; reqK = 0; break;  // Pigeon Pea
            case 8:  reqN = 8;  reqP = 16; reqK = 0; break;  // Moong
            case 9:  reqN = 8;  reqP = 16; reqK = 0; break;  // Urad
            case 10: reqN = 8;  reqP = 16; reqK = 0; break;  // Masoor
            case 11: reqN = 10; reqP = 16; reqK = 20; break; // Groundnut
            case 12: reqN = 32; reqP = 16; reqK = 16; break; // Mustard
            case 13: reqN = 12; reqP = 32; reqK = 24; break; // Soybean
            case 14: reqN = 24; reqP = 30; reqK = 24; break; // Sunflower
            case 15: reqN = 16; reqP = 10; reqK = 0; break;  // Sesame
            case 16: reqN = 100; reqP = 40; reqK = 36; break;// Sugarcane
            case 17: reqN = 48; reqP = 24; reqK = 24; break; // Cotton
            case 18: reqN = 24; reqP = 10; reqK = 10; break; // Jute
            case 19: reqN = 40; reqP = 20; reqK = 40; break; // Tobacco
            case 20: reqN = 40; reqP = 20; reqK = 40; break; // Tea
            case 21: reqN = 60; reqP = 40; reqK = 60; break; // Coffee
            case 22: reqN = 60; reqP = 40; reqK = 40; break; // Potato
            case 23: reqN = 48; reqP = 32; reqK = 24; break; // Tomato
            case 24: reqN = 40; reqP = 20; reqK = 20; break; // Onion
            case 25: reqN = 48; reqP = 24; reqK = 24; break; // Chilli
            case 26: reqN = 40; reqP = 20; reqK = 20; break; // Brinjal
            case 27: reqN = 40; reqP = 20; reqK = 20; break; // Okra
            case 28: reqN = 50; reqP = 30; reqK = 30; break; // Cabbage/Cauliflower
            case 29: reqN = 100; reqP = 40; reqK = 100; break;// Banana
            case 30: reqN = 40;  reqP = 20; reqK = 40; break; // Mango
            case 31: reqN = 60; reqP = 30; reqK = 60; break; // Citrus
        }

        // --- CONVERSION FORMULA ---
        double dapKg = (reqP / 0.46) * acres;
        double nFromDap = dapKg * 0.18;
        double mopKg = (reqK / 0.60) * acres;

        double remainingN = (reqN * acres) - nFromDap;
        if(remainingN < 0) remainingN = 0;
        double ureaKg = remainingN / 0.46;

        // Display using localized string format
        String resultText = getString(R.string.fertilizer_result_format, cropNameForDisplay, acres, ureaKg, dapKg, mopKg);
        tvResult.setText(resultText);
    }
}