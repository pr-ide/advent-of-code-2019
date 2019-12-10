//package ru.ural;
//
//import ru.ural.day5.Day5Computer;
//
//import java.io.IOException;
//import java.lang.reflect.Array;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public class Day7 {
//
//	public static void main(String... arg) {
//
//		Path path = Paths.get("C:\\GitRepos\\Heroku\\advent-of-code-2019\\Java\\res\\day72");
//		String res = null;
//		try (Stream<String> lines = Files.lines(path)) {
//			res = lines.collect(Collectors.joining());
//		} catch (IOException ex) {
//			// do something or re-throw...
//		}
//
//		assert res != null;
//		ProseedDay5Part2 proseedDay5 = new ProseedDay5Part2(res);
//
//		//        System.out.println(proseedDay5.getCalc());
//	}
//
//}
//
//class ProseedDay5 {
//	Day5Computer day5Computer;
//
//	public ProseedDay5(Day5Computer day5Computer) {
//		this.day5Computer = day5Computer;
//	}
//
//	public long getCalc() {
//
//		List<Long> longList = new ArrayList<>() {{
//			add(5L);
//			add(6L);
//			add(7L);
//			add(8L);
//			add(9L);
//		}};
//
//		return longList.stream()
//				.map(aLong ->
//						recCalc(aLong, 0L, longList))
//				.max(Long::compareTo).get();
//
//	}
//
//	private Long recCalc(Long input, Long lastCal, List<Long> restInputs) {
//
//		day5Computer.compile(
//				new ArrayList<Long>() {{
//					add(input);
//					add(lastCal);
//				}});
//
//		Long lastCal1 = day5Computer.getOut().get(0);
//		day5Computer.resetComp();
//
//		final List<Long> localRestInputs = new ArrayList<>(restInputs.stream()
//				.filter(aLong1 -> !aLong1.equals(input))
//				.collect(Collectors.toList()));
//
//		if (localRestInputs.size() == 0) {
//			return lastCal1;
//		}
//
//		List<Long> longStream = localRestInputs.stream()
//				.map(aLong -> {
//					return recCalc(aLong,
//							lastCal1,
//							localRestInputs);
//				}).collect(Collectors.toList());
//		return longStream.stream()
//				.max(Long::compareTo).get();
//	}
//
//	private Long recCalcSecPart(Long input, List<Long> lastCal, List<Long> restInputs) {
//		day5Computer.compile(lastCal);
//		List<Long> lastCal1 = day5Computer.getOut();
//
//		final List<Long> localRestInputs = new ArrayList<>(restInputs.stream()
//				.filter(aLong1 -> !aLong1.equals(input))
//				.collect(Collectors.toList()));
//
//		if (localRestInputs.size() == 0) {
//			return lastCal1.get(0);
//		}
//
//		List<Long> longStream = localRestInputs.stream()
//				.map(aLong -> {
//					return recCalcSecPart(aLong,
//							lastCal1,
//							localRestInputs);
//				}).collect(Collectors.toList());
//		return longStream.stream()
//				.max(Long::compareTo).get();
//	}
//}
//
//class ProseedDay5Part2 {
//	List<Day5Computer> day5Computers = new ArrayList<>();
//	List<List<Long>> allPosibleInputs = new ArrayList<>();
//	String code;
//
//	public ProseedDay5Part2(String s) {
//		code = s;
//		this.day5Computers = new ArrayList<>() {{
//			add(new Day5Computer(s));
//			add(new Day5Computer(s));
//			add(new Day5Computer(s));
//			add(new Day5Computer(s));
//			add(new Day5Computer(s));
//		}};
//
//		allPosibleInputs = getAllPosibleInputs();
//	}
//
//	public long getCalc() {
//
//		this.day5Computers = new ArrayList<>() {{
//			add(new Day5Computer(code));
//			add(new Day5Computer(code));
//			add(new Day5Computer(code));
//			add(new Day5Computer(code));
//			add(new Day5Computer(code));
//		}};
//
//		for (List<Long> codes: allPosibleInputs) {
//			for(Long code: codes) {
//				day5Computers.get(codes.indexOf(code)).compile();
//			}
//		}
//
//		return 0;
//	}
//
//	private List<List<Long>> getAllPosibleInputs() {
//		List<Long> longList = new ArrayList<>() {{
//			add(5L);
//			add(6L);
//			add(7L);
//			add(8L);
//			add(9L);
//		}};
//
//		List<List<Long>> res = new ArrayList<>();
//		longList.forEach(aLong -> res.addAll(recGetAllPosibleInputs(aLong, longList, new ArrayList<>())));
//		return res;
//	}
//
//	private List<List<Long>> recGetAllPosibleInputs(Long i, List<Long> restI, List<List<Long>> buff) {
//
//		buff = new ArrayList<>(buff);
//		if (buff.size() == 0) {
//			buff.add(new ArrayList<>());
//		}
//
//		buff.forEach(longs1 -> longs1.add(i));
//
//		final List<Long> localRestInputs = new ArrayList<>(restI.stream()
//				.filter(aLong1 -> !aLong1.equals(i))
//				.collect(Collectors.toList()));
//		List<List<Long>> res = new ArrayList<>();
//
//		if (localRestInputs.size() == 0) {
//			res.add(new ArrayList<>());
//			res.get(0).add(i);
//			return res;
//		}
//
//		for (Long aLong : localRestInputs) {
//			List<List<Long>> lists = recGetAllPosibleInputs(aLong, localRestInputs, buff);
//
//			lists.forEach(longs -> {
//				res.add(new ArrayList<>());
//				res.get(res.size() - 1).add(i);
//				res.get(res.size() - 1).addAll(longs);
//			});
//
//		}
//
//		return res;
//	}
//
//	//    private Long recCalcSecPart(Long input, List<Long> restInputs) {
//	//        day5Computer.compile(lastCal);
//	//        List<Long> lastCal1 = day5Computer.getOut();
//	//
//	//        final List<Long> localRestInputs = new ArrayList<>(restInputs.stream()
//	//                .filter(aLong1 -> !aLong1.equals(input))
//	//                .collect(Collectors.toList()));
//	//
//	//        if (localRestInputs.size() == 0) {
//	//            return lastCal1.get(0);
//	//        }
//	//
//	//        List<Long> longStream = localRestInputs.stream()
//	//                .map(aLong -> {
//	//                    return recCalcSecPart(aLong,
//	//                            lastCal1,
//	//                            localRestInputs);
//	//                }).collect(Collectors.toList());
//	//        return longStream.stream()
//	//                .max(Long::compareTo).get();
//	//    }
//}
