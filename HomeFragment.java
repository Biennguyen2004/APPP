package com.hynguyen.chitieucanhanNhom1.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.hynguyen.chitieucanhanNhom1.R;
import com.hynguyen.chitieucanhanNhom1.activity.ViTienActivity;
import com.hynguyen.chitieucanhanNhom1.adapter.ChiTieuAdapter;
import com.hynguyen.chitieucanhanNhom1.adapter.ChonThangAdapter;
import com.hynguyen.chitieucanhanNhom1.adapter.ViTienAdapter;
import com.hynguyen.chitieucanhanNhom1.database.AppViewModel;
import com.hynguyen.chitieucanhanNhom1.mdel.ChiTieu;
import com.hynguyen.chitieucanhanNhom1.mdel.ViTien;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private View view;

    //Database
    private AppViewModel appViewModel;
    //Chọn ngày: Được dùng để tạo hiệu ứng mở rộng, cho phép người dùng chọn tháng trong giao diện.
    private ExpandableLayout expMonth;
    // Một GridView để chọn tháng (có thể hiển thị các tháng trong năm).
    private GridView gvPickMonth;
    // TextView txtYear: Hiển thị năm hiện tại mà người dùng đang chọn.
    private TextView txtYear;
    private ChonThangAdapter chonThangAdapter;
    private ImageButton btnLastYear, btnNextYear;
    //Hiển thị tổng thu nhập và chi tiêu.
    private TextView txtTongThu, txtTongChi;


    //Chart : Biểu đồ tròn hiển thị thống kê thu chi.
    private PieChart lcThongKeThuChi;
    //bien Dữ liệu: chứa danh sách chi tiêu, đây là cách mà Android xử lý dữ liệu động và tự động cập nhật UI khi dữ liệu thay đổi.
    private LiveData<List<ChiTieu>> chiTieuLiveData;
    // Danh sách chi tiêu hiện tại sẽ làm việc.
    private List<ChiTieu> listChiTieu;
    private long day;
    private long month;
    private long year;

    //Ví tiền
    private RecyclerView rvViTien;
    private ViTienAdapter viTienAdapter;
    private LiveData<List<ViTien>> viTienLiveData;

    //Thu Chi
    private RecyclerView rvCacChiTieu;
    private ChiTieuAdapter chiTieuAdapter;

    //  Khi giao diện của Fragment được tạo ra, hàm onCreateView() sẽ được gọi
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // tạo giao diện từ R.layout - inflate giúp nạp layout vào view
        view = inflater.inflate(R.layout.fragment_home, container, false);
        // Tìm và ánh xạ (link) đối tượng PieChart từ XML vào biến lcThongKeThuChi.
        // Biểu đồ này sẽ hiển thị thống kê thu chi.
        lcThongKeThuChi = view.findViewById(R.id.lcThongKeThuChi);

        // Ánh xạ các TextView để hiển thị tổng thu và tổng chi từ dữ liệu.
        txtTongThu = view.findViewById(R.id.txtTongThu);
        txtTongChi = view.findViewById(R.id.txtTongChi);

        // xử lý việc cuộn lên đầu màn hình
        buttonScrollToTop();

        // khởi tạo hoặc lấy dữ liệu từ cơ sở dữ liệu, như lấy danh sách chi tiêu
        addDatabase();

        chonThangNam();

        // hiển thị các biểu đồ và danh sách các ví tiền, chi tiêu, và các sự kiện người dùng liên quan
        //Thiết lập các thành phần giao diện, bao gồm RecyclerView và biểu đồ, và quan sát thay đổi dữ liệu từ LiveData.
        addView();


        // Tạo biểu đồ tròn từ dữ liệu thu nhập và chi tiêu. Mỗi mục chi tiêu và thu nhập được tính tổng và hiển thị dưới dạng một đối tượng Entry, sau đó đưa vào biểu đồ tròn (PieChart).
        lineChart();


        return view;
    }

    // giúp khởi tạo AppViewModel, cho phép bạn truy xuất dữ liệu từ cơ sở dữ liệu và tự động cập nhật UI khi có thay đổi trong dữ liệu.
    private void addDatabase() {
        // ViewModelProvider:  xử lý dữ liệu liên quan đến UI, và dữ liệu đó sẽ không bị mất khi có sự thay đổi cấu hình (như xoay màn hình).
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
    }

    // Hàm showThongTinBieuDo() hiển thị một hộp thoại với thông tin chi tiết của một điểm dữ liệu từ biểu đồ:
    //
    //Hiển thị ngày tháng từ giá trị X của điểm dữ liệu.
    //Hiển thị số tiền từ giá trị Y của điểm dữ liệu.
    //Cung cấp cho người dùng khả năng đóng hộp thoại khi họ nhấn vào ngoài hộp thoại.
    private void showThongTinBieuDo(Entry entry) {
        // khởi tạo đối tượng này để hiển thị dialog trong ứng dụng để hiển thị thông báo
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Date date = new Date((long) entry.getX());

        builder.setMessage("Ngày: " + date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getYear()
                + "\nSố tiền: " + numberFormat(String.valueOf((int)entry.getY())));
        // Cho phép hộp thoại có thể đóng lại khi người dùng chạm ra ngoài hoặc nhấn nút "Back".
        builder.setCancelable(true);
        // Create AlertDialog:
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void addView() {

        //Line chart
        chiTieuLiveData = appViewModel.tatCaChiTieu(); // chứa danh sách chi tiêu (ChiTieu)
        listChiTieu = appViewModel.xuatChiTieu();
        chiTieuLiveData.observe(getViewLifecycleOwner(), new Observer<List<ChiTieu>>() {
            @Override
            // Khi dữ liệu trong LiveData thay đổi, hàm này sẽ được gọi
            public void onChanged(List<ChiTieu> chiTieuList) {
                // cập nhật dữ liệu trong adapter, giúp hiển thị danh sách chi tiêu trên RecyclerView.
                chiTieuAdapter.submitList(chiTieuList);
                listChiTieu = chiTieuList;
                lineChart();
            }
        });


        // Cung cấp thông tin chi tiết: Khi người dùng chọn một giá trị trên biểu đồ, phương thức showThongTinBieuDo(e) có thể hiển thị thông tin
        lcThongKeThuChi.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                showThongTinBieuDo(e);
            }

            @Override
            public void onNothingSelected() {

            }
        });


        //Ví Tiền
        rvViTien = view.findViewById(R.id.rvViTien);
        rvViTien.setLayoutManager(new LinearLayoutManager(getContext()));
        viTienAdapter = new ViTienAdapter(getContext());
        rvViTien.setAdapter(viTienAdapter);
        viTienLiveData = appViewModel.tatCaViTien();
        viTienLiveData.observe(getViewLifecycleOwner(), new Observer<List<ViTien>>() {
            @Override
            public void onChanged(List<ViTien> viTienList) {
                viTienAdapter.submitList(viTienList);
            }
        });
        viTienAdapter.setOnItemClickListener(new ViTienAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ViTien viTien) {
                startActivity(new Intent(getActivity(), ViTienActivity.class));
            }
        });

        //Chi Tiêu
        rvCacChiTieu = view.findViewById(R.id.rvCacChiTieu);
        rvCacChiTieu.setLayoutManager(new LinearLayoutManager(getContext()));
        chiTieuAdapter = new ChiTieuAdapter(getContext());
        rvCacChiTieu.setAdapter(chiTieuAdapter);
    }

    // chức năng tạo biểu đồ tròn (Pie Chart) và chuẩn bị dữ liệu
    // hiển thị tổng hợp thông tin thu nhập và chi tiêu của người dùng
    @SuppressLint("SetTextI18n")
    private void lineChart() {

        // Entry thường chứa hai giá trị: Entry thường được sử dụng trong các thư viện như MPAndroidChart để biểu diễn dữ liệu trên đồ thị
        //X-axis value (giá trị trục X): đại diện cho một mốc thời gian (hoặc một chỉ số).
        //Y-axis value (giá trị trục Y): đại diện cho giá trị tương ứng tại mốc thời gian đó (ví dụ: số tiền).


        // chứa các đối tượng Entry, mỗi đối tượng biểu diễn tổng số tiền chi tiêu, thu nhập trong một ngày.

        // 1 đối tượng là tổng thu, chi của một ngày
        List<Entry> listChi = new ArrayList<>();
        List<Entry> listThu = new ArrayList<>();


        // Lặp qua danh sách các chi tiêu để xử lý từng mục
        for (int i = 0; i < listChiTieu.size(); i++) {
            // Lấy mục chi tiêu hiện tại trong danh sách
            ChiTieu chitieui = listChiTieu.get(i);

            // Chuyển ngày của mục chi tiêu sang dạng milliseconds để biểu diễn trên trục X của biểu đồ
            long k = chitieui.getDate().getTime();

            // Kiểm tra xem mục chi tiêu có thuộc năm hiện tại hay không
            // Điều này giúp lọc dữ liệu chỉ lấy các khoản chi tiêu liên quan đến năm cần thống kê
            if (chitieui.getDate().getYear() == year) {
                // Kiểm tra nếu loại của mục chi tiêu là "chi tiêu" (Type = 2)
                // Tác dụng: chỉ quan tâm đến các khoản chi tiêu, bỏ qua các loại khác (ví dụ: thu nhập)
                if (chitieui.getType() == 2) {
                    // Khởi tạo biến để tính tổng chi tiêu cho một ngày
                    long tongChi = 0;

                    // Cộng số tiền của mục chi tiêu hiện tại vào tổng
                    tongChi += Long.parseLong(chitieui.getMoney());

                    // Lặp qua các mục chi tiêu tiếp theo để gộp các khoản chi tiêu cùng ngày
                    for (int j = i + 1; j < listChiTieu.size(); j++) {
                        // Lấy mục chi tiêu tiếp theo trong danh sách
                        ChiTieu chitieuj = listChiTieu.get(j);

                        // Kiểm tra nếu mục hiện tại và mục tiếp theo có cùng ngày và cùng loại (Type = 2)
                        // Tác dụng: gộp các khoản chi tiêu của cùng một ngày lại thành một giá trị tổng
                        if (chitieui.getDate().equals(chitieuj.getDate()) && chitieui.getType() == chitieuj.getType()) {
                            // Cộng số tiền của mục tiếp theo vào tổng chi tiêu
                            tongChi += Long.parseLong(chitieuj.getMoney());

                            // Di chuyển chỉ số `i` đến vị trí của mục cuối cùng được gộp
                            // Tác dụng: tránh xử lý lại các mục đã gộp, tăng hiệu quả vòng lặp
                            i = j;
                        }
                    }

                    // Thêm tổng chi tiêu của ngày này vào danh sách `listChi`
                    // Tác dụng: tạo dữ liệu đầu vào cho biểu đồ, mỗi `Entry` gồm:
                    // - Trục X (`k`): biểu diễn ngày dưới dạng thời gian (milliseconds)
                    // - Trục Y (`tongChi`): tổng số tiền chi tiêu trong ngày
                    listChi.add(new Entry(k, tongChi));
                }
            }
        }


        //Tìm tog khoản thu 1 ngày
        for (int i = 0; i < listChiTieu.size(); i++) {
            ChiTieu chitieui = listChiTieu.get(i);
            long h = chitieui.getDate().getTime();
            if (chitieui.getDate().getYear() == year) {
                if (chitieui.getType() == 1) {
                    long tongThu = 0;
                    tongThu += Long.parseLong(chitieui.getMoney());
                    for (int j = i + 1; j < listChiTieu.size(); j++) {
                        ChiTieu chitieuj = listChiTieu.get(j);
                        if (chitieui.getDate().equals(chitieuj.getDate()) && chitieui.getType() == chitieuj.getType()) {
                            tongThu += Long.parseLong(chitieuj.getMoney());
                            i = j;
                        }
                    }
                    listThu.add(new Entry(h, tongThu));
                }
            }
        }



        //pie chart: sử dụng một thư viện biểu đồ (như MPAndroidChart trong Android) để trực quan hóa dữ liệu.

        // 1. Tính tổng chi tiêu và thu nhập
        long tongChi = 0; // tổng toàn bộ chi tiêu
        long tongThu = 0; // tổng toàn bộ thu
        for (int i = 0; i < listChi.size(); i++) {
            tongChi += listChi.get(i).getY();
        }
        for (int i = 0; i < listThu.size(); i++) {
            tongThu += listThu.get(i).getY();
        }

        // 2. Thiết lập màu sắc cho các phần biểu đồ
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED); // // Màu đỏ đại diện cho "Chi"
        colors.add(Color.BLUE); // Màu xanh đại diện cho "Thu"

        // 3. Chuẩn bị dữ liệu cho Pie Chart
        List<PieEntry> listPie = new ArrayList<>(); // PieEntry: Mỗi phần của biểu đồ tròn đại diện cho một danh mục ("Thu", "Chi").
        if (tongThu > 0) {
            listPie.add(new PieEntry(tongThu, "Thu")); // Nếu tongThu > 0, thêm một phần cho thu nhập
        }

        // Check if chi is greater than 0 and add it to the entries
        if (tongChi > 0) {
            listPie.add(new PieEntry(tongChi, "Chi")); // Nếu tongChi > 0, thêm một phần cho chi tiêu.
        }


        // 4. PieDataSet: Bộ dữ liệu được dùng để tạo biểu đồ tròn.
        PieDataSet thuchi = new PieDataSet(listPie, "");
        thuchi.setColors(colors); // setColors(colors): Gán màu sắc đã định nghĩa ở bước trên cho biểu đồ.


        lcThongKeThuChi.animateX(200);
        lcThongKeThuChi.getDescription().setEnabled(false);


        // 5. Hiển thị biểu đồ
        PieData pieData = new PieData(thuchi);
        pieData.setDrawValues(true);
        Legend legend = lcThongKeThuChi.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        lcThongKeThuChi.setData(pieData);
        lcThongKeThuChi.invalidate();
        txtTongThu.setText(numberFormat(String.valueOf(tongThu))+ " VND");
        txtTongThu.setBackgroundColor(colors.get(0));
        txtTongChi.setText(numberFormat(String.valueOf(tongChi))+ " VND");
        txtTongChi.setBackgroundColor(colors.get(1));
    }

    @SuppressLint("SetTextI18n")
    private void lineChartMonth(long month) {

        //  tổng chi tiêu cho từng ngày trong năm hiện tại.
        List<Entry> listChi = new ArrayList<>();
        //  tổng thu nhập cho từng ngày trong năm hiện tại.
        List<Entry> listThu = new ArrayList<>();


        //Tìm tổng khoản chi theo từng ngày trong một tháng cụ thể của năm được chỉ định
        for (int i = 0; i < listChiTieu.size(); i++) {
            ChiTieu chitieui = listChiTieu.get(i);
            if (chitieui.getDate().getYear() == year && chitieui.getDate().getMonth() == month) {
                int k = chitieui.getDate().getDate();
                if (chitieui.getType() == 2) {
                    long tongChi = 0;
                    tongChi += Long.parseLong(chitieui.getMoney());
                    for (int j = i + 1; j < listChiTieu.size(); j++) {
                        ChiTieu chitieuj = listChiTieu.get(j);
                        if (chitieui.getDate().equals(chitieuj.getDate()) && chitieui.getType() == chitieuj.getType()) {
                            tongChi += Long.parseLong(chitieuj.getMoney());
                            i = j;
                        }
                    }
                    listChi.add(new Entry(k, tongChi));
                }
            }
        }

        //Tìm  t khoản thu theo từng ngày trong một tháng cụ thể của năm được chỉ định
        for (int i = 0; i < listChiTieu.size(); i++) {
            ChiTieu chitieui = listChiTieu.get(i);
            if (chitieui.getDate().getYear() == year && chitieui.getDate().getMonth() == month) {
                int k = chitieui.getDate().getDate();
                if (chitieui.getType() == 1) {
                    long tongThu = 0;
                    tongThu += Long.parseLong(chitieui.getMoney());
                    for (int j = i + 1; j < listChiTieu.size(); j++) {
                        ChiTieu chitieuj = listChiTieu.get(j);
                        if (chitieui.getDate().equals(chitieuj.getDate()) && chitieui.getType() == chitieuj.getType()) {
                            tongThu += Long.parseLong(chitieuj.getMoney());
                            i = j;
                        }
                    }
                    listThu.add(new Entry(k, tongThu));
                }
            }
        }


        long tongChi = 0;
        long tongThu = 0;
        for (int i = 0; i < listChi.size(); i++) {
            tongChi += listChi.get(i).getY();
        }

        for (int i = 0; i < listThu.size(); i++) {
            tongThu += listThu.get(i).getY();
        }

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        List<PieEntry> listPie = new ArrayList<>();
        if (tongThu > 0) {
            listPie.add(new PieEntry(tongThu, "Thu"));
        }

        // Check if chi is greater than 0 and add it to the entries
        if (tongChi > 0) {
            listPie.add(new PieEntry(tongChi, "Chi"));
        }
        PieDataSet thuchi = new PieDataSet(listPie, "");
        thuchi.setColors(colors);
        lcThongKeThuChi.animateX(200);
        lcThongKeThuChi.getDescription().setEnabled(false);




        PieData pieData = new PieData(thuchi);
        pieData.setDrawValues(true);
        Legend legend = lcThongKeThuChi.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        lcThongKeThuChi.setData(pieData);
        lcThongKeThuChi.invalidate();
        txtTongThu.setText(numberFormat(String.valueOf(tongThu))+ " VND");
        txtTongThu.setBackgroundColor(colors.get(0));
        txtTongChi.setText(numberFormat(String.valueOf(tongChi))+ " VND");
        txtTongChi.setBackgroundColor(colors.get(1));


    }

    private void chonThangNam() {
        expMonth = view.findViewById(R.id.expMonth);
        gvPickMonth = view.findViewById(R.id.gvPickMonth);
        txtYear = view.findViewById(R.id.txtYear);
        Calendar calendar = Calendar.getInstance();
        txtYear.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        txtYear.setOnClickListener(this);
        chonThangAdapter = new ChonThangAdapter(getContext());
        gvPickMonth.setAdapter(chonThangAdapter);
        btnLastYear = view.findViewById(R.id.btnLastYear);
        btnNextYear = view.findViewById(R.id.btnNextYear);
        btnLastYear.setOnClickListener(this);
        btnNextYear.setOnClickListener(this);
        gvPickMonth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id < 12) {
                    lineChartMonth(id);
                } else {
                    lineChart();
                }
            }
        });
        getYear();
    }

    private void getYear() {
        year = Long.parseLong(txtYear.getText().toString());
    }

    //Nút cuộn lên đầu trang
    private void buttonScrollToTop() {
        ImageButton btnScrollToTop = view.findViewById(R.id.btnScrollToTop);
        ScrollView scrHome = view.findViewById(R.id.scrHome);
        btnScrollToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrHome.post(new Runnable() {
                    @Override
                    public void run() {
                        scrHome.smoothScrollTo(0, 0);
                    }
                });
            }
        });
        scrHome.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrHome.getScrollY();
                if (scrollY > 500) {
                    btnScrollToTop.setVisibility(View.VISIBLE);
                } else {
                    btnScrollToTop.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtYear:
                expMonth.toggle();
                break;
            case R.id.btnLastYear:
                txtYear.setText(Integer.parseInt(txtYear.getText().toString()) - 1 + "");
                getYear();
                lineChart();
                break;
            case R.id.btnNextYear:
                txtYear.setText(Integer.parseInt(txtYear.getText().toString()) + 1 + "");
                getYear();
                lineChart();
                break;

        }
    }

    //Định dạng số tiền
    public String numberFormat(String string) {
        Long number = Long.parseLong(string);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        String formattedString = formatter.format(number);
        return formattedString;
    }
}
