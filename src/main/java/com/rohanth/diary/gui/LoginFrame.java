package com.rohanth.diary.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rohanth.diary.service.UserService;

@Component
public class LoginFrame extends JFrame {
    @Autowired
    private UserService userService;
    @Autowired
    private MainFrame mainFrame;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Diary Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setResizable(false);
        
        createComponents();
        setLocationRelativeTo(null);
    }

    public UserService getUserService() {
        return userService;
    }

    private void createComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Username field
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        usernamePanel.setBackground(Color.WHITE);

        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(usernameField);

        // Password field
        usernamePanel.add(new JLabel("Password:"));
        usernamePanel.add(passwordField);

        mainPanel.add(usernamePanel, BorderLayout.CENTER);

        // Login button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.addActionListener(e -> login());

        JButton registerButton = new JButton("Register");
        styleButton(registerButton);
        registerButton.addActionListener(e -> {
            this.setVisible(false);
            var registerFrame = new RegisterFrame(this);
            registerFrame.setVisible(true);
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        setContentPane(mainPanel);
        pack();
    }

    private void setupIcon() {
        try {
            Image icon = ImageIO.read(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/diary-icon.png")
            ));
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        var user = userService.authenticate(username, password);
        if (user != null) {
            mainFrame.setUser(user);
            mainFrame.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add the same styleButton method as in RegisterFrame
    private void styleButton(JButton button) {
        button.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        button.setBackground(new Color(0, 122, 255));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 30));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 111, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 122, 255));
            }
        });
    }
}