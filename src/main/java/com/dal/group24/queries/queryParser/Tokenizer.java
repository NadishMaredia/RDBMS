package com.dal.group24.queries.queryParser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private String onsSingleQuery;

    public Tokenizer(String onsSingleQuery) {
        this.onsSingleQuery = onsSingleQuery;
    }

    /**
     *
     * @return the list of tokens after tokenizing the SQL query.
     */
    public List<String> tokenizeQuery() {
        /*
        This regex will split the query separated by space, but it will not separate the word
        which is wrapped inside the single or double quotes.
         */
        Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
        Matcher matcher = regex.matcher(onsSingleQuery);
        List<String> tokens = new ArrayList<>();
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        List<String> spaceProcessedTokens = new ArrayList<>();
        for (String s: tokens) {
            if (s.length() == 0 || s.trim().equals("")) {
                continue;
            }
            spaceProcessedTokens.add(s.trim());
        }
        List<String> commaProcessed = new ArrayList<>();
        for (String s : spaceProcessedTokens) {
            if(isWordInsideQuotes(s)) {
                commaProcessed.add(s);
            } else {
                List<String> list1 = tokenizeBasedOnSpecialChar(s, ",");
                commaProcessed.addAll(list1);
            }
        }

        List<String> openingBracketProcessed = new ArrayList<>();
        for (String s : commaProcessed) {
            if(isWordInsideQuotes(s)) {
                openingBracketProcessed.add(s);
            } else {
                List<String> list1 = tokenizeBasedOnSpecialChar(s, "(");
                openingBracketProcessed.addAll(list1);
            }
        }
        List<String> closingBracketProcessed = new ArrayList<>();
        for (String s : openingBracketProcessed) {
            if(isWordInsideQuotes(s)) {
                closingBracketProcessed.add(s);
            } else {
                List<String> list1 = tokenizeBasedOnSpecialChar(s, ")");
                closingBracketProcessed.addAll(list1);
            }
        }

        List<String> equalSignProcessed = new ArrayList<>();
        for (String s : closingBracketProcessed) {
            if(isWordInsideQuotes(s)) {
                equalSignProcessed.add(s);
            } else {
                List<String> list1 = tokenizeBasedOnSpecialChar(s, "=");
                equalSignProcessed.addAll(list1);
            }
        }

        List<String> semiColonProcessed = new ArrayList<>();
        for (String s : equalSignProcessed) {
            if(isWordInsideQuotes(s)) {
                semiColonProcessed.add(s);
            } else {
                List<String> list1 = tokenizeBasedOnSpecialChar(s, ";");
                semiColonProcessed.addAll(list1);
            }
        }

        List<String> finalList = new ArrayList<>();
        for (String s: semiColonProcessed) {
            if (s.length() == 0 || s.trim().equals("")) {
                continue;
            }
            finalList.add(s.trim());
        }

        return finalList;
    }

    private List<String> tokenizeBasedOnSpecialChar(String word, String del) {
        if(!word.contains(del)) {
            return Collections.singletonList(word);
        }
        if (word.equals(del)) {
            return Collections.singletonList(del);
        }
        List<String> tokenList = new ArrayList<>();
        String regex = del;
        if (del.equals("(")) {
            regex = "\\(";
        } else if (del.equals(")")) {
            regex = "\\)";
        }
        String[] tokens = word.split(regex);
        int index = 0;
        tokenList.add(tokens[index]);
        if (tokens.length == 1) {
            tokenList.add(del);
            return tokenList;
        }
        for(index=1; index<tokens.length; index++) {
            tokenList.add(del);
            tokenList.add(tokens[index]);
        }
        if (word.endsWith(del)) {
            tokenList.add(del);
        }
        return tokenList;
    }

    // Return true if the word is wrapped inside.
    private boolean isWordInsideQuotes(String token) {
        if ((token.startsWith("'") && token.endsWith("'")) ||
                (token.startsWith("\"") && token.endsWith("\""))) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        String q = "insert into player values ('diego maradona', 30);";
        List<String> tokens = new Tokenizer(q).tokenizeQuery();
        int i =0;
        for (String s : tokens) {
            System.out.print(s + "    ");
            i++;
        }
    }
}
