package underscore.com.vlex;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devspark.appmsg.AppMsg;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login_btn = (Button) findViewById(R.id.login_button);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  attemptLogin();
            }
        });
    }

    private void attemptLogin() {

        TextView username_tv = (TextView) findViewById(R.id.ta_username);
        String username = username_tv.getText().toString();

        TextView password_tv = (TextView) findViewById(R.id.ta_password);
        String password = password_tv.getText().toString();

        if(username.equals("s@c") && password.equals("asd")){
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        }else {
            AppMsg.makeText(this,"Username/Password is not matching", AppMsg.STYLE_ALERT).show();
        }
    }
}
