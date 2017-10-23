package com.example.mytranslationapp;

/**
 * Created by yvonne40335 on 2017/10/17.
 */

public class Vocabulary {

    public static final int WORD_TYPE=1;
    public static final int ALPHABET_TYPE=2;

    private String word;
    public int type;
    //private Character alphabet;

    public Vocabulary(int type){
        this.type = type;
    }

    public String getWord(){
        return word;
    }

    public void setWord(String word){
        this.word = word;
    }

    //public void setAlphabet(Character alphabet){
        //this.alphabet = alphabet;
    //}

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
