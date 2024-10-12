package seng202.team5.unittests.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seng202.team5.gui.*;
import seng202.team5.services.OpenWindowsService;

/**
 * Testing the OpenWindowsService class.
 */
public class OpenWindowsServiceTest {
    private final EditUserPopupController editUserPopupController = new EditUserPopupController();
    private final DetailedViewPageController detailedViewPageController = new DetailedViewPageController();
    private final EditTagPopupController editTagPopupController = new EditTagPopupController();

    /**
     * Testing the open and closing methods of the OpenWindowsService class.
     */
    @Test
    public void testOpenAndCloseWindows(){
        OpenWindowsService openWindowsService = OpenWindowsService.getInstance();
        openWindowsService.addWindow(detailedViewPageController);
        openWindowsService.addWindow(editTagPopupController);
        openWindowsService.addWindow(editUserPopupController);

        Assertions.assertEquals(3, openWindowsService.getOpenWindows().size());

        openWindowsService.closeWindow(detailedViewPageController);

        Assertions.assertEquals(2, openWindowsService.getOpenWindows().size());
        Assertions.assertFalse(openWindowsService.getOpenWindows().contains(detailedViewPageController));

    }

}
