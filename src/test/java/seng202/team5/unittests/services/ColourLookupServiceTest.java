package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import seng202.team5.services.ColourLookupService;

/**
 * Simple test to ensure that the colour service is returning
 * the expected colour string values.
 */
public class ColourLookupServiceTest {

    /**
     * Test that the expected colour is returned with a passed in index.
     * This tests all (currently) accepted indices that can be used -
     * An empty string is automatically returned if the index isn't found.
     */
    @Test
    public void correctColourTest() {
        assertEquals("purple",  ColourLookupService.getTagLabelColour(0));
        assertEquals("green",   ColourLookupService.getTagLabelColour(1));
        assertEquals("blue",    ColourLookupService.getTagLabelColour(2));
        assertEquals("red",     ColourLookupService.getTagLabelColour(3));
        assertEquals("yellow",  ColourLookupService.getTagLabelColour(4));
        assertEquals("",        ColourLookupService.getTagLabelColour(-1));
    }
}
