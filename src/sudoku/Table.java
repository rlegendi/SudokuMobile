/*
 * Table.java
 *
 * Created on 2006. február 3., 14:26
 */

package sudoku;

import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.*;

class Coord {
    public int x, y;
    
    public Coord(int x, int y) {
        this.x = x; this.y = y;
    }
    
    public boolean equals(Object o) {
        if ( ! ( o instanceof Coord ) ) {
            return false;
        }
        
        Coord c = (Coord) o;
        return (c.x == x && c.y == y);
    }
    
    public String toString() {
        return "[Coord] [x:" + x + ";y:" + y + "]";
    }
}//class.Coord

/**
 *
 * @author Richard O. Legendi
 */
public class Table extends CustomItem {
    
    private Display display;
    private Sudoku  sudoku;
    
    private final static int UPPER = 0;
    private final static int IN = 1;
    private final static int LOWER = 2;
    private int location = UPPER;
    
    private static final int separatorColor = 0xcc3333;
    private static final int hintColor      = 0xff9966;
    
    private int rows = 9;
    private int cols = 9;
    private int dx = 20;
    private int dy = 20;
    private int currentX = 0;
    private int currentY = 0;
    
    private String[][] data = new String[rows][cols];
    private int[][] riddle  = new int[rows][cols];
    private int remaining;
    private Coord[] defs;
    private Vector hints = new Vector();
    private boolean hintVisible = false;
    
    private Random rnd = new Random();
    
    // Traversal stuff
    // indicating support of horizontal traversal internal to the CustomItem
    boolean horz;
    
    // indicating support for vertical traversal internal to the CustomItem.
    boolean vert;
    
    public Table(Sudoku s, String title, Display d) {
        super(title);
        display = d;
        sudoku  = s;
        
        int interactionMode = getInteractionModes();
        horz = ( (interactionMode & CustomItem.TRAVERSE_HORIZONTAL) != 0 );
        vert = ( (interactionMode & CustomItem.TRAVERSE_VERTICAL)   != 0 );
    }
    
    private void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
    
    public void generatePuzzle() {
        int diff = sudoku.getDiff();
        remaining = (Sudoku.DIFF_HARD == diff) ? 54 : 45;
        currentX=0; currentY=0; location = UPPER;
        riddle = RiddleFactory.createRiddle();
        
        for (int i=0; i<9; ++i) {
            for (int j=0; j<9; ++j) {
                data[i][j] = null;
            }
        }
        
        int[] perm = {0,1,2};
        defs = new Coord[( Sudoku.DIFF_HARD == sudoku.getDiff() ) ? 27 : 36];
        int defsPos = 0;
        for (int i=0; i<9; i+=3) {
            for (int j=0; j<9; j+=3) { // make a number visible in each row
                // Shuffle permutation array
                for (int k=perm.length; k>1; k--) {
                    swap( perm, k-1, rnd.nextInt(k) );
                }
                
                // Determine the found elements
                data[i]  [ j+perm[0] ] = String.valueOf( riddle[i]  [ j+perm[0] ] );
                data[i+1][ j+perm[1] ] = String.valueOf( riddle[i+1][ j+perm[1] ] );
                data[i+2][ j+perm[2] ] = String.valueOf( riddle[i+2][ j+perm[2] ] );
                
                defs[defsPos++] = new Coord(j+perm[0], i);
                defs[defsPos++] = new Coord(j+perm[1], i+1);
                defs[defsPos++] = new Coord(j+perm[2], i+2);
                
                if (Sudoku.DIFF_HARD != diff) { // ... show a 4th one too
                    java.util.Random random = new java.util.Random();
                    int rnd_row = random.nextInt(3);
                    int rnd_col = -1;
                    do {
                        rnd_col = random.nextInt(3);
                    } while ( rnd_col == perm[rnd_row] );
                    
                    data[i+rnd_row][j+rnd_col] = String.valueOf( riddle[i+rnd_row][j+rnd_col] );
                    defs[defsPos++] = new Coord(j+rnd_col, i+rnd_row);
                }
            }
        }
        
        repaint();
    }
    
    protected int getMinContentHeight() {
        return (rows * dy) + 1;
    }
    
    protected int getMinContentWidth() {
        return (cols * dx) + 1;
    }
    
    protected int getPrefContentHeight(int width) {
        return (rows * dy) + 1;
    }
    
    protected int getPrefContentWidth(int height) {
        return (cols * dx) + 1;
    }
    
