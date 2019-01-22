package com.liweile.wigetsdemo;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {
        long stimeStamp = System.currentTimeMillis();
        int  l = (int) ( stimeStamp/ 1000L);
        String time= new SimpleDateFormat("HH:mm:ss").format(new Date(stimeStamp));


        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(stimeStamp);
        int sec = instance.get(Calendar.SECOND);
        System.out.println(time);
        System.out.println(""+sec);
    }
}

