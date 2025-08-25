package FisioTracker.Android.OrtoTracker_Model.Remote;

import java.util.List;

import FisioTracker.Android.OrtoTracker_Model.Entitys.Entity_01_Account;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CurrencyService {
    @GET("users")
    Call<List<Entity_01_Account>> groupList();
}