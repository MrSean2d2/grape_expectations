package seng202.team5.services;

import seng202.team5.gui.ClosableWindow;
import seng202.team5.gui.DetailedViewPageController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Service class to keeps track of open pop-up windows across the application
 */

public class OpenWindowsService {

    private static OpenWindowsService instance;
    private List<ClosableWindow> openWindows;

    /**
     * Constructor ot initialise list of open windows
     */
    private OpenWindowsService() {
        this.openWindows = new ArrayList<>();
    }

    /**
     * Initialise OpenWindowsService
     * @return instance of OpenWindowsService
     */
    public static OpenWindowsService getInstance() {
        if (instance == null) {
            instance = new OpenWindowsService();
        }
        return instance;
    }

    public static void removeInstance() { instance = null;}

    /**
     * Add single window to open windows list
     * @param thisInstance
     */
    public void addWindow(ClosableWindow thisInstance) {
        System.out.println(openWindows);
        openWindows.add(thisInstance);
    }

    /**
     * Remove single instance of window from open windows list
     * @param thisInstance
     */
    public void closeWindow(ClosableWindow thisInstance){
        System.out.println(openWindows);
        openWindows.remove(thisInstance);
    }

    /**
     * Close all open Windows
     */
    public void closeAllWindows(){
        List<ClosableWindow> windowsCopy = new ArrayList<>(openWindows);
        for (ClosableWindow window: windowsCopy){
            window.closeWindow();
        }

    }
}
