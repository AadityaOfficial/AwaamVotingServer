package com.example.aadityasuri.awaam;

/**
 * Created by aadityasuri on 23/03/18.
 */

public class UserClass {
    String mName;
    String mOrganisation;
    String mGender;
    String mRole;
    int mUserId;
    int mInFavour;
    int mAgainst;
    int mTotalVotes;
    int mStatus;


    public UserClass(String name, int userId, int inFavour, int against, int totalVotes, int status, String role, String gender, String organisation) {
        mName = name;
        mUserId = userId;
        mInFavour = inFavour;
        mAgainst = against;
        mTotalVotes = totalVotes;
        mStatus = status;
        mRole = role;
        mGender = gender;
        mOrganisation=organisation;
    }

    public int getmAgainst() {
        return mAgainst;
    }

    public String getmName() {
        return mName;
    }

    public int getmUserId() {
        return mUserId;
    }

    public int getmInFavour() {
        return mInFavour;
    }

    public int getmStatus() {
        return mStatus;
    }

    public int getmTotalVotes() {
        return mTotalVotes;
    }

    public String getmRole() {
        return mRole;
    }

    public String getmOrganisation() {
        return mOrganisation;
    }

}
