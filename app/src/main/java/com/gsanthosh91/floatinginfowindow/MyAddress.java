package com.gsanthosh91.floatinginfowindow;


public class MyAddress {
    private String address;
    private String eta;

    MyAddress(String address, String eta){
        this.address = address;
        this.eta = eta;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }
}
