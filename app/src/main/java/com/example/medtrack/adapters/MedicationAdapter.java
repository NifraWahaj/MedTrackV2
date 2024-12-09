package com.example.medtrack.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.models.Medication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder> {

    private static final String TAG = "MedicationAdapter";

    private final Context context;
    private final List<Medication> medicationList;

    public MedicationAdapter(Context context, List<Medication> medicationList) {
        this.context = context;
        this.medicationList = medicationList;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medication, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Medication medication = medicationList.get(position);
        Log.d(TAG, "adapMedication Name: " + medication.getName() + ", Key: " + medication.getKey());
// Retrieve the key from Firebase if it is null
        if (medication.getKey() == null) {
            DatabaseReference medsRef = FirebaseDatabase.getInstance()
                    .getReference("medications")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            medsRef.orderByChild("name").equalTo(medication.getName())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String key = snapshot.getKey();
                                medication.setKey(key);
                                Log.d(TAG, "Key fetched: " + key + " for medication: " + medication.getName());
                            }
                            // Update UI after key is set
                            notifyItemChanged(position);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Failed to fetch key for medication: " + medication.getName() + " Error: " + error.getMessage());
                        }
                    });
        }        Log.d(TAG, "adappppMedication Name: " + medication.getName() + ", Key: " + medication.getKey());



        Log.d("MedicationAdapter", "Medication Name: " + medication.getName());
        Log.d("MedicationAdapter", "Frequency: " + medication.getFrequency());

        holder.textViewMedName.setText(medication.getName());
        if ("First Intake".equals(medication.getFrequency())) {
            holder.textViewMedDetails.setText("First Dose: " + medication.getReminderTime());
        } else if ("Second Intake".equals(medication.getFrequency())) {
            holder.textViewMedDetails.setText("Second Dose: " + medication.getReminderTime());
        } else {
            holder.textViewMedDetails.setText(medication.getReminderTime());
        }

        // Add the click listener here
        holder.itemView.setOnClickListener(v -> showMedicationDetailsDialog(medication, position));
    }

    private void showMedicationDetailsDialog(Medication medication, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_medication_details, null);
        builder.setView(dialogView);

        TextView medName = dialogView.findViewById(R.id.medName);
        TextView scheduledTime = dialogView.findViewById(R.id.scheduledTime);
        TextView doseDetails = dialogView.findViewById(R.id.doseDetails);
        Button btnTake = dialogView.findViewById(R.id.btnTake);
        Button btnSkip = dialogView.findViewById(R.id.btnSkip);
        ImageView deleteMedIcon = dialogView.findViewById(R.id.deleteMedIcon);
        ImageView refillMedIcon = dialogView.findViewById(R.id.medInfoIcon);


        // Set medication name
        medName.setText(medication.getName());
