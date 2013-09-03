package org.isolution.eternity;


import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: alex
 * Date: 22/11/2007
 * Time: 20:12:58
 */
public class Board {
    public static final Logger LOGGER = Logger.getLogger(Board.class.getName());

    /** Board size */
    public final byte SIZE;

    private Piece[][] board;

    public final byte MAX_NUM_LAYER;

    /**
     * All available pieces that can be placed on the board (including those that
     * has been placed)
     */
    private Piece[] availablePieces;

    private Piece[] cornerPieces;

    private Piece[] edgePieces;

    private Hashtable<Byte, Piece[]> colourToPieces;

    private Hashtable<Integer, Piece[]> twoColourToPieces;

    private Hashtable<Integer, Piece[]> threeColourToPieces;


    /**
     * Create board with the given size and available pieces
     * @param size board size
     * @param inputPieces all pieces that can be placed on the board
     */
    public Board(byte size, Piece[] inputPieces){
        board = new Piece[size][size];
//        this.inputPieces=inputPieces;
        this.SIZE=size;
        this.MAX_NUM_LAYER=(byte) (size/2);
        colourToPieces = new Hashtable<Byte, Piece[]>();
        twoColourToPieces = new Hashtable<Integer, Piece[]>();
        threeColourToPieces = new Hashtable<Integer, Piece[]>();
        init(inputPieces);
    }

    public byte getSize() {
        return SIZE ;
    }


    public Piece[] getInnerPiecesForColour(byte colour) {
        return colourToPieces.get(colour);        
    }

    public Piece[] getInnerPiecesForColour(byte colour1, byte colour2) {
        int total = EternityUtils.hash(colour1, colour2);
        return twoColourToPieces.get(total);
    }

    public Piece[] getInnerPiecesForColour(byte colour1, byte colour2, byte colour3) {
        int total = EternityUtils.hash(colour1, colour2, colour3);
        return threeColourToPieces.get(total);
    }






    public byte getMaxLayer() {
        return MAX_NUM_LAYER;
    }

    /**
     * Set the piece at the given location
     * @param x horizontal coordinate on the board
     * @param y vertical coordinate on the board                            
     * @param piece the {@link Piece} to be allocated at the given coordinate
     */
    public void setPiece(byte x, byte y, Piece piece){
        board[x][y]=piece;
        piece.setPlaced(true);
    }

    public void setLocationEmpty(byte x, byte y) {
        board[x][y].setPlaced(false);
        board[x][y]=null;
    }

