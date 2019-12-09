package ru.ural;

import ru.ural.day5.Day5Computer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {

    public static void main(String... arg) {

        Path path = Paths.get("/home/kirill/Development/GIT/advent-of-code-2019/Java/static/day7");
        String res = null;
        try (Stream<String> lines = Files.lines(path)) {
            res = lines.collect(Collectors.joining());
        } catch (IOException ex) {
            // do something or re-throw...
        }

        assert res != null;
        ProseedDay5 proseedDay5 = new ProseedDay5(new Day5Computer(res));

        System.out.println(proseedDay5.getCalc());
    }

}

class ProseedDay5 {
    Day5Computer day5Computer;

    public ProseedDay5(Day5Computer day5Computer) {
        this.day5Computer = day5Computer;
    }

    public long getCalc() {

        List<Long> longList = new ArrayList<>() {{
            add(5L);
            add(6L);
            add(7L);
            add(8L);
            add(9L);
        }};

        return longList.stream()
                .map(aLong ->
                        recCalc(aLong, 0L, longList))
                .max(Long::compareTo).get();

    }

    private Long recCalc(Long input, Long lastCal, List<Long> restInputs) {

        day5Computer.compile(
                new ArrayList<Long>() {{
                    add(input);
                    add(lastCal);
                }});

        Long lastCal1 = day5Computer.getOut().get(0);
        day5Computer.resetComp();

        final List<Long> localRestInputs = new ArrayList<>(restInputs.stream()
                .filter(aLong1 -> !aLong1.equals(input))
                .collect(Collectors.toList()));

        if (localRestInputs.size() == 0) {
            return lastCal1;
        }

        List<Long> longStream = localRestInputs.stream()
                .map(aLong -> {
                    return recCalc(aLong,
                            lastCal1,
                            localRestInputs);
                }).collect(Collectors.toList());
        return longStream.stream()
                .max(Long::compareTo).get();
    }

    private Long recCalcSecPart(Long input, List<Long> lastCal, List<Long> restInputs) {
        day5Computer.compile(lastCal);
        List<Long> lastCal1 = day5Computer.getOut();

        final List<Long> localRestInputs = new ArrayList<>(restInputs.stream()
                .filter(aLong1 -> !aLong1.equals(input))
                .collect(Collectors.toList()));

        if (localRestInputs.size() == 0) {
            return lastCal1.get(0);
        }

        List<Long> longStream = localRestInputs.stream()
                .map(aLong -> {
                    return recCalcSecPart(aLong,
                            lastCal1,
                            localRestInputs);
                }).collect(Collectors.toList());
        return longStream.stream()
                .max(Long::compareTo).get();
    }
}

class ProseedDay5Part2 {
    List<Day5Computer> day5Computers = new ArrayList<>();
    List<List<Long>> allPosibleInputs = new ArrayList<>();

    public ProseedDay5Part2(String s) {
        this.day5Computers = new ArrayList<>() {{
            add(new Day5Computer(s));
            add(new Day5Computer(s));
            add(new Day5Computer(s));
            add(new Day5Computer(s));
            add(new Day5Computer(s));
        }};

        allPosibleInputs =
    }

    public long getCalc() {



        return longList.stream()
                .map(aLong ->
                        recCalcSecPart(aLong, longList))
                .max(Long::compareTo).get();

    }

    private List<List<Long>> getAllPosibleInputs() {
        List<Long> longList = new ArrayList<>() {{
            add(5L);
            add(6L);
            add(7L);
            add(8L);
            add(9L);
        }};


    }

    private List<List<Long>> recGetAllPosibleInputs(Long i, List<Long> restI, List<Long> res) {

        final List<Long> localRestInputs = new ArrayList<>(restI.stream()
                .filter(aLong1 -> !aLong1.equals(i))
                .collect(Collectors.toList()));

        return res.stream().map()



    }


    private Long recCalcSecPart(Long input, List<Long> restInputs) {
        day5Computer.compile(lastCal);
        List<Long> lastCal1 = day5Computer.getOut();

        final List<Long> localRestInputs = new ArrayList<>(restInputs.stream()
                .filter(aLong1 -> !aLong1.equals(input))
                .collect(Collectors.toList()));

        if (localRestInputs.size() == 0) {
            return lastCal1.get(0);
        }

        List<Long> longStream = localRestInputs.stream()
                .map(aLong -> {
                    return recCalcSecPart(aLong,
                            lastCal1,
                            localRestInputs);
                }).collect(Collectors.toList());
        return longStream.stream()
                .max(Long::compareTo).get();
    }
}
