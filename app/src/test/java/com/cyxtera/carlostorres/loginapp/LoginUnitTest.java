package com.cyxtera.carlostorres.loginapp;

import com.cyxtera.carlostorres.loginapp.model.pojo.InfoLocation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LoginUnitTest {
    @Test
    public void characterListTest() throws Exception {
        List<InfoLocation> infoLocations = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            infoLocations.add(new InfoLocation());
        }
        assertEquals("output should give back 3 results", 3, infoLocations.size());
    }

    @Test
    public void singleCharacterTest() throws Exception {
        InfoLocation infoLocation = new InfoLocation();
        infoLocation.setTime("2019-10-10");
        assertEquals("output should give back infoLocation date", "2019-10-10", infoLocation.getTime());
    }
}