package FisioTracker.Android.OrtoTracker_Model.Services;

import android.content.Context;

import androidx.annotation.RawRes;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;

import java.util.HashMap;
import java.util.Map;

public class service_01_cache_loader {
    private static final Map<Integer, LottieComposition> cache = new HashMap<>();

    public static void preload(Context context, @RawRes int rawRes) {
        LottieCompositionFactory.fromRawRes(context, rawRes)
                .addListener(composition -> cache.put(rawRes, composition));
    }

    public static LottieComposition get(@RawRes int rawRes) {
        return cache.get(rawRes);
    }
}