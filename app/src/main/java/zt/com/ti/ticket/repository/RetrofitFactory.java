package zt.com.ti.ticket.repository;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者：created by ztcao on 2018/11/1 17 : 28
 */
public class RetrofitFactory {

    public static final String BASE_URL = "https://np.xazhihe.cn/";
    public static Retrofit retrofit;

    public static final Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
            retrofit = new Retrofit.Builder()
                    .client(okHttpBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
        }
        return retrofit;
    }

    /*
     * 获取对应的Service
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
}
