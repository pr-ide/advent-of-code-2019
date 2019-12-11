package ru.ural;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10 {
	public static void main(String... arg) {
		List<Day6.Node> nodes = new ArrayList<>();

		Path path = Paths.get("C:\\GitRepos\\Heroku\\advent-of-code-2019\\Java\\res\\day10");
		List<Asteroid> asteroids = new ArrayList<>();
		try (Stream<String> lines = Files.lines(path)) {
			int iLine = 0;
			for (String line : lines.collect(Collectors.toList())) {
				int i = 0;
				for (String s : line.split("")) {
					if ("#".equals(s))
						asteroids.add(new Asteroid(new Point(i, iLine)));
					i++;
				}
				iLine++;
			}
		} catch (
				IOException ex) {
			// do something or re-throw...
		}

		asteroids.forEach(asteroid -> asteroid.calculateAssInView(asteroids));
		int astCount = asteroids.size();
		Asteroid res = asteroids.stream().max(Comparator.comparingInt(Asteroid::getView))
				.orElseThrow(RuntimeException::new);
		System.out.println(res.toString());
		int remainToDestroy = 200;
		Asteroid a200 = null;
		while (a200 == null && (asteroids.size() > astCount - 200 && asteroids.size() > 1)) {
			List<Asteroid> TODESTROY = res.radarCalculateAssInView();

			if (TODESTROY.size() > remainToDestroy || asteroids.size() - 1 - TODESTROY.size() == 0) {
				int index = TODESTROY.size() - remainToDestroy;
				a200 = TODESTROY.get(remainToDestroy - 1);
			}

			remainToDestroy -= TODESTROY.size();
			asteroids.removeAll(TODESTROY);

		}

		assert a200 != null;
		System.out.println(a200.toString());
	}
}

class Asteroid {
	private Point point;
	private int view = 0;
	private List<Asteroid> viewList = new ArrayList<>();

	public Point getPoint() {
		return point;
	}

	public int getView() {
		return view;
	}

	public Asteroid(Point point) {
		this.point = point;
	}

	public void calculateAssInView(List<Asteroid> asteroids) {

		final List<Asteroid> otherAsteroids = asteroids.stream()
				.filter(asteroid -> !asteroid.getPoint().equals(getPoint())).collect(Collectors.toList());

		//TODO ОПТИМИЗИРОВАТЬ ДОБАВИВ СПИСОК ПЕРЕСЕЧЕНИЙ

		otherAsteroids.forEach(asteroid -> {
			boolean isInView = otherAsteroids.stream()
					.filter(asteroid1 -> !asteroid1.getPoint().equals(asteroid.getPoint()))
					.map(asteroid1 -> asteroid1.isAsteroidBetween(this, asteroid))
					.allMatch(Boolean.FALSE::equals);
			if (isInView) {
				viewList.add(asteroid);
				view++;
			}
			;
		});

	}

	public List<Asteroid> radarCalculateAssInView() {

		SortedMap<Double, List<Asteroid>> doubleAsteroidMap = new TreeMap<>();
		viewList.stream()
				.filter(asteroid -> !asteroid.getPoint().equals(getPoint()))
				.forEach(asteroid -> {
					double angle = asteroid.getAngle(this);
					if (!doubleAsteroidMap.containsKey(angle)) {
						doubleAsteroidMap.put(angle, new ArrayList<>());
					}
					doubleAsteroidMap.get(angle).add(asteroid);
				});

		return new ArrayList<>(doubleAsteroidMap.values().stream().flatMap(asteroids -> asteroids.stream()).collect(
				Collectors.toList()));

	}

	private boolean isAsteroidBetween(Asteroid start, Asteroid end) {

		List<Asteroid> asteroids = new ArrayList<>();
		asteroids.add(this);

		Asteroid lineDetectAsteroid = isLineDetectAsteroid(start, asteroids, end.getPoint());
		return lineDetectAsteroid != null;

	}

	private static Integer distance(Point point1, Point point2) {
		return Math.abs(point1.x - point2.x) + Math.abs(point1.y - point2.y);
	}

	private static Asteroid isLineDetectAsteroid(Asteroid start, List<Asteroid> visibleAsteroids, Point point) {

		return visibleAsteroids.stream().map(asteroid -> {
			boolean b = (
					((start.getPoint().getX() * (asteroid.getPoint().getY() - point.getY()))
							+ (asteroid.getPoint().getX() * (point.getY() - start.getPoint().getY()))
							+ (point.getX() * (start.getPoint().getY() - asteroid.getPoint().getY()))
					) / 2) == 0;

			if (b) {

				boolean containsX = Math.min(start.getPoint().getX(), point.getX()) <= asteroid.getPoint().getX()
						&& Math.max(start.getPoint().getX(), point.getX()) >= asteroid.getPoint().getX();
				boolean containsY = Math.min(start.getPoint().getY(), point.getY()) <= asteroid.getPoint().getY()
						&& Math.max(start.getPoint().getY(), point.getY()) >= asteroid.getPoint().getY();

				if (containsX && containsY) {
					return asteroid;
				} else {
					return null;
				}

			}
			return null;
		}).filter(Objects::nonNull).findAny().orElse(null);

	}

	public double getAngle(Asteroid start) {
		Point point = new Point(start.getPoint().x, getPoint().y);

		double xKatet = distance(start.point, point);
		double yKatet = distance(getPoint(), point);

		if (yKatet == 0) {
			return start.getPoint().y > getPoint().y ? 0 : 180;
		}

		if (xKatet == 0) {
			return start.getPoint().x > getPoint().x ? 90 : 270;
		}

		double atan = Math.toDegrees(Math.atan(yKatet / xKatet));

		if (getPoint().y > start.getPoint().y) {
			atan += 90;
		}
		;

		if (getPoint().x > start.getPoint().x)
			return atan;
		else
			return 360 - atan;
	}

	private boolean isNumberBetween(Double num, Double start, Double end) {
		return Math.abs(start - end) > Math.abs(num - end);
	}

	@Override
	public String toString() {
		return "Asteroid{" +
				"point=" + point +
				", view=" + view +
				'}';
	}
}