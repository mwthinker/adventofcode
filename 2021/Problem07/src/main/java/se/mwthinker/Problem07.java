package se.mwthinker;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

class CrabGroup {
    private int pos;
    private int crabs = 0;
    private int distance = 0;

    CrabGroup(int pos) {
        this.pos = pos;
    }

    void walk(int distance) {
        this.distance += distance;
        pos += distance;
    }

    int getPos() {
        return pos;
    }

    int getFuel() {
        return crabs * pos;
    }

    void addCrab() {
        ++crabs;
    }
}

@Command(name = "checksum", mixinStandardHelpOptions = true, description = "Solves the 2021 05-problem")
public class Problem07 implements Callable<Integer> {

    @Option(names = { "-f", "--file" }, paramLabel = "FILE", description = "the file containing the problem data")
    private File datafile;

    @Option(names = { "-p", "--print" }, description = "the file containing the problem data")
    private boolean print = false;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Problem07()).execute(args);
        System.exit(exitCode);
    }

    private void solve(List<Integer> positions) {
        if (print) {

        }
        System.out.println("\nLeast possible fuel: " + 1);
    }

    @Override
    public Integer call() {
        try {
            List<Integer> positions = readFile();

            solve(positions);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        return 0;
    }

    private List<Integer> readFile() {
        if (datafile == null) {
            throw new RuntimeException("No found file");
        }

        Scanner sc;
        try {
            sc = new Scanner(datafile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ArrayList<CrabGroup> crabGroups = new ArrayList<>();

        if (sc.hasNextLine()) {
            return Arrays.stream(sc.nextLine().replaceAll("\\s+", "").split(","))
                    .map(Integer::parseInt)
                    .toList();
        }
        throw new RuntimeException("Missing data in file.");
    }
}
