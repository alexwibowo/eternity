package org.isolution.eternity;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: alex
 * Date: 24/11/2007
 * Time: 00:26:03
 * Copyright: Insight Solution
 */
public class Solver {
    public static final Logger LOGGER = Logger.getLogger(Solver.class.getName());
    static long counter =0;

    private Board board;

    public Solver(Board board){
        this.board=board;
    }

    public void solve() {
        solveLocation((byte) 1, (byte)1, (byte)1, Board.Position.TOP_LEFT_CORNER);
    }


    /**
     * Attempt to solve at the given position
     * @param layer the layer to solve. 1 indicate the layer after the most outer layer. (since we initialize the outer layer
     *        with {@link Piece#EDGE_COLOR}.
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @return <code>true</code> if this position is solved (with all the constraints fulfilled), <code>false<code>otherwise
     */
    private boolean solveLocation(byte layer, byte x, byte y, Board.Position position) {
//        counter++;

        if (layer >= board.getMaxLayer()) {
            // We stop if all layer has been covered. The number of layers available = board size / 2 (if the board size is even)
            return true;
        }

        if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.log(Level.FINEST,"Solving " + x + " - " + y + " for layer " + layer);
        }
        boolean foundPieceForThisLocation = false;
        boolean foundPieceForNextLocation = false;

        // iterate through the given layer, fits one piece at a time
        Piece topPiece = board.getPiece(x, (byte) (y - 1));
        Piece rightPiece = board.getPiece((byte) (x + 1), y);
        Piece bottomPiece = board.getPiece(x, (byte) (y + 1));
        Piece leftPiece = board.getPiece((byte) (x - 1), y);

        Piece[] piecesToUse = null;
        if (position == Board.Position.BOTTOM_LEFT_CORNER
                || position == Board.Position.TOP_LEFT_CORNER
                || position == Board.Position.TOP_RIGHT_CORNER
                || position == Board.Position.BOTTOM_RIGHT_CORNER) {
            piecesToUse = board.getCornerPieces();
        }else if (position == Board.Position.LEFT_EDGE
                || position == Board.Position.TOP_EDGE
                || position == Board.Position.RIGHT_EDGE
                || position == Board.Position.BOTTOM_EDGE) {
            piecesToUse = board.getEdgePieces();
        }else {
            // For inner pieces, for middle pieces we can check against two colours at once
            if (position == Board.Position.TOP_LEFT_INNER) {
               piecesToUse = board.getInnerPiecesForColour(leftPiece.getRight(), topPiece.getBottom());
            }else if (position == Board.Position.TOP_RIGHT_INNER) {
                piecesToUse = board.getInnerPiecesForColour(topPiece.getBottom(), rightPiece.getLeft());
            }else if (position == Board.Position.BOTTOM_RIGHT_INNER) {
                piecesToUse = board.getInnerPiecesForColour(rightPiece.getLeft(), bottomPiece.getTop());
            }else if (position == Board.Position.BOTTOM_LEFT_INNER) {
                piecesToUse = board.getInnerPiecesForColour(bottomPiece.getTop(), leftPiece.getRight());
            }else if (position == Board.Position.LEFT) {
                piecesToUse = board.getInnerPiecesForColour(bottomPiece.getTop(),leftPiece.getRight());
            }else if (position == Board.Position.TOP) {
                piecesToUse = board.getInnerPiecesForColour(leftPiece.getRight(),topPiece.getBottom());
            }else if (position == Board.Position.RIGHT) {
                piecesToUse = board.getInnerPiecesForColour(topPiece.getBottom(),rightPiece.getLeft());
            }else if (position == Board.Position.BOTTOM) {
                piecesToUse = board.getInnerPiecesForColour(rightPiece.getLeft(),bottomPiece.getTop());
            }else if (position == Board.Position.SWITCH_CORNER_TOP) {
                piecesToUse = board.getInnerPiecesForColour(leftPiece.getRight(), topPiece.getBottom(), rightPiece.getLeft());
            }else if (position == Board.Position.SWITCH_CORNER_RIGHT) {
                piecesToUse = board.getInnerPiecesForColour(topPiece.getBottom(), rightPiece.getLeft(), bottomPiece.getTop());
            }else if (position == Board.Position.SWITCH_CORNER_BOTTOM) {
                piecesToUse = board.getInnerPiecesForColour(rightPiece.getLeft(), bottomPiece.getTop(), leftPiece.getRight());
            }else if (position == Board.Position.SWITCH_CORNER_LEFT) {
                piecesToUse = board.getInnerPiecesForColour(bottomPiece.getTop(), leftPiece.getRight(), topPiece.getBottom());
            }
        }
        if (piecesToUse == null) {
            return false;
        }

