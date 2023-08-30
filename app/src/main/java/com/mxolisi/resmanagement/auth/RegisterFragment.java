package com.mxolisi.resmanagement.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mxolisi.resmanagement.R;
import com.mxolisi.resmanagement.databinding.FragmentLoginBinding;
import com.mxolisi.resmanagement.databinding.FragmentRegisterBinding;
import com.mxolisi.resmanagement.utils.Constants;
import com.mxolisi.resmanagement.utils.LoadingDialog;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RegisterFragment extends Fragment {
    public static final String ARG_EMAIL = "arg_email";
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FragmentRegisterBinding binding;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
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
        setInputValidationListener();

        binding.loginButton.setOnClickListener(view1 -> NavHostFragment
                .findNavController(RegisterFragment.this)
                .navigateUp());

        binding.registerButton.setOnClickListener(view1 -> validateAndSignUp());
    }

    private void setInputValidationListener() {
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

        EditText passwordInput = Objects.requireNonNull(binding.passwordTextInput.getEditText());
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence password, int start, int before, int count) {
                binding.passwordTextInput.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        passwordInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && passwordInput.getText().toString().trim().isEmpty()) {
                binding.passwordTextInput.setError("Password is empty.");
            } else if (!hasFocus && passwordInput.getText().toString().trim().length() < 6) {
                binding.passwordTextInput.setError("At least 6 characters are required.");
            }
        });

        EditText passwordConfirmInput = Objects.requireNonNull(binding.passwordConfirmTextInput.getEditText());
        passwordConfirmInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence password, int start, int before, int count) {
                binding.passwordConfirmTextInput.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void validateAndSignUp() {
        EditText emailEditText = Objects.requireNonNull(binding.emailTextInput.getEditText());
        if (emailEditText.getText().toString().isEmpty()){
            binding.emailTextInput.setError("Email is required");
            return;
        } else if (!emailEditText.getText().toString().matches(Constants.EMAIL_PATTERN)) {
            binding.emailTextInput.setError("Email is not valid");
            return;
        }

        EditText passwordEditText = Objects.requireNonNull(binding.passwordTextInput.getEditText());
        if (passwordEditText.getText().toString().isEmpty()){
            binding.passwordTextInput.setError("Password is required");
            return;
        }
        EditText passwordConfirmEditText = Objects.requireNonNull(binding.passwordConfirmTextInput.getEditText());
        if (!passwordEditText.getText().toString().equals(passwordConfirmEditText.getText().toString())){
            binding.passwordConfirmTextInput.setError("Passwords do not match");
            return;
        }

        loadingDialog.showProgress("Creating an account...", null);
        auth.createUserWithEmailAndPassword(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim())
                .addOnSuccessListener(authResult -> {
                    loadingDialog.showSuccess("Registered successfully,\nComplete your profile.", d -> {
                        Intent intent = new Intent(requireActivity(), CompleteProfileActivity.class);
                        intent.putExtra(CompleteProfileActivity.ARG_IS_NEW, true);
                        startActivity(intent);
                        requireActivity().finishAffinity();
                    });
                })
                .addOnFailureListener(e -> loadingDialog.showError(e.getMessage(), null, null));
    }
}