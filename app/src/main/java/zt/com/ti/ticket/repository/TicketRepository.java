package zt.com.ti.ticket.repository;

import io.reactivex.Observable;
import zt.com.ti.ticket.entity.Ticket;
import zt.com.ti.ticket.entity.UserInfo;

/**
 * 作者：created by ztcao on 2018/11/1 18 : 02
 */
public class TicketRepository {

    public static Observable<Ticket> check(String idCard,String deviceId){
        return RetrofitFactory.getRetrofitInstance().create(TicketApiService.class).checkTicket(idCard , deviceId) ;
    }

    public static Observable<UserInfo> login(String userName, String password){
        return RetrofitFactory.getRetrofitInstance().create(TicketApiService.class).login(userName , password) ;
    }
}
