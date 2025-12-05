package com.base.Casamento;

import java.util.ArrayList;

import com.base.CRUD_Meeting.Meeting;

public class KMP {

    
    public static ArrayList<Meeting> check(ArrayList<Meeting> arrayListMeeting, String pattern) {

        ArrayList<Meeting> arrayListMeetingPattern = new ArrayList<>();

        
        if(arrayListMeeting == null){
            return arrayListMeetingPattern;
        }

        if(pattern == null || pattern.length() == 0){
            return arrayListMeetingPattern;
        }

       
        int[] lps = buildLPS(pattern);

        
        for(Meeting meeting : arrayListMeeting){

            String description = meeting.getDescription();

            if(description == null){
                continue;
            }

            if(kmpSearch(description, pattern, lps)){
                arrayListMeetingPattern.add(meeting);
            }
        }

        return arrayListMeetingPattern;
    }


    
    private static boolean kmpSearch(String text, String pattern, int[] lps){

        int i = 0; 
        int j = 0; 

        int textLength = text.length();
        int patternLength = pattern.length();

        while(i < textLength){

         
            if(text.charAt(i) == pattern.charAt(j)){
                i++;
                j++;

                
                if(j == patternLength){
                    return true;
                }
            }

           
            else{

                if(j != 0){
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        
        return false;
    }



    private static int[] buildLPS(String pattern){

        int length = pattern.length();
        int[] lps = new int[length];

        int i = 1;
        int len = 0;

      
        lps[0] = 0;

        while(i < length){

            if(pattern.charAt(i) == pattern.charAt(len)){
                len++;
                lps[i] = len;
                i++;
            }

            else{

                if(len != 0){
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

}