    /**
     * Perform initialization of the board. Fill the edges with {@link Piece#EDGE_COLOR}. Leave
     * the rest location as <code>null</code>
     */
    public void init(Piece[] inputPieces) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (i == 0 || i == SIZE - 1 || j == 0 || j == SIZE - 1) {
                    board[i][j] = new Piece(Piece.EDGE_COLOR, Piece.EDGE_COLOR, Piece.EDGE_COLOR, Piece.EDGE_COLOR);
                }
            }
        }

        List<Piece> edgePieces = new ArrayList<Piece>();
        List<Piece> cornerPieces = new ArrayList<Piece>();
        List<Piece> availablePieces = new ArrayList<Piece>();
        Map<Byte, List<Piece>> mapping = new HashMap<Byte, List<Piece>>();
        Map<Integer, List<Piece>> mapping2 = new HashMap<Integer, List<Piece>>();
        Map<Integer, List<Piece>> mapping3 = new HashMap<Integer, List<Piece>>();

        for (Piece inputPiece : inputPieces) {
            if (inputPiece.isCorner()) {
                cornerPieces.add(inputPiece);
            } else if (inputPiece.isEdge()) {
                edgePieces.add(inputPiece);
            } else {
                List<Piece> list1 = getListForSide(mapping, inputPiece.getTop());
                List<Piece> list2 = getListForSide(mapping, inputPiece.getRight());
                List<Piece> list3 = getListForSide(mapping, inputPiece.getBottom());
                List<Piece> list4 = getListForSide(mapping, inputPiece.getLeft());

                if (!list1.contains(inputPiece))
                    list1.add(inputPiece);
                if (!list2.contains(inputPiece))
                    list2.add(inputPiece);

                if (!list3.contains(inputPiece))
                    list3.add(inputPiece);

                if (!list4.contains(inputPiece))
                    list4.add(inputPiece);

                List<Piece> twoList1 = getListForTwoSides(mapping2, inputPiece.getTop(), inputPiece.getRight());
                List<Piece> twoList2 = getListForTwoSides(mapping2, inputPiece.getRight(), inputPiece.getBottom());
                List<Piece> twoList3 = getListForTwoSides(mapping2, inputPiece.getBottom(), inputPiece.getLeft());
                List<Piece> twoList4 = getListForTwoSides(mapping2, inputPiece.getLeft(), inputPiece.getTop());

                if (!twoList1.contains(inputPiece)) {
                    twoList1.add(inputPiece);
                }
                if (!twoList2.contains(inputPiece)) {
                    twoList2.add(inputPiece);
                }
                if (!twoList3.contains(inputPiece)) {
                    twoList3.add(inputPiece);
                }
                if (!twoList4.contains(inputPiece)) {
                    twoList4.add(inputPiece);
                }

                List<Piece> threeList1 = getListForThreeSides(mapping3, inputPiece.getLeft(), inputPiece.getTop(), inputPiece.getRight());
                List<Piece> threeList2 = getListForThreeSides(mapping3, inputPiece.getTop(), inputPiece.getRight(), inputPiece.getBottom());
                List<Piece> threeList3 = getListForThreeSides(mapping3, inputPiece.getRight(), inputPiece.getBottom(), inputPiece.getLeft());
                List<Piece> threeList4 = getListForThreeSides(mapping3, inputPiece.getBottom(), inputPiece.getLeft(), inputPiece.getTop());

                if (!threeList1.contains(inputPiece)) {
                    threeList1.add(inputPiece);
                }

                if (!threeList2.contains(inputPiece)) {
                    threeList2.add(inputPiece);
                }

                if (!threeList3.contains(inputPiece)) {
                    threeList3.add(inputPiece);
                }

                if (!threeList4.contains(inputPiece)) {
                    threeList4.add(inputPiece);
                }

                availablePieces.add(inputPiece);
            }
        }

        for (Byte availablePiece : mapping.keySet()) {
            List<Piece> pieces = mapping.get(availablePiece);
            colourToPieces.put(availablePiece,  pieces.toArray(new Piece[pieces.size()]));
        }

         for (Integer availablePiece : mapping2.keySet()) {
            List<Piece> pieces = mapping2.get(availablePiece);
            twoColourToPieces.put(availablePiece,  pieces.toArray(new Piece[pieces.size()]));
        }

        for (Integer availablePiece: mapping3.keySet()) {
            List<Piece> pieces = mapping3.get(availablePiece);
            threeColourToPieces.put(availablePiece, pieces.toArray(new Piece[pieces.size()]));
        }


        this.edgePieces = edgePieces.toArray(new Piece[edgePieces.size()]);
        this.cornerPieces = cornerPieces.toArray(new Piece[cornerPieces.size()]);
        this.availablePieces = availablePieces.toArray(new Piece[availablePieces.size()]);
    }

    private List<Piece> getListForSide(Map<Byte, List<Piece>> mapping, byte side) {
        List<Piece> list = mapping.get(side);
        if (list == null) {
            list = new ArrayList<Piece>();
            mapping.put(side, list);
        }
        return list;
    }

    private List<Piece> getListForTwoSides(Map<Integer, List<Piece>> mapping, byte side1, byte side2) {
        int total = EternityUtils.hash(side1, side2);
        List<Piece> list = mapping.get(total);
        if (list == null) {
            list = new ArrayList<Piece>();
            mapping.put(total, list);
        }
        return list;
    }

    private List<Piece> getListForThreeSides(Map<Integer, List<Piece>> mapping, byte side1, byte side2, byte side3) {
        int total = EternityUtils.hash(side1, side2, side3);
        List<Piece> list = mapping.get(total);
        if (list == null) {
            list = new ArrayList<Piece>();
            mapping.put(total, list);
        }
        return list;
    }



    /**
     * For debugging
     */
    public void print() {
        StringBuilder builder = new StringBuilder("\n");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                builder.append("| ");
                Piece piece = board[j][i];
                if (piece != null) {
                    builder.append(piece);
                } else {
                    builder.append("[     N/A     ]");
                }
                builder.append(" |");
            }
            builder.append("\n");
        }
        LOGGER.log(Level.INFO, builder.toString());

    }

    /**
     * @param x x coordinate
     * @param y y coordinate
     * @return the {@link Piece} at the specified location
     */
    public Piece getPiece(byte x, byte y) {
        return board[x][y];
    }

    /**
     * @return all available pieces, including those that have been placed on the board
     */
    public Piece[] getAvailablePieces() {
        //TODO: only return those that has not been placed? Maybe only worth the effort if the board is big enough?
        return availablePieces;
    }

    public Piece[] getCornerPieces() {
        return cornerPieces;
    }

    public Piece[] getEdgePieces() {
        return edgePieces;
    }

    /**
     * Representation of a location on the board
     */
    static class Coordinate {
        private byte x;
        private byte y;

        public Coordinate(byte x, byte y) {
            this.x = x;
            this.y = y;
        }

        public byte getX() {
            return x;
        }

        public byte getY() {
            return y;
        }
    }

    enum Position {
        TOP,RIGHT,BOTTOM,LEFT,
        INNER,
        SWITCH_CORNER_LEFT, SWITCH_CORNER_TOP, SWITCH_CORNER_RIGHT, SWITCH_CORNER_BOTTOM,
        TOP_LEFT_INNER, TOP_RIGHT_INNER, BOTTOM_RIGHT_INNER, BOTTOM_LEFT_INNER,
        TOP_LEFT_CORNER, TOP_RIGHT_CORNER, BOTTOM_RIGHT_CORNER, BOTTOM_LEFT_CORNER,
        TOP_EDGE, RIGHT_EDGE, BOTTOM_EDGE, LEFT_EDGE;
    }
}
