package com.hynguyen.chitieucanhan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hynguyen.chitieucanhan.UserPreSenter;
import com.hynguyen.chitieucanhan.UserView;
import com.hynguyen.chitieucanhan.R;

import com.hynguyen.chitieucanhan.activity.ContainerActivity;

import java.util.Locale;

public class dangnhap extends AppCompatActivity implements UserView, View.OnClickListener {
    private Button btndangnhap;
    private EditText editemail, editpass;
    private UserPreSenter userPreSenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangnhap);
        InitWidget();
        Init();
    }

    private void Init() {
        userPreSenter = new UserPreSenter(this);
        btndangnhap.setOnClickListener(this);
        findViewById(R.id.txtdangky).setOnClickListener(v -> 
            startActivity(new Intent(dangnhap.this, dangky.class))
        );
    }

    private void InitWidget() {
        btndangnhap = findViewById(R.id.btndangnhap);
        editemail = findViewById(R.id.editEmail);
        editpass = findViewById(R.id.editmatkhau);
    }

    @Override
    public void OnLengthEmail() {
        showToast(getString(R.string.error_empty_email));
    }

    @Override
    public void OnValidEmail() {
        showToast(getString(R.string.error_invalid_email));
    }

    @Override
    public void OnPass() {
        showToast(getString(R.string.error_empty_password));
    }

    @Override
    public void OnSucess() {
        startActivity(new Intent(this, ContainerActivity.class));
        showToast(getString(R.string.success_login));
        finish();
    }

    @Override
    public void OnAuthEmail() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
        showToast(getString(R.string.verify_email_message));
    }

    @Override
    public void OnFail() {
        showToast(getString(R.string.error_login_failed));
    }

    @Override
    public void OnPassNotSame() {
        showToast(getString(R.string.error_password_mismatch));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btndangnhap) {
            String email = editemail.getText().toString().trim();
            String pass = editpass.getText().toString().trim();
            userPreSenter.HandleLoginUser(email, pass);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
