package com.mxolisi.resmanagement.auth;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.mxolisi.resmanagement.R;
import com.mxolisi.resmanagement.databinding.FragmentPasswordResetBinding;
import com.mxolisi.resmanagement.utils.Constants;
import com.mxolisi.resmanagement.utils.LoadingDialog;

import java.util.Objects;

public class PasswordResetFragment extends Fragment {
    public static final String ARG_EMAIL = "arg_email";

    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FragmentPasswordResetBinding binding;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPasswordResetBinding.inflate(inflater, container, false);
        loadingDialog = new LoadingDialog(requireActivity());
        String email = getArguments() != null ? getArguments().getString(ARG_EMAIL) : null;
        if (email != null) {
            Objects.requireNonNull(binding.emailTextInput.getEditText()).setText(email);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText emailInput = Objects.requireNonNull(binding.emailTextInput.getEditText());
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.emailTextInput.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        emailInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !emailInput.getText().toString().trim().isEmpty() &&
                    !emailInput.getText().toString().trim().matches(Constants.EMAIL_PATTERN)) {
                binding.emailTextInput.setError("Invalid email address.");
            }
        });

        
        binding.registerButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PasswordResetFragment.this);
            navController.navigateUp();
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });
        binding.loginButton.setOnClickListener(v -> NavHostFragment
                .findNavController(PasswordResetFragment.this)
                .navigateUp());

        binding.sendButton.setOnClickListener (v->{
            v.setEnabled(false);

            String emailAddress = emailInput.getText().toString().trim();
            if (emailAddress.isEmpty()) {
                binding.emailTextInput.setError("Email address is required.");
                v.setEnabled(true);
            } else if (!emailAddress.matches(Constants.EMAIL_PATTERN)){
                binding.emailTextInput.setError("Invalid email address.");
                v.setEnabled(true);
            } else {
                loadingDialog.showProgress("Sending an email...", null);
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnFailureListener(e -> {
                            loadingDialog.showError(e.getMessage(), null,null);
                            v.setEnabled(true);
                        })
                        .addOnSuccessListener(unused -> loadingDialog.showSuccess("Email sent successfully.",
                                dialog -> findNavController(this).navigateUp()));
            }
        });

    }
}