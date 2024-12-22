package com.rohanth.diary;
import javax.swing.SwingUtilities;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.rohanth.diary.gui.LoginFrame;

@SpringBootApplication
public class DiaryApplication {
    public static void main(String[] args) {
        var ctx = new SpringApplicationBuilder(DiaryApplication.class)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            var loginFrame = ctx.getBean(LoginFrame.class);
            loginFrame.setVisible(true);
        });
    }
}