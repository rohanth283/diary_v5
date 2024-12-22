package com.rohanth.diary.model;

import java.time.LocalDateTime;

public class DiaryEntry {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private boolean pinned;

    // Constructors, getters, and setters
    public DiaryEntry(Long id, Long userId, String title, String content, LocalDateTime createdAt) {
        this(id, userId, title, content, createdAt, false);
    }

    public DiaryEntry(Long id, Long userId, String title, String content, LocalDateTime createdAt, boolean pinned) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.pinned = pinned;
    }

    // Getters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isPinned() { return pinned; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setPinned(boolean pinned) { this.pinned = pinned; }
}