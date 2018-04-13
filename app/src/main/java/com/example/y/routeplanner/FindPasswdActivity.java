package com.example.y.routeplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FindPasswdActivity extends BaseActivity {      //找回密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_passwd);
        final EditText tel=findViewById(R.id.find_passwd_tel);
        final EditText passwd=findViewById(R.id.find_passwd_passwd);
        Button ok=findViewById(R.id.find_passwd_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FindPasswdActivity.this,ProvingActivity.class);
                intent.putExtra("type",1);
                intent.putExtra("tel",tel.getText().toString().trim());
                intent.putExtra("passwd",passwd.getText().toString().trim());
                startActivity(intent);
            }
        });
    }
}
