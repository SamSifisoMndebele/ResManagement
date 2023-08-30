package com.mxolisi.resmanagement.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mxolisi.resmanagement.R;
import com.mxolisi.resmanagement.data.UserInfo;
import com.mxolisi.resmanagement.data.models.UserInfoModel;
import com.mxolisi.resmanagement.databinding.ActivityCompleteProfileBinding;
import com.mxolisi.resmanagement.main.MainActivity;
import com.mxolisi.resmanagement.utils.Constants;
import com.mxolisi.resmanagement.utils.LoadingDialog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class CompleteProfileActivity extends AppCompatActivity {

    public static  final String ARG_IS_NEW = "arg_is_new";
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final FirebaseUser firebaseUser = Objects.requireNonNull(auth.getCurrentUser());
    private ActivityCompleteProfileBinding binding;
    private LoadingDialog loadingDialog;

    private String imageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        binding = ActivityCompleteProfileBinding.inflate(getLayoutInflater());
        loadingDialog = new LoadingDialog(this);
        setContentView(binding.getRoot());

        binding.emailAddress.setText(firebaseUser.getEmail());
        binding.skipButton.setOnClickListener(v-> firebaseUser.reload()
                .addOnSuccessListener(unused -> {
                    if (firebaseUser.isEmailVerified()) {
                        startActivity(new Intent(this, MainActivity.class));
                        finishAffinity();
                    } else {
                        startActivity(new Intent(this, EmailVerificationActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Skipping failed.\n"+e.getMessage(), Toast.LENGTH_SHORT).show()
                ));


        UserInfoModel.getImageUrl().observe(this, imageUrl -> {
            this.imageUrl = imageUrl;
            Toast.makeText(this, imageUrl, Toast.LENGTH_SHORT).show();
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.profileImageView);
        });
        UserInfoModel.getNames().observe(this, names ->
                Objects.requireNonNull(binding.namesTextInput.getEditText()).setText(names));
        UserInfoModel.getLastName().observe(this, lastName ->
                Objects.requireNonNull(binding.lastNameTextInput.getEditText()).setText(lastName));
        UserInfoModel.getPhone().observe(this, phone ->
                Objects.requireNonNull(binding.numberTextInput.getEditText()).setText(phone));
        UserInfoModel.getIsAdmin().observe(this, isAdmin -> {
            binding.isAdminSwitch.setChecked(isAdmin);
            binding.isAdminSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                if (isChecked && !isAdmin) binding.verificationCodeTextInput.setVisibility(View.VISIBLE);
                else binding.verificationCodeTextInput.setVisibility(View.GONE);
            });
        });

        EditText nameET = Objects.requireNonNull(binding.namesTextInput.getEditText());
        EditText lastNameET = Objects.requireNonNull(binding.lastNameTextInput.getEditText());
        EditText phoneET = Objects.requireNonNull(binding.numberTextInput.getEditText());

        binding.numberTextInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.numberTextInput.setError(null);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.saveButton.setOnClickListener(view -> {
            String phone = phoneET.getText().toString().trim();
            if (!phone.isEmpty() && !phone.matches(Constants.PHONE_PATTERN)) {
                binding.numberTextInput.setError("Invalid phone number.");
                return;
            }

            saveInformation(
                    nameET.getText().toString().trim(),
                    lastNameET.getText().toString().trim(),
                    phoneET.getText().toString().trim(),
                    imageUrl);
        });

    }


    private void saveInformation(@NotNull String names, @NotNull String lastName, @NotNull String phone, @Nullable String imageUrl) {

        if (binding.isAdminSwitch.isChecked() && Boolean.FALSE.equals(UserInfoModel.getIsAdmin().getValue())) {
            loadingDialog.showProgress("Verifying admin...", null);
            FirebaseFirestore.getInstance().collection("app")
                    .document("appInfo")
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String verificationCode = documentSnapshot.getString("adminPassword");
                        if (Objects.requireNonNull(binding.verificationCodeTextInput.getEditText())
                                .getText().toString().equals(verificationCode)) {
                            loadingDialog.setText("Saving your information...");
                            UserInfo userInfo = new UserInfo(
                                    firebaseUser.getUid(),
                                    Objects.requireNonNull(firebaseUser.getEmail()),
                                    names,
                                    lastName,
                                    phone,
                                    true,
                                    imageUrl,
                                    UserInfoModel.getResident().getValue());
                            saveInformation(userInfo);
                        } else {
                            loadingDialog.showError("Incorrect admin verification password.", null, null);
                        }
                    });
        } else {
            loadingDialog.setText("Saving your information...");
            UserInfo userInfo = new UserInfo(
                    firebaseUser.getUid(),
                    Objects.requireNonNull(firebaseUser.getEmail()),
                    names,
                    lastName,
                    phone,
                    false,
                    imageUrl, UserInfoModel.getResident().getValue());
            saveInformation(userInfo);
        }
    }

    private void saveInformation(@NotNull final UserInfo userInfo) {
        FirebaseFirestore.getInstance().collection("users")
                .document(firebaseUser.getUid())
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    loadingDialog.showSuccess("Information saved successfully.", dialog -> {
                        if (firebaseUser.isEmailVerified()) {
                            startActivity(new Intent(this, MainActivity.class));
                            finishAffinity();
                        } else {
                            startActivity(new Intent(this, EmailVerificationActivity.class));
                            finish();
                        }
                    });
                }).addOnFailureListener(e -> {
                    loadingDialog.showError(e.getMessage(), null, null);
                });
    }

}