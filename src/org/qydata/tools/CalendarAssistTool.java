package org.qydata.tools;

import java.util.Calendar;

/**
 * Created by jonhn on 2017/3/8.
 */
public class CalendarAssistTool {

    public static void main(String[] args) {
        System.out.println(getCurrentYear());
        System.out.println(getCurrentMonth());
        System.out.println( getCurrentDay());
    }

    /**
     * 取得当前时间的年份
     * @return
     */
    public static int getCurrentYear(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        return year;
    }

    /**
     * 取得当前时间的月份
     * @return
     */
    public static int getCurrentMonth(){
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH)+1;
        return month;
    }

    /**
     * 取得当前时间的前一天
     * @return
     */
    public static int getCurrentDay(){
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE)-1;
        return date;
    }


}
