package FisioTracker.Android.OrtoTracker_Model.Services;

public enum service_list {
    service_00_interface(1),
    service_01_cache_loader(2);

    private int code;

    service_list(int code){
        this.code = code;
        service_00_interface(code);
    }

    public void service_00_interface(int code) {
        switch(this.code){
            case 1:
                System.out.println("codigo 1 escolhido");
                break;
            case 2:
                System.out.println("codigo 2 escolhido");
                break;

            default:
                break;

        }
    }
}
