package com.sosyalag.bngsocial3.Model;

public class Begeni {
    public String paylasimID; // Örn: 107-P01
    public int begenenID;     // Örn: 102
    public int tur;// 1: Beğeni, 0: Beğenmeme

    public Begeni(String paylasimID, int begenenID, int tur) {
        this.paylasimID = paylasimID;
        this.begenenID = begenenID;
        this.tur = tur;
    }
}

