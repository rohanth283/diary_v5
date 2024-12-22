package com.rohanth.diary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.rohanth.diary.model.DiaryEntry;

@Service
public class DiaryService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    private void init() {
        try {
            jdbcTemplate.execute("ALTER TABLE diary_entries ADD COLUMN IF NOT EXISTS pinned BOOLEAN DEFAULT FALSE");
        } catch (Exception e) {
            // Column might already exist, that's okay
        }
    }

    public List<DiaryEntry> getEntriesForUser(Long userId) {
        String sql = "SELECT * FROM diary_entries WHERE user_id = ? ORDER BY pinned DESC, created_at DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new DiaryEntry(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getBoolean("pinned")
                ), userId);
    }

    public void saveEntry(DiaryEntry entry) {
        if (entry.getId() == null) {
            String sql = "INSERT INTO diary_entries (user_id, title, content, created_at) VALUES (?, ?, ?, NOW())";
            jdbcTemplate.update(sql, entry.getUserId(), entry.getTitle(), entry.getContent());
        } else {
            String sql = "UPDATE diary_entries SET title = ?, content = ? WHERE id = ? AND user_id = ?";
            jdbcTemplate.update(sql, entry.getTitle(), entry.getContent(), entry.getId(), entry.getUserId());
        }
    }

    public void deleteEntry(Long entryId, Long userId) {
        String sql = "DELETE FROM diary_entries WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, entryId, userId);
    }

    public void togglePin(Long entryId, Long userId) {
        String sql = "UPDATE diary_entries SET pinned = NOT pinned WHERE id = ? AND user_id = ?";
        jdbcTemplate.update(sql, entryId, userId);
    }
}
