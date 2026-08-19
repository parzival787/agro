package com.example.agro; // Declares the package this class belongs to.

import android.os.Bundle; // Imports the Bundle class to pass data between activities.
import android.content.Intent; // Imports the Intent class to perform actions, such as starting a new activity.
import android.util.Log; // Imports the Log class to send log output.
import android.view.View; // Imports the View class, the basic building block for user interface components.
import com.google.android.material.card.MaterialCardView; // Imports MaterialCardView for a customizable card UI element.

import com.google.firebase.auth.FirebaseAuth; // Imports FirebaseAuth for handling user authentication.
import com.google.firebase.auth.FirebaseUser; // Imports FirebaseUser to represent a user's profile.
import com.google.firebase.firestore.FirebaseFirestore; // Imports FirebaseFirestore to interact with the Firestore database.

public class MainActivity extends BaseActivity { // Declares the MainActivity class, which is the main entry point of the app.

    private static final String TAG = "MainActivity"; // Defines a constant for logging purposes.

    private FirebaseAuth mAuth; // Declares a FirebaseAuth instance for authentication.
    private FirebaseFirestore db; // Declares a FirebaseFirestore instance to interact with the database.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Overrides the onCreate method, which is called when the activity is first created.
        super.onCreate(savedInstanceState); // Calls the superclass's onCreate method.
        setContentView(R.layout.activity_main); // Sets the user interface layout for this activity.

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance(); // Gets an instance of FirebaseAuth.
        db = FirebaseFirestore.getInstance(); // Gets an instance of FirebaseFirestore.

        MaterialCardView farmerCard = findViewById(R.id.card_farmer); // Initializes the farmer card from the layout.

        farmerCard.setOnClickListener(new View.OnClickListener() { // Sets a click listener for the farmer card.
            @Override
            public void onClick(View v) { // Called when the farmer card is clicked.
                Intent intent = new Intent(MainActivity.this, UserSignin.class); // Creates an intent to go to the sign-in screen.
                startActivity(intent); // Starts the sign-in activity.
                // Do not call finish() here so the user can press Back to return
            }
        });
    }

    @Override
    protected void onStart() { // Overrides the onStart method, which is called when the activity is about to become visible.
        super.onStart(); // Calls the superclass's onStart method.
        FirebaseUser currentUser = mAuth.getCurrentUser(); // Gets the currently signed-in user.

        if (currentUser != null) { // Checks if a user is already signed in.
            Log.d(TAG, "Farmer " + currentUser.getUid() + " is already logged in. Checking database..."); // Logs that a user is signed in.
            checkFarmerInDatabase(currentUser.getUid()); // Checks if the user exists in the Firestore database.
        } else { // If no user is signed in.
            Log.d(TAG, "No farmer logged in. Showing sign-in screen."); // Logs that no user is signed in.
        }
    }

    // Check if the logged-in user exists in the farmers collection
    private void checkFarmerInDatabase(String uid) { // Defines a method to check for a user in the Firestore database.
        db.collection("farmers").document(uid).get() // Gets the user's document from the "farmers" collection.
                .addOnSuccessListener(doc -> { // Adds a success listener for the database operation.
                    if (doc.exists()) { // Checks if the document exists.
                        Log.d(TAG, "Farmer found in database. Redirecting to FarmerDashboard..."); // Logs that the user was found in the database.
                        redirectToFarmerDashboard(); // Redirects to the farmer dashboard.
                    } else { // If the document does not exist.
                        Log.w(TAG, "Farmer logged in but has no database record. Logging out."); // Logs a warning that the user has no database record.
                        mAuth.signOut(); // Signs the user out.
                    }
                })
                .addOnFailureListener(e -> { // Adds a failure listener for the database operation.
                    Log.e(TAG, "Failed to check farmers collection", e); // Logs the error.
                    mAuth.signOut(); // Signs the user out for safety.
                });
    }

    // Redirect to the Farmer Dashboard
    private void redirectToFarmerDashboard() { // Defines a method to redirect to the farmer dashboard.
        Intent intent = new Intent(MainActivity.this, FarmerDashboardActivity.class); // Creates an intent for the farmer dashboard.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears the activity stack to prevent the user from going back to the main activity.
        startActivity(intent); // Starts the farmer dashboard activity.
        finish(); // Finishes the current activity.
    }
}