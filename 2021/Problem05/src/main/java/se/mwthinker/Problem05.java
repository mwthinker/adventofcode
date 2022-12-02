package se.mwthinker;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

record Position (int x, int y) {

    public Position add(Position p) {
        return new Position(x + p.x, y + p.y);
    }

    public Position subtract(Position p) {
        return new Position(x - p.x, y - p.y);
    }

    public Position normalize() {
        return new Position(x == 0 ? 0 : x / Math.abs(x), y == 0 ? 0 : y / Math.abs(y));
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    @Override
    public int hashCode() {
        return (x * 0x1f1f1f1f) ^ y;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Position p)) {
            return false;
        }
        return x == p.x && y == p.y;
    }

}

enum Direction {
    HORIZONTAL,
    VERTICAL,
    DIAGONAL
}

record Entry(Position start, Position end) {

    public List<Position> produce(EnumSet<Direction> directions) {
        List<Position> positions = new ArrayList<>();

        Position delta = end.subtract(start()).normalize();
        if (delta.x() != 0 && delta.y() == 0 && directions.contains(Direction.HORIZONTAL)
                || delta.x() == 0 && delta.y() != 0 && directions.contains(Direction.VERTICAL)
                || delta.x() != 0 && delta.y() != 0 && directions.contains(Direction.DIAGONAL)
        ) {

            Position position = start;
            do {
                positions.add(position);

                position = position.add(delta);
            } while (!position.equals(end.add(delta)));
        }

        return positions;
    }

    @Override
    public String toString() {
        return "" + start + " -> " + end;
    }
}

class Board {
    private final HashMap<Position, Integer> posToValue = new HashMap<>();

    Board(List<Entry> entries, EnumSet<Direction> directions) {
        entries.stream()
                .flatMap(entry -> entry.produce(directions).stream())
                .forEach(p ->
                        posToValue.compute(p, (position, value) -> {
                            if (value == null) {
                                return 1;
                            }
                            return value + 1;
                        }));
    }

    public long sizeOfValueHigherThan(int value) {
        return posToValue.values().stream()
                .filter(v -> v > value)
                .count();
    }

    @Override
    public String toString() {
        int maxX = posToValue.isEmpty() ? 0 : Collections.max(posToValue.keySet().stream().map(Position::x).toList());
        int maxY = posToValue.isEmpty() ? 0 : Collections.max(posToValue.keySet().stream().map(Position::y).toList());

        StringBuilder str = new StringBuilder();
        for (int y = 0; y <= maxY; ++y) {
            for (int x = 0; x <= maxX; ++x) {
                str.append(posToValue.getOrDefault(new Position(x, y), 0)).append(" ");
            }
            str.append("\n");
        }
        return str.toString();
    }

}

@Command(name = "checksum", mixinStandardHelpOptions = true, description = "Solves the 2021 05-problem")
public class Problem05 implements Callable<Integer> {

    @Option(names = { "-f", "--file" }, paramLabel = "FILE", description = "the file containing the problem data")
    private File datafile;

    @Option(names = { "-p", "--print" }, description = "the file containing the problem data")
    private boolean print = false;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Problem05()).execute(args);
        System.exit(exitCode);
    }

    private void solve(List<Entry> entries, EnumSet<Direction> directions) {
        if (print) {
            for (var entry : entries) {
                System.out.println(entry);
            }
            System.out.println();
        }

        var board = new Board(entries, directions);

        if (print) {
            System.out.println(board);
        }

        if (print) {
            System.out.println(board);
        }

        System.out.print("Directions: ");
        for (Direction direction : directions) {
            System.out.print(direction.name() + " ");
        }

        System.out.println("\nNumber of positions higher than 1: " + board.sizeOfValueHigherThan(1));
    }

    @Override
    public Integer call() {
        try {
            List<Entry> entries = readFile();

            solve(entries, EnumSet.of(Direction.HORIZONTAL, Direction.VERTICAL));
            solve(entries, EnumSet.of(Direction.HORIZONTAL, Direction.VERTICAL, Direction.DIAGONAL));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return 0;
    }

    private Position parsePosition(String str) {
        String[] posStrings = str.split(",");
        if (posStrings.length != 2) {
            throw new RuntimeException();
        }
        try {
            int nbr1 = Integer.parseInt(posStrings[0]);
            int nbr2 = Integer.parseInt(posStrings[1]);
            return new Position(nbr1, nbr2);
        } catch (NumberFormatException ex){
            ex.printStackTrace();
            throw ex;
        }
    }

    private List<Entry> readFile() {
        if (datafile == null) {
            throw new RuntimeException("No found file");
        }

        Scanner sc;
        try {
            sc = new Scanner(datafile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<Entry> entries = new ArrayList<>();
        while (sc.hasNextLine()) {
            String row = sc.nextLine();
            row = row.replaceAll("\\s+","");

            if (row.isEmpty()) {
                continue;
            }

            String[] str = row.split("->");
            if (str.length != 2) {
                throw new RuntimeException();
            }

            entries.add(new Entry(parsePosition(str[0]), parsePosition(str[1])));
        }
        return entries;
    }
}
