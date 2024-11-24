package com.example.medtrack.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.medtrack.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.medtrack.R;

public class Fragment_ChangePassword extends Fragment {

    // Declare variables for the views
    private ImageView logo;
    private TextView tvWelcomeToMedTrac;
    private EditText etEmail, etNewPassword, etCnewPassword;
    private Button btnLogin;

    public Fragment_ChangePassword() {
        // Required empty public constructor
    }

    public static Fragment_ChangePassword newInstance(String param1, String param2) {
        Fragment_ChangePassword fragment = new Fragment_ChangePassword();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Retrieve passed parameters if any
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__change_password, container, false);

        // Initialize view hooks
        logo = view.findViewById(R.id.logo);
        tvWelcomeToMedTrac = view.findViewById(R.id.tvWelcomeToMedTrac);
        etEmail = view.findViewById(R.id.etEmail);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etCnewPassword = view.findViewById(R.id.etCnewPassword);
        btnLogin = view.findViewById(R.id.btnLogin);

        // Set up click listener for the login button
        btnLogin.setOnClickListener(v -> handleChangePassword());

        return view;
    }

    /**
     * Handles the change password functionality.
     */
    private void handleChangePassword() {
        // Retrieve input values
        String oldPassword = etEmail.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etCnewPassword.getText().toString().trim();

        // Validate inputs
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(getActivity(), "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(getActivity(), "New Password and Confirm Password do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implement backend logic for changing the password
        Toast.makeText(getActivity(), "Password changed successfully!", Toast.LENGTH_SHORT).show();
    }
}
