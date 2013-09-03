package org.isolution.eternity;

import junit.framework.TestCase;

/**
 * User: alex
 * Date: 22/11/2007
 * Time: 22:07:17
 * Copyright: Insight Solution
 */
public class PieceTest extends TestCase {

    public void testRotate1(){
        Piece piece = new Piece((byte)1, (byte)2, (byte)3, (byte)4);
        piece.rotate((byte)1);
        assertEquals((byte)4, piece.getTop());
        assertEquals((byte)1, piece.getRight());
        assertEquals((byte)2, piece.getBottom());
        assertEquals((byte)3, piece.getLeft());

    }

    public void testRotate2(){
        Piece piece = new Piece((byte)1, (byte)2, (byte)3, (byte)4);
        piece.rotate((byte)2);
        assertEquals((byte)3, piece.getTop());
        assertEquals((byte)4, piece.getRight());
        assertEquals((byte)1, piece.getBottom());
        assertEquals((byte)2, piece.getLeft());
    }

    public void testRotate3(){
        Piece piece = new Piece((byte)1, (byte)2, (byte)3, (byte)4);
        piece.rotate((byte)3);
        assertEquals((byte)2, piece.getTop());
        assertEquals((byte)3, piece.getRight());
        assertEquals((byte)4, piece.getBottom());
        assertEquals((byte)1, piece.getLeft());
    }

    public void testRotate4(){
        Piece piece = new Piece((byte)1, (byte)2, (byte)3, (byte)4);
        try {
            piece.rotate((byte)4);
            fail("Rotation must only between 1 and 3 inclusive");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }


    public void testFits(){
        Piece piece = new Piece((byte)1, (byte)2, (byte)3, (byte)4);
        assertTrue("Failed to test fit at top - this.top & that.bottom must match",piece.fitsAtTop(new Piece((byte)70,(byte)40,(byte)1,(byte)30)));
        assertTrue("Failed to test fit at right - this.right & that.left must match",piece.fitsAtRight(new Piece((byte)0,(byte)40,(byte)70,(byte)2)));
        assertTrue("Failed to test fit at bottom - this.bottom & that.top must match",piece.fitsAtBottom(new Piece((byte)3,(byte)40,(byte)70,(byte)30)));
        assertTrue("Failed to test fit at left - this.left & that.right must match",piece.fitsAtLeft(new Piece((byte)10,(byte)4,(byte)70,(byte)30)));
    }

    public void testCompatibleNoNull(){
        Piece centerPiece = new Piece((byte)1, (byte)2, (byte)3, (byte)4);


        for (int i = 1; i < 4; i++) {
            Piece topPiece = new Piece((byte) 20, (byte) 30, (byte) 1, (byte) 50);
            Piece rightPiece = new Piece((byte) 20, (byte) 30, (byte) 40, (byte) 2);
            Piece bottomPiece = new Piece((byte) 3, (byte) 30, (byte) 40, (byte) 50);
            Piece leftPiece = new Piece((byte) 20, (byte) 4, (byte) 40, (byte) 50);
            assertTrue("Compatibility test failed on rotation " + i,centerPiece.tryToFitWithNeighbours(topPiece, rightPiece, bottomPiece, leftPiece));
            assertEquals("Piece must fit after compatibility test - top piece does not fit", (byte)1, centerPiece.getTop());
            assertEquals("Piece must fit after compatibility test - right piece does not fit", (byte)2, centerPiece.getRight());
            assertEquals("Piece must fit after compatibility test - bottom piece does not fit", (byte)3, centerPiece.getBottom());
            assertEquals("Piece must fit after compatibility test - left piece does not fit", (byte)4, centerPiece.getLeft());

            // Rotate and test again on the rotation            
            centerPiece.rotate((byte)i);
        }
    }

    public void testCompatibilityWithNull(){
        Piece centerPiece = new Piece((byte)1, (byte)2, (byte)3, (byte)4);


        for (int i = 1; i < 4; i++) {
            Piece topPiece = null;
            Piece rightPiece = null;
            Piece bottomPiece = new Piece((byte) 3, (byte) 30, (byte) 40, (byte) 50);
            Piece leftPiece = new Piece((byte) 20, (byte) 4, (byte) 40, (byte) 50);
            assertTrue("Compatibility test failed on rotation " + i,centerPiece.tryToFitWithNeighbours(topPiece, rightPiece, bottomPiece, leftPiece));
            assertEquals("Piece must fit after compatibility test - top piece does not fit", (byte)1, centerPiece.getTop());
            assertEquals("Piece must fit after compatibility test - right piece does not fit", (byte)2, centerPiece.getRight());
            assertEquals("Piece must fit after compatibility test - bottom piece does not fit", (byte)3, centerPiece.getBottom());
            assertEquals("Piece must fit after compatibility test - left piece does not fit", (byte)4, centerPiece.getLeft());

            // Rotate and test again on the rotation
            centerPiece.rotate((byte)i);
        }
    }

    public void testIncompatible(){
         Piece centerPiece = new Piece((byte)1, (byte)2, (byte)3, (byte)4);


        for (int i = 1; i < 4; i++) {
            Piece topPiece = null;
            Piece rightPiece = new Piece((byte) 20, (byte) 30, (byte) 40, (byte) 20);
            Piece bottomPiece = new Piece((byte) 30, (byte) 30, (byte) 40, (byte) 50);
            Piece leftPiece = new Piece((byte) 20, (byte) 40, (byte) 40, (byte) 50);
            assertFalse("Compatibility test failed on rotation " + i,centerPiece.tryToFitWithNeighbours(topPiece, rightPiece, bottomPiece, leftPiece));

            // Rotate and test again on the rotation
            centerPiece.rotate((byte)i);
        }

    }
}
