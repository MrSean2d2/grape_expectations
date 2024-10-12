package seng202.team5.unittests.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team5.gui.ClosableWindow;
import seng202.team5.gui.DetailedViewPageController;
import seng202.team5.gui.EditTagPopupController;
import seng202.team5.gui.EditUserPopupController;
import seng202.team5.services.OpenWindowsService;

/**
 * Testing the OpenWindowsService class.
 */
public class OpenWindowsServiceTest {
    private final EditUserPopupController editUserPopupController = new EditUserPopupController();
    private final DetailedViewPageController detailedViewPageController
            = new DetailedViewPageController();
    private final EditTagPopupController editTagPopupController = new EditTagPopupController();
    private static OpenWindowsService openWindowsService;

    @BeforeAll
    public static void init() {
        openWindowsService = OpenWindowsService.getInstance();
    }

    @BeforeEach
    public void reset() {
        openWindowsService.getOpenWindows().clear();
    }

    /**
     * Testing the open and closing methods of the OpenWindowsService class.
     */
    @Test
    public void testOpenAndCloseWindows() {
        openWindowsService.addWindow(detailedViewPageController);
        openWindowsService.addWindow(editTagPopupController);
        openWindowsService.addWindow(editUserPopupController);

        assertEquals(3, openWindowsService.getOpenWindows().size());

        openWindowsService.closeWindow(detailedViewPageController);

        assertEquals(2, openWindowsService.getOpenWindows().size());
        assertFalse(openWindowsService.getOpenWindows().contains(detailedViewPageController));

    }

    /**
     * Test closeAllWindows(). Window objects are mocked to provide a basic
     * closing implementation without any dependency on actual JavaFX stuff.
     */
    @Test
    public void testCloseAllWindows() {
        ClosableWindow window1 = mock(ClosableWindow.class);
        ClosableWindow window2 = mock(ClosableWindow.class);
        doAnswer(invocationOnMock -> {
            openWindowsService.closeWindow(
                    (ClosableWindow) invocationOnMock.getMock());
            return null;
        }).when(window1).closeWindow();
        doAnswer(invocationOnMock -> {
            openWindowsService.closeWindow(
                    (ClosableWindow) invocationOnMock.getMock());
            return null;
        }).when(window2).closeWindow();

        openWindowsService.addWindow(window1);
        openWindowsService.addWindow(window2);
        assertEquals(2, openWindowsService.getOpenWindows().size());
        openWindowsService.closeAllWindows();
        verify(window1).closeWindow();
        verify(window2).closeWindow();
        assertEquals(0, openWindowsService.getOpenWindows().size());
    }

}
