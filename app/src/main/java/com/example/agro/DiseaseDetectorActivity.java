package com.example.agro; // Declares the package this class belongs to.

import android.Manifest; // Imports the Manifest class to access Android permissions.
import android.content.Intent; // Imports the Intent class to perform actions, such as starting a new activity.
import android.content.pm.PackageManager; // Imports the PackageManager class to check for permissions.
import android.graphics.Bitmap; // Imports the Bitmap class to work with images.
import android.media.ThumbnailUtils; // Imports ThumbnailUtils to create thumbnails from images.
import android.net.Uri; // Imports the Uri class to represent a URI reference.
import android.os.Bundle; // Imports the Bundle class to pass data between activities.
import android.os.Environment; // Imports the Environment class to access external storage.
import android.os.Handler; // Imports the Handler class to schedule and run code on a specific thread.
import android.os.Looper; // Imports the Looper class to manage a thread's message loop.
import android.provider.MediaStore; // Imports MediaStore to access media files.
import android.util.Log; // Imports the Log class to send log output.
import android.view.View; // Imports the View class, the basic building block for user interface components.
import android.widget.Button; // Imports the Button class for a clickable UI element.
import android.widget.ImageView; // Imports the ImageView class to display images.
import android.widget.TextView; // Imports the TextView class to display text.
import android.widget.Toast; // Imports the Toast class to show short, transient messages to the user.

import androidx.annotation.Nullable; // Imports the Nullable annotation to indicate a value can be null.
import androidx.core.content.FileProvider; // Imports FileProvider to securely share files between apps.

import com.example.agro.ml.PlantDiseaseModel; // Imports the generated model class for plant disease detection.
import com.google.android.material.bottomsheet.BottomSheetDialog; // Imports BottomSheetDialog for a modal bottom sheet.

import org.tensorflow.lite.DataType; // Imports DataType to specify the data type of a tensor.
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer; // Imports TensorBuffer to handle tensor buffers.

import java.io.File; // Imports the File class to work with files.
import java.io.IOException; // Imports IOException to handle input/output errors.
import java.nio.ByteBuffer; // Imports ByteBuffer to work with byte buffers.
import java.nio.ByteOrder; // Imports ByteOrder to specify the byte order of a buffer.
import java.util.concurrent.ExecutorService; // Imports ExecutorService to manage a pool of threads.
import java.util.concurrent.Executors; // Imports Executors to create ExecutorService instances.

public class DiseaseDetectorActivity extends BaseActivity { // Declares the DiseaseDetectorActivity class, which inherits from BaseActivity.

    private static final String TAG = "DiseaseDetectorActivity"; // Defines a constant for logging purposes.

    // --- ADDED viewRemedyBtn ---
    Button cameraBtn, galleryBtn, viewRemedyBtn; // Declares Buttons for camera, gallery, and viewing remedies.
    ImageView imageView; // Declares an ImageView to display the selected image.
    TextView resultText; // Declares a TextView to display the detection result.

    // --- ADDED variables to store the last scan results for the button ---
    String lastDetectedDisease = ""; // Stores the name of the last detected disease.
    float lastConfidence = 0f; // Stores the confidence score of the last detection.
    DiseaseRemedyProvider.Remedy lastRemedy = null; // Stores the remedy for the last detected disease.

    // UPDATED: Kaggle notebook uses 224x224
    int imageSize = 224; // Sets the required image size for the model.

    Uri currentPhotoUri; // Stores the URI of the photo taken with the camera.

    private PlantDiseaseModel model; // Declares an instance of the TFLite model.
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(); // Creates a single-threaded executor for background tasks.
    private final Handler handler = new Handler(Looper.getMainLooper()); // Creates a handler to run code on the main UI thread.

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Overrides the onCreate method, which is called when the activity is first created.
        super.onCreate(savedInstanceState); // Calls the superclass's onCreate method.
        setContentView(R.layout.activity_disease_detector); // Sets the user interface layout for this activity.

        cameraBtn = findViewById(R.id.buttonCamera); // Initializes the camera button.
        galleryBtn = findViewById(R.id.buttonGallery); // Initializes the gallery button.
        resultText = findViewById(R.id.resultText); // Initializes the result TextView.
        imageView = findViewById(R.id.imageView); // Initializes the ImageView.

