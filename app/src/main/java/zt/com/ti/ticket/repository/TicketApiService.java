package zt.com.ti.ticket.repository;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import zt.com.ti.ticket.entity.TicketApiInfo;
import zt.com.ti.ticket.entity.UserApiInfo;

/**
 * 作者：created by ztcao on 2018/11/1 16 : 51
 */
public interface TicketApiService {

    @FormUrlEncoded
    @POST("Often/canaftver")
    Observable<TicketApiInfo> checkTicket(@Field("idcard") String idCard , @Field("userID")String userID);

    @FormUrlEncoded
    @POST("Often/canaftver")
    Observable<UserApiInfo> login(@Field("userName") String userName , @Field("password")String password);
}
