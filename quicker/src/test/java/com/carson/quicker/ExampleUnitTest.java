package com.carson.quicker;

import com.carson.quicker.utils.Base64;
import com.carson.quicker.utils.QDateUtil;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_base64() {
        String originString = "carson2440";
        String resultBase64 = Base64.encode(originString.getBytes());
        System.out.println("Base64: " + originString + " -> " + resultBase64);
        assertEquals("Y2Fyc29uMjQ0MA==", resultBase64);
    }

    @Test
    public void test_date() {
        String date = QDateUtil.getDate();
        String time = QDateUtil.getTime();
        String dateTime = QDateUtil.getDateTime();
        Date date1 = QDateUtil.parseDateTime("2019-10-23 16:45:20");
        System.out.println("date: " + date);
        System.out.println("time: " + time);
        System.out.println("datetime: " + dateTime);
        System.out.println("parse date:" + date1);
    }


}