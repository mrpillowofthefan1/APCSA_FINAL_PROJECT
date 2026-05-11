package com.example.apcsa_final_project.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.apcsa_final_project.data.LoginRepository;
import com.example.apcsa_final_project.data.Result;
import com.example.apcsa_final_project.data.model.LoggedInUser;
import com.example.apcsa_final_project.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password, String role) {
        executor.execute(() -> {
            Result<LoggedInUser> result = loginRepository.login(username, password, role);
            handleResult(result);
        });
    }

    public void register(String username, String password, String role) {
        executor.execute(() -> {
            Result<LoggedInUser> result = loginRepository.register(username, password, role);
            handleResult(result);
        });
    }

    private void handleResult(Result<LoggedInUser> result) {
        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.postValue(new LoginResult(new LoggedInUserView(
                    data.getDisplayName(),
                    data.getDashboardTitle(),
                    data.getRole()
            )));
        } else {
            String errorMsg = "Login failed";
            if (result instanceof Result.Error) {
                errorMsg = ((Result.Error) result).getError().getMessage();
            }
            loginResult.postValue(new LoginResult(errorMsg));
        }
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
