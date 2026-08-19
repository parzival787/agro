package com.example.agro; // Declares the package this class belongs to.

import android.content.Context;

public class DiseaseRemedyProvider { // Declares the DiseaseRemedyProvider class.

    // A simple container class to hold the treatment data
    public static class Remedy { // Declares a static nested class to hold remedy information.
        public String category; // The category of the disease (e.g., Fungal, Viral).
        public String culturalProcess; // The recommended cultural practices to manage the disease.
        public String chemicalTreatment; // The recommended chemical treatments for the disease.

        public Remedy(String category, String culturalProcess, String chemicalTreatment) { // Constructor for the Remedy class.
            this.category = category; // Initializes the category.
            this.culturalProcess = culturalProcess; // Initializes the cultural process.
            this.chemicalTreatment = chemicalTreatment; // Initializes the chemical treatment.
        }
    }

    /**
     * Takes the exact class name from your TFLite model and returns the appropriate treatment.
     */
    public static Remedy getRemedy(String diseaseName, Context context) { // A static method that returns a Remedy based on the disease name.

        // 1. Healthy Plants
        // Matches: "Apple Healthy", "Corn Healthy", etc.
        if (diseaseName.contains("Healthy")) { // Checks if the plant is healthy.
            return new Remedy( // Returns a new Remedy object for healthy plants.
                    context.getString(R.string.remedy_healthy_plant_category),
                    context.getString(R.string.remedy_healthy_plant_cultural),
                    context.getString(R.string.remedy_healthy_plant_chemical)
            );
        }

        // 2. Viral Diseases (Incurable)
        // Matches: "Tomato Yellow Leaf Curl Virus", "Tomato Mosaic Virus"
        if (diseaseName.contains("Virus")) { // Checks if the disease is a virus.
            return new Remedy( // Returns a new Remedy object for viral infections.
                    context.getString(R.string.remedy_viral_category),
                    context.getString(R.string.remedy_viral_cultural),
                    context.getString(R.string.remedy_viral_chemical)
            );
        }

        // 3. Systemic Bacterial (Special Case for Citrus)
        // Matches: "Orange Citrus Greening"
        if (diseaseName.contains("Greening")) { // Checks for Citrus Greening disease.
            return new Remedy( // Returns a new Remedy object for this specific disease.
                    context.getString(R.string.remedy_greening_category),
                    context.getString(R.string.remedy_greening_cultural),
                    context.getString(R.string.remedy_greening_chemical)
            );
        }

        // 4. Standard Bacterial Diseases
        // Matches: "Peach Bacterial Spot", "Pepper Bell Bacterial Spot", "Tomato Bacterial Spot"
        if (diseaseName.contains("Bacterial Spot")) { // Checks for bacterial spot diseases.
            return new Remedy( // Returns a new Remedy object for bacterial infections.
                    context.getString(R.string.remedy_bacterial_spot_category),
                    context.getString(R.string.remedy_bacterial_spot_cultural),
                    context.getString(R.string.remedy_bacterial_spot_chemical)
            );
        }

        // 5. Pests & Insects
        // Matches: "Tomato Spider Mites"
        if (diseaseName.contains("Mites") || diseaseName.toLowerCase().contains("mite")) { // Checks for mite infestations.
            return new Remedy( // Returns a new Remedy object for pest infestations.
                    context.getString(R.string.remedy_mites_category),
                    context.getString(R.string.remedy_mites_cultural),
                    context.getString(R.string.remedy_mites_chemical)
            );
        }

        // 6. Fungal Diseases (The Catch-All for the remaining classes)
        // Matches: Scab, Black Rot, Cedar Rust, Powdery Mildew, Gray Leaf Spot, Blights, Leaf Mold, Target Spot, etc.
        return new Remedy( // Returns a default Remedy object for fungal infections.
                context.getString(R.string.remedy_fungal_category),
                context.getString(R.string.remedy_fungal_cultural),
                context.getString(R.string.remedy_fungal_chemical)
        );
    }
}