package com.example.agro; // Declares the package this class belongs to.

import android.content.Context; // Imports the Context class, which provides information about the application environment.
import android.content.Intent; // Imports the Intent class to perform actions, such as starting a new activity.
import android.net.ConnectivityManager; // Imports ConnectivityManager to check the network status.
import android.net.NetworkCapabilities; // Imports NetworkCapabilities to get information about the network.
import android.os.Bundle; // Imports the Bundle class to pass data between activities.
import android.text.TextUtils; // Imports TextUtils to check for empty strings.
import android.util.Log; // Imports the Log class to send log output.
import android.view.View; // Imports the View class, the basic building block for user interface components.
import android.widget.ProgressBar; // Imports the ProgressBar class to show progress of an operation.
import android.widget.TextView; // Imports the TextView class to display text.
import android.widget.Toast; // Imports the Toast class to show short, transient messages to the user.

import androidx.annotation.Nullable; // Imports the Nullable annotation to indicate a value can be null.
import androidx.appcompat.app.AppCompatActivity; // Imports AppCompatActivity for a modern Android activity with an app bar.

import com.google.android.gms.auth.api.signin.GoogleSignIn; // Imports GoogleSignIn to handle Google Sign-In.
import com.google.android.gms.auth.api.signin.GoogleSignInAccount; // Imports GoogleSignInAccount to represent a signed-in Google account.
import com.google.android.gms.auth.api.signin.GoogleSignInClient; // Imports GoogleSignInClient to interact with the Google Sign-In API.
import com.google.android.gms.auth.api.signin.GoogleSignInOptions; // Imports GoogleSignInOptions to configure Google Sign-In.
import com.google.android.gms.common.api.ApiException; // Imports ApiException to handle errors from Google APIs.
import com.google.android.gms.tasks.Task; // Imports the Task class to represent an asynchronous operation.
import com.google.android.material.button.MaterialButton; // Imports MaterialButton for a customizable button with Material Design styles.
import com.google.android.material.textfield.TextInputEditText; // Imports TextInputEditText for an editable text field with Material Design styles.
import com.google.android.material.textfield.TextInputLayout; // Imports TextInputLayout to wrap a a TextInputEditText and show a hint and error messages.

import com.google.firebase.auth.AuthCredential; // Imports AuthCredential to represent a credential for Firebase Authentication.
import com.google.firebase.auth.FirebaseAuth; // Imports FirebaseAuth for handling user authentication.
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException; // Imports an exception for invalid credentials.
import com.google.firebase.auth.FirebaseAuthUserCollisionException; // Imports an exception for when a user account already exists.
import com.google.firebase.auth.FirebaseUser; // Imports FirebaseUser to represent a user's profile.
import com.google.firebase.auth.GoogleAuthProvider; // Imports GoogleAuthProvider to handle Google Sign-In credentials.
import com.google.firebase.firestore.FieldValue; // Imports FieldValue to work with server-side timestamps.
import com.google.firebase.firestore.FirebaseFirestore; // Imports FirebaseFirestore to interact with the Firestore database.

import java.util.HashMap; // Imports HashMap to store key-value pairs.
import java.util.Map; // Imports the Map interface for key-value mappings.
import java.util.regex.Pattern; // Imports the Pattern class to work with regular expressions.

public class UserSignin extends AppCompatActivity { // Declares the UserSignin class.

    private static final String TAG = "UserSignin"; // Defines a constant for logging purposes.
    private static final int RC_SIGN_IN = 9001; // Defines a request code for the Google Sign-In flow.

