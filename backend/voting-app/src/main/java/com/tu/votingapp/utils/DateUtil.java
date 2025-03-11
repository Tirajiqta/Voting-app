package com.tu.votingapp.utils;

public class DateUtil {
    public class Formats {
        public static final String YYYYMMDD = "yyyyMMdd";
        public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
        public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
        public static final String YYYY_MM_DD = "yyyy-MM-dd";
        private String format;

        public void setFormat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }
//        TODO: add code to recognise if the given date is one of the default/custom format

    }
}
