package com.sosyalag.bngsocial3.Model;

public class Yorum {
    public String paylasimID;   // Örn: 107-P01
    public int yorumYapanID;    // Örn: 118
    public int tur;             // 0: Nötr, 1: Olumlu, 2: Olumsuz

    public Yorum(String paylasimID, int yorumYapanID, int tur) {
        this.paylasimID = paylasimID;
        this.yorumYapanID = yorumYapanID;
        this.tur = tur;
    }
}