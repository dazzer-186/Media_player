package com.noname.Mediaplayer;

public class Settings {
    public static boolean string_checker(String arg1, String arg2){
        boolean answer=false;
        if (arg1.length() == arg2.length()){
            for (int i=0;i<arg1.length();i++){
                if (arg1.charAt(i) == arg2.charAt(i)) answer = true;
                else {
                    answer = false;
                    break;
                }
            }
            if (arg1.length() == 0) answer = true;
        }

        return answer;
    };
    public static int find_element(String[] array, String element){
        int index  = -1;

        for (int i=0;i<array.length;i++){
            if (string_checker(array[i], element)) {index = i;break;}
        }

        return index;
    }
}
