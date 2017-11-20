package com.example.aadityasuri.awaam;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aadityasuri on 22/10/17.
 */

public class Election {
    String mName;
    String mHeading;
    String mPara;
    String mStart_date;
    String mEnd_date;
    int mElectionId;
    String mCategory;
    int mInFavour;
    int mAgainst;
    int mTotalVotes;
    int mStatus;
    String mUserFavour;
    String mUserAgainst;
    String mTotalUser;


    public Election(String name, String heading, String para, String start_date, String end_date, int electionId, String category, int inFavour, int against, int totalVotes, int status, String userFavour, String userAgainst, String totalUser) {
        mName = name;
        mHeading = heading;
        mPara = para;
        mStart_date = start_date;
        mEnd_date = end_date;
        mElectionId = electionId;
        mCategory = category;
        mInFavour = inFavour;
        mAgainst = against;
        mTotalVotes = totalVotes;
        mStatus = status;
        mUserFavour = userFavour;
        mUserAgainst = userAgainst;
        mTotalUser = totalUser;

    }

    public int getmAgainst() {
        return mAgainst;
    }

    public String getmName() {
        return mName;
    }

    public int getmElectionId() {
        return mElectionId;
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

    public String getmCategory() {
        return mCategory;
    }

    public String getmEnd_date() {
        return mEnd_date;
    }

    public String getmHeading() {
        return mHeading;
    }

    public String getmPara() {
        return mPara;
    }

    public String getmStart_date() {
        return mStart_date;
    }

    public String getmTotalUser() {
        return mTotalUser;
    }

    public String getmUserAgainst() {
        return mUserAgainst;
    }

    public String getmUserFavour() {
        return mUserFavour;
    }

    public String getTimeRemaining() throws ParseException {
        String timeLeft = "Few Hours to end";
        DateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss z");
        Date date = new Date();
        String now = dt.format(date);
        String end = mEnd_date+ " IST";

        try {
            Date nowdate = dt.parse(now);
            Date endDate = dt.parse(end);
            long unixTimeEnd = (long) endDate.getTime();
            Log.e("Time End take",unixTimeEnd+"");
            long unixTimeNow = (long) nowdate.getTime();
            Log.e("Time Now take",unixTimeNow+"");
            long difference = unixTimeEnd - unixTimeNow;
            Log.e("Time diff take",difference+"");
            long diff2=difference/1000;
            Log.e("Time diff2 take",diff2+"");
            long hours = diff2 / 3600;
            long minutes = (diff2 % 3600) / 60;
            if (difference <= 0) {
                timeLeft = "Voting period is over";
            } else {
                timeLeft = hours+" hours and " +minutes+" minutes"+" to go";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return timeLeft;
    }

}
