package com.sosyalag.bngsocial3.Model;

public class Kisi {
    private int id;
    private String ad;
    private String cinsiyet;

    public Kisi(int id, String ad, String cinsiyet) {
        this.id = id;
        this.ad = ad;
        this.cinsiyet = cinsiyet;
    }

    public int getId() { return id; }
    public String getAd() { return ad; }

    @Override
    public String toString() {
        return ad + " (ID:" + id + ")";
    }
}