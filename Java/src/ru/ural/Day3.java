package ru.ural;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Day3 {
	public static void main(String... arg) {

		long m = System.currentTimeMillis();

		List<List<Point>> setList = new ArrayList<>();
		Path path = Paths.get("\\home\\day3.txt");
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(s -> setList.add(new ArrayList<>(getPoints(Arrays.asList(s.split(","))))));
		} catch (IOException ex) {
			// do something or re-throw...
		}

		Set<Point> intersection = new HashSet<Point>(setList.get(0));

		setList.forEach(points -> intersection.retainAll(new HashSet<>(points)));

		Point point;
		point = intersection.stream().min(Comparator.comparing(o -> distanceByStep(o, setList)))
				.orElseThrow(() -> new RuntimeException("ERROR"));
		System.out.println(distanceByStep(point, setList));
		System.out.println((double) (System.currentTimeMillis() - m));
	}

	//Для первой части
	private static Integer distance(Point point) {
		return Math.abs(point.x) + Math.abs(point.y);
	}

	private static Integer distanceByStep(Point point, List<List<Point>> setList) {
		Integer res = 0;
		for (List<Point> points : setList) {
			res = res + points.indexOf(point) + 1;
		}
		return res;
	}

	private static List<Point> getPoints(List<String> strings) {
		List<Point> res = new ArrayList<>();
		Point lastP = new Point(0, 0);
		for (String s : strings) {
			List<Point> points = getPoints(lastP, s);
			lastP = points.get(points.size() - 1);
			res.addAll(points);
		}

		return res;
	}

	private static List<Point> getPoints(Point point, String string) {
		List<Point> res = new ArrayList<>();
		Integer i = Integer.parseInt(string.substring(1));
		for (int j = 1; j <= i; j++) {
			switch (string.substring(0, 1)) {
			case "U":
				res.add(new Point(point.x, point.y + j));
				break;
			case "D":
				res.add(new Point(point.x, point.y - j));
				break;
			case "L":
				res.add(new Point(point.x + j, point.y));
				break;
			case "R":
				res.add(new Point(point.x - j, point.y));
				break;
			}
		}

		return res;
	}

}
