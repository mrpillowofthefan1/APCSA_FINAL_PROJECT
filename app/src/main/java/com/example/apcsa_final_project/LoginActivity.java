package com.example.apcsa_final_project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.apcsa_final_project.ui.login.LoginFragment;

// entry activity that holds the login fragment
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_container);
        
        // start fragment if not already there
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .commitNow();
        }
    }
}
