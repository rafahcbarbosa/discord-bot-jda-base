package com.base.Casamento;

import java.util.ArrayList;

import com.base.CRUD_Meeting.Meeting;
public class BM {

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

        // Build bad character table
        int[] badChar = buildBadCharTable(pattern);

        // Check each meeting description
        for(Meeting meeting : arrayListMeeting){

            String description = meeting.getDescription();

            if(description == null){
                continue;
            }

            if(bmSearch(description, pattern, badChar)){
                arrayListMeetingPattern.add(meeting);
            }
        }

        return arrayListMeetingPattern;
    }


    // Boyer-Moore Search using Bad Character Rule
    private static boolean bmSearch(String text, String pattern, int[] badChar){

        int textLength = text.length();
        int patternLength = pattern.length();

        int shift = 0;

        while(shift <= textLength - patternLength){

            int j = patternLength - 1;

            // Compare characters from end to beginning
            while(j >= 0 && pattern.charAt(j) == text.charAt(shift + j)){
                j--;
            }

            // Pattern found
            if(j < 0){
                return true;
            }

            // Bad character mismatch -> use badChar table to decide shift
            int charIndex = text.charAt(shift + j);
            int badIndex = badChar[charIndex];

            int newShift = j - badIndex;

            if(newShift < 1){
                newShift = 1;
            }

            shift += newShift;
        }

        return false;
    }


    // Build Bad Character Table for pattern
    private static int[] buildBadCharTable(String pattern){

        int[] badChar = new int[256];

        int length = pattern.length();

        int i = 0;
        while(i < 256){
            badChar[i] = -1;
            i++;
        }

        i = 0;
        while(i < length){
            int charIndex = pattern.charAt(i);
            badChar[charIndex] = i;
            i++;
        }

        return badChar;
    }

}
