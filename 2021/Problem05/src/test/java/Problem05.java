import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

record Position (int x, int y) {
    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}

class Line {
    private Position start;
    private Position end;
    public Line(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "" + start + " -> " + end;
    }
}

@Command(name = "checksum", mixinStandardHelpOptions = true, description = "Solves the 2021 05-problem")
public class Problem05 implements Callable<Integer> {

    @Option(names = { "-f", "--file" }, paramLabel = "FILE", description = "the file containing the problem data")
    private File datafile;

    public static void main(String args[]) {
        int exitCode = new CommandLine(new Problem05()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        List<Line> lines = readFile();

        for (var line : lines) {
            System.out.println(line);
        }
        return 0;
    }

    private Position parsePosition(String str) {
        String posStrings[] = str.split(",");
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

    private List<Line> readFile() {
        Scanner sc = null;
        try {
            sc = new Scanner(datafile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<Line> lines = new ArrayList<>();
        while (sc.hasNextLine()) {
            String row = sc.nextLine();
            row = row.replaceAll("\\s+","");

            String str[] = row.split("->");
            if (str.length != 2) {
                throw new RuntimeException();
            }

            lines.add(new Line(parsePosition(str[0]), parsePosition(str[1])));
        }
        return lines;
    }
}
