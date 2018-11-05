package zt.com.ti.ticket.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import zt.com.ti.ticket.R;
import zt.com.ti.ticket.entity.UserInfo;
import zt.com.ti.ticket.repository.TicketRepository;

public class LoginActivity extends AppCompatActivity {

    private EditText edt_user_name ;
    private EditText edt_user_password ;
    private Button btn_ok ;
    private ProgressBar circleProgressBar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
    }

    private void initUI() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        edt_user_name = findViewById(R.id.edt_user_name);
        edt_user_password  = findViewById(R.id.edt_user_password);
        btn_ok = findViewById(R.id.btn_ok);
        circleProgressBar = findViewById(R.id.circleProgressBar);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idCard = edt_user_name.getEditableText().toString();
                if (idCard.equals("")) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_LONG).show();
                    return;
                }
                String password = edt_user_password.getEditableText().toString();
                if (password.equals("")) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                    return;
                }
                circleProgressBar.setVisibility(View.VISIBLE);
                TicketRepository.login(idCard, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<UserInfo>() {
                            @Override
                            public void accept(UserInfo userInfo) throws Exception {
                                if(!userInfo.isOk()){
                                    Toast.makeText(LoginActivity.this, userInfo.getMsg(), Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(LoginActivity.this,"操作成功", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                circleProgressBar.setVisibility(View.GONE);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(LoginActivity.this, "出错了", Toast.LENGTH_LONG).show();
                                circleProgressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }
}
