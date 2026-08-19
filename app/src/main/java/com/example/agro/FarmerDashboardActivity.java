package com.example.agro; // Declares the package this class belongs to.

import android.content.Intent; // Imports the Intent class to perform actions, such as starting a new activity.
import android.os.Bundle; // Imports the Bundle class to pass data between activities.
import android.view.MenuItem; // Imports the MenuItem class to represent an item in a menu.
import android.view.View; // Imports the View class, the basic building block for user interface components.
import android.widget.TextView; // Imports the TextView class to display text.
import android.widget.Toast; // Imports the Toast class to show short, transient messages to the user.

import androidx.activity.OnBackPressedCallback; // Imports OnBackPressedCallback to handle back button presses.
import androidx.annotation.NonNull; // Imports the NonNull annotation to indicate a value cannot be null.
import androidx.appcompat.app.ActionBarDrawerToggle; // Imports ActionBarDrawerToggle to link a DrawerLayout and a Toolbar.
import androidx.appcompat.widget.Toolbar; // Imports the Toolbar class for a customizable app bar.
import androidx.core.view.GravityCompat; // Imports GravityCompat for handling gravity constants.
import androidx.drawerlayout.widget.DrawerLayout; // Imports DrawerLayout for creating a navigation drawer.
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout; // Imports SwipeRefreshLayout for a pull-to-refresh UI pattern.

import com.google.android.material.card.MaterialCardView; // Imports MaterialCardView for a customizable card UI element.
import com.google.android.material.navigation.NavigationView; // Imports NavigationView to create a navigation drawer menu.
import com.google.firebase.auth.FirebaseAuth; // Imports FirebaseAuth for handling user authentication.
import com.google.firebase.auth.FirebaseUser; // Imports FirebaseUser to represent a user's profile.
import com.google.firebase.firestore.FirebaseFirestore; // Imports FirebaseFirestore to interact with the Firestore database.

public class FarmerDashboardActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener { // Declares the FarmerDashboardActivity class, which inherits from BaseActivity and implements an interface for navigation item selection.

    private FirebaseAuth mAuth; // Declares a FirebaseAuth instance for authentication.
    private TextView welcomeTextView; // Declares a TextView to display a welcome message.
    private DrawerLayout drawerLayout; // Declares a DrawerLayout for the navigation drawer.

    // --- ADDED FOR SWIPE REFRESH ---
    private FirebaseFirestore db; // Declares a FirebaseFirestore instance to interact with the database.
    private SwipeRefreshLayout swipeRefreshLayout; // Declares a SwipeRefreshLayout for the pull-to-refresh functionality.
    private NavigationView navigationView; // Declares a NavigationView for the navigation drawer.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Overrides the onCreate method, which is called when the activity is first created.
        super.onCreate(savedInstanceState); // Calls the superclass's onCreate method.
        setContentView(R.layout.activity_farmer_dashboard); // Sets the user interface layout for this activity.

        Toolbar toolbar = findViewById(R.id.toolbar); // Initializes the Toolbar.
        setSupportActionBar(toolbar); // Sets the Toolbar as the app bar for this activity.

        drawerLayout = findViewById(R.id.drawer_layout); // Initializes the DrawerLayout.

        // --- UPDATED: Assigned to class-level variables ---
        navigationView = findViewById(R.id.nav_view); // Initializes the NavigationView.
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Initializes the SwipeRefreshLayout.

