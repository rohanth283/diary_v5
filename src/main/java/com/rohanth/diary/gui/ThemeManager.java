package com.rohanth.diary.gui;

import java.awt.Color;

public class ThemeManager {
    // Light theme colors
    public static final Color LIGHT_BACKGROUND = new Color(245, 245, 247);
    public static final Color LIGHT_FOREGROUND = Color.BLACK;
    public static final Color LIGHT_PANEL = Color.WHITE;
    public static final Color LIGHT_SELECTION = new Color(0, 122, 255, 32);
    public static final Color LIGHT_BORDER = new Color(218, 218, 218);

    // Dark theme colors
    public static final Color DARK_BACKGROUND = new Color(30, 30, 30);
    public static final Color DARK_FOREGROUND = new Color(255, 255, 255);
    public static final Color DARK_PANEL = new Color(45, 45, 45);
    public static final Color DARK_SELECTION = new Color(0, 122, 255, 64);
    public static final Color DARK_BORDER = new Color(100, 100, 100);

    private static boolean isDarkMode = false;

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
    }

    public static Color getBackground() {
        return isDarkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }

    public static Color getForeground() {
        return isDarkMode ? DARK_FOREGROUND : LIGHT_FOREGROUND;
    }

    public static Color getPanelBackground() {
        return isDarkMode ? DARK_PANEL : LIGHT_PANEL;
    }

    public static Color getSelectionBackground() {
        return isDarkMode ? DARK_SELECTION : LIGHT_SELECTION;
    }

    public static Color getBorderColor() {
        return isDarkMode ? DARK_BORDER : LIGHT_BORDER;
    }
} 