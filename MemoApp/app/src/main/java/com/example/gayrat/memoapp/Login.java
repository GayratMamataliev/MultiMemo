package com.example.gayrat.memoapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText email, pass;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email =findViewById(R.id.email);
        pass = findViewById(R.id.passwprd);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void btnLoginAction(View view) {

        final ProgressDialog pd = new ProgressDialog(Login.this);
        pd.setMessage("loading ...");
        pd.show();
        pd.show();

        firebaseAuth.signInWithEmailAndPassword(
                email.getText().toString() ,pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();

                        if (task.isSuccessful()){
                            Toast.makeText(Login.this, "Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(Login.this ,"Error",Toast.LENGTH_SHORT ).show();
                        }
                    }
                });
    }

    public void btnRegisterAction(View view) {
        Intent intent = new Intent(Login.this,Register.class);
        startActivity(intent);
    }
}
