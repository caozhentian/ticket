package zt.com.ti.ticket.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 作者：created by ztcao on 2018/11/5 17 : 15
 */
public class CheckCountRepository {

    public static final String CHECK_COUNT = "CheckCount";

    public static final int getCount(Context context) {
        //创建，注意和读取的时候不同，这个是edit，两个参数分别为存储数据的文件data，访问模式私有
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHECK_COUNT, Context.MODE_PRIVATE);
        //存数据
        String key = DateUtil.getSimpleDate() ;
        int curCheckCount = sharedPreferences.getInt( key , 0);
        if(curCheckCount == 0){ //清空数据
            SharedPreferences.Editor editor = context.getSharedPreferences(CHECK_COUNT, Context.MODE_PRIVATE).edit();
            editor.clear();
            //提交
            editor.commit();
        }
        return curCheckCount ;
    }

    public static final void saveCount(Context context) {
        //创建，注意和读取的时候不同，这个是edit，两个参数分别为存储数据的文件data，访问模式私有
        SharedPreferences.Editor editor = context.getSharedPreferences(CHECK_COUNT, Context.MODE_PRIVATE).edit();
        //提交
        int count = getCount(context) ;
        String key = DateUtil.getSimpleDate() ;
        editor.putInt(key ,count + 1 ) ;
        editor.commit();
    }
}
