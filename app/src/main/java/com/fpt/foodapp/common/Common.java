package com.fpt.foodapp.common;

import com.fpt.foodapp.dto.Request;
import com.fpt.foodapp.dto.User;

import java.util.Calendar;
import java.util.Locale;

public class Common {
    //Lấy tên đăng nhập;
    public static User user;
    public static Request request;
    public static final String DELETE = "Xoá";
    public static final String DANHAN = "Đã nhận hàng";

    //

    public static String coverStatus(String status) {
        if (status.equals("0")) {
            return "Chờ xác nhận";
        } else if (status.equals("2")) {
            return "Đã nhận hàng";
        } else if (status.equals("1")) {
            return "Đang Giao";
        } else {
            return "Đã huỷ";
        }
    }



    //Lấy thời gian;
    public static String getTime(long time){
        Calendar calendar =     Calendar.getInstance(new Locale("vi","VN"));
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(android.text.format.DateFormat.format("dd-MM-yyyy HH:mm",calendar).toString());
        return date.toString();
    }
}
