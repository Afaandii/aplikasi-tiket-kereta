package org.example;

import com.formdev.flatlaf.FlatDarkLaf;
import org.example.view.LoadingScreen;
import org.example.view.LoginForm;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Mengatur Tema Modern (Dark) dengan Accent Color
        FlatDarkLaf.setup();

        // Custom Global Styling
        UIManager.put("Button.arc", 15);
        UIManager.put("Component.arc", 15);
        UIManager.put("TextComponent.arc", 15);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("ScrollBar.showButtons", true);
        UIManager.put("ScrollBar.width", 12);

        // Menjalankan Loading Screen Terlebih Dahulu
        SwingUtilities.invokeLater(() -> {
            LoadingScreen loadingScreen = new LoadingScreen();
            loadingScreen.startLoading(() -> {
                // Begitu loading selesai, tampilkan LoginForm
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            });
        });
    }
}