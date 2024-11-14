package com.hynguyen.chitieucanhan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.hynguyen.chitieucanhan.UserPreSenter;
import com.hynguyen.chitieucanhan.UserView;
import com.hynguyen.chitieucanhan.R;

import com.hynguyen.chitieucanhan.activity.ContainerActivity;

public class dangnhap extends AppCompatActivity implements UserView, View.OnClickListener {
    private Button btndangnhap;
    private EditText editemail, editpass;
    private UserPreSenter userPreSenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }
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
        showToast("Email không để trống");
    }

    @Override
    public void OnValidEmail() {
        showToast("Email không hợp lệ");
    }

    @Override
    public void OnPass() {
        showToast("Mật khẩu không để trống");
    }

    @Override
    public void OnSucess() {
        startActivity(new Intent(this, ContainerActivity.class));
        showToast("Đăng nhập thành công");
        finish();
    }

    @Override
    public void OnAuthEmail() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
        showToast("Làm ơn hãy vào gmail xác thực!");
    }

    @Override
    public void OnFail() {
        showToast("Sai tài khoản / Mật khẩu");
    }

    @Override
    public void OnPassNotSame() {
        showToast("Tài khoản mật khẩu không khớp");
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
