package FisioTracker.Android.OrtoTracker_Model.Database.DAOs;

public enum EstadosDeAtivacao {

    ATIVADO(100),
    DESATIVADO(200);

    private final int code;

    EstadosDeAtivacao(int code){
        this.code = code;
    }
}
