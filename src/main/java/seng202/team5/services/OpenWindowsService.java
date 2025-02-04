package seng202.team5.services;


import java.util.ArrayList;
import java.util.List;
import seng202.team5.gui.ClosableWindow;

/**
 * Service class to keeps track of open pop-up windows across the application.
 *
 * @author Caitlin Tam
 */
public class OpenWindowsService {
    private static OpenWindowsService instance;
    private final List<ClosableWindow> openWindows;

    /**
     * Constructor to initialise list of open windows.
     */
    private OpenWindowsService() {
        this.openWindows = new ArrayList<>();
    }

    /**
     * Initialise OpenWindowsService.
     *
     * @return instance of OpenWindowsService
     */
    public static OpenWindowsService getInstance() {
        if (instance == null) {
            instance = new OpenWindowsService();
        }
        return instance;
    }

    /**
     * Add single window to open windows list.
     *
     * @param thisWindow newly opened window
     */
    public void addWindow(ClosableWindow thisWindow) {
        openWindows.add(thisWindow);
    }

    /**
     * Remove single instance of window from open windows list.
     *
     * @param thisWindow window to close
     */
    public void closeWindow(ClosableWindow thisWindow) {
        openWindows.remove(thisWindow);
    }

    /**
     * Close all open Windows.
     */
    public void closeAllWindows() {
        List<ClosableWindow> windowsCopy = new ArrayList<>(openWindows);
        for (ClosableWindow window : windowsCopy) {
            window.closeWindow();
        }
    }

    /**
     * Retrieve a list of open windows.
     *
     * @return the list of windows that are currently open
     */
    public List<ClosableWindow> getOpenWindows() {
        return openWindows;
    }
}
