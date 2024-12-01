package com.hynguyen.chitieucanhan;



import android.content.Context;
import android.database.Cursor;

import com.hynguyen.chitieucanhan.SQL;
import com.hynguyen.chitieucanhan.Taikhoan;

public class TaiKhoanDAO {
    private Context context;
    private SQL dbNauAn;

    public TaiKhoanDAO(Context context) {
        this.context = context;
        dbNauAn = new SQL(context);
    }
    public TaiKhoanDAO() {

        dbNauAn = new SQL();
    }
    public int InsertTaiKhoan(Taikhoan t) // thêm dữ liệu
    {
        try{
            String sql = "INSERT INTO TaiKhoan VALUES('"+t.getTenTk()+"','"+t.getMK()+"','"+t.getHoTen()+"','"+t.getNgaySinh()+"','"+t.getDiaChi()+"','"+t.getMail() + "', null" +")";
            dbNauAn.QuerryData(sql);
            return 1;
        }catch (Exception ex){
            return 0;
        }
    }
    public Cursor GetTaiKhoan(String id){
        Cursor cs =  null;
        try{
            String sql= " SELECT * FROM TaiKhoan WHERE TenTK ="+"'"+id+"'";
            cs = dbNauAn.GetData(sql);
            return cs;
        }catch (Exception ex){
            return cs;
        }
    }


    public int UpdateTaiKhoan(String sql){
        try{
            dbNauAn.QuerryData(sql);
            return 1;
        }catch (Exception ex){
            return 0;
        }

    }
    public void UpdateToTaiKhoan(String id,String pass, String ten, String ngaysinh, String diachi, String Gmail, byte[] hinh){
        dbNauAn.UpdateToTaiKhoan(id,pass,ten, ngaysinh,diachi,Gmail,hinh);

    }

}
