package com.mxolisi.resmanagement.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mxolisi.resmanagement.R;
import com.mxolisi.resmanagement.databinding.ActivityEmailVerificationBinding;
import com.mxolisi.resmanagement.main.MainActivity;
import com.mxolisi.resmanagement.utils.LoadingDialog;
import java.util.Objects;

public class EmailVerificationActivity extends AppCompatActivity {
    public static  final String ARG_IS_NEW = "arg_is_new";
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    private ActivityEmailVerificationBinding binding;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        binding = ActivityEmailVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialog = new LoadingDialog(this);
        boolean isNew = getIntent() != null && getIntent().getBooleanExtra(ARG_IS_NEW, false);
        if (isNew) binding.buttonLayout.setVisibility(View.GONE);

        Objects.requireNonNull(auth.getCurrentUser()).sendEmailVerification()
                .addOnSuccessListener(unused -> binding.verifyDescription.setText(R.string.email_verification_sent))
                .addOnFailureListener(e -> binding.verifyDescription.setText(e.getMessage()));

        binding.emailAddress.setText(Objects.requireNonNull(auth.getCurrentUser()).getEmail());

        binding.refreshButton.setOnClickListener(v-> refresh(true));

        binding.logoutButton.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, AuthActivity.class));
            finishAffinity();
        });

        binding.resendButton.setOnClickListener(v-> Objects.requireNonNull(auth.getCurrentUser()).sendEmailVerification()
                .addOnSuccessListener(unused -> binding.verifyDescription.setText(R.string.email_verification_sent))
                .addOnFailureListener(e -> binding.verifyDescription.setText(e.getMessage())));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh(false);
    }

    private void refresh(boolean showError) {
        FirebaseUser firebaseUser = Objects.requireNonNull(auth.getCurrentUser());
        firebaseUser.reload()
                .addOnSuccessListener(unused -> {
                    if (firebaseUser.isEmailVerified()) {
                        loadingDialog.showSuccess("Email Verified.", d -> {
                            startActivity(new Intent(this, MainActivity.class));
                            finishAffinity();
                        });
                    } else {
                        Toast.makeText(this, "Your is not verified.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if(showError)
                        Toast.makeText(this, "Refresh failed.\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}