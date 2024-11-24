package com.example.medtrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.models.Medication;

import java.util.List;

public class MedicationListAdapter extends RecyclerView.Adapter<MedicationListAdapter.MedicationViewHolder> {

    private final Context context;
    private final List<Medication> medicationList;

    public MedicationListAdapter(Context context, List<Medication> medicationList) {
        this.context = context;
        this.medicationList = medicationList;
    }

    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medication_list, parent, false);
        return new MedicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        Medication medication = medicationList.get(position);

        holder.textViewName.setText(medication.getName());
        holder.textViewDetails.setText(
                "Frequency: " + medication.getFrequency() + "\n" +
                        "Start Date: " + medication.getStartDate() + "\n" +
                        "End Date: " + medication.getEndDate() + "\n" +
                        "Refill Amount: " + medication.getRefillAmount() + "\n" +
                        "Refill Threshold: " + medication.getRefillThreshold() + "\n" +
                        (medication.getReminderTime() != null ? "Reminder: " + medication.getReminderTime() : "") +
                        (medication.getFirstIntakeDetails() != null ? "\nFirst Intake: " + medication.getFirstIntakeDetails() : "") +
                        (medication.getSecondIntakeDetails() != null ? "\nSecond Intake: " + medication.getSecondIntakeDetails() : "") +
                        (medication.getSelectedDays() != null && !medication.getSelectedDays().isEmpty()
                                ? "\nSpecific Days: " + medication.getSelectedDays()
                                : "")
        );
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    public static class MedicationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDetails;

        public MedicationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewMedName);
            textViewDetails = itemView.findViewById(R.id.textViewMedDetails);
        }
    }
}
