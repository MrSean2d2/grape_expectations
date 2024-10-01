package seng202.team5.services;

import seng202.team5.gui.DetailedViewPageController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Service class to keeps track of open pop-up windows across the application
 */

public class OpenWindowsService {

    private static OpenWindowsService instance;
    private List<Object> openWindows;

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
    public void addWindow(Object thisInstance) {
        System.out.println(openWindows);
        openWindows.add(thisInstance);
    }

    /**
     * Remove single instance of window from open windows list
     * @param thisInstance
     */
    public void closeWindow(Object thisInstance){
        System.out.println(openWindows);
        openWindows.remove(thisInstance);
    }

    /**
     * Close all open Windows
     */
    public void closeAllWindows(){
        for (Object window: openWindows){
            closeWindow(window);
        }
//        Iterator<Object> iter = openWindows.iterator();
//        while (iter.hasNext()){
//            Object window = iter.next();
//            if (window.getClass() == DetailedViewPageController.class) {
//                DetailedViewPageController typedWindow= (DetailedViewPageController) window;
//                typedWindow.close();
//            }
//            System.out.println("here");
//            //TODO: need to be able to store controllers - different types of controllers - use their close method
//        }
    }
}
