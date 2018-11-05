package zt.com.ti.ticket.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import zt.com.ti.ticket.R;
import zt.com.ti.ticket.repository.RetrofitFactory;

public class SettingActivity extends AppCompatActivity {

    private EditText edtServer;
    private EditText edtSpotName;
    private Button btnOk ;
    private Button   btnReturn ;

    public static Intent newIntent(Context context){
        Intent   intent = new Intent(context ,SettingActivity.class) ;
        return  intent ;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUI();
    }

    private void initUI(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("设置");
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        edtServer   =  findViewById(R.id.edt_server)      ;
        edtServer.setText(getUrl());
        edtSpotName =  findViewById(R.id.edt_spot_name)  ;
        btnOk = findViewById(R.id.btn_ok) ;
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String server = edtServer.getEditableText().toString() ;
                if(server.equals("")){
                    Toast.makeText(SettingActivity.this , "请输入服务器地址" ,Toast.LENGTH_LONG).show();
                    return ;
                }
//                String spotName = edtSpotName.getEditableText().toString() ;
//                if(spotName.equals("")){
//                    Toast.makeText(SettingActivity.this , "请输入景点名称" ,Toast.LENGTH_LONG).show();
//                    return ;
//                }
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
        //提交
        editor.commit();
        Toast.makeText(this ,"设置成功" ,Toast.LENGTH_LONG).show();
        finish();
    }

    private String getUrl(){
        //创建，注意和读取的时候不同，这个是edit，两个参数分别为存储数据的文件data，访问模式私有
        SharedPreferences  sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        return  sharedPreferences.getString("server" , RetrofitFactory.BASE_URL) ;
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
