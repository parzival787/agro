package com.example.agro; // Declares the package this class belongs to.

import android.content.Intent; // Imports the Intent class to perform actions, such as starting a new activity.
import android.os.Bundle; // Imports the Bundle class to pass data between activities.
import android.view.View; // Imports the View class, the basic building block for user interface components.
import androidx.cardview.widget.CardView; // Imports CardView for a customizable card UI element.

public class MarketplaceActivity extends BaseActivity { // Declares the MarketplaceActivity class.

    // 1. Declare all 6 cards
    CardView cardEnam, cardAgmarknet, cardApeda, cardMkisan, cardSfac, cardNafed; // Declares CardView variables for the different marketplace options.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Overrides the onCreate method, which is called when the activity is first created.
        super.onCreate(savedInstanceState); // Calls the superclass's onCreate method.
        setContentView(R.layout.activity_marketplace); // Sets the user interface layout for this activity.

        // 2. Initialize all views
        cardEnam = findViewById(R.id.cardEnam); // Initializes the e-NAM card.
        cardAgmarknet = findViewById(R.id.cardAgmarknet); // Initializes the Agmarknet card.
        cardApeda = findViewById(R.id.cardApeda); // Initializes the APEDA card.
        cardMkisan = findViewById(R.id.cardMkisan); // Initializes the mKisan card.
        cardSfac = findViewById(R.id.cardSfac); // Initializes the SFAC card.
        cardNafed = findViewById(R.id.cardNafed); // Initializes the NAFED card.

        // 3. Set Click Listeners with correct URLs

        // e-NAM (National Agriculture Market)
        cardEnam.setOnClickListener(v -> openWebsite("https://enam.gov.in/web/")); // Sets a click listener for the e-NAM card to open the corresponding website.

        // Agmarknet (Market Prices)
        cardAgmarknet.setOnClickListener(v -> openWebsite("https://agmarknet.gov.in/")); // Sets a click listener for the Agmarknet card to open the corresponding website.

        // APEDA (Exports)
        cardApeda.setOnClickListener(v -> openWebsite("https://apeda.gov.in/apedawebsite/")); // Sets a click listener for the APEDA card to open the corresponding website.

        // mKisan (Advisories & Services)
        cardMkisan.setOnClickListener(v -> openWebsite("https://mkisan.gov.in/")); // Sets a click listener for the mKisan card to open the corresponding website.

        // SFAC (Small Farmers Agribusiness Consortium)
        cardSfac.setOnClickListener(v -> openWebsite("https://sfacindia.com/")); // Sets a click listener for the SFAC card to open the corresponding website.

        // NAFED e-Samridhi (Pulse/Oilseed Procurement)
        cardNafed.setOnClickListener(v -> openWebsite("https://esamridhi.in/")); // Sets a click listener for the NAFED card to open the corresponding website.
    }

    private void openWebsite(String websiteUrl) { // Defines a method to open a website in a WebViewActivity.
        Intent intent = new Intent(MarketplaceActivity.this, WebViewActivity.class); // Creates an intent to start the WebViewActivity.
        intent.putExtra("url", websiteUrl); // Adds the URL to be loaded as an extra to the intent.
        startActivity(intent); // Starts the WebViewActivity.
    }
}