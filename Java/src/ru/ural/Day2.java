package ru.ural;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author RandomPie
 */
public class Day2 {

	public static void main(String... arg) {
		Long aLong = -1L;
		for (long x = 0; x < 100; x++) {
			for (long y = 0; y < 100; y++) {
				aLong = getLongs(("1," + x + "," + y + ",3,1,1,2,3,1,3,4,3,1,5,0,3,2,10,1,19,1,19,9"
						+ ",23,1,23,13,27,1,10,27,31,2,31,13,35,1,10,35,39,2,9,39,43,2,43,9,4"
						+ "7,1,6,47,51,1,10,51,55,2,55,13,59,1,59,10,63,2,63,13,67,2,67,9,71,1,6,71,75,2,75,9,79,1,79,5,83,2,83,13,87,1,9,87,91,1,13,91,95,1,2,95,99,1,99,6,0,99,2,14,0,0")
						.split(","));
				if (aLong == 19690720L) {
					System.out.println((x * 100) + y);
				}
			}
		}
	}

	private static Long getLongs(String[] strings) {
		List<Long> longs = Arrays.stream(
				strings
				//				"2,4,4,5,99,0"
		).map(Long::parseLong).collect(Collectors.toList());

		for (int i = 0; i < longs.size(); i++) {
			Long a = longs.get(i);
			if (a == 1) {
				longs.set(Math.toIntExact(longs.get(i + 3)), longs.get(Math.toIntExact(longs.get(i + 1))) + longs.get(
						Math.toIntExact(longs.get(i + 2))));
				i += 3;
			} else if (a == 2) {
				longs.set(Math.toIntExact(longs.get(i + 3)), longs.get(Math.toIntExact(longs.get(i + 1))) * longs.get(
						Math.toIntExact(longs.get(i + 2))));
				i += 3;
			} else if (a == 99) {
				break;
			}
		}
		return longs.get(0);
	}
}
