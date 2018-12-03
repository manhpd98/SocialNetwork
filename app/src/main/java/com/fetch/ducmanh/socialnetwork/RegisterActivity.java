package com.fetch.ducmanh.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUsername,edtFullname,edtEmail,edtPassword;
    private Button btnRegister;
    private TextView tvLogin;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        initviews();
    }



    private void initviews() {

        edtUsername = findViewById(R.id.edtUsername);
        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegister:
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Vui lòng đợi ...");
                progressDialog.show();

                String str_name = edtUsername.getText().toString();
                String str_fullname = edtFullname.getText().toString();
                String str_email = edtEmail.getText().toString();
                String str_password = edtPassword.getText().toString();

                if (TextUtils.isEmpty(str_name) ||TextUtils.isEmpty(str_fullname)
                        || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    progressDialog.dismiss();
                    Toast.makeText(this, "Bạn cần điền đủ thông tin!!", Toast.LENGTH_SHORT).show();
                }else if (str_password.length()<6){
                    progressDialog.dismiss();
                    Toast.makeText(this, "Mật khẩu không ít hơn 6 ký tự!! ", Toast.LENGTH_SHORT).show();
                }else {
                    register(str_name,str_fullname,str_email,str_password);
                }
                break;
            case R.id.tvLogin:
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                break;
        }
    }

    private void register(final String username, final String fullname, String email, String password){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String,Object> hashMap = new HashMap<>();

                            hashMap.put("id",userid);
                            hashMap.put("username",username.toLowerCase());
                            hashMap.put("fullname",fullname);
                            hashMap.put("bio","");
                            hashMap.put("imageurl","");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Toàn khoản của bạn đã có!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
