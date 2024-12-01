package com.hynguyen.chitieucanhan.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.hynguyen.chitieucanhan.R;
import com.hynguyen.chitieucanhan.adapter.HinhDanhMucAdapter;
import com.hynguyen.chitieucanhan.database.AppViewModel;
import com.hynguyen.chitieucanhan.mdel.DanhMuc;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ThemDanhMucFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    public static final String TAG = "ThemDanhMuc"; // Tên tag cho log
    private AppViewModel appViewModel; // ViewModel quản lý dữ liệu cho giao diện
    private MaterialButtonToggleGroup tgbLoaiDanhMuc; // Nhóm nút chọn loại danh mục (Thu/Chi)
    private MaterialButton btnDanhMucThu, btnDanhMucChi, btnHuyBo, btnHoanThanh; // Nút chọn danh mục Thu, Chi, Hủy bỏ, Hoàn thành
    private ImageView imgChonHinh; // Hình ảnh chọn hình đại diện cho danh mục
    private EditText txtTenDanhMuc; // Input text cho tên danh mục
    private ExpandableLayout expanHinhDanhMuc; // Layout mở rộng chứa các hình ảnh danh mục
    private RecyclerView rvHinhDanhMuc; // RecyclerView hiển thị các hình ảnh danh mục
    private HinhDanhMucAdapter hinhDanhMucAdapter; // Adapter cho hiển thị danh mục hình ảnh
    private List<String> listHinh; // Danh sách các hình ảnh

    private int idDanhMuc; // ID của danh mục (có thể được dùng để cập nhật hoặc xóa)
    private int loaiDanhMuc; // Loại của danh mục (Thu hoặc Chi)
    private String tenDanhMuc; // Tên của danh mục
    private String hinhDanhMuc; // Hình ảnh của danh mục

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false); // Đảm bảo rằng hộp thoại không thể bị hủy bỏ bởi thao tác quay lại hoặc nhấn nút quay lại của hệ thống
    }

    public static ThemDanhMucFragment newInstance() {
        return new ThemDanhMucFragment(); // Tạo một instance mới của fragment
    }

    // Lấy thông tin danh mục cần cập nhật
    public static ThemDanhMucFragment newInstance(DanhMuc danhMuc) {
        ThemDanhMucFragment dialog = new ThemDanhMucFragment();
        Bundle args = new Bundle();
        args.putInt("id", danhMuc.getId()); // Thêm ID danh mục vào bundle
        args.putString("name", danhMuc.getTenDanhMuc()); // Thêm tên danh mục vào bundle
        args.putString("img", danhMuc.getHinhAnh()); // Thêm hình ảnh danh mục vào bundle
        args.putInt("type", danhMuc.getLoaiDanhMuc()); // Thêm loại danh mục vào bundle
        dialog.setArguments(args); // Đặt các tham số vào bundle của dialog
        return dialog;
    }


    // Fragment quản lý thêm danh mục
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp layout cho fragment này
        return inflater.inflate(R.layout.fragment_themdanhmuc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        // Nhóm chọn loại danh mục (Thu/Chi)
        tgbLoaiDanhMuc = view.findViewById(R.id.tgbLoaiDanhMuc);
        tgbLoaiDanhMuc.check(R.id.btnDanhMucThu); // Mặc định là danh mục Thu
        tgbLoaiDanhMuc.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                switch (checkedId) {
                    case R.id.btnDanhMucThu: // Chọn danh mục Thu
                        buttonDanhMucThu();
                        break;
                    case R.id.btnDanhMucChi: // Chọn danh mục Chi
                        buttonDanhMucChi();
                        break;
                }
            }
        });

        // Khởi tạo các phần tử giao diện
        txtTenDanhMuc = view.findViewById(R.id.txtTenViTien);
        btnDanhMucThu = view.findViewById(R.id.btnDanhMucThu);
        imgChonHinh = view.findViewById(R.id.imgChonHinh);
        imgChonHinh.setOnClickListener(this); // Thiết lập listener cho ảnh khi nhấn vào
        expanHinhDanhMuc = view.findViewById(R.id.expanHinhDanhMuc);

        // Tải ảnh danh mục vào RecyclerView
        rvHinhDanhMuc = view.findViewById(R.id.rvHinhDanhMuc);
        rvHinhDanhMuc.setLayoutManager(new GridLayoutManager(getContext(), 5));
        listHinh = Arrays.asList(getResources().getStringArray(R.array.img_cat));
        hinhDanhMucAdapter = new HinhDanhMucAdapter(getContext(), listHinh);
        rvHinhDanhMuc.setAdapter(hinhDanhMucAdapter);

        // Khởi tạo các nút khác
        btnDanhMucChi = view.findViewById(R.id.btnDanhMucChi);
        btnHuyBo = view.findViewById(R.id.btnHuyBo);
        btnHuyBo.setOnClickListener(this); // Thiết lập listener cho nút Hủy bỏ
        btnHoanThanh = view.findViewById(R.id.btnHoanThanh);
        btnHoanThanh.setOnClickListener(this); // Thiết lập listener cho nút Hoàn thành

        // Hiển thị thông tin khi cập nhật
        capNhatDanhMuc();
    }


    // Hàm cập nhật danh mục khi fragment được khởi tạo với các tham số từ đối số
    private void capNhatDanhMuc() {
        // Kiểm tra xem có tham số không
        if (getArguments() != null) {
            // Lấy giá trị id từ đối số
            idDanhMuc = getArguments().getInt("id");
            // Lấy đường dẫn hình ảnh từ đối số
            hinhDanhMuc = getArguments().getString("img");
            // Cập nhật hình ảnh cho ImageView từ đường dẫn
            imgChonHinh.setImageResource(getIdHinh(getArguments().getString("img")));
            // Cập nhật tên danh mục
            txtTenDanhMuc.setText(getArguments().getString("name"));
            // Lấy loại danh mục từ đối số
            loaiDanhMuc = getArguments().getInt("type");
            // Kiểm tra loại danh mục và gọi hàm tương ứng
            if (loaiDanhMuc == 1) {
                buttonDanhMucThu(); // Nếu là danh mục Thu thì gọi hàm xử lý thu
            } else {
                buttonDanhMucChi(); // Nếu là danh mục Chi thì gọi hàm xử lý chi
            }
        }
    }

    // Hàm xử lý sự kiện click của các thành phần giao diện
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgChonHinh: // Khi nhấn vào hình ảnh
                xuLyChonHinh(); // Xử lý chọn hình ảnh
                break;
            case R.id.btnHuyBo: // Khi nhấn vào nút Hủy bỏ
                Toast.makeText(getContext(), "Hủy bỏ", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo hủy bỏ
                dismiss(); // Đóng fragment
                break;
            case R.id.btnHoanThanh: // Khi nhấn vào nút Hoàn thành
                xuLyThemDanhMuc(); // Xử lý thêm danh mục
                break;
        }
    }


    // Hàm xử lý thêm danh mục mới
    private void xuLyThemDanhMuc() {
        // Lấy tên danh mục từ EditText và loại bỏ khoảng trắng
        tenDanhMuc = txtTenDanhMuc.getText().toString().trim();

        // Nếu không có hình ảnh nào được chọn, thiết lập hình mặc định
        if (TextUtils.isEmpty(hinhDanhMuc)) {
            hinhDanhMuc = "cat_clipboard"; // Hình mặc định nếu không có hình ảnh nào được chọn
        }

        // Kiểm tra nếu tên danh mục chưa được điền
        if (TextUtils.isEmpty(tenDanhMuc)) {
            Toasty.error(getContext(), "Bạn chưa điền tên danh mục!", Toasty.LENGTH_SHORT, true).show();
            return; // Dừng hàm thực thi nếu tên danh mục rỗng
        }

        // Nếu loại danh mục không được xác định, mặc định là danh mục Thu
        if (loaiDanhMuc != 2) {
            loaiDanhMuc = 1; // Mặc định là danh mục Thu
        }

        // Kiểm tra xem fragment có nhận được đối số không
        if (getArguments() != null) {
            // Nếu có đối số, cập nhật danh mục
            DanhMuc danhMuc = new DanhMuc(idDanhMuc, tenDanhMuc, hinhDanhMuc, loaiDanhMuc);
            appViewModel.capNhatDanhMuc(danhMuc); // Gọi phương thức cập nhật của ViewModel
            Toasty.success(getContext(), "Cập nhật danh mục thành công", Toasty.LENGTH_SHORT, true).show(); // Hiển thị thông báo thành công
        } else {
            // Nếu không có đối số, thêm danh mục mới
            DanhMuc danhMuc = new DanhMuc(tenDanhMuc, hinhDanhMuc, loaiDanhMuc);
            appViewModel.themDanhMuc(danhMuc); // Gọi phương thức thêm mới của ViewModel
            Toasty.success(getContext(), "Thêm danh mục thành công", Toasty.LENGTH_SHORT, true).show(); // Hiển thị thông báo thành công
        }
    }


    // Hàm xử lý chọn hình ảnh
    private void xuLyChonHinh() {
        // Toggle trạng thái của expanHinhDanhMuc để hiển thị hoặc ẩn RecyclerView chứa các hình ảnh
        expanHinhDanhMuc.toggle();

        // Cài đặt listener cho adapter của RecyclerView
        hinhDanhMucAdapter.setOnItemClickListener(new HinhDanhMucAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name) {
                // Khi hình ảnh được chọn, cập nhật hình ảnh hiển thị trên ImageView
                imgChonHinh.setImageResource(getIdHinh(name));
                // Lưu tên hình ảnh đã chọn
                hinhDanhMuc = name;
            }
        });
    }

    // Lấy ID của hình ảnh từ tên
    private int getIdHinh(String name) {
        // Tìm ID của hình ảnh từ tài nguyên drawable dựa trên tên hình ảnh
        int drawableResourceId = getContext().getResources().getIdentifier(name, "drawable", getContext().getPackageName());
        return drawableResourceId;
    }

    // Hàm xử lý khi chọn danh mục Thu
    private void buttonDanhMucThu() {
        btnDanhMucThu.setBackgroundColor(getResources().getColor(R.color.button_checked)); // Đổi màu nền của nút danh mục Thu
        btnDanhMucThu.setTextColor(getResources().getColor(R.color.white)); // Đổi màu chữ của nút danh mục Thu
        btnDanhMucChi.setBackgroundColor(getResources().getColor(R.color.button_uncheck)); // Đặt lại màu nền của nút danh mục Chi
        btnDanhMucChi.setTextColor(getResources().getColor(R.color.black)); // Đặt lại màu chữ của nút danh mục Chi
        loaiDanhMuc = 1; // Thiết lập loại danh mục là Thu
    }

    // Hàm xử lý khi chọn danh mục Chi
    private void buttonDanhMucChi() {
        btnDanhMucChi.setBackgroundColor(getResources().getColor(R.color.button_checked)); // Đổi màu nền của nút danh mục Chi
        btnDanhMucChi.setTextColor(getResources().getColor(R.color.white)); // Đổi màu chữ của nút danh mục Chi
        btnDanhMucThu.setBackgroundColor(getResources().getColor(R.color.button_uncheck)); // Đặt lại màu nền của nút danh mục Thu
        btnDanhMucThu.setTextColor(getResources().getColor(R.color.black)); // Đặt lại màu chữ của nút danh mục Thu
        loaiDanhMuc = 2; // Thiết lập loại danh mục là Chi
    }

}
