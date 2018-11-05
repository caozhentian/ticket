package zt.com.ti.ticket.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import zt.com.ti.ticket.R;
import zt.com.ti.ticket.entity.Ticket;
import zt.com.ti.ticket.repository.TicketRepository;
import zt.com.ti.ticket.util.DeviceUtil;

public class ForgetIdCardActivity extends AppCompatActivity {

    private EditText edtIdCard;
    private Button btnOk;
    private Button btnReturn;
    private ProgressBar circleProgressBar  ;

    public static final Intent newIntent(Context context) {
        Intent intent = new Intent(context, ForgetIdCardActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_id_card);
        initUI();
    }

    private void initUI() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        edtIdCard = findViewById(R.id.edt_id_card);
        btnOk = findViewById(R.id.btn_ok);
        circleProgressBar = findViewById(R.id.circleProgressBar);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idCard = edtIdCard.getEditableText().toString();
                if (idCard.equals("")) {
                    Toast.makeText(ForgetIdCardActivity.this, "请输入身份证", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!(idCard.length() != 15 || idCard.length() == 18)) {
                    Toast.makeText(ForgetIdCardActivity.this, "身份证长度必须为15位或者18位", Toast.LENGTH_LONG).show();
                    return;
                }
                String deviceId = DeviceUtil.getDeviceId(ForgetIdCardActivity.this);
                circleProgressBar.setVisibility(View.VISIBLE);
                TicketRepository.check(idCard, deviceId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Ticket>() {
                            @Override
                            public void accept(Ticket ticket) throws Exception {
                                if(ticket.getCode() == 1){
                                    Toast.makeText(ForgetIdCardActivity.this, ticket.getMsg(), Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(ForgetIdCardActivity.this,"操作成功", Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                circleProgressBar.setVisibility(View.GONE);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(ForgetIdCardActivity.this, "出错了", Toast.LENGTH_LONG).show();
                                circleProgressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
        btnReturn = findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
