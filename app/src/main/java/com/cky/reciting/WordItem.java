package com.cky.reciting;

import java.io.Serializable;

public class WordItem implements Serializable {
    public String word;
    public String pronun;
    public String meaning;
    public WordItem(String word, String pronun, String translation) {
        this.word = word;
        this.pronun = pronun;
        this.meaning = translation;
    }
}
