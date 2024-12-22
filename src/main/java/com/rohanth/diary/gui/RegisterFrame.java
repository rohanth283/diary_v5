package com.rohanth.diary.gui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
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
import javax.swing.border.EmptyBorder;

import com.rohanth.diary.service.UserService;

public class RegisterFrame extends JFrame {
    private final UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private final LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.userService = loginFrame.getUserService();
        setupUI();
        setupIcon();
    }

    private void setupUI() {
        setTitle("Register New User");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 247));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        addLabel("Username:", mainPanel, gbc, 0);
        usernameField = addTextField(mainPanel, gbc, 1);

        // Password
        addLabel("Password:", mainPanel, gbc, 2);
        passwordField = addPasswordField(mainPanel, gbc, 3);

        // Confirm Password
        addLabel("Confirm Password:", mainPanel, gbc, 4);
        confirmPasswordField = addPasswordField(mainPanel, gbc, 5);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(245, 245, 247));

        JButton registerButton = new JButton("Register");
        styleButton(registerButton);
        registerButton.addActionListener(e -> register());

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton);
        cancelButton.addActionListener(e -> {
            dispose();
            loginFrame.setVisible(true);
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void addLabel(String text, JPanel panel, GridBagConstraints gbc, int gridy) {
        gbc.gridx = 0;
        gbc.gridy = gridy;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(text);
        label.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        panel.add(label, gbc);
    }

    private JTextField addTextField(JPanel panel, GridBagConstraints gbc, int gridy) {
        gbc.gridx = 1;
        gbc.gridy = gridy;
        JTextField field = new JTextField(20);
        field.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        panel.add(field, gbc);
        return field;
    }

    private JPasswordField addPasswordField(JPanel panel, GridBagConstraints gbc, int gridy) {
        gbc.gridx = 1;
        gbc.gridy = gridy;
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        panel.add(field, gbc);
        return field;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        button.setBackground(new Color(0, 122, 255));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 111, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 122, 255));
            }
        });
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

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters long");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        // Try to create user
        if (userService.createUser(username, password)) {
            JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            loginFrame.setVisible(true);
        } else {
            showError("Username already exists");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
} 