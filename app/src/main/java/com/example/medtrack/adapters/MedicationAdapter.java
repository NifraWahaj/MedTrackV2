package com.example.medtrack.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.models.Medication;

import java.util.List;

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
