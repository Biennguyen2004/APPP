package com.hynguyen.chitieucanhan;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.NonNull;

public class SQL extends SQLiteOpenHelper {
    public SQL (Context context){
        super(context,"AppNauAn.db",null,1);
    }
    public SQL (){
        super(null,"AppNauAn.db",null,1);
    }
    // truy van khong tra ve : them, sua, xoa
    public void QuerryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    // truy van tra ve : tim kiem
    public Cursor GetData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }
    //
    public void UpdateToTaiKhoan(String id,String pass, String ten, String ngaysinh, String diachi, String Gmail, byte[] hinh){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM TaiKhoan WHERE TenTK = '"+id+"'";
        database.execSQL(sql);

        sql = "INSERT INTO TaiKhoan VALUES(?,?,?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, id);
        statement.bindString(2, pass);
        statement.bindString(3, ten);
        statement.bindString(4, ngaysinh);
        statement.bindString(5, diachi);
        statement.bindString(6, Gmail);
        statement.bindBlob(7, hinh);

        statement.executeInsert();

    }

    public void UpdateToMon(String id,String pass, String ten, String ngaysinh, String diachi, byte[] hinh){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM MonAn WHERE MaMon = '"+id+"'";
        database.execSQL(sql);

        sql = "INSERT INTO MonAn VALUES(?,?,?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, id);
        statement.bindString(2, pass);
        statement.bindString(3, ten);
        statement.bindString(4, ngaysinh);
        statement.bindString(5, diachi);
        statement.bindBlob(6, hinh);

        statement.executeInsert();

    }


    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        // tao bang tai khoan

        String sql = "CREATE TABLE TaiKhoan (TenTK Text PRIMARY KEY, MatKhau Text, HoTen Text, NgaySinh Text, DiaChi Text, Gmail Text, Avatar BLOB )";
        db.execSQL(sql);
        // tao bang mon an
        sql = "CREATE TABLE MonAn (MaMon Text PRIMARY KEY, TenMon Text, MaLoai Text, IDNguoiDang Text, CongThuc Text, HinhAnh BLOB)";
        db.execSQL(sql);
        // tao bang cong thuc
        //sql = "CREATE TABLE CongThuc (MaMon Text, NguyenLieu Text, Soluong Text, PRIMARY KEY(MaMon, NguyenLieu))";
        //db.execSQL(sql);
        // tao bang loai mon
        sql = "CREATE TABLE LoaiMon (MaLoai Text PRIMARY KEY, TenLoai Text)";
        db.execSQL(sql);
        // tao bang danh gia mon an
        sql = "CREATE TABLE DanhGia (MaMon Text , IDNguoiDanhGia Text, DanhGia Text, PRIMARY KEY(MaMon, IdNguoiDanhGia))";
        db.execSQL(sql);
        // tao bang binh luan mon an
        //sql = "CREATE TABLE BinhLuan (MaMon Text PRIMARY KEY, IDNguoiBinhLuan Text, NoiDung Text)";
        //db.execSQL(sql);
        // tao bang kho luu tru mon
        sql = "CREATE TABLE YeuThich (MaMon Text, IdChuKho Text, PRIMARY KEY(MaMon, IdChuKho))";
        db.execSQL(sql);
        //
        sql = "CREATE TABLE Follow (IdFled Text, IdFl Text, PRIMARY KEY(IdFled, IdFl))";
        db.execSQL(sql);
        // +++++++++++++++++++++++ Them database _________________________________________________
        // them data vao tai khoan
        sql = "INSERT INTO TaiKhoan VALUES ('quockhanh206', '123456','Nguyen Quoc Khanh','20/06/2001','Topax','abc@gmail.com', null)";
        db.execSQL(sql);
        sql = "INSERT INTO TaiKhoan VALUES ('bechin', '123456','Hoàng Thị Yến Trinh','13/03/2001','Thủ Đức','abc@gmail.com', null)";
        db.execSQL(sql);
        sql = "INSERT INTO TaiKhoan VALUES ('dinhon', '123456','Phan Nguyễn Duy Nhân','09/10/2001','Cửa sổ Topaz','abc@gmail.com', null)";
        db.execSQL(sql);
        sql = "INSERT INTO TaiKhoan VALUES ('beho', '123456','Lưu Văn Hòa','16/03/2001','ToPaz','abc@gmail.com', null)";
        db.execSQL(sql);
        // them bang mon an
