/*
 * RiddleFactory.java
 *
 * Created on 2006. február 7., 18:43
 */

package sudoku;

import java.util.Random;

/**
 * Notes:
 *
 * 1., You could say that I should have made a Riddle class, but it would be
 * useless due the many native array-handling...
 *
 * 2., I could also use some permutation matrixes to handle the transformations,
 * but I like the algorithmical way of counting (using index methods) :-)
 *
 * @author Richard O. Legendi
 */
public class RiddleFactory {
    
    public static final int TRAFOS = 20;
    private static Random random = new Random();
    private static final int table[][] = {
        {1,2,3,4,5,6,7,8,9},
        {4,5,6,7,8,9,1,2,3},
        {7,8,9,1,2,3,4,5,6},
        {2,3,4,5,6,7,8,9,1},
        {5,6,7,8,9,1,2,3,4},
        {8,9,1,2,3,4,5,6,7},
        {3,4,5,6,7,8,9,1,2},
        {6,7,8,9,1,2,3,4,5},
        {9,1,2,3,4,5,6,7,8}
    };
    
    private static int rnd(int max) {
        return ( random.nextInt(max) + 1);
    }
    
    /**
     * The clone() on an array of array won't work, due the references points to
     * the REAL arrays, so any changes made will effect the real numbers!
     */
    private static void cloneTable(int[][] dest) {
        for (int j=0; j<9; ++j) {
            for (int k=0; k<9; ++k) {
                dest[j][k] = table[j][k];
            }
        }
    }
    
    /**
     * I use only 6 of the possible 17 subtype of Sudoku (yet it seems there're
     * 17 of them ...). For more info check "Minimum sudoku" on the web (use
     * Google).
     */
    private static void countRiddle(int[][] a, int m) {
        for (int i=0; i<9; ++i) {
            for (int j=0; j<9; ++j) {
                a[i][j] = ( a[i][j] * m ) % 9 + 1;
            }
        }
    }
    
    private static void vflip(int[][] a) {
        for (int i=0; i<4; ++i) {
            for (int j=0; j<9; ++j) {
                int tmp = a[i][j];
                a[i][j] = a[8-i][j];
                a[8-i][j] = tmp;
            }
        }
    }
    
    private static void hflip(int[][] a) {
        for (int i=0; i<9; ++i) {
            for (int j=0; j<4; ++j) {
                int tmp = a[i][j];
                a[i][j] = a[i][8-j];
                a[i][8-j] = tmp;
            }
        }
    }
    
    /**
     * Diagonal reflection.
     */
    private static void flip(int[][] a) {
        for (int i=0; i<9; ++i) {
            for(int j=i+1; j<9; ++j) {
                int tmp = a[i][j];
                a[i][j] = a[j][i];
                a[j][i] = tmp;
            }
        }
    }
    
    /**
     * Bidiagonal reflection.
     */
    private static void deflip(int[][] a) {
        for (int i=0; i<9; ++i) {
            for (int j=0; j<8-i; ++j) {
                int tmp = a[j][i];
                a[j][i] = a[8-i][8-j];
                a[8-i][8-j] = tmp;
            }
        }
    }
    
    /**
     * @param type should be 1, 2, 3 to determine the range of the possible
     *      column change.
     */
    private static void chg_row(int[][] a, final int type) {
        int r1=-1, r2=-1;
        switch (type) {
            case 1:
                r1 = random.nextInt(3);
                do { r2=random.nextInt(3); } while (r1 == r2);
                break;
            case 2:
                r1 = random.nextInt(3) + 3;
                do { r2=random.nextInt(3)+3; } while (r1 == r2);
                break;
            case 3:
                r1 = random.nextInt(3)+6;
                do { r2=random.nextInt(3)+6; } while (r1 == r2);
                break;
        }
        
        for (int j=0; j<9; ++j) {
            int tmp = a[r1][j];
            a[r1][j] = a[r2][j];
            a[r2][j] = tmp;
        }
    }
    
