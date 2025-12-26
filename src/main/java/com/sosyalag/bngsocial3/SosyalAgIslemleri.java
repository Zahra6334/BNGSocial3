package com.sosyalag.bngsocial3;

import com.sosyalag.bngsocial3.Model.Begeni;
import com.sosyalag.bngsocial3.Model.Kisi;
import com.sosyalag.bngsocial3.Model.Yorum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SosyalAgIslemleri {

    private Kisi[] kisilerDizisi;
    private int[][] iliskiMatrisi;
    private ArrayList<Begeni> begeniListesi;
    private ArrayList<Yorum> yorumListesi;
    private StringBuilder logBuffer;

    public SosyalAgIslemleri() {
        begeniListesi = new ArrayList<>();
        yorumListesi = new ArrayList<>();
        logBuffer = new StringBuilder();
    }

    public String getSonMesaj() {
        String m = logBuffer.toString();
        logBuffer.setLength(0);
        return m;
    }

    private void log(String mesaj) {
        logBuffer.append(mesaj).append("\n");
    }

    public void tumDosyalariYukle() {
        dosyaOkuKisiler();
        grafOlusturIliskiler();
        dosyaOkuBegeni();
        dosyaOkuYorum();
    }

    private void dosyaOkuKisiler() {
        ArrayList<Kisi> liste = new ArrayList<>();
        File dosya = new File("Kisiler.txt");
        if (!dosya.exists()) dosya = new File("TXT/Kisiler.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                if (satir.trim().isEmpty()) continue;
                String[] p = satir.trim().split(",");
                if (p.length >= 3) {
                    liste.add(new Kisi(Integer.parseInt(p[0].trim()), p[1].trim(), p[2].trim()));
                }
            }
            kisilerDizisi = new Kisi[liste.size()];
            liste.toArray(kisilerDizisi);
            log("✅ Kisiler.txt yüklendi: " + kisilerDizisi.length);
        } catch (IOException e) { log("❌ HATA: Kisiler.txt okunamadı."); }
    }

    private void grafOlusturIliskiler() {
        if (kisilerDizisi == null) return;
        int N = kisilerDizisi.length;
        iliskiMatrisi = new int[N][N];

        File dosya = new File("Iliski.txt");
        if (!dosya.exists()) dosya = new File("TXT/Iliski.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir;
            int row = 0;
            while ((satir = br.readLine()) != null && row < N) {
                if (satir.trim().isEmpty()) continue;
                String[] p = satir.trim().split(",");
                for (int col = 0; col < p.length && col < N; col++) {
                    if (!p[col].trim().equals("-")) iliskiMatrisi[row][col] = Integer.parseInt(p[col].trim());
                }
                row++;
            }
            log("✅ İlişki Grafı oluşturuldu.");
        } catch (Exception e) { log("❌ HATA: Iliski.txt okunamadı."); }
    }

    private void dosyaOkuBegeni() {
        begeniListesi.clear();
        File dosya = new File("Begeni.txt");
        if (!dosya.exists()) dosya = new File("TXT/Begeni.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String s;
            while ((s = br.readLine()) != null) {
                String[] p = s.trim().split(",");
                if (p.length >= 3) begeniListesi.add(new Begeni(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2])));
            }
        } catch (Exception e) {}
    }

    private void dosyaOkuYorum() {
        yorumListesi.clear();
        File dosya = new File("Yorum.txt");
        if (!dosya.exists()) dosya = new File("TXT/Yorum.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String s;
            while ((s = br.readLine()) != null) {
                String[] p = s.trim().split(",");
                if (p.length >= 3) yorumListesi.add(new Yorum(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2])));
            }
        } catch (Exception e) {}
    }

    public int binarySearch(int id) {
        if (kisilerDizisi == null) return -1;
        int sol = 0, sag = kisilerDizisi.length - 1;
        while (sol <= sag) {
            int orta = sol + (sag - sol) / 2;
            if (kisilerDizisi[orta].getId() == id) return orta;
            if (kisilerDizisi[orta].getId() < id) sol = orta + 1;
            else sag = orta - 1;
        }
        return -1;
    }

    public void arkadaslikDurumuSorgula(int kID, int hID) {
        int k = binarySearch(kID);
        int h = binarySearch(hID);
        if (k == -1 || h == -1) { log("❌ HATA: Kişi bulunamadı."); return; }
        int durum = iliskiMatrisi[k][h];
        String txt = (durum == 0) ? "Arkadaş Değil" : (durum == 1 ? "Arkadaş" : "Yakın Arkadaş");
        log("🔍 SONUÇ: " + kisilerDizisi[k].getAd() + " ile " + kisilerDizisi[h].getAd() + " -> " + txt);
    }

    public double iliskiPuaniHesapla(int iID, int jID) {
        double puan = 0;
        int iIdx = binarySearch(iID);
        int jIdx = binarySearch(jID);

        if (iIdx != -1 && jIdx != -1) {
            int durum = iliskiMatrisi[iIdx][jIdx];
            if (durum == 1) puan += 15;
            else if (durum == 2) puan += 30;
        }

        for (Begeni b : begeniListesi) {
            int postSahibi = Integer.parseInt(b.paylasimID.split("-")[0]);
            if (postSahibi == iID && b.begenenID == jID) {
                puan += (b.tur == 1 ? 5 : -5);
            }
        }

        for (Yorum y : yorumListesi) {
            int postSahibi = Integer.parseInt(y.paylasimID.split("-")[0]);
            if (postSahibi == iID && y.yorumYapanID == jID) {
                if (y.tur == 0) puan += 5;
                else if (y.tur == 1) puan += 10;
                else if (y.tur == 2) puan -= 5;
            }
        }
        return puan;
    }

    public void arkadasCikarmaOner(int userID) {
        if (kisilerDizisi == null) return;
        int uIdx = binarySearch(userID);
        if (uIdx == -1) { log("❌ HATA: Kişi bulunamadı."); return; }

        log("\n📉 " + kisilerDizisi[uIdx].getAd() + " İÇİN ÇIKARILMASI ÖNERİLENLER:");
        ArrayList<SkorItem> arkadasSkorlari = new ArrayList<>();

        for (int i = 0; i < kisilerDizisi.length; i++) {
            if (iliskiMatrisi[uIdx][i] == 1 || iliskiMatrisi[uIdx][i] == 2) {
                int arkID = kisilerDizisi[i].getId();
                double p = iliskiPuaniHesapla(userID, arkID);
                String durum = (iliskiMatrisi[uIdx][i] == 2) ? "Yakın Arkadaş" : "Arkadaş";
                arkadasSkorlari.add(new SkorItem(kisilerDizisi[i].getAd() + " (" + durum + ")", arkID, p));
            }
        }

        arkadasSkorlari.sort((o1, o2) -> Double.compare(o1.puan, o2.puan));

        int limit = Math.min(3, arkadasSkorlari.size());
        for (int i = 0; i < limit; i++) {
            SkorItem item = arkadasSkorlari.get(i);
            log((i+1) + ". Öneri: " + item.ad + " | Puan: " + item.puan);
        }
    }

    public void arkadasOner(int userID) {
        if (kisilerDizisi == null) return;
        int uIdx = binarySearch(userID);
        if (uIdx == -1) return;

        ArrayList<SkorItem> adaylar = new ArrayList<>();
        for (Kisi aday : kisilerDizisi) {
            if (aday.getId() == userID) continue;
            int aIdx = binarySearch(aday.getId());
            if (iliskiMatrisi[uIdx][aIdx] != 0) continue;

            double oneriPuani = iliskiPuaniHesapla(aday.getId(), userID);
            for (int k = 0; k < kisilerDizisi.length; k++) {
                if (iliskiMatrisi[uIdx][k] != 0) {
                    oneriPuani += iliskiPuaniHesapla(aday.getId(), kisilerDizisi[k].getId());
                }
            }
            adaylar.add(new SkorItem(aday.getAd(), aday.getId(), oneriPuani));
        }

        Collections.sort(adaylar);
        log("\n🌟 " + kisilerDizisi[uIdx].getAd() + " İÇİN ARKADAŞ ÖNERİLERİ:");
        for (int i = 0; i < Math.min(3, adaylar.size()); i++) {
            log((i+1) + ". Öneri: " + adaylar.get(i).ad + " (Puan: " + adaylar.get(i).puan + ")");
        }
    }

    public void genelSiralama() {
        if (kisilerDizisi == null) return;
        ArrayList<SkorItem> tumu = new ArrayList<>();
        for(int i=0; i<kisilerDizisi.length; i++) {
            for(int j=0; j<kisilerDizisi.length; j++) {
                if(i==j) continue;
                double p = iliskiPuaniHesapla(kisilerDizisi[i].getId(), kisilerDizisi[j].getId());
                if (p != 0) {
                    tumu.add(new SkorItem(kisilerDizisi[i].getAd() + " -> " + kisilerDizisi[j].getAd(), p));
                }
            }
        }
        Collections.sort(tumu);
        log("\n📊 GENEL İLİŞKİ SIRALAMASI:");
        for(int i=0; i<Math.min(20, tumu.size()); i++) {
            log((i+1) + ". " + tumu.get(i).aciklama + " : " + tumu.get(i).puan);
        }
    }

    public void enCokEtkilesimAlanlar() {
        // BEĞENİ
        Map<Integer, Integer> likes = new HashMap<>();
        for(Begeni b : begeniListesi) {
            if(b.tur == 1) {
                int id = Integer.parseInt(b.paylasimID.split("-")[0]);
                likes.put(id, likes.getOrDefault(id, 0) + 1);
            }
        }
        ArrayList<SkorItem> likeList = new ArrayList<>();
        for(Integer id : likes.keySet()) {
            int idx = binarySearch(id);
            if(idx != -1) likeList.add(new SkorItem(kisilerDizisi[idx].getAd(), id, likes.get(id)));
        }
        Collections.sort(likeList);

        log("\n❤️ EN ÇOK BEĞENİ ALANLAR:");
        for(int i = 0; i < Math.min(3, likeList.size()); i++) {
            log((i+1) + ". " + likeList.get(i).ad + " (" + (int)likeList.get(i).puan + " Beğeni)");
        }

        // YORUM
        Map<Integer, Integer> comments = new HashMap<>();
        for(Yorum y : yorumListesi) {
            int id = Integer.parseInt(y.paylasimID.split("-")[0]);
            comments.put(id, comments.getOrDefault(id, 0) + 1);
        }

        ArrayList<SkorItem> commentList = new ArrayList<>();
        for(Integer id : comments.keySet()) {
            int idx = binarySearch(id);
            if(idx != -1) commentList.add(new SkorItem(kisilerDizisi[idx].getAd(), id, comments.get(id)));
        }
        Collections.sort(commentList);

        log("\n💬 EN ÇOK YORUM ALANLAR:");
        for(int i = 0; i < Math.min(3, commentList.size()); i++) {
            log((i+1) + ". " + commentList.get(i).ad + " (" + (int)commentList.get(i).puan + " Yorum)");
        }
    }

    class SkorItem implements Comparable<SkorItem> {
        String ad; int id; double puan; String aciklama;
        public SkorItem(String ad, int id, double puan) { this.ad = ad; this.id = id; this.puan = puan; }
        public SkorItem(String txt, double puan) { this.aciklama = txt; this.puan = puan; }
        @Override
        public int compareTo(SkorItem o) { return Double.compare(o.puan, this.puan); }
    }
}