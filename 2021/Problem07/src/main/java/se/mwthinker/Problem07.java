package se.mwthinker;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

class CrabGroup {
    final private int pos;
    private int crabs = 1;

    CrabGroup(int pos) {
        this.pos = pos;
    }

    int getPos() {
        return pos;
    }

    int getCrabs() {
        return crabs;
    }

    void addCrab() {
        ++crabs;
    }
}

record Value(int fuel, int pos) { }

interface FuelConsumer {
    int walkTo(int pos, CrabGroup crabGroup);
}

@Command(name = "checksum", mixinStandardHelpOptions = true, description = "Solves the 2021 05-problem")
public class Problem07 implements Callable<Integer> {

    @Option(names = { "-f", "--file" }, paramLabel = "FILE", description = "the file containing the problem data")
    private File datafile;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Problem07()).execute(args);
        System.exit(exitCode);
    }

    static int part1WalkTo(int pos, CrabGroup crabGroup) {
        return Math.abs(pos - crabGroup.getPos()) * crabGroup.getCrabs();
    }

    static int part2WalkTo(int pos, CrabGroup crabGroup) {
        int n = Math.abs(pos - crabGroup.getPos());
        return n *(n + 1) / 2 * crabGroup.getCrabs();
    }

    static Value leastFuelConsumption(Map<Integer, CrabGroup> posToCrabGroup, FuelConsumer fuelConsumer) {
        int lowest = posToCrabGroup.values().stream().mapToInt(CrabGroup::getPos).min().getAsInt();
        int highest = posToCrabGroup.values().stream().mapToInt(CrabGroup::getPos).max().getAsInt();

        int minFuel = Integer.MAX_VALUE;
        int optimalPos = lowest;
        for (int pos = lowest; pos <= highest; ++pos) {
            int finalPos = pos;
            int fuel = posToCrabGroup.values().stream()
                    .mapToInt(crabGroup -> fuelConsumer.walkTo(finalPos, crabGroup)).sum();
            if (fuel < minFuel) {
                minFuel = fuel;
                optimalPos = pos;
            }
        }
        return new Value(minFuel, optimalPos);
    }

    private void solve(List<Integer> positions) {
        Map<Integer, CrabGroup> posToCrabGroup = new HashMap<>();
        positions.forEach(pos -> posToCrabGroup.compute(pos, (position, crabGroup) -> {
            if (crabGroup == null) {
                return new CrabGroup(position);
            }
            crabGroup.addCrab();
            return crabGroup;
        }));

        Value value1 = leastFuelConsumption(posToCrabGroup, Problem07::part1WalkTo);
        System.out.println("\nPart 1");
        System.out.println("\nLeast possible fuel: " + value1.fuel() + " Pos: " + value1.pos());

        Value value2 = leastFuelConsumption(posToCrabGroup, Problem07::part2WalkTo);
        System.out.println("\nPart 2");
        System.out.println("\nLeast possible fuel: " + value2.fuel() + " Pos: " + value2.pos());
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

        if (sc.hasNextLine()) {
            return Arrays.stream(sc.nextLine().replaceAll("\\s+", "").split(","))
                    .map(Integer::parseInt)
                    .toList();
        }
        throw new RuntimeException("Missing data in file.");
    }
}