    protected void paint(Graphics g, int w, int h) {
        for (int i = 0; i <= rows; i++) {
            int original = 0;
            if ( 0 == i%3 && i != rows && i != 0) {
                original = g.getColor();
                g.setColor( separatorColor);
            }
            
            g.drawLine(0, i * dy, cols * dx, i * dy);
            g.drawLine(i * dx, 0, i * dx, rows * dy);
            
            if ( 0 == i%3 && i != rows && i != 0) {
                g.setColor( original );
            }
        }
        
        int oldColor = g.getColor();
        g.setColor(0x00D0D0D0);
        g.fillRect((currentX * dx) + 1, (currentY * dy) + 1, dx - 1, dy - 1);
        g.setColor(oldColor);

        if (hintVisible) {
            int prev = g.getColor();
            g.setColor(hintColor);
            
            for (int i=0; i<hints.size(); ++i) {
                Coord c = (Coord) hints.elementAt(i);
                g.fillRect( (c.x * dx) + 1, (c.y * dy) + 1, dx - 1, dy - 1 );
            }
            
            g.setColor(prev);
        }        
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (data[i][j] != null) {
                    
                    // store clipping properties
                    int oldClipX = g.getClipX();
                    int oldClipY = g.getClipY();
                    int oldClipWidth = g.getClipWidth();
                    int oldClipHeight = g.getClipHeight();
                    
                    g.setClip((j * dx) + 1, i * dy, dx - 1, dy - 1);
                    
                    if ( defsContains(j, i) ) {
                        Font prev = g.getFont();
                        g.setFont( Font.getFont(Font.FONT_STATIC_TEXT, Font.STYLE_BOLD, Font.SIZE_MEDIUM) );
                        g.drawString(data[i][j], (j * dx) + 2, ((i + 1) * dy) - 2,
                                Graphics.BOTTOM | Graphics.LEFT);
                        g.setFont(prev);
                    } else {
                        g.drawString(data[i][j], (j * dx) + 2, ((i + 1) * dy) - 2,
                                Graphics.BOTTOM | Graphics.LEFT);
                    }
                    
                    // restore clipping properties
                    g.setClip(oldClipX, oldClipY, oldClipWidth, oldClipHeight);
                }
            }
        }
    }
    
    protected boolean traverse(int dir, int viewportWidth, int viewportHeight,
            int[] visRect_inout) {
        
        if (horz && vert) {
            
            switch (dir) {
                
                case Canvas.DOWN:
                    
                    if (location == UPPER) {
                        location = IN;
                    } else {
                        
                        if (currentY < (rows - 1)) {
                            currentY++;
                            repaint(currentX * dx, (currentY - 1) * dy, dx, dy);
                            repaint(currentX * dx, currentY * dy, dx, dy);
                        } else {
                            location = LOWER;
                            
                            return false;
                        }
                    }
                    
                    break;
                    
                case Canvas.UP:
                    
                    if (location == LOWER) {
                        location = IN;
                    } else {
                        
                        if (currentY > 0) {
                            currentY--;
                            repaint(currentX * dx, (currentY + 1) * dy, dx, dy);
                            repaint(currentX * dx, currentY * dy, dx, dy);
                        } else {
                            location = UPPER;
                            
                            return false;
                        }
                    }
                    
                    break;
                    
                case Canvas.LEFT:
                    
                    if (currentX > 0) {
                        currentX--;
                        repaint((currentX + 1) * dx, currentY * dy, dx, dy);
                        repaint(currentX * dx, currentY * dy, dx, dy);
                    }
                    
                    break;
                    
                case Canvas.RIGHT:
                    
                    if (currentX < (cols - 1)) {
                        currentX++;
                        repaint((currentX - 1) * dx, currentY * dy, dx, dy);
                        repaint(currentX * dx, currentY * dy, dx, dy);
                    }
            }
        } else if (horz || vert) {
            switch (dir) {
                
                case Canvas.UP:
                case Canvas.LEFT:
                    
                    if (location == LOWER) {
                        location = IN;
                    } else {
                        
                        if (currentX > 0) {
                            currentX--;
                            repaint((currentX + 1) * dx, currentY * dy, dx, dy);
                            repaint(currentX * dx, currentY * dy, dx, dy);
                        } else if (currentY > 0) {
                            currentY--;
                            repaint(currentX * dx, (currentY + 1) * dy, dx, dy);
                            currentX = cols - 1;
                            repaint(currentX * dx, currentY * dy, dx, dy);
                        } else {
                            location = UPPER;
                            return false;
                        }
                    }
                    
                    break;
                    
                case Canvas.DOWN:
                case Canvas.RIGHT:
                    if (location == UPPER) {
                        location = IN;
                    } else {
                        
                        if (currentX < (cols - 1)) {
                            currentX++;
                            repaint((currentX - 1) * dx, currentY * dy, dx, dy);
                            repaint(currentX * dx, currentY * dy, dx, dy);
                        } else if (currentY < (rows - 1)) {
                            currentY++;
                            repaint(currentX * dx, (currentY - 1) * dy, dx, dy);
                            currentX = 0;
                            repaint(currentX * dx, currentY * dy, dx, dy);
                        } else {
                            location = LOWER;
                            return false;
                        }
                    }
                    
            }
        } else {
            // In case of no Traversal at all: (horz|vert) == 0
        }
        
        visRect_inout[0] = currentX;
        visRect_inout[1] = currentY;
        visRect_inout[2] = dx;
        visRect_inout[3] = dy;
        
        return true;
    }// traverse
    
    /**
     * Linear trying - will be optimized soon, but it's enough for now :-))
     */
    private boolean defsContains(Coord c) {
        for (int i=0; i<defs.length; ++i) {
            if ( defs[i].equals(c) ) return true;
        }
        
        return false;
    }
    
    private boolean defsContains(int x, int y) {
        return defsContains( new Coord(x,y) );
    }
    
    private void win() {
        Alert alert = new Alert("Won!", "Congratulations!\n You've " +
                "made the puzzle!", null, AlertType.CONFIRMATION);
        alert.setTimeout(Alert.FOREVER);
        display.setCurrent(alert);
        
        generatePuzzle();
    }
    
    public void keyPressed(int keyCode) {
        if (Canvas.KEY_NUM0 <= keyCode && keyCode <= Canvas.KEY_NUM9) {
            
            if ( defsContains( new Coord(currentX, currentY) ) ||
                    0 == remaining ) {
                return;
            }
            
            String input = String.valueOf( (char) keyCode );
            
            // if trying to fill in the same
            if ( input.equals( String.valueOf( data[currentY][currentX]) ) ) {
                return;
            }
            
            // easy one - vibrate & flash if the answer is wrong
            if ( Sudoku.DIFF_EASY == sudoku.getDiff() ) {
                if ( data[currentY][currentX] != null) return; // already filled
                
                if ( input.equals( String.valueOf( riddle[currentY][currentX]) ) ) {
                    setText("" + input);
                    remaining--;
                } else {
                    display.vibrate(1000);
                    display.flashBacklight(1000);
                }
                
                // if it's norm or hard we allow the user to fill in wrong ones.
            } else {
                
                String prev = data[currentY][currentX];
                setText("" + input);
                if ( input.equals( String.valueOf( riddle[currentY][currentX]) ) ) {
                    remaining--;
                } else {
                    // the prev. was good and we spoiled it
                    if ( String.valueOf( riddle[currentY][currentX]).equals(prev) ) {
                        remaining++;
                    }
                }
            }
            
            if (0 == remaining) {
                win();
            }
            
        } else if (Sudoku.DEBUG && Canvas.KEY_POUND == keyCode) {
            System.out.println("rem:"+remaining);
        }
    }
    
    private void setText(String text) {
        data[currentY][currentX] = text;
        repaint(currentX * dx, currentY * dy, dx, dy);
    }
    
    public void showSol() {
        for (int i=0; i<rows; ++i) {
            for (int j=0; j<cols; ++j) {
                data[i][j] = String.valueOf( riddle[i][j] );                
            }
        }
        
        remaining = 0;
        repaint();
    }
    
    public void hint() {
        for (int i=0; i<rows; ++i) {
            for (int j=0; j<cols; ++j) {
                if ( data[i][j] != null && ! String.valueOf( riddle[i][j] ).equals( data[i][j] ) ) {
                    hints.addElement( new Coord(j, i) );
                }                
            }
        }
        
        System.out.println(hints);
        
        if ( ! hints.isEmpty() ) {
            Thread ticker = new Thread() {
                public void run() {
                    hintVisible = true;
                    repaint();
                    
                    try {
                        sleep(2000);
                    } catch(InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    
                    hintVisible = false;
                    hints.removeAllElements();
                    repaint();
                }
            };
            
            ticker.start();
        }
    }
    
}// class.Table
