/*
 * About.java
 *
 * Created on 2006. február 3., 14:59
 */

package sudoku;

import javax.microedition.lcdui.*;

/**
 * Typical about box.
 */
public class About {
    
    private static StringBuffer props = new StringBuffer();
    
    /**
     * Suppresses default constructor, ensuring non-instantiability. 
     */
    private About() {}
    
    /**
     * Writes a property.
     */
    private static void writeProp(String prop) {
        String value = System.getProperty(prop);
        props.append(prop);
        props.append(" = ");
        if (value == null) {
            props.append("<undefined>");
        } else {
            props.append("\"");
            props.append(value);
            props.append("\"");
        }
        props.append("\n");
    }
    
    /**
     * Put up the About box and when the user click ok return
     * to the previous screen.
     *
     * @param display The <code>Display</code> to return to when the
     *                 about screen is dismissed.
     */
    public static void showAbout(Display display) {
        props.delete( 0,  props.length() );
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        
        Alert alert = new Alert("About:");
        alert.setTimeout(Alert.FOREVER);
        
        props.append("See http://leriaat.web.elte.hu for more Java games " +
                "& source codes!\n\n");
        
        props.append("General:\n");
        props.append("Programmer: Legendi Richard Oliver\n");
        props.append("Game Version: " + Sudoku.VERSION + "\n");
        props.append("Default Timeout: " + Sudoku.DefaultTimeout + "\n");
        props.append("Debugging mode: " + Sudoku.DEBUG + "\n\n");
        
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        
        props.append("Memory report:\n");
        props.append("Free Memory  = "  + free + "\n");
        props.append("Total Memory = " + total + "\n\n");
        
        props.append("System report:\n");
        writeProp("microedition.configuration");
        writeProp("microedition.profiles");
        
        writeProp("microedition.platform");
        writeProp("microedition.locale");
        writeProp("microedition.encoding");
        
        alert.setString( props.toString() );
        display.setCurrent(alert);
    }
    
}// class.About
