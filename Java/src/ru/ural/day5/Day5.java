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

		Path path = Paths.get("\\home\\day5.txt");
		String res = null;
		try (Stream<String> lines = Files.lines(path)) {
			res = lines.collect(Collectors.joining());
		} catch (IOException ex) {
			// do something or re-throw...
		}

		assert res != null;
		Day5Computer day5Computer = new Day5Computer(res);
		System.out.println(day5Computer.compile(5L));
	}

}
