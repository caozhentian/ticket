package zt.com.ti.ticket.ui;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.reactivex.functions.Consumer;
import zt.com.ti.ticket.R;
import zt.com.ti.ticket.entity.TicketApiInfo;
import zt.com.ti.ticket.repository.TicketRepository;
import zt.com.ti.ticket.util.UserRepository;

/**
 * 作者：created by ztcao on 2018/11/5 15 : 47
 */
public class CheckActivity extends AppCompatActivity {
    protected ProgressBar circleProgressBar  ;

    protected void initUI() {
        circleProgressBar = findViewById(R.id.circleProgressBar);
    }
    protected void check( String idCard){
        String userId = UserRepository.getUserID(CheckActivity.this);
        if(userId.length() == 0){
            Toast.makeText(CheckActivity.this,"请重新登录", Toast.LENGTH_LONG).show();
            return ;
        }
        circleProgressBar.setVisibility(View.VISIBLE);
        TicketRepository.check(idCard, userId)
                .subscribe(new Consumer<TicketApiInfo>() {
                    @Override
                    public void accept(TicketApiInfo ticket) throws Exception {
                        if(ticket.isOk()){
                            Toast.makeText(CheckActivity.this,"操作成功", Toast.LENGTH_LONG).show();
                            next();
                        }
                        else{
                            Toast.makeText(CheckActivity.this, ticket.getMsg(), Toast.LENGTH_LONG).show();
                        }
                        circleProgressBar.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(CheckActivity.this, "出错了,请重试!", Toast.LENGTH_LONG).show();
                        circleProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    protected void next(){
        finish();
    }
}
