package com.example.dontlate_ver1;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;





public class MainActivity extends AppCompatActivity {
    private Button join;
    private Button login;
    private EditText email_login;
    private EditText pwd_login;
    FirebaseAuth firebaseAuth;

    //private Button sendbt;
    //private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        join = (Button) findViewById(R.id.main_join_btn);
        login = (Button) findViewById(R.id.main_login_btn);
        email_login = (EditText) findViewById(R.id.main_email);
        pwd_login = (EditText) findViewById(R.id.main_pwd);
        firebaseAuth = firebaseAuth.getInstance();//firebaseAuth의 인스턴스를 가져옴

/*

        sendbt = (Button) findViewById(R.id.button);

        sendbt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                System.out.println("hello~");
                db = new Database();
                db.insertTake("aa9919", "os","Operating system");

                // thisWeek 는 원래 0부터 시작이다.
                db.insertWeek("aa9919", "pl","0", "2021.0.0", 3);
                db.insertFinish("aa9919", "pl", "1", 3);
                db.getTake("aa9919", "pl",1);
            }


        });
*/
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_login.getText().toString().trim();
                String pwd = pwd_login.getText().toString().trim();
                //String형 변수 email.pwd(edittext에서 받오는 값)으로 로그인하는것
                firebaseAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {//성공했을때
                                    Intent intent = new Intent(MainActivity.this, PlanCalendar.class); //successactivity는 다음 화면 : 캘린더? 메뉴?
                                    startActivity(intent);
                                } else {//실패했을때
                                    Toast.makeText(MainActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
            }
        });
    }
}