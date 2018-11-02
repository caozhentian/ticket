package zt.com.ti.ticket.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import zt.com.ti.ticket.R;

public class SettingActivity extends AppCompatActivity {

    private EditText edtServer;
    private EditText edtSpotName;
    private Button btnOk ;
    private Button   btnReturn ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUI();
    }

    private void initUI(){
        edtServer   =  findViewById(R.id.edt_server)      ;
        edtSpotName =  findViewById(R.id.edt_spot_name)  ;
        btnOk = findViewById(R.id.btn_ok) ;
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String server = edtServer.getEditableText().toString() ;
                if(edtServer.equals("")){
                    Toast.makeText(SettingActivity.this , "请输入服务器地址" ,Toast.LENGTH_LONG).show();
                    return ;
                }
                String spotName = edtSpotName.getEditableText().toString() ;
                if(spotName.equals("")){
                    Toast.makeText(SettingActivity.this , "请输入景点名称" ,Toast.LENGTH_LONG).show();
                    return ;
                }
                save();
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

    private void save(){
        //创建，注意和读取的时候不同，这个是edit，两个参数分别为存储数据的文件data，访问模式私有
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        //存数据
        String server = edtServer.getEditableText().toString() ;
        editor.putString("server",server);
        String spotName = edtSpotName.getEditableText().toString() ;
        editor.putString("spotName",spotName);
        //提交
        editor.commit();
    }
}
