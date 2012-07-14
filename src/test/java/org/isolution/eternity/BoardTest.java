package org.isolution.eternity;

import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

/**
 * User: alex
 * Date: 22/11/2007
 * Time: 22:07:24
 * Copyright: Insight Solution
 */
public class BoardTest extends TestCase {

    public void testInit(){
        List<Piece> pieces = new ArrayList<Piece>();
        Board board = new Board((byte)6, pieces.toArray(new Piece[pieces.size()]));
//        board.init();

        for (int i = 0; i < 6; i++) {
            assertTrue("Top row must be edge pieces",board.getPiece((byte) i, (byte) 0).isEdge());
            assertTrue("Left column must be edge pieces",board.getPiece((byte) 0, (byte) i).isEdge());
            assertTrue("Right column must be edge pieces",board.getPiece((byte) 5, (byte) i).isEdge());
            assertTrue("Bottom row must be edge pieces",board.getPiece((byte) i, (byte) 5).isEdge());
        }

        for (int i = 1; i < 5; i++) {
            assertNull("Inner coordinate must be empty",board.getPiece((byte) i, (byte) i));
        }
    }



}
