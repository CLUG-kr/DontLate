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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;





public class Signup extends AppCompatActivity {

    private EditText email_join;
    private EditText pwd_join;
    private EditText stdnum_join,name_join;
    private Button btn;
    FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email_join = (EditText) findViewById(R.id.sign_up_email);
        pwd_join = (EditText) findViewById(R.id.sign_up_pwd);
        stdnum_join=(EditText)findViewById(R.id.sign_up_stdnum);
        name_join=(EditText)findViewById(R.id.sign_up_name);
        btn = (Button) findViewById(R.id.sign_up_btn);


        firebaseAuth = FirebaseAuth.getInstance();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = email_join.getText().toString().trim();
                final String pwd = pwd_join.getText().toString().trim();
                final String stdnum = stdnum_join.getText().toString().trim();
                final String name = name_join.getText().toString().trim();
                //공백인 부분을 제거하고 보여주는 trim();


                firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(Signup.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(Signup.this, "등록 에러", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
            }
        });


    }
}