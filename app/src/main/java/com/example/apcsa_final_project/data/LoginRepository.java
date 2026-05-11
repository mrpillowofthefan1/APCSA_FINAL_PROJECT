package com.example.apcsa_final_project.data;

import com.example.apcsa_final_project.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    private LoggedInUser user = null;

    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public LoggedInUser getUser() {
        return user;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    public Result<LoggedInUser> login(String username, String password, String role) {
        // handle login
        Result<LoggedInUser> result = dataSource.login(username, password, role);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public Result<LoggedInUser> register(String username, String password, String role) {
        // handle register
        Result<LoggedInUser> result = dataSource.register(username, password, role);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }
}
