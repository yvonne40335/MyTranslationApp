package com.example.mytranslationapp;

/**
 * Created by yvonne40335 on 2017/10/17.
 */

public class Vocabulary {
    private String word;

    public Vocabulary(){
    }

    public String getWord(){
        return word;
    }

    public void setWord(String word){
        this.word = word;
    }

    /*public static List<Vocabulary> generateSampleList(){
        List<Vocabulary> list = new ArrayList<>();
        for(int i=0; i<30; i++){
            Vocabulary vocabulary = new Vocabulary();
            vocabulary.setWord("Word - "+i);
            list.add(vocabulary);
        }
        return list;
    }*/

}
