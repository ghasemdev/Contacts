package com.jakode.contacts.utils.date;

import android.annotation.SuppressLint;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

/**
 * <h1>DateTime</h1>
 * <p>The datetime class allows us to take the present date or create our own date.
 * It also allows us to calculate the time between two dates and display it in a specific format.</p>
 *
 * @author ghasem shirdel
 * @version 1.0.0
 * @since 07-17-2020
 */
public class DateTime {
    private int year, month, day;
    private int hour, minute, second;

    /**
     * The constructor takes the present DateTime
     */
    public DateTime() {
        Calendar time = Calendar.getInstance();
        setYear(time.get(Calendar.YEAR));
        setMonth(time.get(Calendar.MONTH) + 1);
        setDay(time.get(Calendar.DAY_OF_MONTH));
        setHour(time.get(Calendar.HOUR));
        setMinute(time.get(Calendar.MINUTE));
        setSecond(time.get(Calendar.SECOND));
    }

    /**
     * The constructor takes the date and creates DateTime object without Time
     *
     * @param year  Integer
     * @param month Integer
     * @param day   Integer
     */
    public DateTime(int year, int month, int day) {
        setYear(year);
        setMonth(month);
        setDay(day);
    }

    /**
     * The constructor takes date, hour and ,minute then creates DateTime object without second
     *
     * @param year   Integer
     * @param month  Integer
     * @param day    Integer
     * @param hour   Integer
     * @param minute Integer
     */
    public DateTime(int year, int month, int day, int hour, int minute) {
        setYear(year);
        setMonth(month);
        setDay(day);
        setHour(hour);
        setMinute(minute);
    }

    /**
     * The constructor takes date and time then creates DateTime object
     *
     * @param year   Integer
     * @param month  Integer
     * @param day    Integer
     * @param hour   Integer
     * @param minute Integer
     * @param second Integer
     */
    public DateTime(int year, int month, int day, int hour, int minute, int second) {
        setYear(year);
        setMonth(month);
        setDay(day);
        setHour(hour);
        setMinute(minute);
        setSecond(second);
    }

    /**
     * @param year Integer
     */
    public void setYear(int year) {
        if (year < 0) {
            throw new IllegalArgumentException("year can't be negative");
        }
        this.year = year;
    }

    /**
     * @param month Integer
     */
    public void setMonth(int month) {
        if (month < 0 || month > 12) {
            throw new IllegalArgumentException("month can't be negative or > 12");
        }
        this.month = month;
    }

    /**
     * @param day Integer
     */
    public void setDay(int day) {
        if (day < 0 || day > 31) {
            throw new IllegalArgumentException("day can't be negative or > 31");
        }
        this.day = day;
    }

    /**
     * @param hour Integer
     */
    public void setHour(int hour) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("hour can't be negative or > 23");
        }
        this.hour = hour;
    }

    /**
     * @param minute Integer
     */
    public void setMinute(int minute) {
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("minute can't be negative or > 59");
        }
        this.minute = minute;
    }

    /**
     * @param second Integer
     */
    public void setSecond(int second) {
        if (second < 0 || second > 59) {
            throw new IllegalArgumentException("second can't be negative or > 59");
        }
        this.second = second;
    }

    /**
     * @return Integer year
     */
    public int getYear() {
        return year;
    }

    /**
     * @return Integer month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @return Integer day
     */
    public int getDay() {
        return day;
    }

    /**
     * @return Integer hour
     */
    public int getHour() {
        return hour;
    }

    /**
     * @return Integer minute
     */
    public int getMinute() {
        return minute;
    }

    /**
     * @return Integer second
     */
    public int getSecond() {
        return second;
    }

    /**
     * calculate time between two DateTime
     *
     * @param from Calendar
     * @param to   Calendar
     * @return DateTime object
     */
    public static DateTime between(Calendar from, Calendar to) {
        // calculate seconds between two time
        long second = Math.abs(from.getTimeInMillis() - to.getTimeInMillis()) / 1000;

        if (second < 0) { // end time < start time
            throw new IllegalArgumentException("The end time cannot be shorter than the start time");
        } else {
            // calculate year between end and start datetime
            int year = (int) (second / 31556952L);
            second = second % 31556952L;
            // calculate month between end and start datetime
            int month = (int) (second / (31556952L / 12));
            second = second % (31556952L / 12);
            // calculate day between end and start datetime
            int day = (int) (second / 86400);
            second = second % 86400;
            // calculate hour between end and start datetime
            int hour = (int) (second / 3600);
            second = second % 3600;
            // calculate minute between end and start datetime
            int minute = (int) (second / 60);
            second = second % 60;
            return new DateTime(year, month, day, hour, minute, (int) second);
        }
    }

    @NotNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%04d %02d %02d %02d %02d %02d",
                getYear(), getMonth(), getDay(),
                getHour(), getMinute(), getSecond()
        );
    }
}