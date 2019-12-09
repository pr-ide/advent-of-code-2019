package ru.ural;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author RandomPie
 */
public class Day4 {
	public static void main(String... arg) {
		int count = 0;
		for (int i = 134564; i < 585159; i++) {


			List<Integer> integers = Arrays.stream(String.valueOf(i).split(""))
					.map(Integer::parseInt).collect(
							Collectors.toList());

			boolean isDouble = false;
			boolean isOk = true;
			for (int z = 1; z < integers.size(); z++) {
				if (integers.get(z) < integers.get(z - 1)) {
					isOk = false;
					break;
				}

				if (integers.get(z).equals(integers.get(z - 1))  && !isMoreDouble(integers, z)) {
					isDouble = true;
				}
			}

			if (isOk && isDouble) {
				count++;
			}
		}
		System.out.println(count);
	}

	private static boolean isMoreDouble(List<Integer> integers, int i) {
		boolean left = false;
		boolean right = false;
		try {
			if (integers.get(i - 1).equals(integers.get(i - 2))) {
				left = true;
			}
		} catch (IndexOutOfBoundsException ignored) {
		}
		try {
			if (integers.get(i).equals(integers.get(i + 1))) {
				right = true;
			}
		} catch (IndexOutOfBoundsException ignored) {
		}
		return left || right;
	}
}
