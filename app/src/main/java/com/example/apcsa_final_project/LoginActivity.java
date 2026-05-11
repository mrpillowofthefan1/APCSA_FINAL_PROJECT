package com.example.apcsa_final_project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.apcsa_final_project.ui.login.LoginFragment;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_container);
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new LoginFragment())
                .commitNow();
        }
    }
}
