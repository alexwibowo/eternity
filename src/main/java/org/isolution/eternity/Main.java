package org.isolution.eternity;

import org.isolution.util.StreamUtils;

/**
 * User: alex
 * Date: 22/11/2007
 * Time: 21:58:33
 */
public class Main {

    public static void main(String[] args) throws Exception {
        String[] lines = StreamUtils.readLines(Main.class.getResourceAsStream(args[0]));
        Piece[] pieces = EternityUtils.fromString(lines);

        Board board = new Board((byte) (Byte.parseByte(args[1]) + (byte)2), pieces);
        board.print();

        long start = System.currentTimeMillis();
        Solver solver = new Solver(board);
        solver.solve();
        long finish = System.currentTimeMillis();
        System.out.println("Solved in " + (finish-start) + " milliseconds");
        board.print();

    }
}