        // --- ADDED: Initialize the View Remedy Button ---
        viewRemedyBtn = findViewById(R.id.buttonViewRemedy); // Initializes the "View Remedy" button.

        // --- ADDED: Click Listener to re-open the bottom sheet ---
        viewRemedyBtn.setOnClickListener(view -> { // Sets a click listener for the "View Remedy" button.
            if (lastRemedy != null) { // Checks if a remedy has been stored.
                showRemedyBottomSheet(lastDetectedDisease, lastConfidence, lastRemedy); // Shows the remedy bottom sheet with the stored information.
            }
        });

        // Initialize the model in the background
        executorService.execute(() -> { // Executes the model initialization on a background thread.
            try {
                model = PlantDiseaseModel.newInstance(getApplicationContext()); // Creates a new instance of the plant disease model.
                Log.i(TAG, "Model loaded successfully."); // Logs a success message.
            } catch (IOException e) { // Catches errors that occur during model initialization.
                Log.e(TAG, "Error initializing TFLite model.", e); // Logs the error.
                handler.post(() -> Toast.makeText(DiseaseDetectorActivity.this, "Error loading model.", Toast.LENGTH_SHORT).show()); // Shows a toast message indicating the error.
            }
        });

        // Camera Logic
        cameraBtn.setOnClickListener(view -> { // Sets a click listener for the camera button.
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) { // Checks if the app has camera permission.
                launchCameraWithHighRes(); // Launches the camera if permission is granted.
            } else { // If permission is not granted.
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100); // Requests camera permission.
            }
        });

        // Gallery Logic
        galleryBtn.setOnClickListener(view -> { // Sets a click listener for the gallery button.
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // Creates an intent to pick an image from the gallery.
            startActivityForResult(galleryIntent, 2); // Starts the gallery activity.
        });
    }

    private void launchCameraWithHighRes() { // Defines a method to launch the camera and capture a high-resolution image.
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Creates an intent to capture an image.
        if (cameraIntent.resolveActivity(getPackageManager()) != null) { // Checks if there is an app that can handle the intent.
            File photoFile = null; // Declares a file to store the photo.
            try {
                photoFile = createImageFile(); // Creates a file to store the image.
            } catch (IOException ex) { // Catches errors that occur during file creation.
                Toast.makeText(this, "Error creating file for photo", Toast.LENGTH_SHORT).show(); // Shows a toast message for the error.
            }

            if (photoFile != null) { // Checks if the file was created successfully.
                currentPhotoUri = FileProvider.getUriForFile(this, // Gets a content URI for the file.
                        getApplicationContext().getPackageName() + ".provider", // The authority of the FileProvider.
                        photoFile); // The file to be shared.
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri); // Puts the URI as an extra in the intent, so the camera saves the image to this file.
                startActivityForResult(cameraIntent, 1); // Starts the camera activity.
            }
        }
    }

    private File createImageFile() throws IOException { // Defines a method to create an image file.
        String imageFileName = "AGRO_IMG_"; // Sets the prefix for the image file name.
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // Gets the directory for storing pictures.
        return File.createTempFile(imageFileName, ".jpg", storageDir); // Creates a temporary file in the specified directory.
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // Overrides the method to handle the result from started activities.
        super.onActivityResult(requestCode, resultCode, data); // Calls the superclass's method.

        if (resultCode == RESULT_OK) { // Checks if the result is OK.
            executorService.execute(() -> { // Executes the image processing on a background thread.
                Bitmap image = null; // Declares a Bitmap to hold the image.
                try {
                    // Handle Camera Image (Request Code 1)
                    if (requestCode == 1 && currentPhotoUri != null) { // Checks if the result is from the camera and the URI is not null.
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPhotoUri); // Gets the bitmap from the camera's photo URI.
                    }
                    // Handle Gallery Image (Request Code 2)
                    else if (requestCode == 2 && data != null) { // Checks if the result is from the gallery and the data is not null.
                        Uri dat = data.getData(); // Gets the URI of the selected image.
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat); // Gets the bitmap from the gallery image URI.
                    }

                    if (image != null) { // Checks if an image was successfully loaded.
                        // Crop to square
                        int dimension = Math.min(image.getWidth(), image.getHeight()); // Gets the smaller dimension of the image.
                        image = ThumbnailUtils.extractThumbnail(image, dimension, dimension); // Crops the image to a square.

                        // Resize to model input size (224x224)
                        image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false); // Resizes the image to the required input size for the model.

                        Bitmap finalImage = image; // Creates a final version of the image.
                        handler.post(() -> imageView.setImageBitmap(finalImage)); // Sets the ImageView to display the processed image.

                        classifyImage(finalImage); // Classifies the processed image.
                    }
                } catch (IOException e) { // Catches errors during image loading.
                    Log.e(TAG, "Failed to load image", e); // Logs the error.
                    handler.post(() -> Toast.makeText(DiseaseDetectorActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show()); // Shows a toast message for the error.
                }
            });
        }
    }

    public void classifyImage(Bitmap image) { // Defines a method to classify an image using the TFLite model.
        if (model == null) { // Checks if the model has been initialized.
            handler.post(() -> { // Runs the following code on the main UI thread.
                Toast.makeText(this, "Model is not initialized yet. Please wait.", Toast.LENGTH_LONG).show(); // Shows a toast message if the model is not ready.
            });
            return; // Exits the method.
        }

        try {
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, imageSize, imageSize, 3}, DataType.FLOAT32); // Creates a tensor buffer for the model's input.
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3); // Allocates a direct byte buffer for the image data.
            byteBuffer.order(ByteOrder.nativeOrder()); // Sets the byte order to the native order of the underlying platform.

            int[] intValues = new int[imageSize * imageSize]; // Creates an array to hold the pixel values of the image.
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight()); // Gets the pixel values from the image.


            int pixel = 0; // Initializes a pixel counter.
            for (int i = 0; i < imageSize; i++) { // Iterates over the rows of the image.
                for (int j = 0; j < imageSize; j++) { // Iterates over the columns of the image.
                    int val = intValues[pixel++]; // Gets the color value of the current pixel.

                    // UPDATED: Math matching Kaggle's rescale = 1/255.0
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f)); // Extracts and normalizes the red channel value.
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));  // Extracts and normalizes the green channel value.
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));         // Extracts and normalizes the blue channel value.
                }
            }
            inputFeature0.loadBuffer(byteBuffer); // Loads the byte buffer into the input tensor buffer.

            PlantDiseaseModel.Outputs outputs = model.process(inputFeature0); // Runs the model with the input data.
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer(); // Gets the output tensor buffer.

            float[] confidences = outputFeature0.getFloatArray(); // Gets the confidence scores for each class.

            int maxPos = 0; // Initializes the position of the class with the highest confidence.
            float maxConfidence = 0; // Initializes the highest confidence score.
            for (int i = 0; i < confidences.length; i++) { // Iterates over the confidence scores.
                if (confidences[i] > maxConfidence) { // Checks if the current confidence is higher than the max.
                    maxConfidence = confidences[i]; // Updates the max confidence.
                    maxPos = i; // Updates the position of the max confidence.
                }
            }

            // Labels matched exactly to Kaggle class_indices
            String[] classes = { // Defines an array of class labels.
                    "Apple Scab", "Apple Black Rot", "Apple Cedar Rust", "Apple Healthy",
                    "Blueberry Healthy",
                    "Cherry Powdery Mildew", "Cherry Healthy",
                    "Corn Gray Leaf Spot", "Corn Common Rust", "Corn Northern Leaf Blight", "Corn Healthy",
                    "Grape Black Rot", "Grape Black Measles", "Grape Leaf Blight", "Grape Healthy",
                    "Orange Citrus Greening",
                    "Peach Bacterial Spot", "Peach Healthy",
                    "Pepper Bell Bacterial Spot", "Pepper Bell Healthy",
                    "Potato Early Blight", "Potato Late Blight", "Potato Healthy",
                    "Raspberry Healthy",
                    "Soybean Healthy",
                    "Squash Powdery Mildew",
                    "Strawberry Leaf Scorch", "Strawberry Healthy",
                    "Tomato Bacterial Spot", "Tomato Early Blight", "Tomato Late Blight", "Tomato Leaf Mold",
                    "Tomato Septoria Leaf Spot", "Tomato Spider Mites", "Tomato Target Spot",
                    "Tomato Yellow Leaf Curl Virus", "Tomato Mosaic Virus", "Tomato Healthy"
            };

            // Grab the disease string and get the remedy
            String detectedDisease = classes[maxPos]; // Gets the name of the detected disease.
            float finalConfidence = maxConfidence; // Stores the final confidence score.
            DiseaseRemedyProvider.Remedy remedy = DiseaseRemedyProvider.getRemedy(detectedDisease, DiseaseDetectorActivity.this); // Gets the remedy for the detected disease.

            // Update UI
            handler.post(() -> { // Runs the following code on the main UI thread.
                resultText.setText("Detected: " + detectedDisease); // Sets the result TextView to display the detected disease.

                // --- ADDED: Save the details for the button ---
                lastDetectedDisease = detectedDisease; // Stores the detected disease name.
                lastConfidence = finalConfidence; // Stores the confidence score.
                lastRemedy = remedy; // Stores the remedy.

                // --- ADDED: Make the button visible ---
                viewRemedyBtn.setVisibility(View.VISIBLE); // Makes the "View Remedy" button visible.

                // Show bottom sheet automatically on the first scan
                showRemedyBottomSheet(detectedDisease, finalConfidence, remedy); // Shows the remedy bottom sheet.
            });

        } catch (Exception e) { // Catches exceptions that occur during inference.
            Log.e(TAG, "Error during inference.", e); // Logs the error.
            handler.post(() -> resultText.setText("Error during inference.")); // Sets the result TextView to show an error message.
        }
    }

    // --- NEW METHOD FOR BOTTOM SHEET ---
    private void showRemedyBottomSheet(String diseaseName, float confidence, DiseaseRemedyProvider.Remedy remedy) { // Defines a method to show the remedy bottom sheet.
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DiseaseDetectorActivity.this); // Creates a new BottomSheetDialog.

        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_remedy, null); // Inflates the layout for the bottom sheet.
        bottomSheetDialog.setContentView(bottomSheetView); // Sets the content view of the bottom sheet.

        TextView tvDiseaseName = bottomSheetView.findViewById(R.id.bsDiseaseName); // Initializes the TextView for the disease name.
        TextView tvConfidence = bottomSheetView.findViewById(R.id.bsConfidence); // Initializes the TextView for the confidence score.
        TextView tvCategory = bottomSheetView.findViewById(R.id.bsCategory); // Initializes the TextView for the remedy category.
        TextView tvCultural = bottomSheetView.findViewById(R.id.bsCultural); // Initializes the TextView for the cultural remedy.
        TextView tvChemical = bottomSheetView.findViewById(R.id.bsChemical); // Initializes the TextView for the chemical remedy.
        Button btnClose = bottomSheetView.findViewById(R.id.bsCloseBtn); // Initializes the close button.

        tvDiseaseName.setText(diseaseName); // Sets the disease name TextView.
        tvConfidence.setText("Confidence: " + String.format("%.1f%%", confidence * 100)); // Sets the confidence TextView, formatted as a percentage.
        tvCategory.setText(remedy.category); // Sets the category TextView.
        tvCultural.setText(remedy.culturalProcess); // Sets the cultural remedy TextView.
        tvChemical.setText(remedy.chemicalTreatment); // Sets the chemical remedy TextView.

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss()); // Sets a click listener to dismiss the bottom sheet.

        bottomSheetDialog.show(); // Shows the bottom sheet.
    }

    @Override
    protected void onDestroy() { // Overrides the onDestroy method, which is called when the activity is being destroyed.
        super.onDestroy(); // Calls the superclass's method.
        executorService.shutdown(); // Shuts down the executor service.
        if (model != null) { // Checks if the model has been initialized.
            model.close(); // Closes the model to release resources.
        }
    }
}
