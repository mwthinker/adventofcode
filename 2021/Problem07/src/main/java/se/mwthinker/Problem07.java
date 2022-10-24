package se.mwthinker;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

class CrabGroup {
    final private int pos;
    private int crabs = 1;

    CrabGroup(int pos) {
        this.pos = pos;
    }

    int walkToPos(int pos) {
        return Math.abs(pos - getPos()) * crabs;
    }

    int getPos() {
        return pos;
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
        Map<Integer, CrabGroup> posToCrabGroup = new HashMap<>();
        positions.forEach(pos -> {
            posToCrabGroup.compute(pos, (position, crabGroup) -> {
                if (crabGroup == null) {
                    return new CrabGroup(position);
                }
                crabGroup.addCrab();
                return crabGroup;
            });
        });

        int lowest = posToCrabGroup.values().stream().mapToInt(crabGroup -> crabGroup.getPos()).min().getAsInt();
        int highest = posToCrabGroup.values().stream().mapToInt(crabGroup -> crabGroup.getPos()).max().getAsInt();

        //int fuel = posToCrabGroup.values().stream().mapToInt(CrabGroup::getFuel).sum();

        int minFuel = Integer.MAX_VALUE;
        int optimalPos = lowest;
        for (int pos = lowest; pos <= highest; ++pos) {
            int finalPos = pos;
            int fuel = posToCrabGroup.values().stream()
                    .mapToInt(crabGroup -> crabGroup.walkToPos(finalPos)).sum();
            if (fuel < minFuel) {
                minFuel = fuel;
                optimalPos = pos;
            }
        }


        if (print) {

        }
        System.out.println("\nLeast possible fuel: " + minFuel + " Pos: " + optimalPos);
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
