package zt.com.ti.ticket.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：created by ztcao on 2018/11/5 17 : 19
 */
public class DateUtil {
    public static  final String getSimpleDate(){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return  format.format(new Date());
    }
}
