package com.hynguyen.chitieucanhan.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hynguyen.chitieucanhan.R;
import com.hynguyen.chitieucanhan.database.AppViewModel;
import com.hynguyen.chitieucanhan.mdel.LoaiTienTe;

import es.dmoral.toasty.Toasty;

public class SuaLoaiTienTeFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    // Định nghĩa một chuỗi hằng làm nhãn cho Fragment
    public static final String TAG = "LoaiTienTe";

    // Khai báo các thành phần giao diện
    private AppViewModel appViewModel; // ViewModel dùng trong Fragment
    private EditText txtLoaiTienTe; // EditText để nhập loại tiền tệ
    private Button btnHuyBo, btnHoanThanh; // Các nút Hủy bỏ và Hoàn thành

    // Hàm được gọi khi Fragment được tạo
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false); // Ngăn không cho người dùng hủy bỏ Fragment bằng cách nhấn ngoài màn hình
    }

    // Hàm tạo view cho Fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate view từ layout XML và trả về
        return inflater.inflate(R.layout.fragment_sualoaitiente, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tạo ViewModel cho Fragment
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        // Tìm kiếm `EditText` trong layout và gán giá trị từ ViewModel
        txtLoaiTienTe = view.findViewById(R.id.txtLoaiTienTe);
        if (appViewModel.loaiTienTe() != null) {
            txtLoaiTienTe.setHint(appViewModel.loaiTienTe().getName()); // Hiển thị gợi ý nếu đã có tên loại tiền tệ
            txtLoaiTienTe.setText(appViewModel.loaiTienTe().getName()); // Đặt giá trị hiện tại của loại tiền tệ nếu có
        }

        // Tìm kiếm và gán sự kiện click cho các button
        btnHuyBo = view.findViewById(R.id.btnHuyBo);
        btnHuyBo.setOnClickListener(this);

        btnHoanThanh = view.findViewById(R.id.btnHoanThanh);
        btnHoanThanh.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnHuyBo:
                // Hủy bỏ thao tác và đóng Fragment
                dismiss();
                break;
            case R.id.btnHoanThanh:
                // Xử lý khi người dùng nhấn Hoàn Thành
                xuLySuaLoaiTienTe();
                break;
        }
    }

    private void xuLySuaLoaiTienTe() {
        // Lấy tên loại tiền tệ từ `EditText`
        String loaitiente = txtLoaiTienTe.getText().toString().trim();

        // Kiểm tra xem tên loại tiền tệ có trống không
        if (TextUtils.isEmpty(loaitiente)) {
            Toasty.error(getContext(), "Không được để trống", Toasty.LENGTH_SHORT, true).show();
            return;
        }

        // Kiểm tra xem loại tiền tệ đã tồn tại chưa
        if (appViewModel.loaiTienTe() != null) {
            // Nếu loại tiền tệ tồn tại và tên mới khác với tên hiện tại, cập nhật
            if (!loaitiente.equals(appViewModel.loaiTienTe().getName())) {
                LoaiTienTe loaiTienTe = new LoaiTienTe(1, loaitiente);
                appViewModel.capNhatLoaiTienTe(loaiTienTe);
            }
        } else {
            // Nếu loại tiền tệ chưa tồn tại, thêm mới
            LoaiTienTe loaiTienTe = new LoaiTienTe(loaitiente);
            appViewModel.themLoaiTienTe(loaiTienTe);
        }

        // Thông báo thành công và đóng Fragment
        Toasty.success(getContext(), "Cập nhật thành công!", Toasty.LENGTH_SHORT, true).show();
        dismiss();
    }

    // Tạo mới Fragment
    public static SuaLoaiTienTeFragment newInstance() {
        return new SuaLoaiTienTeFragment();
    }


}
