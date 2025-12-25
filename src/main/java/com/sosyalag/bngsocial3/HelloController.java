package com.sosyalag.bngsocial3; // Paket ismin bu olmalı

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class HelloController {

    // Arka plandaki mantık sınıfımız
    private SosyalAgIslemleri islemYonetici = new SosyalAgIslemleri();

    @FXML private TextField txtKaynakID;
    @FXML private TextField txtHedefID;
    @FXML private TextArea logEkrani;

    // --- BUTON AKSİYONLARI ---
    // FXML dosyasındaki kırmızı hatalar bu fonksiyonlar eksik olduğu için çıkıyor

    @FXML
    protected void onVeriYukle() {
        if (logEkrani != null) logEkrani.clear();
        yaz(">> Veritabanına bağlanılıyor...", true);
        islemYonetici.tumDosyalariYukle();
        yaz(islemYonetici.getSonMesaj(), false);
    }

    @FXML
    protected void onArkadasMi() {
        try {
            int k = getInt(txtKaynakID);
            int h = getInt(txtHedefID);
            islemYonetici.arkadaslikDurumuSorgula(k, h);
            yaz(islemYonetici.getSonMesaj(), true);
        } catch (Exception e) { hataGoster(); }
    }

    @FXML
    protected void onPuanHesapla() {
        try {
            int k = getInt(txtKaynakID);
            int h = getInt(txtHedefID);
            double puan = islemYonetici.iliskiPuaniHesapla(k, h);
            yaz(">> İlişki Puanı Hesaplanıyor...", true);
            yaz(k + " ve " + h + " arasındaki puan: " + puan, false);
        } catch (Exception e) { hataGoster(); }
    }

    @FXML
    protected void onOneriYap() {
        try {
            int k = getInt(txtKaynakID);
            yaz(">> Akıllı Öneri Algoritması Çalışıyor...", true);
            islemYonetici.arkadasOner(k);
            yaz(islemYonetici.getSonMesaj(), false);
        } catch (Exception e) { hataGoster(); }
    }

    @FXML
    protected void onSilmeOnerisi() {
        try {
            int k = getInt(txtKaynakID);
            yaz(">> Negatif İlişki Analizi Yapılıyor...", true);
            islemYonetici.arkadasCikarmaOner(k);
            yaz(islemYonetici.getSonMesaj(), false);
        } catch (Exception e) { hataGoster(); }
    }

    @FXML
    protected void onGenelSiralama() {
        yaz(">> Tüm Ağ Analiz Ediliyor...", true);
        islemYonetici.genelSiralama();
        yaz(islemYonetici.getSonMesaj(), false);
    }

    @FXML
    protected void onPopuler() {
        yaz(">> Trendler Analiz Ediliyor...", true);
        islemYonetici.enCokEtkilesimAlanlar();
        yaz(islemYonetici.getSonMesaj(), false);
    }

    // --- YARDIMCI METOTLAR ---

    private int getInt(TextField field) {
        if (field.getText().isEmpty()) return 0;
        return Integer.parseInt(field.getText().trim());
    }

    private void yaz(String mesaj, boolean baslik) {
        if (logEkrani == null) return;
        if (baslik) logEkrani.appendText("\n━━━ " + mesaj + " ━━━\n");
        else logEkrani.appendText(mesaj + "\n");
    }

    private void hataGoster() {
        if (logEkrani != null) logEkrani.appendText("\n[!] Lütfen ID alanlarına geçerli sayı giriniz.\n");
    }
}