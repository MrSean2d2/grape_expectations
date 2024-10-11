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
        assertEquals("lavender",  ColourLookupService.getTagLabelColour(0));
        assertEquals("mint",   ColourLookupService.getTagLabelColour(1));
        assertEquals("seafoam",    ColourLookupService.getTagLabelColour(2));
        assertEquals("claret",     ColourLookupService.getTagLabelColour(3));
        assertEquals("gold",  ColourLookupService.getTagLabelColour(4));
        assertEquals("rose",  ColourLookupService.getTagLabelColour(5));
        assertEquals("",        ColourLookupService.getTagLabelColour(-1));
    }
}
