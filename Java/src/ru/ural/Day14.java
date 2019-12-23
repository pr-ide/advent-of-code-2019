package ru.ural;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author k.lapihin
 */
public class Day14 {

	final static String ORE = "ORE";
	final static String FUEL = "FUEL";

	public static void main(String... arg) {
		long startTime = new Date().getTime();
		Path path = Paths.get("C:\\GitRepos\\Heroku\\advent-of-code-2019\\Java\\res\\day14");

		List<String> strings = null;
		try (Stream<String> lines = Files.lines(path)) {
			strings = lines.collect(Collectors.toList());
		} catch (IOException ex) {
			// do something or re-throw...
		}

		List<Wrapper> res = new ArrayList<>();
		List<Reaction> reactions = new ArrayList<>();
		for (String s : strings) {
			String[] forParce = s.split(" => ");
			Reaction e = new Reaction();
			e.setOut(new Wrapper(getElemArray(forParce[1])));
			e.setIn(Arrays.stream(forParce[0].split(", ")).map(s1 -> new Wrapper(getElemArray(s1)))
					.collect(Collectors.toList()));
			reactions.add(e);
		}

		MOLOTILKA molotilka = new MOLOTILKA(reactions);
		molotilka.recFinder(FUEL, 1);

		System.out.println("Total en: " + molotilka.res());

	}

	private static String[] getElemArray(String s) {
		return s.split(" ");
	}

	static class MOLOTILKA {
		private List<Wrapper> allRes = new ArrayList<>();
		private List<Reaction> reactions;

		public MOLOTILKA(List<Reaction> reactions) {
			this.reactions = reactions;
		}

		public void recFinder(String element, int count) {

			Reaction outReact = reactions.stream().filter(reaction -> reaction.getOut().getName().equals(element))
					.findAny().orElseThrow();

			int calcCount =
					outReact.getOut().getCount() >= count ? 1:
							(int) Math.ceil((double) count /(double) outReact.getOut().getCount());

			for (Wrapper outWrapper : outReact.getIn()) {
				if (!outWrapper.getName().equals(ORE)) {
					Wrapper findedWrapperInAllList = allRes.stream()
							.filter(wrapper2 -> wrapper2.getName().equals(outWrapper.getName())).findAny().orElse(null);
					if (findedWrapperInAllList == null) {
						allRes.add(new Wrapper(outWrapper.getName(), outWrapper.getCount() * calcCount));
					} else {
						findedWrapperInAllList.addToCount(outWrapper.getCount() * calcCount);
					}

					recFinder(outWrapper.getName(), outWrapper.getCount() * calcCount);
				}
			}

		}

		public int res() {
			List<Reaction> oreReact = reactions.stream().filter(reaction -> reaction.getIn().stream()
					.anyMatch(wrapper -> wrapper.getName().equals(ORE)))
					.collect(Collectors.toList());

			long res = 0;

			for (Reaction reaction : oreReact) {
				Wrapper wrapper = allRes.stream()
						.filter(wrapper1 -> wrapper1.getName().equals(reaction.getOut().getName())).findAny()
						.orElseThrow();

				res += (wrapper.getCount() <= reaction.getOut().getCount() ? 1 :
						(int) Math.ceil((double) wrapper.getCount() / (double) reaction.getOut().getCount())) * reaction.getIn().get(0).getCount();
			}

			return Math.toIntExact(res);

		}
	}

	static class Reaction {
		private Wrapper out;
		private List<Wrapper> in = new ArrayList<>();

		public Wrapper getOut() {
			return out;
		}

		public void setOut(Wrapper out) {
			this.out = out;
		}

		public List<Wrapper> getIn() {
			return in;
		}

		public void setIn(List<Wrapper> in) {
			this.in = in;
		}
	}

	static class Wrapper {
		private String name;
		private int count;

		public Wrapper(String[] list) {
			this.name = list[1];
			this.count = Integer.parseInt(list[0]);
		}

		public Wrapper(String name, int count) {
			this.name = name;
			this.count = count;
		}

		public String getName() {
			return name;
		}

		public int getCount() {
			return count;
		}

		public void addToCount(int i) {
			count += i;
		}
	}
}
