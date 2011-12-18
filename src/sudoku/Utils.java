/*
 * Utils.java
 *
 * Created on 2006. február 12., 19:15
 */

package sudoku;

/**
 *
 * @author Richard O. Legendi
 */
public class Utils {
    
    private Utils() {}
    
    public static void dump(int[] arr) {
        System.out.print("[[length:" + arr.length + "]");

        for (int i=0; i<arr.length; ++i) {
            System.out.print(arr[i] + " ");
        }
        
        System.out.println("]\n");
    }
    
    public static void dump(Object[] arr) {
        System.out.print("[[length:" + arr.length + "]");
        
        for (int i=0; i<arr.length; ++i) {
            System.out.print(arr[i].toString() + " ");
        }
        
        System.out.println("]\n");
    }    
    
}// class.Utils