        byte maxCoordforLayer = (byte) (board.getSize() - layer -1);
        int piecesLength = piecesToUse.length;
        for (int i = 0; i < piecesLength; i++) {
            Piece nextPiece = piecesToUse[i];
            if (!nextPiece.isPlaced()
                    && nextPiece.tryToFitWithNeighbours(topPiece, rightPiece, bottomPiece, leftPiece)
                    ) {
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(Level.FINEST, "Piece " + i + "     : " + nextPiece + " fits at (" + x + "," + y + ")");
                }
                foundPieceForThisLocation = true;
                board.setPiece(x, y, nextPiece);

                if (prune(layer, x, y, maxCoordforLayer)) {
                    // if we need to prune, then take out the current solved piece
                    foundPieceForThisLocation=false;
                    board.setLocationEmpty(x, y);
                }
            }else {
                if (LOGGER.isLoggable(Level.FINEST)) {
                    LOGGER.log(Level.FINEST,"       Piece " +nextPiece + " doesnt fit at ("+x+","+y+")");
                }
                foundPieceForThisLocation = false;
            }




            if (foundPieceForThisLocation) {
                // determine the next position we want to solve
                SolveLocation nextLocation = getNextLocationToSolve(layer, x, y);

                byte nextLayer = layer;
                if (nextLocation == null) {
                    // If we cant find the next position to solve for this layer, it means we have filled up this layer. So we proceed
                    // with the next layer.
                  /*  if (LOGGER.isLoggable(Level.FINEST)) {
                        board.print();
                    }*/
                    nextLayer += 1;
                    nextLocation = new SolveLocation(Board.Position.TOP_LEFT_INNER, nextLayer, nextLayer);
                }



                foundPieceForNextLocation = solveLocation(nextLayer, nextLocation.x, nextLocation.y, nextLocation.position);

                if (!foundPieceForNextLocation) {
                    // If we cant find a solution for the next position (at all, with all current remaining pieces), try to find
                    // other solution for the current position, and see if that helps to solve the next position. Take the piece
                    // out from the board, and mark the placed piece available again.
                    if (LOGGER.isLoggable(Level.FINEST)){
                        LOGGER.log(Level.FINEST,"Putting back (" + x + "," + y + ") :  and looking at next available piece");
                    }
                    board.setLocationEmpty(x,y);
                    foundPieceForThisLocation = false;
                } else {
                    // If the next position is solved, then we pretty much have solved the puzzle! Since at the next position,
                    // it requires the next one to be solved, and so on (recursive truth)
                    return true;
                }
            }
        }
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.log(Level.FINER,"Failed to find piece that fit. Backtrack for (" + x +"," +y + ") at level " + layer);
        }
        return false;
    }

    public boolean prune(byte currentLayer, byte currentX, byte currentY, byte maxCoordforLayer){
        if (currentLayer >= (board.getMaxLayer() - 1) ) {
            // We stop if all layer has been covered. The number of layers available = board size / 2 (if the board size is even)
            return false;
        }

        byte nextXToTest=1;
        byte nextYToTest=1;

        boolean performTest =false;
        Board.Position testPosition = null;
        if (currentX == (currentLayer + 1) && (currentY == currentLayer)) {
          // check top left next layer
          nextXToTest = currentX;
          nextYToTest = (byte) (currentY + 1);
          testPosition = Board.Position.TOP_LEFT_INNER;
          performTest = true;
        }else if ((currentX == maxCoordforLayer) && (currentY == (currentLayer + 1))) {
          // check top right next layer
            nextXToTest = (byte) (currentX - 1);
            nextYToTest = currentY;
            testPosition = Board.Position.TOP_RIGHT_INNER;
            performTest = true;

        }else if ((currentX == (maxCoordforLayer - 1)) && (currentY == maxCoordforLayer)) {
            // check bottom right next layer
            nextXToTest = currentX;
            nextYToTest = (byte) (currentY - 1);
            testPosition = Board.Position.BOTTOM_RIGHT_INNER;
            performTest = true;
        }else if ((currentX == (currentLayer + 1) && (currentY == maxCoordforLayer))) {
            nextXToTest = currentX;
            nextYToTest = (byte) (currentY - 1);
            testPosition = Board.Position.BOTTOM_LEFT_INNER;
            performTest = true;
        }

        if (performTest) {
             Piece topPiece = board.getPiece(nextXToTest, (byte) (nextYToTest - 1));
            Piece rightPiece = board.getPiece((byte) (nextXToTest + 1), nextYToTest);
            Piece bottomPiece = board.getPiece(nextXToTest, (byte) (nextYToTest + 1));
            Piece leftPiece = board.getPiece((byte) (nextXToTest - 1), nextYToTest);

            // This pruning technique is only useful for inner pieces.
            Piece[] piecesToUse = null;
            if (testPosition == Board.Position.TOP_LEFT_INNER) {
                piecesToUse = board.getInnerPiecesForColour(leftPiece.getRight(), topPiece.getBottom());
            }else if (testPosition == Board.Position.TOP_RIGHT_INNER){
                piecesToUse = board.getInnerPiecesForColour(topPiece.getBottom(), rightPiece.getLeft());
            }else if (testPosition == Board.Position.BOTTOM_RIGHT_INNER){
                piecesToUse = board.getInnerPiecesForColour(rightPiece.getLeft(), bottomPiece.getTop());
            }else if (testPosition == Board.Position.BOTTOM_LEFT_INNER){
                piecesToUse = board.getInnerPiecesForColour(bottomPiece.getTop(), leftPiece.getRight());
            }
            for (Piece piece : piecesToUse) {
                if (!piece.isPlaced() &&
                        piece.tryToFitWithNeighbours(topPiece, rightPiece, bottomPiece, leftPiece)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private class SolveLocation{
        final Board.Position position;
        final byte x;
        final byte y;

        public SolveLocation(Board.Position position, byte x, byte y) {
            this.position = position;
            this.x = x;
            this.y = y;
        }
    }


    public SolveLocation getNextLocationToSolve(byte currentLayer, byte currentX, byte currentY) {
        byte maxCoordforLayer = (byte) (board.getSize() - currentLayer -1);

        //------------------------- CORNERS -----------------------------------
        // Prioritise filling up the corner pieces, since this introduces more constraints 
        if (board.getPiece(maxCoordforLayer, currentLayer) == null) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.log(Level.FINEST, "Next : TOP RIGHT at level " + currentLayer);
            }
            // check top right corner
            return new SolveLocation(
                    currentLayer==1? Board.Position.TOP_RIGHT_CORNER : Board.Position.TOP_RIGHT_INNER,
                    maxCoordforLayer, currentLayer);
        }

        if (board.getPiece(maxCoordforLayer, maxCoordforLayer) == null) {
            if (LOGGER.isLoggable(Level.FINEST)){
                LOGGER.log(Level.FINEST,"Next : BOTTOM RIGHT at level " + currentLayer);
            }
            // check bottom right corner
            return new SolveLocation(
                    currentLayer==1? Board.Position.BOTTOM_RIGHT_CORNER : Board.Position.BOTTOM_RIGHT_INNER,
                    maxCoordforLayer, maxCoordforLayer);
        }

        if (board.getPiece(currentLayer, maxCoordforLayer) == null) {
            if (LOGGER.isLoggable(Level.FINEST)){
                LOGGER.log(Level.FINEST,"Next : BOTTOM LEFT at level " + currentLayer);
            }
            // check bottom left corner
            return new SolveLocation(
                    currentLayer==1? Board.Position.BOTTOM_LEFT_CORNER : Board.Position.BOTTOM_LEFT_INNER,
                    currentLayer, maxCoordforLayer);
        }
        

        //------------------------- INNER PIECES -----------------------------
        // since all corners at this layer has been filled, we attempt to fill the inners
        if ( (currentY == currentLayer) && (currentX < (maxCoordforLayer-1))) {
            // TOP INNER
            byte newX = (byte) (currentX + 1);
            Board.Position newPosition = null;
            if (newX == maxCoordforLayer - 1 && (currentLayer!=1)) {
               newPosition = Board.Position.SWITCH_CORNER_TOP;
            }else{
               newPosition = currentLayer==1? Board.Position.TOP_EDGE : Board.Position.TOP;
            }
            return new SolveLocation(newPosition,newX, currentY );
        }

        if (currentX == maxCoordforLayer && (currentY < (maxCoordforLayer-1))) {
            // RIGHT INNER
            byte newY = (byte) (currentY + 1);
            Board.Position newPosition = null;
            if (newY == maxCoordforLayer - 1 && currentLayer != 1) {
                newPosition = Board.Position.SWITCH_CORNER_RIGHT;
            }else{
                newPosition = currentLayer==1? Board.Position.RIGHT_EDGE : Board.Position.RIGHT;
            }
            return new SolveLocation(newPosition, currentX, newY);
        }

        if (currentY == maxCoordforLayer && (currentX > (currentLayer+1))) {
            // BOTTOM INNER
            byte newX= (byte)(currentX-1);
            Board.Position newPosition = null;
            if (newX == currentLayer + 1 && currentLayer != 1) {
                newPosition = Board.Position.SWITCH_CORNER_BOTTOM;
            }else{
                newPosition = currentLayer==1? Board.Position.BOTTOM_EDGE : Board.Position.BOTTOM;
            }
            return new SolveLocation(newPosition,newX, currentY);
        }

        if (currentX == currentLayer && (currentY > (currentLayer+1))) {
            // LEFT INNER
            byte newY = (byte) (currentY - 1);
            Board.Position newPosition = null;
            if (newY == currentLayer + 1 && currentLayer != 1) {
                newPosition = Board.Position.SWITCH_CORNER_LEFT;
            }else{
                newPosition = currentLayer == 1 ? Board.Position.LEFT_EDGE : Board.Position.LEFT;
            }
            return new SolveLocation(newPosition, currentX, newY);
        }




                                                        
        //------------------------- INNER PIECES WHERE WE HAVE TO SWITCH TO OTHER SIDE ---------
        SolveLocation nextCoordinate=null;
        if (currentY == (currentLayer + 1)) {
            // Switch to top inner
            nextCoordinate= new SolveLocation(
                    currentLayer==1? Board.Position.TOP_EDGE : Board.Position.TOP,
                    (byte) (currentLayer+1), currentLayer );
        }else if ((currentX == (maxCoordforLayer - 1))) {
            // Switch to right inner
            nextCoordinate= new SolveLocation(
                    currentLayer==1? Board.Position.RIGHT_EDGE : Board.Position.RIGHT,
                    maxCoordforLayer, (byte) (currentLayer+1));
        }else if (currentY == (maxCoordforLayer - 1)) {
            // Switch to bottom inner
            nextCoordinate= new SolveLocation(
                    currentLayer==1? Board.Position.BOTTOM_EDGE : Board.Position.BOTTOM,
                    (byte)(maxCoordforLayer-1),maxCoordforLayer);
        }else if (currentX == (currentLayer + 1)) {
            // Switch to left inner
            nextCoordinate= new SolveLocation(
                    currentLayer==1? Board.Position.LEFT_EDGE : Board.Position.LEFT,
                    currentLayer, (byte) (maxCoordforLayer-1));
        }

        // No more coordinate to solve for this layer
        if (nextCoordinate==null || board.getPiece(nextCoordinate.x, nextCoordinate.y) != null) {
            return null;
        }else{
            return nextCoordinate;
        }

    }

}
