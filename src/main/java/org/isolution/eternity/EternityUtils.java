package org.isolution.eternity;

import java.util.List;
import java.util.ArrayList;

/**
 * User: alex
 * Date: 24/11/2007
 * Time: 00:20:54
 */
public class EternityUtils {
    /**
     * @param strings array of lines to be parsed
     * @return an array of {@link Piece} as a result of parsing the string passed in as parameter
     * @see Piece#fromString(String) 
     */
    public static Piece[] fromString(String[] strings) {
        List<Piece> pieces = new ArrayList<Piece>();
        for (String string : strings) {
            pieces.add(Piece.fromString(string));
        }
        return pieces.toArray(new Piece[pieces.size()]);
    }

    public static int hash(byte... side1) {
        byte total=0;
        for (byte b : side1) {
            total >>= 3;
            total += b;
        }
        return total;
    }
}
