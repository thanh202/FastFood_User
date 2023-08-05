package com.fpt.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fpt.foodapp.R;
import com.fpt.foodapp.common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DoiPassActivity extends AppCompatActivity {


    private Toolbar tool_barDoiMk;
    private TextView tv_TbTitle;
    private Button button3;
    private TextInputLayout textInputLayout1, textInputLayout2, textInputLayout3;

    private DatabaseReference table_userDbr;

    String passCu, passNew, passReNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mk);

        tool_barDoiMk = findViewById(R.id.tool_barDoiMk);
        tv_TbTitle = findViewById(R.id.tv_TbTitle);

        setSupportActionBar(tool_barDoiMk);
        tv_TbTitle.setText("Đổi mật khẩu");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tool_barDoiMk.setNavigationIcon(R.drawable.ic_quaylai);
        tool_barDoiMk.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        textInputLayout1 = findViewById(R.id.textInputLayout1);
        textInputLayout2 = findViewById(R.id.textInputLayout2);
        textInputLayout3 = findViewById(R.id.textInputLayout3);
        button3 = findViewById(R.id.button3);

        table_userDbr = FirebaseDatabase.getInstance().getReference("User");

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoiMatKhau();
            }
        });


    }


    private Boolean valPass1() {
        String valPass = textInputLayout1.getEditText().getText().toString().trim();
        if (valPass.isEmpty()) {
            textInputLayout1.setError("Không được bỏ trống");
            return false;
        } else {
            textInputLayout1.setError(null);
            textInputLayout1.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean valPass2() {
        String valPass = textInputLayout2.getEditText().getText().toString().trim();
        if (valPass.isEmpty()) {
            textInputLayout2.setError("Không được bỏ trống");
            return false;
        } else {
            textInputLayout2.setError(null);
            textInputLayout2.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean valPass3() {
        String valPass = textInputLayout3.getEditText().getText().toString().trim();
        if (valPass.isEmpty()) {
            textInputLayout3.setError("Không được bỏ trống");
            return false;
        } else {
            textInputLayout3.setError(null);
            textInputLayout3.setErrorEnabled(false);
            return true;
        }
    }


    private void DoiMatKhau() {
        if (!valPass1() | !valPass2() | !valPass3()) {
            return;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);


            passCu = textInputLayout1.getEditText().getText().toString().trim();
            passNew = textInputLayout2.getEditText().getText().toString().trim();
            passReNew = textInputLayout3.getEditText().getText().toString().trim();
            //Kiểm tra mật khẩu hiện tại;
            if (passCu.equals(Common.user.getPass())) {
                //Kiểm tra mật khẩu mới và nhập lại mật khẩu;
                if (passNew.equals(passReNew)) {
                    Map<String, Object> passUpdate = new HashMap<>();
                    passUpdate.put("pass", passNew);

                    //Bắt đầu Cập nhật;
                    //Lấy bảng User;
                    table_userDbr.child(Common.user.getUser()).updateChildren(passUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(DoiPassActivity.this, "Đổi thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DoiPassActivity.this,LoginActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DoiPassActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    builder.setTitle("Thông báo !");
                    builder.setMessage("Mật khẩu không khớp");
                    builder.setPositiveButton("Xác nhận", null);

                }
            } else {
                builder.setTitle("Thông báo !");
                builder.setMessage("Mật khẩu hiện tại sai");
                builder.setPositiveButton("Xác nhận", null);
            }
            builder.show();
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}