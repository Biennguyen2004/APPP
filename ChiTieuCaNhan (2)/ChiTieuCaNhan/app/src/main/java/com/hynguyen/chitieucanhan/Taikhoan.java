package com.hynguyen.chitieucanhan;


public class Taikhoan {
    private String TenTk;
    private String HoTen;
    private String NgaySinh;
    private String DiaChi;
    private String Mail;
    private String MK;
    private byte[] avatar;
    public Taikhoan(String tenTk, String hoTen, String ngaySinh, String diaChi, String mail, String MK) {
        this.TenTk = tenTk;
        this.HoTen = hoTen;
        this.NgaySinh = ngaySinh;
        this.DiaChi = diaChi;
        this.Mail = mail;
        this.MK = MK;
    }

    public Taikhoan(String tenTk, String hoTen, String ngaySinh, String diaChi, String mail, String MK, byte[] avatar) {
        TenTk = tenTk;
        HoTen = hoTen;
        NgaySinh = ngaySinh;
        DiaChi = diaChi;
        Mail = mail;
        this.MK = MK;
        this.avatar = avatar;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public Taikhoan() {
    }

    public String getTenTk() {
        return TenTk;
    }

    public void setTenTk(String tenTk) {
        TenTk = tenTk;
    }

    public String getHoTen() {
        return HoTen;
    }

    public void setHoTen(String hoTen) {
        HoTen = hoTen;
    }

    public String getNgaySinh() {
        return NgaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        NgaySinh = ngaySinh;
    }

    public String getDiaChi() {
        return DiaChi;
    }

    public void setDiaChi(String diaChi) {
        DiaChi = diaChi;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getMK() {
        return MK;
    }

    public void setMK(String MK) {
        this.MK = MK;
    }
}
