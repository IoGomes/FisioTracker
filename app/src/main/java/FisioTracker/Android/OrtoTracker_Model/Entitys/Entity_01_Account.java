package FisioTracker.Android.OrtoTracker_Model.Entitys;

import android.util.Log;

import FisioTracker.Android.OrtoTracker_View.Dialogs.Dialog_02_Login_Credentials;

public class Entity_01_Account implements Entity_00_Interface {

    private String         userName;
    private String         userEmail;
    private String         userPassword;
    private String         userCPF;
    private String         userCrefito;
    private String         userRegisterPR;
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
                Dialog_02_Login_Credentials.collector("Nome de Usuario Invalido");
                userEnabled = false;
            }
        } else {
            Dialog_02_Login_Credentials.collector("Nome de Usuario Invalido");
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
                    Dialog_02_Login_Credentials.collector("• Email deve conter @ e .");
                    return;
                }
            } else {
                userEnabled = false;
                Dialog_02_Login_Credentials.collector("• Email deve conter 3>200 caracteres");
                return;
            }
        } else {
            userEnabled = false;
            Dialog_02_Login_Credentials.collector("• Campo de Email vazio");
            return;
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
                Dialog_02_Login_Credentials.collector("• Senha deve conter 8>200 caracteres");
            }
        } else {
            userEnabled = false;
            Dialog_02_Login_Credentials.collector("• Campo de Senha Vazio");
        }
    }

    public String getUserCrefito() {
        return userCrefito;
    }

    public void setUserCrefito(String userCrefito) {
        this.userCrefito = userCrefito;
    }


    public static boolean isUserEnabled() {
        if (userEnabled == true) {
            return true;
        } else {
            return false;
        }
    }

    public String getUserCPF() {
        return userCPF;
    }

    public void setUserCPF(String userCPF) {
        this.userCPF = userCPF;
    }

    @Override
    public boolean enabled() {
        if (userEnabled == true) {
            return true;
        } else {
            return false;
        }
    }
}
