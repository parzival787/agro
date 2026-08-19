package com.example.agro; // Declares the package this class belongs to.

import android.content.Intent; // Imports the Intent class to perform actions, such as starting a new activity.
import android.net.Uri; // Imports the Uri class to represent a URI reference.
import android.os.Bundle; // Imports the Bundle class to pass data between activities.
import androidx.cardview.widget.CardView; // Imports CardView for a customizable card UI element.

public class SchemesActivity extends BaseActivity { // Declares the SchemesActivity class.

    CardView cardHelpline, cardPmKisan, cardKcc, cardPmfby, cardPmksy,
            cardPkvy, cardSoil, cardKusum, cardLivestock, cardFisheries,
            cardSvamitva, cardInfra; // Declares CardView variables for the different government schemes.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Overrides the onCreate method, which is called when the activity is first created.
        super.onCreate(savedInstanceState); // Calls the superclass's onCreate method.
        setContentView(R.layout.activity_schemes); // Sets the user interface layout for this activity.

        // --- 1. Initialize Views ---
        cardHelpline = findViewById(R.id.cardHelpline); // Initializes the helpline card.
        cardPmKisan = findViewById(R.id.cardPmKisan); // Initializes the PM-KISAN card.
        cardKcc = findViewById(R.id.cardKcc); // Initializes the Kisan Credit Card card.
        cardPmfby = findViewById(R.id.cardPmfby); // Initializes the PMFBY card.
        cardPmksy = findViewById(R.id.cardPmksy); // Initializes the PMKSY card.
        cardPkvy = findViewById(R.id.cardPkvy); // Initializes the PKVY card.
        cardSoil = findViewById(R.id.cardSoil); // Initializes the Soil Health Card card.
        cardKusum = findViewById(R.id.cardKusum); // Initializes the PM-KUSUM card.
        cardLivestock = findViewById(R.id.cardLivestock); // Initializes the National Livestock Mission card.
        cardFisheries = findViewById(R.id.cardFisheries); // Initializes the PMMSY card.
        cardSvamitva = findViewById(R.id.cardSvamitva); // Initializes the SVAMITVA card.
        cardInfra = findViewById(R.id.cardInfra); // Initializes the Agri Infra Fund card.

        // --- 2. Set Click Listeners with WORKING GOVT LINKS ---

        // 1. Helpline (Opens Phone Dialer)
        cardHelpline.setOnClickListener(v -> { // Sets a click listener for the helpline card.
            Intent intent = new Intent(Intent.ACTION_DIAL); // Creates an intent to open the phone dialer.
            intent.setData(Uri.parse("tel:18001801551")); // Sets the phone number to be dialed.
            startActivity(intent); // Starts the dialer activity.
        });

        // 2. PM-KISAN (Check Status / Apply)
        // Official Portal: pmkisan.gov.in
        cardPmKisan.setOnClickListener(v -> openWebsite("https://pmkisan.gov.in/")); // Sets a click listener for the PM-KISAN card to open the corresponding website.

        // 3. Kisan Credit Card (Info & Apply)
        // Best Portal: MyScheme (Aggregator) or individual banks. MyScheme is safest.
        cardKcc.setOnClickListener(v -> openWebsite("https://www.myscheme.gov.in/schemes/kcc")); // Sets a click listener for the KCC card to open the corresponding website.

        // 4. PMFBY (Crop Insurance Claim/Calculator)
        // Official Portal: pmfby.gov.in
        cardPmfby.setOnClickListener(v -> openWebsite("https://pmfby.gov.in/")); // Sets a click listener for the PMFBY card to open the corresponding website.

        // 5. PMKSY (Per Drop More Crop - Irrigation)
        // Official Portal: pmksy.gov.in
        cardPmksy.setOnClickListener(v -> openWebsite("https://pmksy.gov.in/")); // Sets a click listener for the PMKSY card to open the corresponding website.

        // 6. PKVY (Organic Farming)
        // Official Portal: dms.jaivikheti.in
        cardPkvy.setOnClickListener(v -> openWebsite("https://www.pgsindia-ncof.gov.in/")); // Sets a click listener for the PKVY card to open the corresponding website.

        // 7. Soil Health Card (Download Report)
        // Official Portal: soilhealth.dac.gov.in
        cardSoil.setOnClickListener(v -> openWebsite("https://soilhealth.dac.gov.in/home")); // Sets a click listener for the Soil Health Card card to open the corresponding website.

        // 8. PM-KUSUM (Solar Pumps)
        // Official Portal: pmkusum.mnre.gov.in
        cardKusum.setOnClickListener(v -> openWebsite("https://pmkusum.mnre.gov.in/")); // Sets a click listener for the PM-KUSUM card to open the corresponding website.

        // 9. National Livestock Mission (Subsidy for Goats/Poultry)
        // Official Portal: nlm.udyamimitra.in (Application Portal)
        cardLivestock.setOnClickListener(v -> openWebsite("https://nlm.udyamimitra.in/")); // Sets a click listener for the National Livestock Mission card to open the corresponding website.

        // 10. Fisheries - PMMSY (Fish Farming)
        // Official Portal: pmmsy.dof.gov.in
        cardFisheries.setOnClickListener(v -> openWebsite("https://pmmsy.dof.gov.in/")); // Sets a click listener for the PMMSY card to open the corresponding website.

        // 11. SVAMITVA (Property Cards)
        // Official Portal: svamitva.nic.in
        cardSvamitva.setOnClickListener(v -> openWebsite("https://svamitva.nic.in/")); // Sets a click listener for the SVAMITVA card to open the corresponding website.

        // 12. Agri Infra Fund (Warehousing Loans)
        // Official Portal: agriinfra.dac.gov.in
        cardInfra.setOnClickListener(v -> openWebsite("https://agriinfra.dac.gov.in/")); // Sets a click listener for the Agri Infra Fund card to open the corresponding website.
    }

    private void openWebsite(String websiteUrl) { // Defines a method to open a website in a WebViewActivity.
        Intent intent = new Intent(SchemesActivity.this, WebViewActivity.class); // Creates an intent to start the WebViewActivity.
        intent.putExtra("url", websiteUrl); // Adds the URL to be loaded as an extra to the intent.
        startActivity(intent); // Starts the WebViewActivity.
    }
}