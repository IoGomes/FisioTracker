package FisioTracker.Android.OrtoTracker_Model.Entitys;

import FisioTracker.Android.OrtoTracker_View.Dialogs.Dialog_02_Login_Credentials;

@SuppressWarnings("all")
public class Entity_01_Account_subutilized implements Entity_00_Interface {

    private String userName;
    private String userEmail;
    private String userPassword;
    private String userCPF;
    private String userCrefito;
    private String userRegisterPR;
    private static Boolean userEnabled;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {

        if (userName.isEmpty()) {
            userEnabled = false;
            Dialog_02_Login_Credentials.collector("Campo de Email vazio");
            return;
        }

        if (!(userName.length() < 3) && userName.length() > 30) {
            userEnabled = false;
            Dialog_02_Login_Credentials.collector("Credenciais Incorretas");
            return;
        }

        this.userName = userName;
        userEnabled = true;
        return;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {

        if (userEmail.isEmpty()) {
            userEnabled = false;
            Dialog_02_Login_Credentials.collector("Campo de Email Vazio");
            return;
        }

        if (userEmail.length() < 3 && userEmail.length() > 200) {
            userEnabled = false;
            Dialog_02_Login_Credentials.collector("Quantidade de Caracteres invalida");
            return;
        }

        if (!userEmail.contains("@") && !userEmail.contains(".")) {
            userEnabled = false;
            Dialog_02_Login_Credentials.collector("Email deve conter @ e .");
            return;
        }

        userEnabled = true;
        this.userEmail = userEmail;
    }


    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {

        if (userPassword.isEmpty()) {
            userEnabled = false;
            Dialog_02_Login_Credentials.collector("• Campo de Senha Vazio");
            return;
        }

        if (userPassword.length() < 8 && userPassword.length() > 200) {
            userEnabled = false;
            Dialog_02_Login_Credentials.collector("• Senha deve conter 8>200 caracteres");
            return;
        }

        userEnabled = true;
        this.userPassword = userPassword;
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
        if (userEnabled != true) {
            return false;
        }
        return true;
    }
}