    private static void chg_row(int[][] a) {
        chg_row( a, rnd(3) );
    }
    
    /**
     * @param type should be 1, 2, 3 to determine the range of the possible
     *      column change.
     */
    private static void chg_col(int[][] a, final int type) {
        int c1=-1, c2=-1;
        switch (type) {
            case 1:
                c1 = random.nextInt(3);
                do { c2=random.nextInt(3); } while (c1 == c2);
                break;
            case 2:
                c1 = random.nextInt(3) + 3;
                do { c2=random.nextInt(3)+3; } while (c1 == c2);
                break;
            case 3:
                c1 = random.nextInt(3)+6;
                do { c2=random.nextInt(3)+6; } while (c1 == c2);
                break;
        }
        
        for (int j=0; j<9; ++j) {
            int tmp = a[c1][j];
            a[c1][j] = a[c2][j];
            a[c2][j] = tmp;
        }
    }
    
    /**
     * Changes random cols.
     */
    private static void chg_col(int[][] a) {
        chg_col( a, rnd(3) );
    }
    
    private static void transform(int[][] a) {
        for (int i=0; i<9; ++i) {
            chg_col(a);
            chg_row(a);
        }

        for (int i=1; i<=3; ++i) { // make sure there'll be a shuffleing 
                                  // in each row & col
            chg_col(a, i);
            chg_row(a, i);
        }
        
        for (int i=0; i<TRAFOS; ++i) {
            int type = rnd(8);
            
            switch (type) {
                case 1: vflip(a);  break;
                case 2: hflip(a);  break;
                case 3: flip(a);   break;
                case 4: deflip(a); break;
                case 5: case 6: chg_row(a); break;
                case 7: case 8: chg_col(a); break;
            }
        }
    }
    
    /**
     * Just for Debugging.
     */
    static void isCorrect(int[][] a) {
        boolean corr = true;
        
        // checking the lines & rows for unique instances
        outer1: for (int i=0; i<9; ++i) {
            for (int j=0; j<9; ++j) {
                for (int k=j+1; k<9; ++k) {
                    if (a[i][j] == a[i][k] ||
                            a[j][i] == a[k][i]) {
                        corr = false;
                        break outer1;
                    }
                }
            }
        }
        
        // if they were good we check the 3x3 sized subtables
        if ( corr ) {
            int[] tmp = new int[9];
            for (int i=0; i<9; i+=3) {
                for (int j=0; j<9; j+=3) {
                    int pos=0;
                    for (int k=0; k<3; ++k) {
                        for (int l=0; l<3; ++l) {
                            tmp[pos++] = a[i+k][j+l];
                        }
                    }
                }
            }
            
            outer2: for (int i=0; i<9; ++i) {
                for (int j=i+1; j<9; ++j) {
                    if (tmp[i] == tmp[j]) {
                        corr = false;
                        break outer2;
                    }
                }
            }
        }
        
        System.out.println( "Correct? " + corr );
    }// isCorrect
    
    /**
     * Just for Debugging.
     */
    private static void writeRiddle(int[][] a) {
        for (int i=0; i<9; ++i) {
            for (int j=0; j<9; ++j) {
                System.out.print(a[i][j]+ " ");
                if ((j+1)%3==0) System.out.print(" | ");
            }
            
            if ( (i+1)%3==0 && i!=8 ) {
                System.out.print("\n--------------------------");
            }
            
            System.out.print("\n");
        }
        
        System.out.println("\n");
    }
    
    public static int[][] createRiddle() {
        int type = -1;
        do {
            type = rnd(9);
        } while ( 0 == (type % 3) ); // 3, 6 are useless
        
        int[][] back = new int[9][9]; // step 1: creating a new table
        cloneTable(back);
        
        countRiddle(back, type); // step 2: determine the subtype
        
        transform(back); // step 3: transforming the matrix
        
        if (Sudoku.DEBUG) {
            writeRiddle(back);
            isCorrect(back);
        }
        
        return back;
    }
    
    private RiddleFactory() {}
    
}// class.RiddleFactory
