package com.rohanth.diary.service;

import org.springframework.stereotype.Service;

@Service
public class SentimentService {
    public String analyzeSentiment(String text) {
        // Simple sentiment analysis logic
        text = text.toLowerCase();
        
        // Count positive and negative words
        int positiveCount = countOccurrences(text, new String[]{"happy", "good", "great", "excellent", "wonderful", "joy"});
        int negativeCount = countOccurrences(text, new String[]{"sad", "bad", "terrible", "awful", "unhappy", "miserable"});
        
        // Determine sentiment
        if (positiveCount > negativeCount) {
            return "good";
        } else if (negativeCount > positiveCount) {
            return "sad";
        } else {
            return "neutral";
        }
    }
    
    private int countOccurrences(String text, String[] words) {
        int count = 0;
        for (String word : words) {
            int index = 0;
            while ((index = text.indexOf(word, index)) != -1) {
                count++;
                index += word.length();
            }
        }
        return count;
    }
} 