        navigationView.setNavigationItemSelectedListener(this); // Sets a listener to handle navigation item selections.

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close); // Creates a toggle for the navigation drawer.
        drawerLayout.addDrawerListener(toggle); // Adds the toggle as a listener to the DrawerLayout.
        toggle.syncState(); // Synchronizes the state of the toggle with the DrawerLayout.

        mAuth = FirebaseAuth.getInstance(); // Gets an instance of FirebaseAuth.
        db = FirebaseFirestore.getInstance(); // Gets an instance of FirebaseFirestore.
        welcomeTextView = findViewById(R.id.tv_farmer_name); // Initializes the welcome TextView.

        // --- ADDED: Initial Data Load ---
        loadUserProfileData(); // Loads the user's profile data.

        // --- ADDED: Pull-to-Refresh Logic ---
        swipeRefreshLayout.setOnRefreshListener(() -> { // Sets a listener to be invoked when a refresh is triggered.
            loadUserProfileData(); // Reloads the user's profile data.
        });

        MaterialCardView cropManagementCard = findViewById(R.id.card_crop_management); // Initializes the crop management card.
        cropManagementCard.setOnClickListener(v -> { // Sets a click listener for the crop management card.
            Toast.makeText(this, "Crop Management Clicked", Toast.LENGTH_SHORT).show(); // Shows a toast message.
            startActivity(new Intent(this, activity_crop_management.class)); // Starts the crop management activity.
        });

        MaterialCardView marketplaceCard = findViewById(R.id.card_marketplace); // Initializes the marketplace card.
        marketplaceCard.setOnClickListener(v -> { // Sets a click listener for the marketplace card.
            Toast.makeText(this, "Marketplace Clicked", Toast.LENGTH_SHORT).show(); // Shows a toast message.
            startActivity(new Intent(this, MarketplaceActivity.class)); // Starts the marketplace activity.
        });

        MaterialCardView schemesCard = findViewById(R.id.card_schemes); // Initializes the schemes card.
        schemesCard.setOnClickListener(v -> { // Sets a click listener for the schemes card.
            Toast.makeText(this, "Government Schemes Clicked", Toast.LENGTH_SHORT).show(); // Shows a toast message.
            startActivity(new Intent(this, SchemesActivity.class)); // Starts the schemes activity.
        });

        MaterialCardView weatherCard = findViewById(R.id.card_weather); // Initializes the weather card.
        weatherCard.setOnClickListener(v -> { // Sets a click listener for the weather card.
            Toast.makeText(this, "Weather Clicked", Toast.LENGTH_SHORT).show(); // Shows a toast message.
            startActivity(new Intent(this, WeatherActivity.class)); // Starts the weather activity.
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) { // Adds a callback to handle back button presses.
            @Override
            public void handleOnBackPressed() { // Called when the back button is pressed.
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) { // Checks if the navigation drawer is open.
                    drawerLayout.closeDrawer(GravityCompat.START); // Closes the navigation drawer if it is open.
                } else { // If the drawer is not open.
                    if (isEnabled()) { // Checks if the callback is enabled.
                        setEnabled(false); // Disables the callback to avoid an infinite loop.
                        getOnBackPressedDispatcher().onBackPressed(); // Triggers the default back button behavior.
                    }
                }
            }
        });
    }

    // --- ADDED: Method to fetch data from Firestore ---
    private void loadUserProfileData() { // Defines a method to load user profile data from Firestore.
        FirebaseUser currentUser = mAuth.getCurrentUser(); // Gets the currently signed-in user.
        if (currentUser != null) { // Checks if a user is signed in.
            String userId = currentUser.getUid(); // Gets the user's unique ID.

            View headerView = navigationView.getHeaderView(0); // Gets the header view from the navigation drawer.
            TextView navUserName = headerView.findViewById(R.id.nav_user_name); // Initializes the TextView for the user's name in the navigation header.
            TextView navUserEmail = headerView.findViewById(R.id.nav_user_email); // Initializes the TextView for the user's email in the navigation header.

            // Set email from Auth immediately
            if (currentUser.getEmail() != null) { // Checks if the user has an email address.
                navUserEmail.setText(currentUser.getEmail()); // Sets the email TextView to the user's email.
            }

            // Fetch the updated name from Firestore
            db.collection("farmers").document(userId).get() // Gets the user's document from the "farmers" collection.
                    .addOnSuccessListener(documentSnapshot -> { // Adds a success listener for the database operation.
                        if (documentSnapshot.exists()) { // Checks if the document exists.
                            String farmerName = documentSnapshot.getString("farmerName"); // Gets the farmer's name from the document.
                            if (farmerName != null && !farmerName.isEmpty()) { // Checks if the name is not null or empty.
                                welcomeTextView.setText(farmerName); // Sets the welcome TextView to the farmer's name.
                                navUserName.setText(farmerName); // Sets the navigation header's name TextView to the farmer's name.
                            }
                        }
                        // Stop the loading spinner
                        swipeRefreshLayout.setRefreshing(false); // Hides the refresh indicator.
                    })
                    .addOnFailureListener(e -> { // Adds a failure listener for the database operation.
                        Toast.makeText(this, "Failed to load name", Toast.LENGTH_SHORT).show(); // Shows a toast message indicating the failure.
                        // Stop the loading spinner even on failure
                        swipeRefreshLayout.setRefreshing(false); // Hides the refresh indicator.
                    });
        } else { // If no user is signed in.
            swipeRefreshLayout.setRefreshing(false); // Hides the refresh indicator.
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) { // Overrides the method to handle navigation item selections.
        int itemId = item.getItemId(); // Gets the ID of the selected menu item.
        if (itemId == R.id.nav_profile) { // Checks if the "Profile" item was selected.
            Toast.makeText(this, "Profile selected", Toast.LENGTH_SHORT).show(); // Shows a toast message.
            startActivity(new Intent(this, ProfileActivity.class)); // Starts the profile activity.
        } else if (itemId == R.id.nav_settings) { // Checks if the "Settings" item was selected.
            startActivity(new Intent(this, SettingsActivity.class)); // Starts the settings activity.
        } else if (itemId == R.id.nav_logout) { // Checks if the "Logout" item was selected.
            mAuth.signOut(); // Signs the user out.
            Intent intent = new Intent(FarmerDashboardActivity.this, UserSignin.class); // Creates an intent to go to the sign-in screen.
            startActivity(intent); // Starts the sign-in activity.
            finish(); // Finishes the current activity.
        }

        drawerLayout.closeDrawer(GravityCompat.START); // Closes the navigation drawer.
        return true; // Returns true to indicate that the item selection has been handled.
    }
}