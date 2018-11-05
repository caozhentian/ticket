package zt.com.ti.ticket.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 作者：created by ztcao on 2018/11/2 15 : 01
 */
public class UserRepository {

    public static final String DATATICKET = "dataticket";
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";

    public static final String getUserID(Context context) {
        //创建，注意和读取的时候不同，这个是edit，两个参数分别为存储数据的文件data，访问模式私有
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATATICKET, Context.MODE_PRIVATE);
        //存数据
        return sharedPreferences.getString(USER_NAME, "");
    }

    public static final String getUserPassword(Context context) {
        //创建，注意和读取的时候不同，这个是edit，两个参数分别为存储数据的文件data，访问模式私有
        SharedPreferences sharedPreferences = context.getSharedPreferences(DATATICKET, Context.MODE_PRIVATE);
        //存数据
        return sharedPreferences.getString(PASSWORD, "");
    }

    public static final void saveUser(Context context, String userName, String password) {
        //创建，注意和读取的时候不同，这个是edit，两个参数分别为存储数据的文件data，访问模式私有
        SharedPreferences.Editor editor = context.getSharedPreferences(DATATICKET, Context.MODE_PRIVATE).edit();
        //存数据
        editor.putString(USER_NAME, userName);
        editor.putString(PASSWORD, password);
        //提交
        editor.commit();
    }

}
