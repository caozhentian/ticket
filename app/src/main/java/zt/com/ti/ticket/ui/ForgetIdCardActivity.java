package zt.com.ti.ticket.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import zt.com.ti.ticket.R;
import zt.com.ti.ticket.entity.Ticket;
import zt.com.ti.ticket.repository.TicketRepository;
import zt.com.ti.ticket.util.DeviceUtil;

public class ForgetIdCardActivity extends AppCompatActivity {

    private EditText edtIdCard ;
    private Button   btnOk ;
    private Button   btnReturn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_id_card);
        initUI() ;
    }

    private void initUI(){
        edtIdCard = findViewById(R.id.edt_id_card) ;
        btnOk = findViewById(R.id.btn_ok) ;
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idCard = edtIdCard.getEditableText().toString() ;
                if(idCard.equals("")){
                    Toast.makeText(ForgetIdCardActivity.this , "请输入身份证" ,Toast.LENGTH_LONG).show();
                    return ;
                }
                String deviceId = DeviceUtil.getDeviceId(ForgetIdCardActivity.this) ;
                TicketRepository.check(idCard,deviceId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Ticket>(){
                            @Override
                            public void accept(Ticket ticket) throws Exception {
                            }
                        },new Consumer<Throwable>(){
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                            }
                        });
            }
        });
        btnReturn = findViewById(R.id.btn_return) ;
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
