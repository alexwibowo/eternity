package org.isolution.eternity;

import java.util.Collections;
import java.util.logging.Logger;

/**
 * User: alex
 * Date: 22/11/2007
 * Time: 20:13:15
 * Copyright: Insight Solution
 */
public class Piece {
    public static final Logger LOGGER = Logger.getLogger(Piece.class.getName());

    /**
     * To indicate edge, we use arbitrary large number that MUST NOT be used for
     * any existing pattern
     */
    public static final byte EDGE_COLOR = 0;

    /**
     * Representation of the sides
     */
    public byte[] sides;

    /**
     * Has this piece been placed on the board?
     */
    private boolean placed = false;


    /**
     * Create a piece with the given patterns. We use CSS notation here. Top-right-bottom-left
     *
     * @param top    top pattern
     * @param right  right pattern
     * @param bottom bottom pattern
     * @param left   left pattern
     */
    public Piece(byte top, byte right, byte bottom, byte left) {
        sides = new byte[]{top, right, bottom, left};
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    public boolean hasColour(byte colour) {
        return getTop() == colour || getRight() == colour || getBottom() == colour || getLeft() == colour;
    }

    /**
     * Rotate the piece clockwise
     *
     * @param times the number of time this piece is to be rotated (clockwise)
     */
    public void rotate(byte times) {
//        byte[] newside =  null;
        if (times == 1) {
            this.sides = new byte[]{sides[3], sides[0], sides[1], sides[2]};
            return;
        }

        if (times == 2) {
            this.sides = new byte[]{sides[2], sides[3], sides[0], sides[1]};
            return;
        }

        if (times == 3) {
            this.sides = new byte[]{sides[1], sides[2], sides[3], sides[0]};
            return;
        }


        throw new IllegalArgumentException("Rotation is between 1 and 3");
//        this.sides=newside;
    }

    /**
     * @param piece2 the piece to be tested against
     * @return <code>true</code> if piece2 fits at the top of this piece.
     *         i.e. piece2's bottom side has the same pattern as the top side of this piece
     */
    public boolean fitsAtTop(Piece piece2) {
        return piece2.getBottom() == getTop();
//        return (piece2.getBottom() ^ getTop())==0;
    }

    /**
     * @param piece2 the piece to be tested against
     * @return <code>true</code> if piece2 fits at the right of this piece
     *         i.e. piece2's left side has the same pattern as the right side of this piece
     */
    public boolean fitsAtRight(Piece piece2) {
        return piece2.getLeft() == getRight();
//        return (piece2.getLeft() ^ getRight())==0;
    }

    /**
     * @param piece2 the piece to be tested against
     * @return <code>true</code> if piece2 fits at the bottom of this piece
     *         i.e. piece2's top side has the same pattern as the bottom side of this piece
     */
    public boolean fitsAtBottom(Piece piece2) {
        return piece2.getTop() == getBottom();
//        return (piece2.getTop() ^ getBottom())==0;
    }

    /**
     * @param piece2 the piece to be tested against
     * @return <code>true</code> if piece2 fits at the left of this piece
     *         i.e. piece2's right side has the same pattern as the left side of this piece
     */
    public boolean fitsAtLeft(Piece piece2) {
        return piece2.getRight() == getLeft();
//        return (piece2.getRight() ^ getLeft())==0;
    }

    public String toString() {
        return String.format("[ %2d,%2d,%2d,%2d ]", getTop(), getRight(), getBottom(), getLeft());
    }

    /**
     * @return <code>true</code> if this piece is an edge piece. i.e piece with all side =  {@link #EDGE_COLOR}
     */
    public boolean isEdge() {
        return getTop() == EDGE_COLOR
                || getBottom() == EDGE_COLOR
                || getLeft() == EDGE_COLOR
                || getRight() == EDGE_COLOR;
    }

    public boolean isCorner() {
        int numCorner = 0;
        for (byte side : sides) {
            if (side == EDGE_COLOR) {
                numCorner++;
            }
        }
        return numCorner == 2;
    }

    /**
     * @param string the string to be parsed
     * @return Parse a piece from a string. Four numbers, separated by ','. The numbers represent
     *         the top,right,bottom,and left side (in that order) of the piece.
     */
    public static Piece fromString(String string) {
        String[] sides = string.split(",");
//        String[] sides = string.split(" ");

        Piece piece = new Piece(
                Byte.parseByte(sides[0]), Byte.parseByte(sides[1]),
                Byte.parseByte(sides[2]), Byte.parseByte(sides[3])
        );
        return piece;
    }

    /**
     * @param top    the top piece to match against
     * @param right  the right piece to match against
     * @param bottom the bottom piece to match against
     * @param left   the left piece to match against
     * @return <code>true</code> if this piece is compatible with all the four parameter pieces (i.e.
     *         see if this piece can fit in the middle), and also try to rotate this piece so that it matches
     *         the configuration of the parameters
     */
    public boolean isCompatible(final Piece top, final Piece right, final Piece bottom, final Piece left) {
        if (left != null && !this.hasColour(left.getRight())) {
            return false;
        }

        Collections.addAll(null);


        if (right != null && !this.hasColour(right.getLeft())) {
            return false;
        }
        if (top != null && !this.hasColour(top.getBottom())) {
            return false;
        }
        if (bottom != null && !this.hasColour(bottom.getTop())) {
            return false;
        }


        boolean compatible = false;

        for (byte i = 1; i <= 4; i++) {
            // At the start, we have to reset all matching condition

            if (top == null || fitsAtTop(top)) {
            } else if (i < 4) {
                rotate(i);
                continue;
            } else {
                return false;
            }


            if (right == null || fitsAtRight(right)) {
            } else if (i < 4) {
                rotate(i);
                continue;
            } else {
                return false;
            }

            if (bottom == null || fitsAtBottom(bottom)) {
            } else if (i < 4) {
                rotate(i);
                continue;
            } else {
                return false;
            }

            if (left == null || fitsAtLeft(left)) {
            } else if (i < 4) {
                rotate(i);

                continue;
            } else {
                return false;
            }
            return true;
        }

        return compatible;
    }


    public byte getTop() {
        return sides[0];
    }

    public byte getRight() {
        return sides[1];
    }

    public byte getBottom() {
        return sides[2];
    }

    public byte getLeft() {
        return sides[3];
    }
}
