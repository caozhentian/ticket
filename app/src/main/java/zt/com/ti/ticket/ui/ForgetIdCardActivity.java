package zt.com.ti.ticket.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import zt.com.ti.ticket.R;

public class ForgetIdCardActivity extends CheckActivity {

    private EditText edtIdCard;
    private Button btnOk;


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

    protected void initUI() {
        super.initUI();
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("手动核销");
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        edtIdCard = findViewById(R.id.edt_id_card);
        btnOk = findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idCard = edtIdCard.getEditableText().toString();
                if (idCard.equals("")) {
                    Toast.makeText(ForgetIdCardActivity.this, "请输入身份证", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!(idCard.length() == 15 || idCard.length() == 18)) {
                    Toast.makeText(ForgetIdCardActivity.this, "身份证长度必须为15位或者18位", Toast.LENGTH_LONG).show();
                    return;
                }
                check(idCard);
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
