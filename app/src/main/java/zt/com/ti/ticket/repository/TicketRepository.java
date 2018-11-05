package zt.com.ti.ticket.repository;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import zt.com.ti.ticket.entity.TicketApiInfo;
import zt.com.ti.ticket.entity.UserApiInfo;

/**
 * 作者：created by ztcao on 2018/11/1 18 : 02
 */
public class TicketRepository {

    public static Observable<TicketApiInfo> check(String idCard, String userID){
        return RetrofitFactory.getRetrofitInstance().create(TicketApiService.class).checkTicket(idCard , userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) ;
    }

    public static Observable<UserApiInfo> login(String userName, String password){
        return RetrofitFactory.getRetrofitInstance().create(TicketApiService.class).login(userName , password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) ;
    }
}
