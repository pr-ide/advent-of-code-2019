package ru.ural.day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author RandomPie
 */
public class Day5 {
    public static void main(String... arg) {

        Path path = Paths.get("/home/kirill/Development/GIT/advent-of-code-2019/Java/static/day5");
        String res = null;
        try (Stream<String> lines = Files.lines(path)) {
            res = lines.collect(Collectors.joining());
        } catch (IOException ex) {
            // do something or re-throw...
        }

        assert res != null;
        Day5Computer day5Computer = new Day5Computer(res);
        day5Computer.compile(new ArrayList<>() {{
            add(4L);
            add(0L);
        }});
        if (day5Computer.getStatus().equals(Day5Computer.Status.END)) {
            System.out.println(day5Computer.getOut().stream().map(Object::toString).collect(Collectors.joining(",")));
        } else {
            throw new RuntimeException("INPUT ERROR");
        }
    }

}
