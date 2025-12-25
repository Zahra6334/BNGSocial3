package com.sosyalag.bngsocial3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // --- HATA AYIKLAMA BAŞLANGICI ---
        System.out.println(">>> Uygulama Başlatılıyor...");

        // 1. Dosyayı 'resources' kök dizininde ara (Tavsiye edilen)
        URL fxmlLocation = getClass().getResource("social-view.fxml");

        if (fxmlLocation == null) {
            System.err.println("❌ HATA 1: '/social-view.fxml' (Kök dizin) bulunamadı.");

            // 2. Bulamazsa bir de paketin içinde ara (Yedek plan)
            fxmlLocation = getClass().getResource("social-view.fxml");
            if (fxmlLocation == null) {
                System.err.println("❌ HATA 2: 'social-view.fxml' (Paket içi) de bulunamadı.");
                System.err.println("⚠️ ÇÖZÜM: Lütfen 'Build' menüsünden 'Rebuild Project' seçeneğine tıkla!");
                throw new IllegalStateException("FXML dosyası hiçbir yerde bulunamadı! Lütfen projeyi Rebuild yapın.");
            } else {
                System.out.println("✅ BULUNDU: Dosya paket içinde bulundu.");
            }
        } else {
            System.out.println("✅ BULUNDU: Dosya resources kök dizininde bulundu: " + fxmlLocation);
        }
        // --- HATA AYIKLAMA BİTİŞİ ---

        // Dosya bulunduysa yükle
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);

        // CSS Yükleme (Opsiyonel, hata verirse programı durdurmaz)
        URL cssLocation = getClass().getResource("/style.css");
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        } else {
            System.out.println("⚠️ UYARI: style.css bulunamadı, tasarımsız devam ediliyor.");
        }

        stage.setTitle("BingölGram - Sosyal Ağ Analizi");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}