    // --- Regex Patterns for Validation ---
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s.']{2,}$"); // Defines a regex pattern for validating names.
    private static final Pattern LOCATION_PATTERN = Pattern.compile("^[A-Za-z0-9\\s.,#-]{5,}$"); // Defines a regex pattern for validating locations.
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$"); // Defines a regex pattern for validating Indian phone numbers.
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"); // Defines a regex pattern for validating email addresses.
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=\\S+$).{6,}$"); // Defines a regex pattern for validating passwords.

    // --- UI Views ---
    private TextInputLayout tilFarmerName, tilFarmLocation, tilPhone; // Declares TextInputLayouts for input fields.
    private TextInputEditText etFarmerName, etFarmLocation, etPhone, etEmail, etPassword; // Declares TextInputEditTexts for user input.
    private MaterialButton btnAction, btnGoogleSignIn; // Declares MaterialButtons for actions.
    private ProgressBar progressBar; // Declares a ProgressBar to indicate loading.
    private TextView tvToggleMode, tvTitle, tvForgotPassword; // Declares TextViews for various UI elements.

    // --- Firebase & Google ---
    private FirebaseAuth mAuth; // Declares a FirebaseAuth instance.
    private FirebaseFirestore db; // Declares a FirebaseFirestore instance.
    private GoogleSignInClient mGoogleSignInClient; // Declares a GoogleSignInClient.

    // --- State Tracking ---
    private boolean isRegisterMode = true; // A boolean to track whether the UI is in register or login mode.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Overrides the onCreate method.
        super.onCreate(savedInstanceState); // Calls the superclass's method.
        setContentView(R.layout.activity_user_signin); // Sets the layout for this activity.

        db = FirebaseFirestore.getInstance(); // Initializes the FirebaseFirestore instance.
        mAuth = FirebaseAuth.getInstance(); // Initializes the FirebaseAuth instance.

        // --- Initialize UI Components ---
        tilFarmerName = findViewById(R.id.til_farmer_name); // Initializes the farmer name TextInputLayout.
        tilFarmLocation = findViewById(R.id.til_farm_location); // Initializes the farm location TextInputLayout.
        tilPhone = findViewById(R.id.til_phone); // Initializes the phone TextInputLayout.

        etFarmerName = findViewById(R.id.etFarmerName); // Initializes the farmer name EditText.
        etFarmLocation = findViewById(R.id.etFarmLocation); // Initializes the farm location EditText.
        etPhone = findViewById(R.id.etPhone); // Initializes the phone EditText.
        etEmail = findViewById(R.id.etEmail); // Initializes the email EditText.
        etPassword = findViewById(R.id.etPassword); // Initializes the password EditText.

        btnAction = findViewById(R  .id.btnAction); // Initializes the action button.
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn); // Initializes the Google Sign-In button.
        progressBar = findViewById(R.id.progressBar); // Initializes the progress bar.
        tvToggleMode = findViewById(R.id.tvLoginLink); // Initializes the toggle mode TextView.
        tvTitle = findViewById(R.id.tv_title); // Initializes the title TextView.

        tvForgotPassword = findViewById(R.id.tvForgotPassword); // Initializes the "Forgot Password" TextView.
        if (tvForgotPassword == null) tvForgotPassword = new TextView(this); // Creates a new TextView if it's not found in the layout.

        // --- Configure Google Sign-In ---
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // Creates a new GoogleSignInOptions builder.
                .requestIdToken(getString(R.string.default_web_client_id)) // Requests an ID token for Firebase authentication.
                .requestEmail() // Requests the user's email address.
                .build(); // Builds the options.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso); // Gets a GoogleSignInClient with the specified options.

        // --- Check Auto-Login with Role Validation ---
        if (mAuth.getCurrentUser() != null) { // Checks if a user is already signed in.
            validateUserRole(mAuth.getCurrentUser(), false); // Validates the user's role.
            return; // Exits the method.
        }

        updateUI(); // Updates the UI based on the current mode (register or login).

        // --- Set Click Listeners ---
        btnAction.setOnClickListener(v -> handleAuthAction()); // Sets a click listener for the main action button.
        tvToggleMode.setOnClickListener(v -> { // Sets a click listener for the toggle mode TextView.
            isRegisterMode = !isRegisterMode; // Toggles the register/login mode.
            updateUI(); // Updates the UI.
        });
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle()); // Sets a click listener for the Google Sign-In button.

        tvForgotPassword.setOnClickListener(v -> { // Sets a click listener for the "Forgot Password" TextView.
            String email = etEmail.getText().toString().trim(); // Gets the email from the EditText.
            if (TextUtils.isEmpty(email)) { // Checks if the email is empty.
                etEmail.setError("Enter email first to reset password"); // Sets an error on the email EditText.
                return; // Exits the method.
            }
            sendPasswordReset(email); // Sends a password reset email.
        });
    }

    // --- PRIORITY VALIDATION (Check Farmers FIRST) ---
    private void validateUserRole(FirebaseUser user, boolean isNewGoogleUser) { // Defines a method to validate the user's role.
        if (user == null) return; // Exits the method if the user is null.
        String email = user.getEmail(); // Gets the user's email.
        progressBar.setVisibility(View.VISIBLE); // Shows the progress bar.

        db.collection("farmers") // Accesses the "farmers" collection in Firestore.
                .whereEqualTo("email", email) // Queries for documents where the "email" field matches the user's email.
                .get() // Gets the documents.
                .addOnSuccessListener(snapshots -> { // Adds a success listener.
                    if (!snapshots.isEmpty()) { // Checks if any documents were found.
                        // SUCCESS: Already a Farmer -> Login
                        Toast.makeText(UserSignin.this, "Login Successful!", Toast.LENGTH_SHORT).show(); // Shows a success message.
                        navigateToDashboard(); // Navigates to the dashboard.
                    } else { // If no documents were found.
                        // NOT FOUND: Check if they are an Admin (Block)
                        checkIfAdmin(user, isNewGoogleUser); // Checks if the user is an admin.
                    }
                })
                .addOnFailureListener(this::handleAuthFailure); // Adds a failure listener.
    }

    private void checkIfAdmin(FirebaseUser user, boolean isNewGoogleUser) { // Defines a method to check if a user is an admin.
        db.collection("admins") // Accesses the "admins" collection.
                .whereEqualTo("email", user.getEmail()) // Queries for documents with a matching email.
                .get() // Gets the documents.
                .addOnSuccessListener(snapshots -> { // Adds a success listener.
                    if (!snapshots.isEmpty()) { // Checks if any documents were found.
                        // BLOCK: This email is an Admin
                        mAuth.signOut(); // Signs the user out.
                        if (mGoogleSignInClient != null) mGoogleSignInClient.signOut(); // Signs the user out of Google.
                        progressBar.setVisibility(View.GONE); // Hides the progress bar.
                        Toast.makeText(UserSignin.this, // Shows a toast message.
                                "Restricted: This account is an Admin. Please use the Admin App.",
                                Toast.LENGTH_LONG).show();
                    } else { // If no documents were found.
                        // ALLOW: User Registration / Login
                        if (isNewGoogleUser) { // Checks if this is a new Google user.
                            // DIRECT LOGIN LOGIC: Save a minimal profile without forcing them to fill location/phone.
                            saveMinimalGoogleProfile(user); // Saves a minimal profile for the new Google user.
                        } else { // If this is not a new Google user.
                            // Manual login fallback
                            Toast.makeText(UserSignin.this, "Login Successful!", Toast.LENGTH_SHORT).show(); // Shows a success message.
                            navigateToDashboard(); // Navigates to the dashboard.
                        }
                    }
                })
                .addOnFailureListener(this::handleAuthFailure); // Adds a failure listener.
    }

    // DIRECT GOOGLE SIGN-UP: Saves only what Google gives us.
    private void saveMinimalGoogleProfile(FirebaseUser user) { // Defines a method to save a minimal profile for a Google user.
        Map<String, Object> farmerProfile = new HashMap<>(); // Creates a new HashMap to store the profile data.
        // Save the Google Name if available, otherwise "Unknown"
        farmerProfile.put("farmerName", user.getDisplayName() != null ? user.getDisplayName() : "Unknown"); // Adds the user's display name to the profile.
        farmerProfile.put("email", user.getEmail()); // Adds the user's email to the profile.
        farmerProfile.put("createdAt", FieldValue.serverTimestamp()); // Adds a server timestamp for when the profile was created.
        farmerProfile.put("userType", "farmer"); // Sets the user type to "farmer".
        // NOTE: We purposely DO NOT save "farmLocation" or "phone" here.
        // The dashboard or app will handle those null/missing fields later if needed.

        db.collection("farmers").document(user.getUid()).set(farmerProfile) // Sets the profile data in a new document in the "farmers" collection.
                .addOnSuccessListener(aVoid -> { // Adds a success listener.
                    Toast.makeText(UserSignin.this, "Google Sign-Up Successful!", Toast.LENGTH_SHORT).show(); // Shows a success message.
                    navigateToDashboard(); // Navigates to the dashboard.
                })
                .addOnFailureListener(e -> { // Adds a failure listener.
                    progressBar.setVisibility(View.GONE); // Hides the progress bar.
                    Toast.makeText(this, "Failed to setup Google profile.", Toast.LENGTH_SHORT).show(); // Shows a failure message.
                });
    }

    // --- FORGOT PASSWORD ---
    private void sendPasswordReset(String email) { // Defines a method to send a password reset email.
        progressBar.setVisibility(View.VISIBLE); // Shows the progress bar.
        mAuth.sendPasswordResetEmail(email) // Sends a password reset email to the specified address.
                .addOnCompleteListener(task -> { // Adds a completion listener.
                    progressBar.setVisibility(View.GONE); // Hides the progress bar.
                    if (task.isSuccessful()) { // Checks if the email was sent successfully.
                        Toast.makeText(this, "Reset Link Sent! Check your email.", Toast.LENGTH_LONG).show(); // Shows a success message.
                    } else { // If the email failed to send.
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show(); // Shows an error message.
                    }
                });
    }

    private void handleAuthAction() { // Defines a method to handle the main authentication action (register or login).
        if (!isNetworkAvailable()) { // Checks if there is a network connection.
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show(); // Shows a message if there is no network.
            return; // Exits the method.
        }

        if (!validateInputs()) return; // Validates the user's input and exits if invalid.

        progressBar.setVisibility(View.VISIBLE); // Shows the progress bar.
        String email = etEmail.getText().toString().trim(); // Gets the email from the EditText.
        String password = etPassword.getText().toString().trim(); // Gets the password from the EditText.

        if (isRegisterMode) { // Checks if the UI is in register mode.
            // MANUAL REGISTRATION: Forces them to fill all details
            String farmerName = etFarmerName.getText().toString().trim(); // Gets the farmer's name.
            String farmLocation = etFarmLocation.getText().toString().trim(); // Gets the farm location.
            String phone = etPhone.getText().toString().trim(); // Gets the phone number.

            mAuth.createUserWithEmailAndPassword(email, password) // Creates a new user with the specified email and password.
                    .addOnCompleteListener(task -> { // Adds a completion listener.
                        if (task.isSuccessful()) { // Checks if the user was created successfully.
                            checkAdminAndSaveManualProfile(mAuth.getCurrentUser(), farmerName, farmLocation, phone, email); // Checks if the user is an admin and saves their profile.
                        } else { // If user creation failed.
                            handleAuthFailure(task.getException()); // Handles the authentication failure.
                        }
                    });
        } else { // If the UI is in login mode.
            mAuth.signInWithEmailAndPassword(email, password) // Signs the user in with the specified email and password.
                    .addOnCompleteListener(task -> { // Adds a completion listener.
                        if (task.isSuccessful()) { // Checks if the sign-in was successful.
                            validateUserRole(mAuth.getCurrentUser(), false); // Validates the user's role.
                        } else { // If sign-in failed.
                            handleAuthFailure(task.getException()); // Handles the authentication failure.
                        }
                    });
        }
    }

    private void checkAdminAndSaveManualProfile(FirebaseUser user, String farmerName, String farmLocation, String phone, String email) { // Defines a method to check if a user is an admin and save their profile.
        db.collection("admins") // Accesses the "admins" collection.
                .whereEqualTo("email", email) // Queries for documents with a matching email.
                .get() // Gets the documents.
                .addOnSuccessListener(snapshots -> { // Adds a success listener.
                    if (!snapshots.isEmpty()) { // Checks if any documents were found.
                        mAuth.signOut(); // Signs the user out.
                        progressBar.setVisibility(View.GONE); // Hides the progress bar.
                        Toast.makeText(UserSignin.this, "This email is an Admin. Cannot register as Farmer.", Toast.LENGTH_LONG).show(); // Shows a message indicating that the email belongs to an admin.
                    } else { // If no documents were found.
                        saveFarmerProfile(user.getUid(), farmerName, farmLocation, phone, email); // Saves the farmer's profile.
                    }
                })
                .addOnFailureListener(this::handleAuthFailure); // Adds a failure listener.
    }

    // FULL MANUAL REGISTRATION: Saves everything they typed.
    private void saveFarmerProfile(String userId, String farmerName, String farmLocation, String phone, String email) { // Defines a method to save the farmer's profile.
        Map<String, Object> farmerProfile = new HashMap<>(); // Creates a new HashMap to store the profile data.
        farmerProfile.put("farmerName", farmerName); // Adds the farmer's name.
        farmerProfile.put("farmLocation", farmLocation); // Adds the farm location.
        farmerProfile.put("phone", phone); // Adds the phone number.
        farmerProfile.put("email", email); // Adds the email.
        farmerProfile.put("createdAt", FieldValue.serverTimestamp()); // Adds a server timestamp.
        farmerProfile.put("userType", "farmer"); // Sets the user type to "farmer".

        db.collection("farmers").document(userId).set(farmerProfile) // Sets the profile data in a new document in the "farmers" collection.
                .addOnSuccessListener(aVoid -> { // Adds a success listener.
                    Toast.makeText(UserSignin.this, "Registration Successful!", Toast.LENGTH_SHORT).show(); // Shows a success message.
                    navigateToDashboard(); // Navigates to the dashboard.
                })
                .addOnFailureListener(e -> { // Adds a failure listener.
                    progressBar.setVisibility(View.GONE); // Hides the progress bar.
                    Toast.makeText(this, "Failed to save data.", Toast.LENGTH_SHORT).show(); // Shows a failure message.
                });
    }

    private void signInWithGoogle() { // Defines a method to sign in with Google.
        if (!isNetworkAvailable()) { // Checks for network connectivity.
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show(); // Shows a message if there is no network.
            return; // Exits the method.
        }
        progressBar.setVisibility(View.VISIBLE); // Shows the progress bar.
        Intent signInIntent = mGoogleSignInClient.getSignInIntent(); // Gets the Google Sign-In intent.
        startActivityForResult(signInIntent, RC_SIGN_IN); // Starts the Google Sign-In activity.
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // Overrides the method to handle the result from started activities.
        super.onActivityResult(requestCode, resultCode, data); // Calls the superclass's method.
        if (requestCode == RC_SIGN_IN) { // Checks if the result is from the Google Sign-In flow.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data); // Gets the signed-in account from the intent.
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class); // Gets the result of the sign-in task.
                firebaseAuthWithGoogle(account.getIdToken()); // Authenticates with Firebase using the Google ID token.
            } catch (ApiException e) { // Catches exceptions from the Google Sign-In API.
                progressBar.setVisibility(View.GONE); // Hides the progress bar.

                // --- DETAILED GOOGLE SIGN-IN ERROR HANDLING ---
                // Extracts the exact error code to determine WHY the sign-in failed.
                int statusCode = e.getStatusCode(); // Gets the status code of the exception.
                Log.e(TAG, "Google Sign In Failed. Error Code: " + statusCode, e); // Logs the error.

                String errorMsg = "Google Sign In Failed (Code: " + statusCode + ")"; // Creates an error message.

                if (statusCode == 10) { // Checks for a specific error code.
                    /* ERROR CODE 10 MEANS:
                       1. Your SHA-1 fingerprint is missing in the Firebase Console.
                       2. You forgot to download the new google-services.json file after adding the SHA-1.
                       3. The ID in 'R.string.default_web_client_id' is an Android ID instead of a Web Client ID.
                       4. You did not set a 'Support Email' in your Firebase General Settings. */
                    errorMsg = "Setup Error (Code 10): Check SHA-1, Web Client ID, and Firebase JSON.";
                } else if (statusCode == 12501) { // Checks for another specific error code.
                    /* ERROR CODE 12501 MEANS: The user closed/cancelled the Google popup. */
                    errorMsg = "Sign-In cancelled by user.";
                } else if (statusCode == 7) { // Checks for another specific error code.
                    /* ERROR CODE 7 MEANS: No internet access during the handshake. */
                    errorMsg = "Network error. Check your internet connection.";
                }

                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show(); // Shows a toast with the detailed error message.
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) { // Defines a method to authenticate with Firebase using a Google ID token.
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null); // Creates a new Google Auth credential.
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> { // Signs in with the credential.
            if (task.isSuccessful()) { // Checks if the sign-in was successful.
                FirebaseUser user = mAuth.getCurrentUser(); // Gets the current user.
                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser(); // Checks if the user is new.
                validateUserRole(user, isNewUser); // Validates the user's role.
            } else { // If sign-in failed.
                handleAuthFailure(task.getException()); // Handles the authentication failure.
            }
        });
    }

    // --- ENHANCED FIREBASE ERROR HANDLING ---
    private void handleAuthFailure(Exception e) { // Defines a method to handle authentication failures.
        progressBar.setVisibility(View.GONE); // Hides the progress bar.
        if (e instanceof FirebaseAuthInvalidCredentialsException) { // Checks for invalid credentials.
            Toast.makeText(this, "Wrong credentials. Try 'Forgot Password' or Google Sign-In.", Toast.LENGTH_LONG).show(); // Shows a message for wrong credentials.

        } else if (e instanceof FirebaseAuthUserCollisionException) { // Checks for a user collision.
            /* COLLISION EXCEPTION MEANS:
               The user signed up using Google in the past. Today, they typed the exact same Gmail address
               into the manual Register form and hit "Register" with a password. Firebase blocks this to
               protect the account. */
            Toast.makeText(this, "Email already exists! If you used Google before, please click 'Sign Up with Google'.", Toast.LENGTH_LONG).show(); // Shows a message for a user collision.

        } else { // For other exceptions.
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show(); // Shows a generic error message.
        }
    }

    private void updateUI() { // Defines a method to update the UI based on the current mode.
        int visibility = isRegisterMode ? View.VISIBLE : View.GONE; // Sets the visibility based on the register mode.
        tilFarmerName.setVisibility(visibility); // Sets the visibility of the farmer name TextInputLayout.
        tilFarmLocation.setVisibility(visibility); // Sets the visibility of the farm location TextInputLayout.
        tilPhone.setVisibility(visibility); // Sets the visibility of the phone TextInputLayout.

        if (tvForgotPassword != null) { // Checks if the "Forgot Password" TextView is not null.
            tvForgotPassword.setVisibility(isRegisterMode ? View.GONE : View.VISIBLE); // Sets the visibility of the "Forgot Password" TextView.
        }

        if (isRegisterMode) { // Checks if the UI is in register mode.
            tvTitle.setText("Create Account"); // Sets the title to "Create Account".
            btnAction.setText("Register"); // Sets the button text to "Register".
            tvToggleMode.setText("Already have an account? Login"); // Sets the toggle mode text.
        } else { // If the UI is in login mode.
            tvTitle.setText("Login"); // Sets the title to "Login".
            btnAction.setText("Login"); // Sets the button text to "Login".
            tvToggleMode.setText("Don't have an account? Register"); // Sets the toggle mode text.
        }

        etEmail.setError(null); // Clears any errors on the email EditText.
        etPassword.setError(null); // Clears any errors on the password EditText.
    }

    private void navigateToDashboard() { // Defines a method to navigate to the dashboard.
        progressBar.setVisibility(View.GONE); // Hides the progress bar.
        Intent intent = new Intent(UserSignin.this, FarmerDashboardActivity.class); // Creates an intent for the dashboard activity.
        startActivity(intent); // Starts the dashboard activity.
        finish(); // Finishes the current activity.
    }

    private boolean validateInputs() { // Defines a method to validate the user's input.
        String email = etEmail.getText().toString().trim(); // Gets the email.
        String password = etPassword.getText().toString().trim(); // Gets the password.

        if (isRegisterMode) { // Checks if in register mode.
            if (TextUtils.isEmpty(etFarmerName.getText().toString().trim()) || !NAME_PATTERN.matcher(etFarmerName.getText().toString().trim()).matches()) { // Validates the farmer's name.
                etFarmerName.setError("Enter a valid name (at least 2 letters)"); // Sets an error if invalid.
                return false; // Returns false.
            }
            if (TextUtils.isEmpty(etFarmLocation.getText().toString().trim()) || !LOCATION_PATTERN.matcher(etFarmLocation.getText().toString().trim()).matches()) { // Validates the farm location.
                etFarmLocation.setError("Enter a valid farm location (at least 5 characters)"); // Sets an error if invalid.
                return false; // Returns false.
            }
            if (TextUtils.isEmpty(etPhone.getText().toString().trim()) || !PHONE_PATTERN.matcher(etPhone.getText().toString().trim()).matches()) { // Validates the phone number.
                etPhone.setError("Enter a valid 10-digit Indian mobile number"); // Sets an error if invalid.
                return false; // Returns false.
            }
        }

        if (TextUtils.isEmpty(email) || !EMAIL_PATTERN.matcher(email).matches()) { // Validates the email address.
            etEmail.setError("Enter a valid email address"); // Sets an error if invalid.
            return false; // Returns false.
        }

        if (TextUtils.isEmpty(password) || !PASSWORD_PATTERN.matcher(password).matches()) { // Validates the password.
            etPassword.setError("Password must be at least 6 non-whitespace characters"); // Sets an error if invalid.
            return false; // Returns false.
        }

        return true; // Returns true if all inputs are valid.
    }

    private boolean isNetworkAvailable() { // Defines a method to check for network connectivity.
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); // Gets the ConnectivityManager.
        if (cm == null) return false; // Returns false if the manager is null.
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork()); // Gets the network capabilities.
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET); // Returns true if the network is available and has an internet connection.
    }
}