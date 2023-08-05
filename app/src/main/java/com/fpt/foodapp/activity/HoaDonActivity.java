package com.fpt.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fpt.foodapp.R;
import com.fpt.foodapp.adapter.viewholder.HoaDonViewHolder;
import com.fpt.foodapp.common.Common;
import com.fpt.foodapp.dto.Request;
import com.fpt.foodapp.interfaces.ItemClick;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.Locale;

public class HoaDonActivity extends AppCompatActivity {


    private Toolbar tool_barHoaDon;
    private RecyclerView re_ViewListHoaDon;
    private TextView tv_TbTitle;
    private BottomNavigationView bottom_NaView;
    private ConstraintLayout layoutHoaDon;

    //
    private FirebaseRecyclerOptions options;
    private FirebaseRecyclerAdapter<Request, HoaDonViewHolder> adapter;
    private DatabaseReference table_GioHangDbr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoa_don);

        layoutHoaDon = findViewById(R.id.layoutHoaDon);
        re_ViewListHoaDon = findViewById(R.id.re_ViewListHoaDon);


        //
        tool_barHoaDon = findViewById(R.id.tool_barHoaDon);
        setSupportActionBar(tool_barHoaDon);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //
        tv_TbTitle = findViewById(R.id.tv_TbTitle);
        tv_TbTitle.setText("Hoá Đơn");

        //Lấy bảng Requests;
        table_GioHangDbr = FirebaseDatabase.getInstance().getReference("Requests");

        // khoi tao xoa hoa don
//        ItemTouchHelper.SimpleCallback simpleCallback = new ReViewSwipedHoaDon(0, ItemTouchHelper.LEFT, this);
//        new ItemTouchHelper(simpleCallback).attachToRecyclerView(re_ViewListHoaDon);

        //
        bottom_NaView = findViewById(R.id.bottom_NaView);
        bottom_NaView.setSelectedItemId(R.id.menu_HoaDon);
        bottom_NaView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menu_Home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.menu_GioHang:
                        startActivity(new Intent(getApplicationContext(), GioHangActivity.class));
                        overridePendingTransition(0, 0);
                        break;

                    case R.id.menu_HoaDon:
                        break;

                    case R.id.menu_CaNhan:
                        startActivity(new Intent(getApplicationContext(), CaNhanActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                }
                return true;
            }
        });


        loadHoaDon(Common.user.getUser());
    }

    private void loadHoaDon(String user) {
        LinearLayoutManager manager = new LinearLayoutManager(HoaDonActivity.this, LinearLayoutManager.VERTICAL, false);
        re_ViewListHoaDon.setLayoutManager(manager);
        options = new FirebaseRecyclerOptions.Builder<Request>().setQuery(table_GioHangDbr.orderByChild("user").equalTo(user), Request.class).build();
        adapter = new FirebaseRecyclerAdapter<Request, HoaDonViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HoaDonViewHolder holder, int position, @NonNull Request model) {
                holder.tv_idHoaDonItem.setText(adapter.getRef(position).getKey());
                holder.tv_statusHoaDonItem.setText(Common.coverStatus(model.getStatus()));
                holder.tv_userHoaDonItem.setText(model.getUser());
                holder.tv_addressHoaDonItem.setText(model.getAddress());
                holder.tv_phoneHoaDonItem.setText(model.getPhone());
                holder.tv_timeHoaDonItem.setText(Common.getTime(Long.parseLong(adapter.getRef(position).getKey())));

                Locale locale = new Locale("vi", "VN");
                NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
                holder.tv_totalHoaDonItem.setText(nf.format(Integer.parseInt(String.valueOf(model.getTotal()))));
                holder.setItemClick(new ItemClick() {
                    @Override
                    public void itemClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(HoaDonActivity.this, ThongTinDonHangActivity.class);
                        Common.request = model;
                        intent.putExtra("id", adapter.getRef(position).getKey());  //Put id;
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public HoaDonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_hoadon, parent, false);
                return new HoaDonViewHolder(view);
            }
        };


        adapter.startListening();
        re_ViewListHoaDon.setAdapter(adapter);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if ((item.getTitle().equals(Common.DELETE))) {
            if (Validate_delete(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()))) {
                Toast.makeText(this, "Không thể xóa đơn hàng đang giao", Toast.LENGTH_SHORT).show();
            } else {
                DeleteCatehory(adapter.getRef(item.getOrder()).getKey());
            }
        } else if ((item.getTitle().equals(Common.DANHAN))) {
            if (Validate_da_nhan_hang(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()))) {
                Toast.makeText(this, "Đơn hàng chưa nhận", Toast.LENGTH_SHORT).show();
            } else {
                Da_Nhan_Hang(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
                Toast.makeText(this, "Nhận hàng thành công", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onContextItemSelected(item);


    }

    //Validate; kiểm tra tính hợp lệ
    private boolean Validate_da_nhan_hang(String key, Request item) {
        if (item.getStatus().equals("0") || item.getStatus().equals("3")) {
            return true;
        }
        //nếu là 3 sẽ hủy đơn
        return false;
    }

    //Validate;
    private boolean Validate_delete(String key, Request item) {
        if (item.getStatus().equals("1")) {
            return true;
        }
        return false;
    }

    //Nhận hàng;
    private void Da_Nhan_Hang(String key, Request item) {
        item.setStatus("2");
        table_GioHangDbr.child(key).setValue(item);
    }


    //Xoá thực đơn;
    private void DeleteCatehory(String key) {

        table_GioHangDbr.child(key).removeValue();

    }


}