// In showMedicationDetailsDialog method
        // Handle interval-specific details
        if ("Interval".equalsIgnoreCase(medication.getFrequency())) {
            try {
                String reminderTime = medication.getReminderTime();
                String[] parts = reminderTime.split(", ");

                if (parts.length >= 4) {
                    int intervalHours = Integer.parseInt(parts[0].split(": ")[1].replace("hours", "").trim());
                    String startTimeStr = parts[1].split(": ")[1].trim();
                    String endTimeStr = parts[2].split(": ")[1].trim();
                    String doseDetails2 = parts[3].split(": ")[1].trim();

                    String intervalInfo = "Interval: " + intervalHours + " hours, Start: " + startTimeStr +
                            ", End: " + endTimeStr;
                    scheduledTime.setText(intervalInfo);
                } else {
                    scheduledTime.setText("Incomplete interval details");
                    Log.e(TAG, "Invalid reminderTime format for interval medication: " + reminderTime);
                }
            } catch (Exception e) {
                scheduledTime.setText("Error parsing interval details");
                Log.e(TAG, "Error parsing interval details: " + e.getMessage());
            }
        } else {
            // Handle non-interval medications
            scheduledTime.setText("Scheduled Time: " +
                    (medication.getReminderTime() != null ? medication.getReminderTime() : "N/A"));
        }

        doseDetails.setText(extractDoseInfo(medication));

        // Extract dosage details
        //    String doseInfo = extractDoseInfo(medication);
        //  scheduledTime.setText("Scheduled Time: " + (medication.getReminderTime() != null ? medication.getReminderTime() : "N/A"));
        // doseDetails.setText(doseInfo);

        // Set Take button click listener
        btnTake.setOnClickListener(v -> {
            Log.d(TAG, medication.getName() + " marked as taken.");
            Toast.makeText(context, medication.getName() + " taken!", Toast.LENGTH_SHORT).show();

            int dosageTaken = extractDosageAmount(medication);
            if (dosageTaken > 0) {
                int newRefillAmount = medication.getRefillAmount() - dosageTaken;
                if (newRefillAmount < 0) {
                    Toast.makeText(context, "Not enough medication left.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update local object
                medication.setRefillAmount(newRefillAmount);
                notifyItemChanged(position);

                String key = medication.getKey();
                if (key == null) {
                    Log.e(TAG, "Medication key is null for: " + medication.getName());
                    Toast.makeText(context, "Unable to update medication. Key is missing.", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference medsRef = FirebaseDatabase.getInstance()
                        .getReference("medications")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                medsRef.child(key).child("refillAmount").setValue(newRefillAmount)
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Updated refillAmount successfully.");
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to update refillAmount: " + e.getMessage());
                        });


            }
            // Hide buttons after action
            btnTake.setVisibility(View.GONE);
            btnSkip.setVisibility(View.GONE);
        });

        // Set Skip button click listener
        btnSkip.setOnClickListener(v -> {
            Log.d(TAG, medication.getName() + " marked as skipped.");
            Toast.makeText(context, medication.getName() + " skipped.", Toast.LENGTH_SHORT).show();
            btnTake.setVisibility(View.GONE);
            btnSkip.setVisibility(View.GONE);
        });

        // Set Delete button click listener
        deleteMedIcon.setOnClickListener(v -> {
            String key = medication.getKey(); // Get the key for the medication
            if (key == null) {
                Toast.makeText(context, "Unable to delete medication. Key is missing.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show a confirmation dialog
            new AlertDialog.Builder(context)
                    .setTitle("Delete Medication")
                    .setMessage("Are you sure you want to delete this medication?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        DatabaseReference medsRef = FirebaseDatabase.getInstance()
                                .getReference("medications")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        medsRef.child(key).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Medication deleted successfully!", Toast.LENGTH_SHORT).show();
                                    medicationList.remove(position); // Remove from local list
                                    notifyItemRemoved(position); // Notify adapter
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete medication: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        refillMedIcon.setOnClickListener(v -> {
            // Inflate the custom dialog layout
            View dialogView2 = LayoutInflater.from(context).inflate(R.layout.dialog_update_refill, null);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                    .setView(dialogView2);

            AlertDialog dialog = dialogBuilder.create();

            // Bind views
            EditText etRefillAmount = dialogView2.findViewById(R.id.etRefillAmount);
            EditText etRefillThreshold = dialogView2.findViewById(R.id.etRefillThreshold);
            Button btnUpdateRefill = dialogView2.findViewById(R.id.btnUpdateRefill);

            // Set button click listener
            btnUpdateRefill.setOnClickListener(updateView -> {
                String refillAmountStr = etRefillAmount.getText().toString().trim();
                String refillThresholdStr = etRefillThreshold.getText().toString().trim();

                if (refillAmountStr.isEmpty() || refillThresholdStr.isEmpty()) {
                    Toast.makeText(context, "Please enter both values", Toast.LENGTH_SHORT).show();
                    return;
                }

                int newRefillAmount = Integer.parseInt(refillAmountStr);
                int newRefillThreshold = Integer.parseInt(refillThresholdStr);

                String key = medication.getKey();
                if (key == null) {
                    Toast.makeText(context, "Unable to update medication. Key is missing.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                // Update Firebase
                DatabaseReference medsRef = FirebaseDatabase.getInstance()
                        .getReference("medications")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                medsRef.child(key).child("refillAmount").setValue(newRefillAmount);
                medsRef.child(key).child("refillThreshold").setValue(newRefillThreshold)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Refill details updated!", Toast.LENGTH_SHORT).show();
                            medication.setRefillAmount(newRefillAmount);
                            medication.setRefillThreshold(newRefillThreshold);
                            notifyItemChanged(position);
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to update refill details.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            });

            dialog.show();
        });



        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMedName, textViewMedDetails;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMedName = itemView.findViewById(R.id.textViewMedName);
            textViewMedDetails = itemView.findViewById(R.id.textViewMedDetails);
        }
    }

    private String extractDoseInfo(Medication medication) {
        try {
            if ("Interval".equalsIgnoreCase(medication.getFrequency())) {
                // Extract dose info for interval medications
                String reminderTime = medication.getReminderTime();
                if (reminderTime.contains("Dose:")) {
                    // Extract the part after "Dose:"
                    return reminderTime.substring(reminderTime.indexOf("Dose:") + 5).trim();
                }
            } else if (medication.getReminderTime() != null && medication.getReminderTime().contains("Dosage:")) {
                // Extract dose info for non-interval medications
                return medication.getReminderTime().split("Dosage:")[1].trim();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting dose info: " + e.getMessage());
        }
        return "Dose Info: Unavailable";
    }



    private int extractDosageAmount(Medication medication) {
        try {
            if ("Interval".equalsIgnoreCase(medication.getFrequency())) {
                String dosePart = medication.getReminderTime().split("Dose:")[1].trim();
                return Integer.parseInt(dosePart.split(" ")[0]);
            } else if (medication.getReminderTime() != null && medication.getReminderTime().contains("Dosage:")) {
                String dosagePart = medication.getReminderTime().split("Dosage:")[1].trim();
                return Integer.parseInt(dosagePart.split(" ")[0]);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting dosage amount: " + e.getMessage());
        }
        return 0;
    }
}