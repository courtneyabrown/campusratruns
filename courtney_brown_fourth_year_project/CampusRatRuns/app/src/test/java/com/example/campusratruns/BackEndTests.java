package com.example.campusratruns;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class BackEndTests {

    MapsActivity ma = new MapsActivity();

    Double LTLat = 55.8611;
    Double LTLon = -4.2435;
    Double WCLat = 55.8621;
    Double WCLon = -4.2411;
    Double JWLat = 55.8624;
    Double JWLon = -4.2458;
    Double CULat = 55.8636;
    Double CULon = -4.2411;

    @Test
    public void correctFileReadingTest() throws FileNotFoundException {
        InputStream barony = new FileInputStream("src/test/java/com/example/campusratruns/Barony.txt");
        assertEquals(12, ma.readFile(barony).size());
    }

    @Test
    public void correctCoordinatesTest() {
        assertEquals(LTLat,  ma.getCoords("Livingstone Tower").get(0));
        assertEquals(LTLon,  ma.getCoords("Livingstone Tower").get(1));
        assertEquals(WCLat,  ma.getCoords("Wolfson Centre").get(0));
        assertEquals(WCLon,  ma.getCoords("Wolfson Centre").get(1));

        assertEquals(JWLat,  ma.getCoords("James Weir Building").get(0));
        assertEquals(JWLon,  ma.getCoords("James Weir Building").get(1));
        assertEquals(CULat,  ma.getCoords("Curran Building").get(0));
        assertEquals(CULon,  ma.getCoords("Curran Building").get(1));
    }
}