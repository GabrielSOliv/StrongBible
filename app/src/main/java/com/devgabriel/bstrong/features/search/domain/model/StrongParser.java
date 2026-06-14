package com.devgabriel.bstrong;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.devgabriel.bstrong.features.verses.domain.model.WordToken;

public class StrongParser {

    public static List<WordToken> parseVerseText(String rawText) {
        List<WordToken> tokens = new ArrayList<>();
        if (rawText == null || rawText.trim().isEmpty()) {
            return tokens;
        }

        Pattern mainPattern = Pattern.compile("([^<]+)((?:<[HG]\\d+\\s*)+)?");
        Matcher matcher = mainPattern.matcher(rawText);

        while (matcher.find()) {
            String word = matcher.group(1);
            if (word != null) {
                word = word.trim();
            }
            if (word == null || word.isEmpty()) {
                continue;
            }

            String markersRaw = matcher.group(2);
            List<String> strongCodes = new ArrayList<>();

            if (markersRaw != null && !markersRaw.isEmpty()) {
                Pattern codePattern = Pattern.compile("<([HG]\\d+)");
                Matcher codeMatcher = codePattern.matcher(markersRaw);
                while (codeMatcher.find()) {
                    strongCodes.add(codeMatcher.group(1));
                }
            }
            tokens.add(new WordToken(word, strongCodes));
        }
        return tokens;
    }
}