//        sql = "INSERT INTO MonAn VALUES ('MC1', 'Đậu hũ rang','CHAY','quockhanh206','Đậu hũ 1 miếng , Nước tương 1 muỗng',null)";
//        db.execSQL(sql);
//        sql = "INSERT INTO MonAn VALUES ('MM2', 'Cá khô hấp phò mai','MAN','quockhanh206','Cá khô x1 , Phomai x1',null)";
//        db.execSQL(sql);
//        sql = "INSERT INTO MonAn VALUES ('MM3', 'Cá khô hấp phò mai','MAN','bechin','Cá khô x1 , Phomai x1',null)";
//        db.execSQL(sql);
//        sql = "INSERT INTO MonAn VALUES ('MV4', 'Cá khô hấp phò mai','MAN','beho','Cá khô x1 , Phomai x1',null)";
//        db.execSQL(sql);
//        sql = "INSERT INTO MonAn VALUES ('MK5', 'Cá khô hấp phò mai','MAN','dinhon','Cá khô x1 , Phomai x1',null)";
//        db.execSQL(sql);

        // them bang cong thuc
        /*
        sql = "INSERT INTO CongThuc VALUES ('MC1', 'Đậu hũ','1 miếng')";
        db.execSQL(sql);
        sql = "INSERT INTO CongThuc VALUES ('MC1', 'Nước tương','1 muỗng')";
        db.execSQL(sql);
        sql = "INSERT INTO CongThuc VALUES ('MM2', 'Cá khô','1 con')";
        db.execSQL(sql);
        sql = "INSERT INTO CongThuc VALUES ('MM2', 'Phô mai','1 miếng')";
        db.execSQL(sql);

         */
        // them bang loai mon
        sql = "INSERT INTO LoaiMon VALUES ('CHAY', 'Món chay' )";
        db.execSQL(sql);
        sql = "INSERT INTO LoaiMon VALUES ('MAN', 'Món Mặn' )";
        db.execSQL(sql);
        sql = "INSERT INTO LoaiMon VALUES ('KIENG', 'Món Kiêng' )";
        db.execSQL(sql);
        sql = "INSERT INTO LoaiMon VALUES ('VAT', 'Tất cả' )";
        db.execSQL(sql);
        // them bang danh gia mon an
        sql = "INSERT INTO DanhGia VALUES ('MC1', 'quockhanh206','5' )";
        db.execSQL(sql);
        sql = "INSERT INTO DanhGia VALUES ('MM2', 'bechin', '3' )";
        db.execSQL(sql);
        // them bang binh luan mon an
        // them bang kho luu tru mon
        //
        sql = "INSERT INTO Follow VALUES ('quockhanh206', 'bechin' )";
        db.execSQL(sql);
        sql = "INSERT INTO Follow VALUES ('quockhanh206', 'dinhon910' )";
        db.execSQL(sql);
        sql = "INSERT INTO Follow VALUES ('quockhanh206', 'beho1603' )";
        db.execSQL(sql);
        sql = "INSERT INTO Follow VALUES ('bechin', 'quockhanh206' )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS TaiKhoan");
        db.execSQL("DROP TABLE IF EXISTS MonAn");
        //db.execSQL("DROP TABLE IF EXISTS CongThuc");
        db.execSQL("DROP TABLE IF EXISTS LoaiMon");
        db.execSQL("DROP TABLE IF EXISTS DanhGia");
        //db.execSQL("DROP TABLE IF EXISTS BinhLuan");
        db.execSQL("DROP TABLE IF EXISTS YeuThich");
    }

}
