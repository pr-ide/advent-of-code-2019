package ru.ural.day5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author RandomPie
 * КОГДА НИБУДЬ Я ЭТО ПЕРЕПЕШУ КРАСИВО
 */
public class Day5Computer {

	private List<Long> numberList;

	public Day5Computer(String s) {
		numberList = Arrays.stream(
				s.split(",")
		).map(Long::parseLong).collect(Collectors.toList());
	}

	public String compile(Long input) {

		List<Long> out = new ArrayList<>();
		for (int i = 0; i < numberList.size(); i++) {

			Long a = numberList.get(i);
			long opt = a % 100;
			List<Long> map = new ArrayList<>();
			for (int m = 2; m < 5; m++) {
				map.add((a / ((long) Math.pow(10, m)) % 10));
			}
			if (opt == 1) {
				numberList.set(
						Math.toIntExact(getaLong(numberList, 1L, i + 3)),
						getaLong(numberList, map.get(0), i + 1) +
								getaLong(numberList, map.get(1), i + 2));
				i = i + map.size();
			} else if (opt == 2) {
				numberList.set(
						Math.toIntExact(getaLong(numberList, 1L, i + 3)),
						getaLong(numberList, map.get(0), i + 1)
								*
								getaLong(numberList, map.get(1), i + 2));
				i = i + map.size();
			} else if (opt == 3) {
				numberList.set(Math.toIntExact(numberList.get(i + 1)), input);
				i += 1;
			} else if (opt == 4) {
				out.add(getaLong(numberList, map.get(0), i + 1));
				i += 1;
			} else if (opt == 5) {
				if (getaLong(numberList, map.get(0), i + 1) != 0) {

					i = Math.toIntExact(getaLong(numberList, map.get(1), i + 2)) - 1;
					continue;
				}
				i += 2;
			} else if (opt == 6) {
				if (getaLong(numberList, map.get(0), i + 1) == 0) {
					i = Math.toIntExact(getaLong(numberList, map.get(1), i + 2)) - 1;
					continue;
				}
				i += 2;
			} else if (opt == 8) {
				numberList.set(
						Math.toIntExact(getaLong(numberList, 1L, i + 3)),
						Objects.equals(getaLong(numberList, map.get(0), i + 1), getaLong(numberList, map.get(1), i + 2)) ?
								1L :
								0L);
				i = i + 3;
			} else if (opt == 7) {
				numberList.set(
						Math.toIntExact(getaLong(numberList, 1L, i + 3)),
						getaLong(numberList, map.get(0), i + 1) < getaLong(numberList, map.get(1), i + 2) ? 1L : 0L);
				i = i + 3;
			} else if (opt == 99) {
				break;
			}

		}
		return out.stream().map(Object::toString).collect(Collectors.joining(","));
	}

	private static Long getaLong(List<Long> longs, Long aLong, int i) {
		if (aLong == null || aLong == 0) {
			return longs.get(Math.toIntExact(longs.get(i)));
		} else {
			return longs.get(i);
		}
	}

}
