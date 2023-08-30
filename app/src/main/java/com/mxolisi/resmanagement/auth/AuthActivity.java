package com.mxolisi.resmanagement.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mxolisi.resmanagement.data.models.UserInfoModel;
import com.mxolisi.resmanagement.databinding.ActivityAuthBinding;

import com.mxolisi.resmanagement.R;
import com.mxolisi.resmanagement.main.MainActivity;
import com.mxolisi.resmanagement.utils.LoadingDialog;

public class AuthActivity extends AppCompatActivity {

    private static final FirebaseAuth auth = FirebaseAuth.getInstance();
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        FirebaseUser firebaseUser = auth.getCurrentUser();
        splashScreen.setKeepOnScreenCondition(() -> true);

        if (firebaseUser != null) {
            UserInfoModel.getData();
            UserInfoModel.getIsProfileComplete().observe(this, isProfileComplete -> {
                if (isProfileComplete) {
                    if (firebaseUser.isEmailVerified()) {
                        startActivity(new Intent(this, MainActivity.class));
                        finishAffinity();
                    } else {
                        startActivity(new Intent(this, EmailVerificationActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(this, CompleteProfileActivity.class));
                    finish();
                }
            });
        } else {
            splashScreen.setKeepOnScreenCondition(() -> false);
        }


        ActivityAuthBinding binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_auth);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        /*binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });*/


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_auth);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}