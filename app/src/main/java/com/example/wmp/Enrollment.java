package com.example.wmp;

import java.util.ArrayList;
public class Enrollment {
    private String userId;
    private ArrayList<String> enrolledSubjectIds;
    private int totalCredits;

    public Enrollment() {
    }

    public Enrollment(String userId, ArrayList<String> enrolledSubjectIds, int totalCredits) {
        this.userId = userId;
        this.enrolledSubjectIds = enrolledSubjectIds;
        this.totalCredits = totalCredits;
    }

    public String getUserId() {
        return userId;
    }

    public ArrayList<String> getEnrolledSubjectIds() {
        return enrolledSubjectIds;
    }

    public int getTotalCredits() {
        return totalCredits;
    }
}
