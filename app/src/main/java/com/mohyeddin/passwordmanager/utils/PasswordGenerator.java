package com.mohyeddin.passwordmanager.utils;


import java.util.Arrays;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {
    public static final String ALPHABET="abcdefghijklmnopqrstuvwxyz";
    public static final String NUMBER="0123456789";
    public static final String OTHER_CHARS="!#$%&()*+,-./:;<=>?@[\\]^_{|}~";
    public String[] alphabetLowerCase;
    public String[] alphabetUpperCase;
    public String[] numbers;
    public String[] specialChars;
    public PasswordGenerator(){
        setAlphabetLowerCase();
        setAlphabetUpperCase();
        setNumbers();
        setSpecialChars();
    }
    public void setAlphabetLowerCase(){
        alphabetLowerCase=new String[ALPHABET.length()];
        for (int i=0;i<ALPHABET.length();i++){
            alphabetLowerCase[i]=shuffle(ALPHABET);
        }
    }

    public void setAlphabetUpperCase(){
        alphabetUpperCase=new String[ALPHABET.length()];
        for (int i=0;i<ALPHABET.length();i++){
            alphabetUpperCase[i]=shuffle(ALPHABET.toUpperCase());
        }
    }

    public void setNumbers(){
        numbers=new String[NUMBER.length()];
        for (int i=0;i<NUMBER.length();i++){
            numbers[i]=shuffle(NUMBER);
        }
    }

    public void setSpecialChars(){
        specialChars=new String[OTHER_CHARS.length()];
        for (int i=0;i<OTHER_CHARS.length();i++){
            specialChars[i]=shuffle(OTHER_CHARS);
        }
    }
    public String[] shuffle(String[] array){
        Random random=new Random();
        for (int i=0;i<array.length;i++){
//            int r1=random.nextInt(array.length);
//            int r2=random.nextInt(array.length);
//            if (r1==r2)continue;
//            String temp=shuffle(array[r1]);
//            array[r1]=shuffle(array[r2]);
//            array[r2]=shuffle(temp);
            List<String> list= Arrays.asList(array);
            Collections.shuffle(list,random);
            list.toArray(array);
        }
        return array;
    }
    public String shuffle(String array){
        Random random=new Random();
        for (int i=0;i<array.length();i++){
            int r1=random.nextInt(array.length());
            int r2=random.nextInt(array.length());
            if (r1==r2)continue;
            char temp=array.charAt(r1);
            array=array.replace(array.charAt(r1),array.charAt(r2));
            array=array.replace(array.charAt(r2),temp);
        }
        return array;
    }
    public String generatePassword(boolean lowerAlphabet,boolean upperAlphabet,boolean number,boolean specialType,int length){
        String[] alphabetLowerCase=shuffle(this.alphabetLowerCase);
        String[] alphabetUpperCase=shuffle(this.alphabetUpperCase);
        String[] numbers=shuffle(this.numbers);
        String[] specialChars=shuffle(this.specialChars);
        Random random=new Random();
        StringBuilder sb=new StringBuilder();
        while (sb.length()<length){
            int type=random.nextInt(40);
            if (!lowerAlphabet &&type/10==0)
                continue;
            if (!upperAlphabet&&type/10==1)
                continue;
            if (!number&&type/10==2)
                continue;
            if (!specialType&&type/10==3)
                continue;
            int r1;
            int r2;
            switch (type/10){
                case 1:
                    r1=random.nextInt(alphabetUpperCase.length);
                    r2=random.nextInt(alphabetUpperCase.length);
                    sb.append(alphabetUpperCase[r1].charAt(r2));
                    break;
                case 2:
                    r1=random.nextInt(numbers.length);
                    r2=random.nextInt(numbers.length);
                    sb.append(numbers[r1].charAt(r2));
                    break;
                case 3:
                    r1=random.nextInt(specialChars.length);
                    r2=random.nextInt(specialChars[0].length());
                    sb.append(specialChars[r1].charAt(r2));
                    break;
                case 0:
                    r1=random.nextInt(alphabetLowerCase.length);
                    r2=random.nextInt(alphabetLowerCase.length);
                    sb.append(alphabetLowerCase[r1].charAt(r2));
                    break;
            }
        }
        return sb.toString();
    }
}
