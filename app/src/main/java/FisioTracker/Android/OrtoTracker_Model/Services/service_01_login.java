package FisioTracker.Android.OrtoTracker_Model.Services;

import FisioTracker.Android.OrtoTracker_Model.Entitys.Entity_01_Account;

public class service_01_login implements service_00_Interface {

    private boolean loginEnabled = false;

    Entity_01_Account account = new Entity_01_Account();

    public void loginVerifier () {
        if(account.enabled()) {
            loginEnabled = true;
        }
        else{
            loginEnabled = false;

        }
    }


    @Override
    public boolean enabled() {
        return false;
    }

    public boolean isLoginEnabled() {
        return loginEnabled;
    }

    public void setLoginEnabled(boolean loginEnabled) {
        this.loginEnabled = loginEnabled;
    }
}



