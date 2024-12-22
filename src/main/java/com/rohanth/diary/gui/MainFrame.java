package com.rohanth.diary.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rohanth.diary.model.DiaryEntry;
import com.rohanth.diary.model.User;
import com.rohanth.diary.service.DiaryService;
import com.rohanth.diary.service.SentimentService;

import jakarta.annotation.PostConstruct;

@Component
public class MainFrame extends JFrame {
    @Autowired
    private DiaryService diaryService;
    @Autowired
    private SentimentService sentimentService;

    private User currentUser;
    private DefaultListModel<DiaryEntry> entriesListModel;
    private JList<DiaryEntry> entriesList;
    private JTextField titleField;
    private JTextArea contentArea;
    private DiaryEntry currentEntry;
    private List<DiaryEntry> allEntries = new ArrayList<>();
    private JTextField searchField;
    private boolean isInitialized = false;
    private JSplitPane splitPane;
    private JToggleButton themeToggle;

    public MainFrame() {
        setTitle("Personal Diary");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 400));
    }

    @PostConstruct
    private void init() {
        setupUI();
        isInitialized = true;
    }

    public void setUser(User user) {
        this.currentUser = user;
        refreshEntries();
    }

    private void setupUI() {
        setTitle("My Diary");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        getContentPane().setBackground(new Color(245, 245, 247));

        splitPane = new JSplitPane();
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);

        // Add theme toggle to the top right
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(ThemeManager.getBackground());
        
        themeToggle = new JToggleButton("🌙");  // Moon emoji for dark mode
        themeToggle.setFont(new Font("Dialog", Font.PLAIN, 16));
        themeToggle.setFocusPainted(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setContentAreaFilled(false);
        themeToggle.addActionListener(e -> toggleTheme());
        topPanel.add(themeToggle);
        
        add(topPanel, BorderLayout.NORTH);

        // Left panel
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(245, 245, 247));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(new Color(245, 245, 247));
        searchField = new JTextField();
        searchField.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 218, 218)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add placeholder text
        searchField.setForeground(Color.GRAY);
        searchField.setText("Search");
        searchField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Search")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search");
                }
            }
        });
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { 
                if (!searchField.getText().equals("Search")) {
                    filterEntries(); 
                }
            }
            @Override
            public void removeUpdate(DocumentEvent e) { 
                if (!searchField.getText().equals("Search")) {
                    filterEntries(); 
                }
            }
            @Override
            public void changedUpdate(DocumentEvent e) { 
                if (!searchField.getText().equals("Search")) {
                    filterEntries(); 
                }
            }
        });
        
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Entries list
        entriesListModel = new DefaultListModel<>();
        entriesList = new JList<>(entriesListModel);
        entriesList.setBackground(Color.WHITE);
        entriesList.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        entriesList.setCellRenderer(new EntryListCellRenderer());
        entriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entriesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedEntry();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(entriesList);
        listScrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 218, 218)));

        // Button panel with FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton newButton = new JButton("New Entry");
        styleButton(newButton);
        newButton.addActionListener(e -> newEntry());
        buttonPanel.add(newButton);

        // Add delete button
        JButton deleteButton = new JButton("Delete");
        styleDeleteButton(deleteButton);
        deleteButton.addActionListener(e -> deleteEntry());
        buttonPanel.add(deleteButton);

        // Add pin button
        JButton pinButton = new JButton("📌");  // Pin emoji
        styleButton(pinButton);
        pinButton.setToolTipText("Pin/Unpin Entry");
        pinButton.addActionListener(e -> togglePin());
        buttonPanel.add(pinButton);

        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Right panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(new Color(245, 245, 247));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        titleField = new JTextField();
        titleField.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 218, 218)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        contentArea = new JTextArea();
        contentArea.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createLineBorder(new Color(218, 218, 218)));

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        bottomPanel.setBackground(Color.WHITE);
        
        // Add sentiment button and label
        JButton analyzeButton = new JButton("Analyze Mood");
        styleButton(analyzeButton);
        JLabel sentimentLabel = new JLabel("Mood: NEUTRAL");
        analyzeButton.addActionListener(e -> updateSentiment(sentimentLabel));
        bottomPanel.add(analyzeButton);
        bottomPanel.add(sentimentLabel);
        
        JButton saveButton = new JButton("Save");
        styleButton(saveButton);
        saveButton.addActionListener(e -> saveEntry());
        bottomPanel.add(saveButton);

        rightPanel.add(titleField, BorderLayout.NORTH);
        rightPanel.add(contentScroll, BorderLayout.CENTER);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane);
    }

    private void setupIcon() {
        try {
            // Load the icon from resources
            java.awt.Image icon = ImageIO.read(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/diary-icon.png")
            ));
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
    }

    private void refreshEntries() {
        if (!isInitialized || currentUser == null) return;
        
        // Store current search text and selected entry
        String searchText = searchField.getText();
        DiaryEntry selectedEntry = entriesList.getSelectedValue();
        
        // Refresh entries from database
        allEntries = diaryService.getEntriesForUser(currentUser.getId());
        
        // Restore search and selection
        searchField.setText(searchText);
        filterEntries();
        
        // Only clear editor fields if no entries and no current entry
        if (allEntries.isEmpty() && currentEntry == null) {
            titleField.setText("");
            contentArea.setText("");
        }
        
        // If we had a selected entry, try to reselect it or select the first entry
        if (selectedEntry != null) {
            int index = -1;
            for (int i = 0; i < entriesListModel.size(); i++) {
                if (entriesListModel.get(i).getId().equals(selectedEntry.getId())) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                entriesList.setSelectedIndex(index);
            } else if (entriesListModel.size() > 0) {
                entriesList.setSelectedIndex(0);
            }
        }
    }

    private void filterEntries() {
        if (searchField.getText().equals("Search")) {
            // Show all entries if search field has placeholder text
            entriesListModel.clear();
            for (DiaryEntry entry : allEntries) {
                entriesListModel.addElement(entry);
            }
            return;
        }
        
        String searchText = searchField.getText().toLowerCase().trim();
        entriesListModel.clear();
        
        for (DiaryEntry entry : allEntries) {
            if (searchText.isEmpty() || 
                entry.getTitle().toLowerCase().contains(searchText) ||
                entry.getContent().toLowerCase().contains(searchText)) {
                entriesListModel.addElement(entry);
            }
        }
        
        // Ensure the list is visible and has proper size
        entriesList.revalidate();
        entriesList.repaint();
    }

    private void loadSelectedEntry() {
        var selected = entriesList.getSelectedValue();
        if (selected != null) {
            currentEntry = selected;
            titleField.setText(selected.getTitle());
            contentArea.setText(selected.getContent());
        }
    }

    private void newEntry() {
        entriesList.clearSelection();
        currentEntry = null;
        titleField.setText("");
        contentArea.setText("");
        titleField.requestFocus();
    }

    private void saveEntry() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a title",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate titles, excluding the current entry being edited
        boolean isDuplicate = allEntries.stream()
                .anyMatch(entry -> 
                    entry.getTitle().equalsIgnoreCase(title) && 
                    (currentEntry == null || !entry.getId().equals(currentEntry.getId()))
                );

        if (isDuplicate) {
            JOptionPane.showMessageDialog(this,
                    "An entry with this title already exists. Please choose a different title.",
                    "Duplicate Title",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DiaryEntry entry = new DiaryEntry(
                currentEntry != null ? currentEntry.getId() : null,
                currentUser.getId(),
                title,
                content,
                currentEntry != null ? currentEntry.getCreatedAt() : null
        );

        // Store current search text
        String searchText = searchField.getText();
        
        diaryService.saveEntry(entry);
        
        // Refresh entries while maintaining search
        refreshEntries();
        searchField.setText(searchText);
        filterEntries();
        
        // Only clear if it's a new entry
        if (currentEntry == null) {
            newEntry();
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        button.setBackground(new Color(0, 122, 255));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 111, 230));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 122, 255));
            }
        });
    }

    private void styleDeleteButton(JButton button) {
        button.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        button.setBackground(new Color(220, 53, 69));  // Red color for delete
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(200, 35, 51));  // Darker red on hover
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(220, 53, 69));
            }
        });
    }

    private void updateSentiment(JLabel sentimentLabel) {
        String content = contentArea.getText();
        String sentiment = sentimentService.analyzeSentiment(content);
        
        sentimentLabel.setText("Mood: " + sentiment.toUpperCase());
        switch (sentiment.toLowerCase()) {
            case "good" -> {
                sentimentLabel.setForeground(new Color(40, 167, 69));  // Green
                sentimentLabel.setFont(sentimentLabel.getFont().deriveFont(Font.BOLD, 14));
            }
            case "sad" -> {
                sentimentLabel.setForeground(new Color(220, 53, 69));  // Red
                sentimentLabel.setFont(sentimentLabel.getFont().deriveFont(Font.BOLD, 14));
            }
            default -> {
                sentimentLabel.setForeground(new Color(108, 117, 125));  // Grey
                sentimentLabel.setFont(sentimentLabel.getFont().deriveFont(Font.PLAIN, 14));
            }
        }
    }

    private void toggleTheme() {
        ThemeManager.toggleTheme();
        themeToggle.setText(ThemeManager.isDarkMode() ? "☀️" : "🌙");
        applyTheme();
    }

    private void applyTheme() {
        // Update main components
        getContentPane().setBackground(ThemeManager.getBackground());
        
        // Update split pane
        splitPane.setBackground(ThemeManager.getBackground());
        splitPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));

        // Update search field
        searchField.setBackground(ThemeManager.getPanelBackground());
        searchField.setForeground(ThemeManager.getForeground());
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.setCaretColor(ThemeManager.getForeground());

        // Update title field
        titleField.setBackground(ThemeManager.getPanelBackground());
        titleField.setForeground(ThemeManager.getForeground());
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        titleField.setCaretColor(ThemeManager.getForeground());

        // Update content area
        contentArea.setBackground(ThemeManager.getPanelBackground());
        contentArea.setForeground(ThemeManager.getForeground());
        contentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        contentArea.setCaretColor(ThemeManager.getForeground());

        // Update entries list
        entriesList.setBackground(ThemeManager.getPanelBackground());
        entriesList.setForeground(ThemeManager.getForeground());
        
        // Update all components recursively
        updateComponentsRecursively(getContentPane());

        // Refresh UI
        SwingUtilities.updateComponentTreeUI(this);
        revalidate();
        repaint();
    }

    private void updateComponentsRecursively(java.awt.Container container) {
        for (java.awt.Component comp : container.getComponents()) {
            // Only set background for Swing components
            if (comp instanceof javax.swing.JComponent) {
                ((javax.swing.JComponent) comp).setBackground(ThemeManager.getBackground());
            }
            
            // Specific component handling
            if (comp instanceof javax.swing.JScrollPane) {
                javax.swing.JScrollPane scrollPane = (javax.swing.JScrollPane) comp;
                scrollPane.setBackground(ThemeManager.getPanelBackground());
                scrollPane.getViewport().setBackground(ThemeManager.getPanelBackground());
                scrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
            }
            if (comp instanceof javax.swing.JLabel) {
                ((javax.swing.JLabel) comp).setForeground(ThemeManager.getForeground());
            }
            if (comp instanceof javax.swing.JButton && !(comp instanceof javax.swing.JToggleButton)) {
                javax.swing.JButton button = (javax.swing.JButton) comp;
                if (!button.equals(themeToggle)) {
                    button.setBackground(new java.awt.Color(0, 122, 255));
                    button.setForeground(java.awt.Color.WHITE);
                }
            }

            // Recursively update child components
            if (comp instanceof java.awt.Container) {
                updateComponentsRecursively((java.awt.Container) comp);
            }
        }
    }

    private void deleteEntry() {
        DiaryEntry selectedEntry = entriesList.getSelectedValue();
        if (selectedEntry == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an entry to delete",
                    "No Entry Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete \"" + selectedEntry.getTitle() + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            diaryService.deleteEntry(selectedEntry.getId(), currentUser.getId());
            
            // Clear the editor if we're deleting the current entry
            if (currentEntry != null && currentEntry.getId().equals(selectedEntry.getId())) {
                currentEntry = null;
                titleField.setText("");
                contentArea.setText("");
            }
            
            // Refresh the entries list
            refreshEntries();
        }
    }

    private void togglePin() {
        DiaryEntry selectedEntry = entriesList.getSelectedValue();
        if (selectedEntry == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an entry to pin/unpin",
                    "No Entry Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        diaryService.togglePin(selectedEntry.getId(), currentUser.getId());
        refreshEntries();
    }
}
