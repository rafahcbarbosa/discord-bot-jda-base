package com.base.Casamento;

import java.util.ArrayList;

import com.base.CRUD_Meeting.Meeting;

public class KMP {

    // Public method to search pattern in all Meeting descriptions
    public static ArrayList<Meeting> check(ArrayList<Meeting> arrayListMeeting, String pattern) {

        ArrayList<Meeting> arrayListMeetingPattern = new ArrayList<>();

        // Edge cases
        if(arrayListMeeting == null){
            return arrayListMeetingPattern;
        }

        if(pattern == null || pattern.length() == 0){
            return arrayListMeetingPattern;
        }

        // Build LPS array (Longest Prefix Suffix)
        int[] lps = buildLPS(pattern);

        // Check each Meeting description
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


    // KMP Search for a single text
    private static boolean kmpSearch(String text, String pattern, int[] lps){

        int i = 0; // index for text
        int j = 0; // index for pattern

        int textLength = text.length();
        int patternLength = pattern.length();

        while(i < textLength){

            // Characters match
            if(text.charAt(i) == pattern.charAt(j)){
                i++;
                j++;

                // Found the full pattern
                if(j == patternLength){
                    return true;
                }
            }

            // Mismatch
            else{

                if(j != 0){
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        // Pattern not found
        return false;
    }


    // Build the LPS array
    private static int[] buildLPS(String pattern){

        int length = pattern.length();
        int[] lps = new int[length];

        int i = 1;
        int len = 0;

        // lps[0] is always zero
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
