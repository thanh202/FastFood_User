package com.fpt.foodapp.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.foodapp.R;
import com.fpt.foodapp.adapter.adapter.GioHangAdapter;
import com.fpt.foodapp.adapter.viewholder.GioHangViewHolder;
import com.fpt.foodapp.common.Common;
import com.fpt.foodapp.database.dao.OderDetailDAO;
import com.fpt.foodapp.dto.OderDetail;
import com.fpt.foodapp.dto.Request;
import com.fpt.foodapp.interfaces.ReViewXoa;
import com.fpt.foodapp.swiped.ReViewSwiped;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class GioHangActivity extends AppCompatActivity implements View.OnClickListener, ReViewXoa {

    private Toolbar tool_barCuaHang;
    private RecyclerView re_ViewListGioHang;
    private TextView tv_TbTitle, tv_moneyGioHang;
    private BottomNavigationView bottom_NaView;
    private Button btn_DatHang;
    private ConstraintLayout layoutGioHang;

    private DatabaseReference GioHangDbr;

    private ArrayList<OderDetail> listOderDetails = new ArrayList<>();

    private GioHangAdapter gioHangAdapter;

    private OderDetailDAO oderDetailDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);

        re_ViewListGioHang = findViewById(R.id.re_ViewListGioHang);
        layoutGioHang = findViewById(R.id.layoutGioHang);

        tv_moneyGioHang = findViewById(R.id.tv_moneyGioHang);
        //
        tool_barCuaHang = findViewById(R.id.tool_barCuaHang);
        setSupportActionBar(tool_barCuaHang);
        tv_TbTitle = findViewById(R.id.tv_TbTitle);
        tv_TbTitle.setText("Giỏ hàng");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        oderDetailDAO = new OderDetailDAO(this);

        //Tạo bảng Giỏ Hàng;
        GioHangDbr = FirebaseDatabase.getInstance().getReference("Requests");


        // Khởi tạo Swiped xoá;
        ItemTouchHelper.SimpleCallback simpleCallback = new ReViewSwiped(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(re_ViewListGioHang);

        //
        bottom_NaView = findViewById(R.id.bottom_NaView);
        bottom_NaView.setSelectedItemId(R.id.menu_GioHang);
        bottom_NaView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_Home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.menu_GioHang:
                        break;

                    case R.id.menu_HoaDon:
                        startActivity(new Intent(getApplicationContext(), HoaDonActivity.class));
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.menu_CaNhan:
                        startActivity(new Intent(getApplicationContext(), CaNhanActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                }
                return true;
            }
        });

        //Set hiện thị theo hướng dọc;
        resetData();
        LinearLayoutManager manager = new LinearLayoutManager(GioHangActivity.this, LinearLayoutManager.VERTICAL, false);
        re_ViewListGioHang.setLayoutManager(manager);


        //Hiện giỏ hàng;
        loadGiaTien();

        //
        btn_DatHang = findViewById(R.id.btn_DatHang);
        btn_DatHang.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_DatHang:
                if (listOderDetails.size() > 0) {
                    DatHang();
                } else {
                    KoCoSp();
                }
                break;
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //
    private void KoCoSp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GioHangActivity.this);
        builder.setTitle("Thông báo !!!");
        builder.setMessage("Không có sản phẩm ? Vui lòng mua sản phẩm");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(GioHangActivity.this, MainActivity.class));
                finish();
                dialog.dismiss();
            }
        });

        builder.show();
    }


    //
    private void DatHang() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GioHangActivity.this);
        builder.setTitle("Thông báo !!!");
        builder.setMessage("Thêm địa chỉ ?");


        LayoutInflater inflater = this.getLayoutInflater();
        View custom_thanh_toan = inflater.inflate(R.layout.custom_thanh_toan, null);

        EditText edt_addressOder = custom_thanh_toan.findViewById(R.id.edt_addressOder);
        RadioButton rdi_Cod = custom_thanh_toan.findViewById(R.id.rdi_Cod);

        builder.setView(custom_thanh_toan);
        builder.setIcon(R.drawable.ic_shop_black);

        builder.setPositiveButton("Đặt hàng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Cover về tiền số;
                int tong = 0;
                for (OderDetail oderDetail : listOderDetails)
                    tong = tong + (Integer.parseInt(oderDetail.getPrice())) * (Integer.parseInt(oderDetail.getQuanTity()));
                tv_moneyGioHang.setText(String.valueOf(tong));


                if (!rdi_Cod.isChecked()) {
                    Toast.makeText(GioHangActivity.this, "Vui lòng chọn thanh toán !", Toast.LENGTH_SHORT).show();
                } else {

                    //Lấy dữ liệu ;
                    Request request = new Request(Common.user.getUser(),
                            Common.user.getPhone(),
                            edt_addressOder.getText().toString().trim(),
                            tv_moneyGioHang.getText().toString().trim(),
                            listOderDetails);

                    //Thêm vào bảng Requests;
                    String keyNumber = String.valueOf(System.currentTimeMillis());
                    GioHangDbr.child(keyNumber).setValue(request);

                    //Mua hàng xoá hết sản phẩm;
                    oderDetailDAO.deleteCart();
                    startActivity(new Intent(GioHangActivity.this, HoaDonActivity.class));
                    Toast.makeText(GioHangActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
        resetData();
    }


    //Lấy tất cả hiện lên RecyclerView ;
    private void resetData() {
        listOderDetails = (ArrayList<OderDetail>) oderDetailDAO.getAll();
        gioHangAdapter = new GioHangAdapter(listOderDetails, this);
        re_ViewListGioHang.setAdapter(gioHangAdapter);
    }

    //Format tiền;
    private void loadGiaTien() {
        int tong = 0;
        for (OderDetail oderDetail : listOderDetails)
            tong = tong + (Integer.parseInt(oderDetail.getPrice())) * (Integer.parseInt(oderDetail.getQuanTity()));
        Locale locale = new Locale("vi", "VN");
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        tv_moneyGioHang.setText(nf.format(Integer.parseInt(String.valueOf(tong))));

    }


    //Kéo sang trái xoá;
    @SuppressLint("ResourceType")
    @Override
    public void itemSwiped(RecyclerView.ViewHolder viewHolder, int direction, int id) {
        if (viewHolder instanceof GioHangViewHolder) {
            //Lấy tên sản phẩm;
            String name = ((GioHangAdapter) re_ViewListGioHang.getAdapter()).getItem(viewHolder.getLayoutPosition()).getProductName();
            OderDetail deleteItem = ((GioHangAdapter) re_ViewListGioHang.getAdapter()).getItem(viewHolder.getLayoutPosition());
            int deleteIndex = viewHolder.getLayoutPosition();

            gioHangAdapter.xoa(deleteIndex);
            oderDetailDAO.deleteId(deleteItem.getProductId());
            loadGiaTien();

            //Hoàn tác;
            Snackbar snackbar = Snackbar.make(layoutGioHang, name + " Xoá thành công", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Hoàn tác", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    oderDetailDAO.insertCart(deleteItem);
                    gioHangAdapter.hoantac(deleteItem, deleteIndex);
                    loadGiaTien();
                }
            });

            snackbar.setText(Color.BLACK);
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();


        }
    }
}