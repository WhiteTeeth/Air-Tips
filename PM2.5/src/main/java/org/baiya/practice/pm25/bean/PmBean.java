package org.baiya.practice.pm25.bean;

import java.io.Serializable;

public class PmBean implements Serializable {

    public static final String TAG = PmBean.class.getSimpleName();

    public Style style = Style.UNKNOWN;
    public Display display = Display.UNKNOWN;
    public Type type = Type.UNKNOWN;
    public City city;
    public String pm25;
    public String updateTime;
    public int widgetId;

    @Override
    public String toString() {
        return "PmBean{" +
                "style=" + style +
                ", display=" + display +
                ", type=" + type +
                ", city=" + city +
                ", pm25='" + pm25 + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", widgetId=" + widgetId +
                '}';
    }

    public static enum Style {
        WHITE, BLACK, UNKNOWN
    }

    public static enum Display {
        GRAPH, VALUE, UNKNOWN
    }

    public static enum Type {
        BIG, SMALL, UNKNOWN
    }

}
