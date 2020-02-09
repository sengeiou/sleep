package com.szip.smartdream.Interface;

public interface OnClickForRegister {
    void onRegisterForPhone(String country, String code, String phone,String password, String verificationCode);
    void onRegisterForMail(String mail, String password,String verificationCode);
}
