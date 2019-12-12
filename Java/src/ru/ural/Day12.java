package ru.ural;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.ural.Day12.Part.PART1;

/**
 * @author k.lapihin
 */
public class Day12 {

	public enum Part {PART1, PART2}

	public static final Integer END = 1000;

	public static void main(String... arg) throws IOException {

		long startTime = new Date().getTime();

		Part parts = Part.PART2;

		Path path = Paths.get("C:\\GitRepos\\Heroku\\advent-of-code-2019\\Java\\res\\day12");

		List<String> res = null;
		try (Stream<String> lines = Files.lines(path)) {
			res = lines.collect(Collectors.toList());
		} catch (IOException ex) {
			// do something or re-throw...
		}

		final String regex = "<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)>";

		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

		List<Sputnik> sputniks = new ArrayList<>();
		assert res != null;
		for (String s : res) {
			final Matcher matcher = pattern.matcher(s);
			while (matcher.find()) {
				Vector<Integer> integers = new Vector<>();
				for (int i = 1; i <= 3; i++) {
					integers.add(Integer.valueOf(matcher.group(i)));
				}
				sputniks.add(new Sputnik(res.indexOf(s), integers));
			}
		}

		if (PART1.equals(parts)) {
			for (int i = 0; i <= END; i++) {

				System.out.println("Step " + i);
				sputniks.forEach(Day12::logSpitnic);
				System.out.println();

				if (i == END) {
					break;
				}

				sputniks.forEach(sputnik -> sputnik.calcVelocity(sputniks.stream()
						.filter(
								sputnik1 -> !sputnik1.getId().equals(sputnik.getId())).collect(
								Collectors.toList())));
				sputniks.forEach(Sputnik::calcPos);

			}

			System.out.println("Total en:" + sputniks.stream().map(Sputnik::getEnergy).mapToInt(value -> value).sum());
		} else {
			HashMap<Integer, List<Integer>> integerListHashMap = new HashMap<>();

			integerListHashMap.put(getVelocityHash(sputniks), new ArrayList<>() {{
				add(getPosHash(sputniks));
			}});

			boolean isFounded = false;
			Long step = 0L;
			while (!isFounded) {
				sputniks.forEach(sputnik -> sputnik.calcVelocity(sputniks.stream()
						.filter(
								sputnik1 -> !sputnik1.getId().equals(sputnik.getId())).collect(
								Collectors.toList())));
				sputniks.forEach(Sputnik::calcPos);

				Integer velHash = getVelocityHash(sputniks);

				if (integerListHashMap.containsKey(velHash)) {
					Integer posHash = getPosHash(sputniks);
					if (integerListHashMap.get(velHash).contains(posHash)) {
						isFounded = true;
					} else {
						integerListHashMap.get(velHash).add(posHash);
					}
				}
				step++;

				if (step % 1000000L == 0L) {
					long endTime = new Date().getTime();
					System.out.println("elapsed seconds: " + (endTime - startTime) / 1000);
					System.out.println(
							"Steps: " + step);
				}
			}

			System.out.println(step);
		}

	}

	public static Integer getVelocityHash(List<Sputnik> sputniks) {
		StringBuilder stringBuilder = new StringBuilder();

		sputniks.forEach(sputnik -> stringBuilder
				.append(sputnik.getVelocity().get(0))
				.append(sputnik.getVelocity().get(1))
				.append(sputnik.getVelocity().get(2))
		);

		return stringBuilder.toString().hashCode();

	}

	public static Integer getPosHash(List<Sputnik> sputniks) {
		StringBuilder stringBuilder = new StringBuilder();

		sputniks.forEach(sputnik -> stringBuilder
				.append(sputnik.getPoint().get(0))
				.append(sputnik.getPoint().get(1))
				.append(sputnik.getPoint().get(2))
		);

		return stringBuilder.toString().hashCode();
	}

	private static void logSpitnic(Sputnik sputnik) {
		System.out.println(String.format("pos=<x=%s, y= %s, z=%s>, vel=<x=%s, y=%s, z=%s>",
				sputnik.getPoint().get(0),
				sputnik.getPoint().get(1),
				sputnik.getPoint().get(2),
				sputnik.getVelocity().get(0),
				sputnik.getVelocity().get(1),
				sputnik.getVelocity().get(2)
		));
	}

}

class Sputnik {
	private Integer id;
	private List<Integer> point;
	private List<Integer> velocity = new ArrayList<>() {{
		add(0);
		add(0);
		add(0);
	}};

	public Sputnik(Integer id, Vector<Integer> point) {
		this.id = id;
		this.point = point;
	}

	public void calcPos() {
		addFirstVectorToSecond(point, velocity);
	}

	public void calcVelocity(List<Sputnik> sputniks) {
		Vector<Integer> res = new Vector<>() {{
			add(0);
			add(0);
			add(0);
		}};

		sputniks.forEach(sputnik -> {
			getOs(res, sputnik, 0);
			getOs(res, sputnik, 1);
			getOs(res, sputnik, 2);
		});

		addFirstVectorToSecond(velocity, res);
	}

	private void getOs(Vector<Integer> res, Sputnik sputnik, int i) {
		int resCacl;
		if (sputnik.getPoint().get(i) > point.get(i)) {
			resCacl = 1;
		} else if (sputnik.getPoint().get(i) < point.get(i)) {
			resCacl = -1;
		} else {
			resCacl = 0;
		}
		;
		res.set(i, res.get(i) + resCacl);
	}

	private static void addFirstVectorToSecond(List<Integer> point, List<Integer> velocity) {

		point.set(0, point.get(0) + velocity.get(0));
		point.set(1, point.get(1) + velocity.get(1));
		point.set(2, point.get(2) + velocity.get(2));

	}

	public Integer getId() {
		return id;
	}

	public List<Integer> getPoint() {
		return point;
	}

	public List<Integer> getVelocity() {
		return velocity;
	}

	public Integer getEnergy() {
		int a = 0, b = 0;
		for (int i = 0; i < 3; i++) {
			a += Math.abs(point.get(i));
			b += Math.abs(velocity.get(i));
		}
		return a * b;

	}

}
