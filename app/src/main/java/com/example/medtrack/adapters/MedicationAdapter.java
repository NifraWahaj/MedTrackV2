package com.example.medtrack.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.models.Medication;

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
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medicationList.get(position);

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
        holder.itemView.setOnClickListener(v -> showMedicationDetailsDialog(medication));
    }


    private void showMedicationDetailsDialog(Medication medication) {
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

        // Set medication name
        medName.setText(medication.getName());

        // If the medication frequency is interval-based, calculate times between start and end
        if ("Interval".equalsIgnoreCase(medication.getFrequency())) {
            Log.d(TAG, "Processing interval-based medication for: " + medication.getName());

            String reminderTime = medication.getReminderTime();
            if (reminderTime != null) {
                Log.d(TAG, "Reminder time details: " + reminderTime);

                String[] parts = reminderTime.split(", ");
                int intervalHours = 0;
                String startTimeStr = null;
                String endTimeStr = null;
                String dose = "";

                // Extract details from the reminderTime field
                for (String part : parts) {
                    if (part.startsWith("Interval:")) {
                        intervalHours = Integer.parseInt(part.split(": ")[1].replace("hours", "").trim());
                        Log.d(TAG, "Extracted interval hours: " + intervalHours);
                    } else if (part.startsWith("Start Time:")) {
                        startTimeStr = part.split(": ")[1].trim();
                        Log.d(TAG, "Extracted start time: " + startTimeStr);
                    } else if (part.startsWith("End Time:")) {
                        endTimeStr = part.split(": ")[1].trim();
                        Log.d(TAG, "Extracted end time: " + endTimeStr);
                    } else if (part.startsWith("Dose:")) {
                        dose = part.split(": ")[1].trim();
                        Log.d(TAG, "Extracted dose: " + dose);
                    }
                }

                if (intervalHours > 0 && startTimeStr != null && endTimeStr != null) {
                    try {
                        // Parse start and end times
                        String[] startSplit = startTimeStr.split(":");
                        String[] endSplit = endTimeStr.split(":");

                        int startHour = Integer.parseInt(startSplit[0]);
                        int startMinute = Integer.parseInt(startSplit[1]);
                        int endHour = Integer.parseInt(endSplit[0]);
                        int endMinute = Integer.parseInt(endSplit[1]);

                        Log.d(TAG, "Start time: " + startHour + ":" + startMinute);
                        Log.d(TAG, "End time: " + endHour + ":" + endMinute);

                        Calendar startCalendar = Calendar.getInstance();
                        startCalendar.set(Calendar.HOUR_OF_DAY, startHour);
                        startCalendar.set(Calendar.MINUTE, startMinute);

                        Calendar endCalendar = (Calendar) startCalendar.clone();
                        endCalendar.set(Calendar.HOUR_OF_DAY, endHour);
                        endCalendar.set(Calendar.MINUTE, endMinute);

                        StringBuilder calculatedTimes = new StringBuilder();

                        // Calculate the times at which reminders should occur
                        while (startCalendar.before(endCalendar) || startCalendar.equals(endCalendar)) {
                            String time = String.format(Locale.getDefault(), "%02d:%02d", startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE));
                            Log.d(TAG, "Calculated reminder time: " + time);
                            calculatedTimes.append(time).append("\n");
                            startCalendar.add(Calendar.HOUR_OF_DAY, intervalHours);
                        }

                        scheduledTime.setText("Scheduled Times:\n" + calculatedTimes.toString().trim());
                        doseDetails.setText("Dose: " + dose);
                    } catch (Exception e) {
                        Log.e(TAG, "Error while calculating interval times: " + e.getMessage());
                        scheduledTime.setText("Time: Unavailable");
                        doseDetails.setText("Dose: Unavailable");
                    }
                } else {
                    Log.w(TAG, "Invalid interval hours or start/end time. Cannot calculate reminders.");
                    scheduledTime.setText("Time: Unavailable");
                    doseDetails.setText("Dose: Unavailable");
                }
            } else {
                Log.w(TAG, "Reminder time is null for interval-based medication.");
                scheduledTime.setText("Time: Unavailable");
                doseDetails.setText("Dose: Unavailable");
            }
        } else {
            // Extract time and dosage for non-interval-based medications
            String reminderDetails = medication.getReminderTime() != null ? medication.getReminderTime() : medication.getFirstIntakeDetails();
            Log.d(TAG, "Processing non-interval-based medication for: " + medication.getName());

            if (reminderDetails != null) {
                String[] detailsArray = reminderDetails.split(", ");
                if (detailsArray.length == 2) {
                    String time = detailsArray[0]; // e.g., "11:24"
                    String dosage = detailsArray[1]; // e.g., "Dosage: 11 Tablet(s)"

                    Log.d(TAG, "Extracted time: " + time);
                    Log.d(TAG, "Extracted dosage: " + dosage);

                    scheduledTime.setText("Scheduled for: " + time);
                    doseDetails.setText(dosage);
                } else {
                    Log.w(TAG, "Reminder details format is incorrect.");
                    scheduledTime.setText("Time: Unavailable");
                    doseDetails.setText("Dosage: Unavailable");
                }
            } else {
                Log.w(TAG, "Reminder details are null for non-interval-based medication.");
                scheduledTime.setText("Time: Unavailable");
                doseDetails.setText("Dosage: Unavailable");
            }
        }

        // Set Take button click listener
        btnTake.setOnClickListener(v -> {
            Log.d(TAG, medication.getName() + " marked as taken.");
            Toast.makeText(context, medication.getName() + " taken!", Toast.LENGTH_SHORT).show();
        });

        // Set Skip button click listener
        btnSkip.setOnClickListener(v -> {
            Log.d(TAG, medication.getName() + " marked as skipped.");
            Toast.makeText(context, medication.getName() + " skipped.", Toast.LENGTH_SHORT).show();
        });

        // Set Delete button click listener
        deleteMedIcon.setOnClickListener(v -> {
            Log.d(TAG, medication.getName() + " delete icon clicked.");
            Toast.makeText(context, "Deleted " + medication.getName(), Toast.LENGTH_SHORT).show();
            // You may implement delete logic here if needed.
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
}
