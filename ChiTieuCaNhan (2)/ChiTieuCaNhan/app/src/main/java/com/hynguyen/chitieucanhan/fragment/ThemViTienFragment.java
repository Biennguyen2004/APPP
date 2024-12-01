package com.hynguyen.chitieucanhan.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.util.StringUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hynguyen.chitieucanhan.R;
import com.hynguyen.chitieucanhan.adapter.HinhDanhMucAdapter;
import com.hynguyen.chitieucanhan.database.AppViewModel;
import com.hynguyen.chitieucanhan.mdel.ViTien;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class ThemViTienFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    public static final String TAG = "ViTien"; // Gắn nhãn để debug/log
    private AppViewModel appViewModel; // ViewModel để quản lý dữ liệu UI
    private ImageView imgChonHinh; // Hiển thị hình ảnh đại diện ví tiền
    private ExpandableLayout expanHinhViTien; // Layout mở rộng để chọn hình
    private RecyclerView rvHinhViTien; // Danh sách hình ảnh ví tiền
    private List<String> listHinh; // Danh sách hình ảnh (URL hoặc tên file)
    private HinhDanhMucAdapter hinhDanhMucAdapter; // Adapter cho RecyclerView
    private MaterialButton btnHuyBo, btnHoanThanh; // Nút Hủy và Hoàn thành
    private TextView txtThemViTien; // Tiêu đề giao diện "Thêm Ví Tiền"
    private TextInputEditText txtTenViTien; // Trường nhập tên ví
    private TextInputEditText txtSoTien; // Trường nhập số tiền khởi tạo
    private TextView txtLoaiTienTe; // Hiển thị loại tiền tệ (VNĐ, USD, ...)
    private String hinhViTien; // Lưu hình ảnh đã chọn cho ví tiền
    private String tenViTien; // Lưu tên ví tiền
    private String soTien; // Lưu số tiền đã nhập


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false); // Đặt DialogFragment không thể bị hủy bằng cách nhấn ra ngoài hoặc nhấn nút Back.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Tạo và trả về giao diện của DialogFragment từ file XML `fragment_themvitien`.
        return inflater.inflate(R.layout.fragment_themvitien, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel để quản lý dữ liệu trong Fragment.
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);

        // Liên kết và thiết lập TextView hiển thị tiêu đề.
        txtThemViTien = view.findViewById(R.id.txtThemViTien);

        // Liên kết ImageView để chọn hình ví tiền và thiết lập sự kiện click.
        imgChonHinh = view.findViewById(R.id.imgChonHinh);
        imgChonHinh.setOnClickListener(this);

        // Liên kết và thiết lập ExpandableLayout để mở rộng danh sách hình.
        expanHinhViTien = view.findViewById(R.id.expanHinhViTien);

        // Liên kết RecyclerView để hiển thị danh sách hình ảnh ví tiền.
        rvHinhViTien = view.findViewById(R.id.rvHinhViTien);
        rvHinhViTien.setLayoutManager(new GridLayoutManager(getContext(), 5)); // Sử dụng GridLayout với 5 cột.

        // Lấy danh sách hình ảnh từ tài nguyên (array) và gắn adapter vào RecyclerView.
        listHinh = Arrays.asList(getResources().getStringArray(R.array.img_wallet));
        hinhDanhMucAdapter = new HinhDanhMucAdapter(getContext(), listHinh);
        rvHinhViTien.setAdapter(hinhDanhMucAdapter);

        // Thiết lập sự kiện click cho từng item trong danh sách hình.
        hinhDanhMucAdapter.setOnItemClickListener(new HinhDanhMucAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name) {
                // Đổi hình ảnh hiển thị khi người dùng chọn hình ví.
                imgChonHinh.setImageResource(getIdHinh(name));
                hinhViTien = name; // Lưu tên hình ảnh được chọn.
            }
        });

        // Liên kết và thiết lập các trường nhập liệu.
        txtTenViTien = view.findViewById(R.id.txtTenViTien); // Nhập tên ví tiền.
        txtSoTien = view.findViewById(R.id.txtSoTien); // Nhập số tiền ban đầu.

        // Thêm TextWatcher để theo dõi và định dạng khi nhập số tiền.
        txtSoTien.addTextChangedListener(onTextChangedListener());

        // Hiển thị loại tiền tệ từ ViewModel.
        txtLoaiTienTe = view.findViewById(R.id.txtLoaiTienTe);
        txtLoaiTienTe.setText(appViewModel.loaiTienTe().getName());

        // Liên kết và thiết lập sự kiện click cho nút "Hủy bỏ".
        btnHuyBo = view.findViewById(R.id.btnHuyBo);
        btnHuyBo.setOnClickListener(this);

        // Liên kết và thiết lập sự kiện click cho nút "Hoàn thành".
        btnHoanThanh = view.findViewById(R.id.btnHoanThanh);
        btnHoanThanh.setOnClickListener(this);

        // Kiểm tra nếu có dữ liệu truyền vào qua Bundle thì cập nhật giao diện.
        if (getArguments() != null) {
            // Lấy hình ảnh, tên ví, số tiền từ Bundle và cập nhật giao diện.
            hinhViTien = getArguments().getString("img");
            imgChonHinh.setImageResource(getIdHinh(getArguments().getString("img")));
            tenViTien = getArguments().getString("name");
            txtTenViTien.setText(tenViTien);
            soTien = getArguments().getString("money");
            txtSoTien.setText(soTien);

            // Thay đổi tiêu đề thành "Cập nhật ví tiền" khi chỉnh sửa.
            txtThemViTien.setText("Cập nhật ví tiền");
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgChonHinh:
                // Toggle trạng thái của ExpandableLayout khi người dùng nhấn vào hình ảnh chọn hình ví.
                expanHinhViTien.toggle();
                break;

            case R.id.btnHuyBo:
                // Đóng dialog hoặc Fragment hiện tại khi người dùng nhấn nút "Hủy bỏ".
                dismiss();
                break;

            case R.id.btnHoanThanh:
                // Xử lý khi người dùng nhấn nút "Hoàn thành". Thực hiện các tác vụ thêm ví tiền.
                xuLyThemViTien();
                break;
        }
    }


    private void xuLyThemViTien() {
        tenViTien = txtTenViTien.getText().toString().trim(); // Lấy tên ví tiền từ `TextInputEditText` và loại bỏ khoảng trắng dư thừa.
        soTien = txtSoTien.getText().toString().replace(",", ""); // Lấy số tiền từ `TextInputEditText` và loại bỏ dấu phẩy.

        // Kiểm tra tên ví tiền không được để trống.
        if (TextUtils.isEmpty(tenViTien)) {
            Toasty.error(getContext(), "Tên ví tiền không được để trống!", Toasty.LENGTH_SHORT, true).show();
            return;
        }

        // Kiểm tra số tiền không được để trống.
        if (TextUtils.isEmpty(soTien)) {
            Toasty.error(getContext(), "Bạn chưa nhập số tiền ban đầu!", Toasty.LENGTH_SHORT, true).show();
            return;
        }

        // Kiểm tra nếu người dùng không chọn hình ảnh, mặc định hình ảnh là "wallet_cash".
        if (TextUtils.isEmpty(hinhViTien)) {
            hinhViTien = "wallet_cash";
        }

        // Kiểm tra nếu số tiền có ký tự đặc biệt thì hiển thị thông báo lỗi.
        if (!isNumeric(soTien)) {
            Toasty.error(getContext(), "Số tiền không được chứa ký tự đặc biệt!", Toasty.LENGTH_SHORT, true).show();
            return;
        }

        // Kiểm tra nếu dialog không có arguments (có nghĩa là đây là một hành động thêm mới).
        if (getArguments() == null) {
            // Thêm ví tiền mới vào dữ liệu với thông tin hiện tại.
            ViTien viTien = new ViTien(hinhViTien, tenViTien, soTien);
            appViewModel.themViTien(viTien); // Gọi phương thức thêm ví tiền từ ViewModel.
            Toasty.success(getContext(), "Thêm ví tiền thành công", Toasty.LENGTH_SHORT, true).show(); // Hiển thị thông báo thành công.
        } else {
            // Cập nhật thông tin ví tiền hiện tại nếu có arguments.
            ViTien viTien = new ViTien(getArguments().getInt("id"), hinhViTien, tenViTien, soTien);
            appViewModel.capNhatViTien(viTien); // Gọi phương thức cập nhật ví tiền từ ViewModel.
            Toasty.success(getContext(), "Cập nhật ví tiền thành công", Toasty.LENGTH_SHORT, true).show(); // Hiển thị thông báo cập nhật thành công.
        }

        dismiss(); // Đóng dialog hoặc Fragment hiện tại.
    }


    //Lấy thông tin danh mục cần cập nhật
    public static ThemViTienFragment newInstance(ViTien viTien) {
        ThemViTienFragment dialog = new ThemViTienFragment();
        Bundle args = new Bundle();
        args.putInt("id", viTien.getId());
        args.putString("name", viTien.getName());
        args.putString("img", viTien.getImg());
        args.putString("money", viTien.getMoney());
        dialog.setArguments(args);
        return dialog;
    }

    public static ThemViTienFragment newInstance() {
        return new ThemViTienFragment();
    }


    private int getIdHinh(String name) {
        // Trả về ID của hình ảnh tương ứng trong tài nguyên của ứng dụng.
        int drawableResourceId = getContext().getResources().getIdentifier(name, "drawable", getContext().getPackageName());
        return drawableResourceId;
    }


    public static boolean isNumeric(String str) {
        try {
            // Dùng phương thức `Long.parseLong()` để cố gắng chuyển đổi `str` thành một số kiểu `long`.
            Long.parseLong(str);
            return true; // Nếu chuyển đổi thành công, `str` là một số.
        } catch(NumberFormatException e) {
            // Nếu có lỗi trong quá trình chuyển đổi, `str` không phải là một số.
            return false;
        }
    }


    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không làm gì trước khi văn bản thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Không làm gì khi văn bản đang thay đổi
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Xóa listener hiện tại
                txtSoTien.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    // Xóa dấu phẩy nếu có
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }

                    // Chuyển đổi thành số long
                    Long longval = Long.parseLong(originalString);

                    // Định dạng số theo kiểu Mỹ (ví dụ: 1,000,000)
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    // Đặt văn bản sau khi định dạng vào EditText
                    txtSoTien.setText(formattedString);
                    txtSoTien.setSelection(txtSoTien.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                // Thêm lại listener
                txtSoTien.addTextChangedListener(this);
            }
        };
    }

}
