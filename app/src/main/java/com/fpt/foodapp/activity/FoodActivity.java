package com.fpt.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.fpt.foodapp.R;
import com.fpt.foodapp.adapter.viewholder.FoodViewHolder;
import com.fpt.foodapp.dto.Food;
import com.fpt.foodapp.interfaces.ItemClick;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class FoodActivity extends AppCompatActivity {


    private RecyclerView re_Food;
    private FirebaseRecyclerOptions options;
    private FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    private DatabaseReference foodDbr;

    public static String categoryId = "";


    private Toolbar tool_barFood;
    private TextView tv_TbTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        re_Food = findViewById(R.id.re_Food);

        //Tạo bảng Foods;
        foodDbr = FirebaseDatabase.getInstance().getReference("Foods");


        //Tạo layout theo chiều dọc;
        LinearLayoutManager manager = new LinearLayoutManager(FoodActivity.this, LinearLayoutManager.VERTICAL, false);
        re_Food.setLayoutManager(manager);

        //Tạo ngăn;
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        re_Food.addItemDecoration(itemDecoration);
        //Lấy key;
        Intent intent = getIntent();
        if (intent != null) {
            categoryId = intent.getStringExtra("CategoryId");
        }


        if (!categoryId.isEmpty() && categoryId != null) {
            loadFood(categoryId);
        }

        tool_barFood = findViewById(R.id.tool_barFood);
        setSupportActionBar(tool_barFood);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tool_barFood.setNavigationIcon(R.drawable.ic_quaylai);
        tool_barFood.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tv_TbTitle = findViewById(R.id.tv_TbTitle);
        tv_TbTitle.setText("Món ăn");

    }

    private void loadFood(String categoryId) {
        options = new FirebaseRecyclerOptions.Builder<Food>().setQuery(foodDbr.orderByChild("menuid").equalTo(categoryId), Food.class).build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull Food model) {
                holder.tv_nameFoodItem.setText("Tên:" + model.getName());

                Locale locale = new Locale("vi", "VN");
                NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
                holder.tv_tienFoodItem.setText("Đơn giá: "+nf.format(Integer.parseInt(String.valueOf(model.getPrice()))));
                holder.tv_desFoodItem.setText("Mô tả: " + model.getDescription());
                Picasso.get().load(model.getImage()).into(holder.img_FoodItem);

                Food food = model;

                holder.setItemClick(new ItemClick() {
                    @Override
                    public void itemClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(FoodActivity.this, FoodDetailActivity.class);
                        intent.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_food, parent, false);
                return new FoodViewHolder(view);
            }
        };


        adapter.startListening();
        Log.d("====", "" + adapter.getItemCount());
        re_Food.setAdapter(adapter);
    }

}
