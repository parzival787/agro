package com.example.agro; // Declares the package this class belongs to.

import android.os.Bundle; // Imports the Bundle class to pass data between activities.
import android.text.TextUtils; // Imports TextUtils to check for empty strings.
import android.view.View; // Imports the View class, the basic building block for user interface components.
import android.widget.Button; // Imports the Button class for a clickable UI element.
import android.widget.EditText; // Imports the EditText class for user input.
import android.widget.Toast; // Imports the Toast class to show short, transient messages to the user.

import com.google.firebase.auth.FirebaseAuth; // Imports FirebaseAuth for handling user authentication.
import com.google.firebase.auth.FirebaseUser; // Imports FirebaseUser to represent a user's profile.
import com.google.firebase.auth.UserProfileChangeRequest; // Imports UserProfileChangeRequest to update a user's profile information.
import com.google.firebase.firestore.DocumentReference; // Imports DocumentReference to refer to a specific document in Firestore.
import com.google.firebase.firestore.FirebaseFirestore; // Imports FirebaseFirestore to interact with the Firestore database.

import java.util.HashMap; // Imports HashMap to store key-value pairs.
import java.util.Map; // Imports the Map interface for key-value mappings.

public class ProfileActivity extends BaseActivity { // Declares the ProfileActivity class.

    private EditText etName, etEmail, etPhone, etAddress; // Declares EditText fields for user profile information.
    private Button btnUpdate; // Declares a Button to update the profile.

    private FirebaseAuth mAuth; // Declares a FirebaseAuth instance for authentication.
    private FirebaseFirestore db; // Declares a FirebaseFirestore instance to interact with the database.
    private DocumentReference userRef; // Declares a DocumentReference to point to the user's document in Firestore.
    private String userId; // Declares a string to store the user's ID.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Overrides the onCreate method, which is called when the activity is first created.
        super.onCreate(savedInstanceState); // Calls the superclass's onCreate method.
        setContentView(R.layout.activity_profile); // Sets the user interface layout for this activity.

        // Bind views
        etName = findViewById(R.id.etProfileName); // Initializes the name EditText.
        etEmail = findViewById(R.id.etProfileEmail); // Initializes the email EditText.
        etPhone = findViewById(R.id.etProfilePhone); // Initializes the phone EditText.
        etAddress = findViewById(R.id.etProfileAddress); // Initializes the address EditText.
        btnUpdate = findViewById(R.id.btnUpdateProfile); // Initializes the update button.

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance(); // Gets an instance of FirebaseAuth.
        db = FirebaseFirestore.getInstance(); // Gets an instance of FirebaseFirestore.
        FirebaseUser currentUser = mAuth.getCurrentUser(); // Gets the currently signed-in user.

        if (currentUser != null) { // Checks if a user is signed in.
            userId = currentUser.getUid(); // Gets the user's unique ID.
            // Point to the specific farmer's document in the "farmers" collection
            userRef = db.collection("farmers").document(userId); // Creates a reference to the user's document in the "farmers" collection.

            // Fetch data from Firestore
            loadUserData(); // Loads the user's data from Firestore.
        } else { // If no user is signed in.
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show(); // Shows a toast message indicating that no user is logged in.
            finish(); // Finishes the activity.
        }

        // Set up the update button click listener
        btnUpdate.setOnClickListener(v -> updateProfile()); // Sets a click listener for the update button.
    }

    private void loadUserData() { // Defines a method to load user data from Firestore.
        userRef.get().addOnSuccessListener(documentSnapshot -> { // Gets the user's document from Firestore and adds a success listener.
            if (documentSnapshot.exists()) { // Checks if the document exists.
                // Map the fields exactly as they appear in your Firestore screenshot
                String name = documentSnapshot.getString("farmerName"); // Gets the farmer's name from the document.
                String email = documentSnapshot.getString("email"); // Gets the user's email from the document.
                String phone = documentSnapshot.getString("phone"); // Gets the user's phone number from the document.
                String location = documentSnapshot.getString("farmLocation"); // Gets the user's farm location from the document.

                // Populate the UI
                etName.setText(name != null ? name : ""); // Sets the name EditText to the retrieved name.
                etEmail.setText(email != null ? email : ""); // Sets the email EditText to the retrieved email.
                etPhone.setText(phone != null ? phone : ""); // Sets the phone EditText to the retrieved phone number.
                etAddress.setText(location != null ? location : ""); // Sets the address EditText to the retrieved location.
            } else { // If the document does not exist.
                Toast.makeText(ProfileActivity.this, "Profile document does not exist", Toast.LENGTH_SHORT).show(); // Shows a toast message indicating that the profile document doesn't exist.
            }
        }).addOnFailureListener(e -> { // Adds a failure listener for the database operation.
            Toast.makeText(ProfileActivity.this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // Shows a toast message indicating that the data failed to load.
        });
    }

    private void updateProfile() { // Defines a method to update the user's profile.
        String name = etName.getText().toString().trim(); // Gets the name from the EditText and removes leading/trailing whitespace.
        String phone = etPhone.getText().toString().trim(); // Gets the phone number from the EditText and removes leading/trailing whitespace.
        String address = etAddress.getText().toString().trim(); // Gets the address from the EditText and removes leading/trailing whitespace.

        if (TextUtils.isEmpty(name)) { // Checks if the name is empty.
            etName.setError("Name cannot be empty"); // Sets an error on the name EditText.
            return; // Exits the method.
        }

        // Disable button to prevent multiple clicks during update
        btnUpdate.setEnabled(false); // Disables the update button.
        btnUpdate.setText("Updating..."); // Changes the button text to "Updating...".

        // 1. Update Firebase Auth Display Name (so it shows in the navigation drawer easily)
        FirebaseUser user = mAuth.getCurrentUser(); // Gets the currently signed-in user.
        if (user != null) { // Checks if a user is signed in.
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder() // Creates a new UserProfileChangeRequest.
                    .setDisplayName(name) // Sets the display name.
                    .build(); // Builds the request.
            user.updateProfile(profileUpdates); // Updates the user's profile.
        }

        // 2. Update Cloud Firestore
        Map<String, Object> updates = new HashMap<>(); // Creates a new HashMap to store the updates.
        updates.put("farmerName", name);      // Adds the farmer's name to the updates.
        updates.put("phone", phone);          // Adds the phone number to the updates.
        updates.put("farmLocation", address); // Adds the farm location to the updates.

        userRef.update(updates).addOnSuccessListener(aVoid -> { // Updates the user's document in Firestore and adds a success listener.
            Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show(); // Shows a toast message indicating that the profile was updated successfully.
            finish(); // Finishes the activity.
        }).addOnFailureListener(e -> { // Adds a failure listener for the database operation.
            btnUpdate.setEnabled(true); // Re-enables the update button.
            btnUpdate.setText("Save Changes"); // Resets the button text.
            Toast.makeText(ProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show(); // Shows a toast message indicating that the profile update failed.
        });
    }
}