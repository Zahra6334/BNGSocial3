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

    // --- VERİ YAPILARI (Global Değişkenler) ---
    private Kisi[] kisilerDizisi;
    private int[][] iliskiMatrisi; // GRAF YAPISI (Adjacency Matrix)
    private ArrayList<Begeni> begeniListesi;
    private ArrayList<Yorum> yorumListesi;

    // Log yazdırmak için arayüzden gelen fonksiyonu tutacağız
    private StringBuilder logBuffer;

    public SosyalAgIslemleri() {
        begeniListesi = new ArrayList<>();
        yorumListesi = new ArrayList<>();
        logBuffer = new StringBuilder();
    }

    // Arayüze mesaj göndermek için yardımcı metot
    public String getSonMesaj() {
        String m = logBuffer.toString();
        logBuffer.setLength(0); // Tamponu temizle
        return m;
    }
    private void log(String mesaj) {
        logBuffer.append(mesaj).append("\n");
    }

    // =============================================================
    // 1. DOSYA OKUMA VE GRAF OLUŞTURMA İŞLEMLERİ
    // =============================================================
    public void tumDosyalariYukle() {
        dosyaOkuKisiler();
        grafOlusturIliskiler();
        dosyaOkuBegeni();
        dosyaOkuYorum();
    }

    private void dosyaOkuKisiler() {
        ArrayList<Kisi> liste = new ArrayList<>();
        File dosya = new File("TXT/Kisiler.txt");
        if (!dosya.exists()) dosya = new File("Kisiler.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                if (satir.trim().isEmpty()) continue;
                String[] p = satir.trim().split("[,\\s]+");
                if (p.length >= 3) {
                    try {
                        liste.add(new Kisi(Integer.parseInt(p[0].trim()), p[1].trim(), p[2].trim()));
                    } catch (Exception e) {}
                }
            }
            // Listeyi diziye çeviriyoruz (Binary Search için sıralı olması iyi olur ama ID'ye göre zaten sıralı geliyor)
            kisilerDizisi = new Kisi[liste.size()];
            liste.toArray(kisilerDizisi);
            log(">> Kisiler.txt yüklendi. Kişi Sayısı: " + kisilerDizisi.length);
        } catch (IOException e) { log("HATA: Kisiler.txt okunamadı."); }
    }

    private void grafOlusturIliskiler() {
        if (kisilerDizisi == null) return;
        int N = kisilerDizisi.length;
        iliskiMatrisi = new int[N][N]; // Graf Matrisi başlatılıyor

        File dosya = new File("TXT/Iliski.txt");
        if (!dosya.exists()) dosya = new File("Iliski.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir; br.readLine(); // Başlık
            int row = 0;
            while ((satir = br.readLine()) != null && row < N) {
                if (satir.trim().isEmpty()) continue;
                String[] p = satir.trim().split("\\s+");
                int col = 0;
                for (int i = 1; i < p.length; i++) {
                    if (col >= N) break;
                    if (!p[i].equals("-")) iliskiMatrisi[row][col] = Integer.parseInt(p[i]);
                    col++;
                }
                row++;
            }
            log(">> İlişki Grafı (Matris) oluşturuldu.");
        } catch (Exception e) { log("HATA: Iliski.txt okunamadı."); }
    }

    private void dosyaOkuBegeni() {
        begeniListesi.clear();
        File dosya = new File("TXT/Begeni.txt");
        if (!dosya.exists()) dosya = new File("Begeni.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String s; br.readLine();
            while ((s = br.readLine()) != null) {
                String[] p = s.trim().split("[,\\s]+");
                if (p.length >= 3) begeniListesi.add(new Begeni(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2])));
            }
        } catch (Exception e) {}
    }

    private void dosyaOkuYorum() {
        yorumListesi.clear();
        File dosya = new File("TXT/Yorum.txt");
        if (!dosya.exists()) dosya = new File("Yorum.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String s; br.readLine();
            while ((s = br.readLine()) != null) {
                String[] p = s.trim().split("[,\\s]+");
                if (p.length >= 3) yorumListesi.add(new Yorum(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2])));
            }
        } catch (Exception e) {}
    }

    // =============================================================
    // 2. ARAMA ALGORİTMASI (BINARY SEARCH) - HOCANIN İSTEDİĞİ
    // =============================================================
    public int binarySearch(int id) {
        if (kisilerDizisi == null) return -1;
        int sol = 0, sag = kisilerDizisi.length - 1;

        while (sol <= sag) {
            int orta = sol + (sag - sol) / 2;
            int ortaID = kisilerDizisi[orta].getId();

            if (ortaID == id) return orta; // Bulundu
            if (ortaID < id) sol = orta + 1;
            else sag = orta - 1;
        }
        return -1; // Bulunamadı
    }

    // =============================================================
    // 3. GRAF ve İLİŞKİ SORGULAMA İŞLEMLERİ
    // =============================================================
    public void arkadaslikDurumuSorgula(int kID, int hID) {
        int k = binarySearch(kID);
        int h = binarySearch(hID);

        if (k == -1 || h == -1) {
            log("HATA: Girilen ID'ye sahip kişi bulunamadı.");
            return;
        }

        // Graf Matrisinden Oku
        int durum = iliskiMatrisi[k][h];
        String txt = (durum == 0) ? "Arkadaş Değil" : (durum == 1 ? "Arkadaş" : "Yakın Arkadaş");

        log("SONUÇ: " + kisilerDizisi[k].getAd() + " ile " + kisilerDizisi[h].getAd() + " -> " + txt);
    }

    // İLİŞKİ PUANI HESAPLAMA FORMÜLÜ
    public double iliskiPuaniHesapla(int kaynakID, int hedefID) {
        double puan = 0;
        int kIdx = binarySearch(kaynakID);
        int hIdx = binarySearch(hedefID);

        if (kIdx != -1 && hIdx != -1) {
            int durum = iliskiMatrisi[kIdx][hIdx];
            if (durum == 1) puan += 15;
            else if (durum == 2) puan += 30;
        }

        for (Begeni b : begeniListesi) {
            String sahipStr = b.paylasimID.split("-")[0];
            try {
                if (Integer.parseInt(sahipStr) == kaynakID && b.begenenID == hedefID) {
                    puan += (b.tur == 1 ? 5 : -5);
                }
            } catch (Exception e) {}
        }

        for (Yorum y : yorumListesi) {
            String sahipStr = y.paylasimID.split("-")[0];
            try {
                if (Integer.parseInt(sahipStr) == kaynakID && y.yorumYapanID == hedefID) {
                    if (y.tur == 0) puan += 5;
                    else if (y.tur == 1) puan += 10;
                    else puan -= 5;
                }
            } catch (Exception e) {}
        }
        return puan;
    }

    // =============================================================
    // 4. SIRALAMA ALGORİTMALARI VE ÖNERİLER
    // =============================================================

    // Yardımcı sınıf (Sıralama için)
    class SkorItem implements Comparable<SkorItem> {
        String ad; int id; double puan;
        // String ad2; (Genel sıralama için gerekirse eklenebilir)
        String aciklama;

        public SkorItem(String ad, int id, double puan) { this.ad = ad; this.id = id; this.puan = puan; }
        public SkorItem(String txt, double puan) { this.aciklama = txt; this.puan = puan; } // Genel kullanım

        @Override
        public int compareTo(SkorItem o) {
            // Büyükten küçüğe sıralama mantığı
            return Double.compare(o.puan, this.puan);
        }
    }

    public void arkadasOner(int userID) {
        if (kisilerDizisi == null) return;
        int uIdx = binarySearch(userID);
        if (uIdx == -1) { log("Kişi bulunamadı."); return; }

        log("\n--- " + kisilerDizisi[uIdx].getAd() + " İÇİN ÖNERİLER ---");

        ArrayList<Integer> arkadaslar = new ArrayList<>();
        // Graf üzerinde gezinerek arkadaşları buluyoruz
        for (int i = 0; i < kisilerDizisi.length; i++) {
            if (iliskiMatrisi[uIdx][i] != 0) arkadaslar.add(kisilerDizisi[i].getId());
        }

        ArrayList<SkorItem> adaylar = new ArrayList<>();
        for (Kisi aday : kisilerDizisi) {
            if (aday.getId() == userID) continue;
            int aIdx = binarySearch(aday.getId());
            if (iliskiMatrisi[uIdx][aIdx] != 0) continue; // Zaten arkadaş

            // Formül Uygulama
            double toplamPuan = iliskiPuaniHesapla(aday.getId(), userID);
            for (int arkID : arkadaslar) {
                toplamPuan += iliskiPuaniHesapla(aday.getId(), arkID);
            }
            adaylar.add(new SkorItem(aday.getAd(), aday.getId(), toplamPuan));
        }

        // SIRALAMA ALGORİTMASI KULLANIMI (Collections.sort -> Timsort/MergeSort)
        Collections.sort(adaylar);

        int limit = Math.min(3, adaylar.size());
        for (int i = 0; i < limit; i++) {
            log((i+1) + ". Öneri: " + adaylar.get(i).ad + " (Puan: " + adaylar.get(i).puan + ")");
        }
    }

    public void arkadasCikarmaOner(int userID) {
        if (kisilerDizisi == null) return;
        int uIdx = binarySearch(userID);
        if (uIdx == -1) { log("Kişi bulunamadı."); return; }

        log("\n--- " + kisilerDizisi[uIdx].getAd() + " İÇİN SİLİNECEKLER ---");
        ArrayList<SkorItem> list = new ArrayList<>();

        for (int i = 0; i < kisilerDizisi.length; i++) {
            if (iliskiMatrisi[uIdx][i] != 0) { // Arkadaş ise
                int arkID = kisilerDizisi[i].getId();
                double p = iliskiPuaniHesapla(userID, arkID);
                list.add(new SkorItem(kisilerDizisi[i].getAd(), arkID, p));
            }
        }

        // Küçükten büyüğe sıralamak için ters çevirebiliriz veya compareTo'yu ters kullanırız
        list.sort((o1, o2) -> Double.compare(o1.puan, o2.puan)); // Küçükten büyüğe

        int limit = Math.min(3, list.size());
        for (int i = 0; i < limit; i++) {
            log((i+1) + ". Sil: " + list.get(i).ad + " (Puan: " + list.get(i).puan + ")");
        }
    }

    public void genelSiralama() {
        if (kisilerDizisi == null) return;
        log("\n--- GENEL İLİŞKİ SIRALAMASI ---");
        ArrayList<SkorItem> tumu = new ArrayList<>();

        for(int i=0; i<kisilerDizisi.length; i++) {
            for(int j=0; j<kisilerDizisi.length; j++) {
                if(i==j) continue;
                double p = iliskiPuaniHesapla(kisilerDizisi[i].getId(), kisilerDizisi[j].getId());
                String aciklama = kisilerDizisi[i].getAd() + " -> " + kisilerDizisi[j].getAd();
                tumu.add(new SkorItem(aciklama, p));
            }
        }
        Collections.sort(tumu); // Sıralama Algoritması

        int limit = Math.min(20, tumu.size());
        for(int i=0; i<limit; i++) {
            log((i+1) + ". " + tumu.get(i).aciklama + " : " + tumu.get(i).puan);
        }
    }

    public void enCokEtkilesimAlanlar() {
        log("\n--- EN POPÜLER KİŞİLER ---");
        // Basit frekans sayımı
        Map<Integer, Integer> likes = new HashMap<>();
        for(Begeni b : begeniListesi) {
            if(b.tur==1) {
                int id = Integer.parseInt(b.paylasimID.split("-")[0]);
                likes.put(id, likes.getOrDefault(id, 0)+1);
            }
        }

        ArrayList<SkorItem> likeList = new ArrayList<>();
        for(Integer id : likes.keySet()) {
            int idx = binarySearch(id);
            if(idx!=-1) likeList.add(new SkorItem(kisilerDizisi[idx].getAd(), id, likes.get(id)));
        }
        Collections.sort(likeList);

        log("[En Çok Like Alanlar]");
        for(int i=0; i<Math.min(3, likeList.size()); i++)
            log((i+1) + ". " + likeList.get(i).ad + " (" + (int)likeList.get(i).puan + ")");
    }
}