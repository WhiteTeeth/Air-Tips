package org.baiya.practice.pm25.bean;

import android.content.Context;

import org.baiya.practice.pm25.R;

import java.io.Serializable;

public class City implements Serializable {
    public String chRCN;
    public String chRTW;
    public String en;

    @Override
    public String toString() {
        return "Location{" +
                "chRCN='" + chRCN + '\'' +
                ", chRTW='" + chRTW + '\'' +
                ", en='" + en + '\'' +
                '}';
    }

    public String getLocaleStr(Context context) {
        switch (context.getResources().getInteger(R.integer.language)) {
            case 0:
                return en;
            case 1:
                return chRCN;
            default:
                return en;
        }
    }

}
