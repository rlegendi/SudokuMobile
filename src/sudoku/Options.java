/*
 * Options.java
 *
 * Created on 2006. február 11., 17:18
 */

package sudoku;

import javax.microedition.lcdui.*;

/**
 *
 * @author Richard O. Legendi
 */
public class Options {

    private static Sudoku sudoku;
    private static Displayable prev;
    private static Display display;
    private final static Command CMD_OK   = new Command("Ok", Command.EXIT, 1);
    private final static Command CMD_CANC = new Command("Cancel", Command.EXIT, 1);
    
    private static Form mainForm;
    private static ChoiceGroup[] groups;
    static {
        mainForm = new Form("Options:");
        mainForm.append("Changing any of the listed values will " +
                "generate a new puzzle!");
        groups = new ChoiceGroup[] {
            new ChoiceGroup("Difficulty", ChoiceGroup.EXCLUSIVE,
                    new String[] {"Easy", "Normal", "Hard"}, null)
        };
        
        for (int iter = 0; iter < groups.length; ++iter) {
            mainForm.append(groups[iter]);
        }
        
        mainForm.addCommand(CMD_OK);
        mainForm.addCommand(CMD_CANC);
        
        mainForm.setCommandListener( new CommandListener() {
            public void commandAction(Command c, Displayable d) {
                if (c == CMD_OK) {
                    sudoku.setDiff( groups[0].getSelectedIndex() );
                    display.setCurrent(prev);
                    sudoku.generatePuzzle();
                } else if (c == CMD_CANC) {
                    display.setCurrent(prev);
                }
            }
        });
    }
    
    /**
     * Suppresses default constructor, ensuring non-instantiability.
     */
    private Options() {}
    
    public static void showOptions(Sudoku s, Display d, int diff) {
        sudoku   = s;
        display  = d;
        prev = d.getCurrent();
        
        groups[0].setSelectedIndex(diff, true);
        
        d.setCurrent(mainForm);
    }
    
}// class.Options
