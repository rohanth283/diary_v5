package com.rohanth.diary.gui;

import java.awt.Component;
import java.awt.Font;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import com.rohanth.diary.model.DiaryEntry;

public class EntryListCellRenderer extends DefaultListCellRenderer {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof DiaryEntry entry) {
            String textColor = ThemeManager.isDarkMode() ? "#CCCCCC" : "#666666";
            String pinIcon = entry.isPinned() ? "📌 " : "";
            setText(String.format("<html>%s<b>%s</b><br><font color='%s'>%s</font></html>",
                    pinIcon,
                    entry.getTitle(),
                    textColor,
                    entry.getCreatedAt().format(formatter)));
        }

        setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        
        if (isSelected) {
            setBackground(ThemeManager.getSelectionBackground());
            setForeground(ThemeManager.getForeground());
        } else {
            setBackground(ThemeManager.getPanelBackground());
            setForeground(ThemeManager.getForeground());
        }

        return this;
    }
}
