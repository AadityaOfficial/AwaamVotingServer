package com.example.aadityasuri.awaam;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_main);

        FloatingActionButton trial = (FloatingActionButton) findViewById(R.id.btNext);
        trial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!session.isLoggedIn()) {
                    Intent nextActivity = new Intent(MainActivity.this, SignUpScreen.class);
                    startActivity(nextActivity);
                } else {
                    Intent nextActivity=new Intent(MainActivity.this,FingerPrint.class);
                    startActivity(nextActivity);
                }

            }
        });
    }


}
