/*
 * Sudoku.java
 *
 * Created on 2006. február 3., 13:24
 */

package sudoku;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.*;

/**
 *
 * @author Richard O. Legendi
 */
public class Sudoku extends MIDlet implements CommandListener {
    
    public static final String  VERSION = "0.1";
    public static final boolean DEBUG   = true;
    
    ///////////////////////////////////////////////////////////
    
    private RecordStore store;
    
    // Current difficulty record = {byte LEVEL_TAG; int level}
    private static final int  DIFF_LEN = 9;
    private static final byte DIFF_TAG = 1;
    private int diffId = 0; // The record Id of the difficulty record
    private byte[] diffRec = new byte[DIFF_LEN]; // Byte array holding the level
    {
        diffRec[0] = DIFF_TAG;
        putInt(diffRec, 1, 0);
        putInt(diffRec, 5, 0);
    }
    
    ///////////////////////////////////////////////////////////
    
    public static final int DIFF_EASY = 0;
    public static final int DIFF_NORM = 1;
    public static final int DIFF_HARD = 2;
    private int DIFF = DIFF_NORM;
    
    private boolean firstTime = true;
    static final int DefaultTimeout = 3000;
    
    private static final Command CMD_NEW = new Command("New", Command.SCREEN, 1);
    private static final Command CMD_OPTIONS = new Command("Options", Command.SCREEN, 2);
    private static final Command CMD_HINT    = new Command("Hint", Command.SCREEN, 3);
    private static final Command CMD_SHOW_SOL = new Command("Show solution", Command.SCREEN, 4);
    private static final Command CMD_ABOUT = new Command("About", Command.HELP, 2);
    private static final Command CMD_EXIT = new Command("Exit", Command.STOP, 3);
    
    private Display display;    // The display for this MIDlet
    private Form mainForm = new Form("Sudoku v" + VERSION);
    private Table table;
    private Alert splashScreenAlert;
    
    private static final Image splashScreen = createImage();
    private static Image createImage() {
        Image back = null;

        try {
            back = Image.createImage("/splashScreen.png");
        } catch (java.io.IOException ioe) {
            System.err.println("No splashimage found.");
        }

        return back;
    }
    
    /** Creates a new instance of Sudoku */
    public Sudoku() {
        init();
        display = Display.getDisplay(this);
        String str = (DIFF == DIFF_EASY) ? "Easy" :
            (DIFF == DIFF_NORM) ? "Normal" : "Hard";
        table   = new Table(this, "Difficulty: " + str, display);
        
        splashScreenAlert = new Alert("SuDoKu - mobile edition", "",
                splashScreen, AlertType.INFO);
        splashScreenAlert.setTimeout(DefaultTimeout);
        
        mainForm.append(table);
        mainForm.addCommand(CMD_NEW);
        mainForm.addCommand(CMD_OPTIONS);
        mainForm.addCommand(CMD_HINT);
        mainForm.addCommand(CMD_SHOW_SOL);
        mainForm.addCommand(CMD_ABOUT);
        mainForm.addCommand(CMD_EXIT);
        mainForm.setCommandListener(this);
    }
    
    /**
     * Get an integer from an array.
     */
    private int getInt(byte[] buf, int offset) {
        return  (buf[offset+0] & 0xff) << 24 |
                (buf[offset+1] & 0xff) << 16 |
                (buf[offset+2] & 0xff) <<  8 |
                (buf[offset+3] & 0xff);
    }
    
    /**
     * Put an integer to an array
     */
    private void putInt(byte[] buf, int offset, int value) {
        buf[offset+0] = (byte) ( (value >> 24) & 0xff );
        buf[offset+1] = (byte) ( (value >> 16) & 0xff );
        buf[offset+2] = (byte) ( (value >>  8) & 0xff );
        buf[offset+3] = (byte) ( (value >>  0) & 0xff );
    }
    
    /**
     * Reading saved options.
     */
    private void init() {
        try {
            store = RecordStore.openRecordStore("SudokuDiffStore", true);
        } catch (RecordStoreException rse) {
            System.err.println("First startup, will create record file.");
            DIFF = DIFF_NORM;
        }
        
        if (null == store) {
            return;
        }
        
        try {
            RecordEnumeration enm = store.enumerateRecords(null, null, false);
            while (enm.hasNextElement()) {
                int ndx = enm.nextRecordId();
                if (store.getRecordSize(ndx) == DIFF_LEN) {
                    int l = store.getRecord(ndx, diffRec, 0);
                    if (l == DIFF_LEN && diffRec[0] == DIFF_TAG) {
                        diffId = ndx;
                        DIFF = getInt(diffRec, 1);
                        break;
                    }
                }
            }
        } catch (RecordStoreException rse) {
            rse.printStackTrace();
        }
    }
    
    /**
     * Saving options.
     */
    private void outit() {
        putInt(diffRec, 1, DIFF);
        
        try {
            if (0 == diffId) {
                diffId = store.addRecord(diffRec, 0, diffRec.length);
            } else {
                store.setRecord(diffId, diffRec, 0, diffRec.length);
            }
        } catch (RecordStoreException rse) {
            rse.printStackTrace();
        }
    }
    
    /**
     * Start up the MIDlet.
     */
    public void startApp() {
        if (firstTime) {
            if ( ! DEBUG ) {
                display.setCurrent(splashScreenAlert, mainForm);
            } else {
                display.setCurrent(mainForm);
            }
            
            table.generatePuzzle();
            firstTime = false;
        }
    }
    
    /**
     * Pause is a no-op since there are no background activities or
     * record stores that need to be closed.
     */
    public void pauseApp() {
    }
    
    /**
     * Destroy must cleanup everything not handled by the garbage collector.
     * In this case there is nothing to cleanup.
     */
    public void destroyApp(boolean unconditional) {
        outit();
    }
    
    public int getDiff() {
        return DIFF;
    }
    
    public void setDiff(int val) {
        DIFF = val;
    }
    
    public void generatePuzzle() {
        table.generatePuzzle();
    }
    
    /*
     * Respond to commands, including exit
     * On the exit command, cleanup and notify that the MIDlet has been destroyed.
     */
    public void commandAction(Command c, Displayable s) {
        if (c == CMD_EXIT) {
            destroyApp(false);
            notifyDestroyed();
        } else if (c == CMD_OPTIONS) {
            Options.showOptions(this, display, DIFF);
        } else if (c == CMD_HINT) {
            table.hint();
        } else if (c == CMD_SHOW_SOL) {
            table.showSol();
        } else if (c == CMD_ABOUT) {
            About.showAbout(display);
        } else if (c == CMD_NEW) {
            table.generatePuzzle();
        }
    }
    
}// class.Sudoku
