package com.mxolisi.resmanagement.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mxolisi.resmanagement.R;
import com.mxolisi.resmanagement.data.models.UserInfoModel;
import com.mxolisi.resmanagement.databinding.FragmentLoginBinding;
import com.mxolisi.resmanagement.main.MainActivity;
import com.mxolisi.resmanagement.utils.Constants;
import com.mxolisi.resmanagement.utils.LoadingDialog;
import com.mxolisi.resmanagement.utils.Utils;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FragmentLoginBinding binding;
    private LoadingDialog loadingDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        loadingDialog = new LoadingDialog(requireActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInputValidationListener();

        binding.registerButton.setOnClickListener(v -> {
            Utils.hideKeyboard(requireContext(), v);
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_loginFragment_to_registerFragment);
        });
        binding.forgotPasswordButton.setOnClickListener(v -> {
            Utils.hideKeyboard(requireContext(), v);
            String email = Objects.requireNonNull(binding.emailTextInput.getEditText())
                    .getText().toString().trim();
            NavController navController = NavHostFragment.findNavController(LoginFragment.this);
            if (email.isEmpty()) {
                navController.navigate(R.id.action_loginFragment_to_passwordResetFragment);
            } else {
                Bundle arguments = new Bundle();
                arguments.putString(PasswordResetFragment.ARG_EMAIL, email);
                navController.navigate(R.id.action_loginFragment_to_passwordResetFragment, arguments);
            }
        });
        binding.loginButton.setOnClickListener(btn -> {
            Utils.tempDisable(btn);

            String email = Objects.requireNonNull(binding.emailTextInput.getEditText())
                    .getText().toString().trim();
            String password = Objects.requireNonNull(binding.passwordTextInput.getEditText())
                    .getText().toString().trim();

            if (email.isEmpty()){
                binding.emailTextInput.setError("Enter your email address.");
                return;
            }
            if (!email.matches(Constants.EMAIL_PATTERN)){
                binding.emailTextInput.setError("Invalid email address.");
                return;
            }
            if (password.isEmpty()){
                binding.passwordTextInput.setError("Enter your password.");
                return;
            }

            loadingDialog.showProgress(getString(R.string.signing_in),  null);
            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        // Sign in success, update UI with the signed-in user's information
                        UserInfoModel.getIsProfileComplete().observe(getViewLifecycleOwner(), isProfileComplete -> {
                            if (isProfileComplete) {
                                if (Objects.requireNonNull(authResult.getUser()).isEmailVerified()) {
                                    loadingDialog.showSuccess("Signed in successfully.", d -> {
                                        startActivity(new Intent(requireActivity(), MainActivity.class));
                                        requireActivity().finishAffinity();
                                    });
                                } else {
                                    loadingDialog.showSuccess("Signed in successfully.\nVerify your email.", d -> {
                                        startActivity(new Intent(requireActivity(), EmailVerificationActivity.class));
                                        requireActivity().finishAffinity();
                                    });
                                }
                            } else {
                                loadingDialog.showSuccess("Signed in successfully.\nComplete your profile.", d -> {
                                    startActivity(new Intent(requireActivity(), CompleteProfileActivity.class));
                                    requireActivity().finishAffinity();
                                });
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // If sign in fails, display a message to the user.
                        if (Objects.equals(e.getMessage(), "There is no user record corresponding to this identifier. The user may have been deleted.")) {
                            loadingDialog.showError(e.getMessage(), null, dialog -> {
                                Bundle arguments = new Bundle();
                                arguments.putString(RegisterFragment.ARG_EMAIL, email);
                                NavController navController = NavHostFragment.findNavController(LoginFragment.this);
                                //dialog.dismiss();
                                navController.navigate(R.id.action_loginFragment_to_registerFragment, arguments);
                            });
                        } else loadingDialog.showError(e.getMessage(), null,null);
                    });
        });
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
    }
}