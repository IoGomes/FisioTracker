package FisioTracker.Android.OrtoTracker_Core.Entitys;

import android.util.Log;

import FisioTracker.Android.OrtoTracker_Dialogs.Dialog_01_Login_Credentials;

public class Entity_01_Account {

    private String userName;
    private String userEmail;
    private String userPassword;
    private String userCrefito;
    private String registerPR;
    private static Boolean userEnabled;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {

        Log.d("BOTAO", "Botão clicado!");

        if (!userName.isEmpty()) {
            if (userName.length() > 1 && userName.length() < 30) {
                this.userName = userName;
                userEnabled = true;
            } else {
                Dialog_01_Login_Credentials.collector("Nome de Usuario Invalido");
                userEnabled = false;
            }
        } else {
            Dialog_01_Login_Credentials.collector("Nome de Usuario Invalido");
            userEnabled = false;
        }
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        if (!userEmail.isEmpty()) {
            if (userEmail.length() > 3 && userEmail.length() < 200) {
                if (userEmail.contains("@") && userEmail.contains(".")) {
                    this.userEmail = userEmail;
                    userEnabled = true;
                } else {
                    userEnabled = false;
                }
            } else {
                userEnabled = false;
            }
        } else {
            userEnabled = false;
        }
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        if (!userPassword.isEmpty()) {
            if (userPassword.length() > 8 && userPassword.length() < 200) {
                this.userPassword = userPassword;
                userEnabled = true;
            } else {
                userEnabled = false;
            }
        } else {
            userEnabled = false;
        }
    }

    public String getUserCrefito() {
        return userCrefito;
    }

    public void setUserCrefito(String userCrefito) {
        this.userCrefito = userCrefito;
    }

    public String getRegisterPR() {
        return registerPR;
    }

    public void setRegisterPR(String registerPR) {
        this.registerPR = registerPR;
    }

    public static boolean isUserEnabled() {
        if (userEnabled == true) {
            Log.d("BOTAO", "Botão clicado!");
            return true;
        } else {
            return false;
        }
    }

}
