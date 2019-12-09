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

    enum Status {
        WAIT_INPUT, IN_PROGRESS, END;
    }

    private List<Long> localMemory;
    private List<Long> orginalMemory;


    private List<Long> out = new ArrayList<>();
    private List<Long> input = new ArrayList<>();
    private int i = 0;
    private Status status = null;

    public Day5Computer(String s) {
        orginalMemory = Arrays.stream(
                s.split(",")
        ).map(Long::parseLong).collect(Collectors.toList());
        resetComp();
    }

    public void resetComp() {
        i = 0;
        localMemory = new ArrayList<>(orginalMemory);
        out.clear();
        input.clear();
        status = Status.IN_PROGRESS;
    }

    public List<Long> getOut() {
        return out;
    }

    public Status getStatus() {
        return status;
    }

    public void compile(List<Long> input) {

        this.input.addAll(input);

        int inputI = 0;
        List<Long> out = new ArrayList<>();
        while (status.equals(Status.IN_PROGRESS)) {
            status = makeStep();
        }
    }


    private Status makeStep() {

        Long a = localMemory.get(i);
        long opt = a % 100;
        List<Long> map = new ArrayList<>();
        for (int m = 2; m < 5; m++) {
            map.add((a / ((long) Math.pow(10, m)) % 10));
        }
        if (opt == 1) {
            localMemory.set(
                    Math.toIntExact(getaLong(localMemory, 1L, i + 3)),
                    getaLong(localMemory, map.get(0), i + 1) +
                            getaLong(localMemory, map.get(1), i + 2));
            i = i + map.size();
        } else if (opt == 2) {
            localMemory.set(
                    Math.toIntExact(getaLong(localMemory, 1L, i + 3)),
                    getaLong(localMemory, map.get(0), i + 1)
                            *
                            getaLong(localMemory, map.get(1), i + 2));
            i = i + map.size();
        } else if (opt == 3) {

            if (input.size() <= 0) {
                return Status.WAIT_INPUT;
            }

            localMemory.set(Math.toIntExact(localMemory.get(i + 1)), input.get(0));
            input = input.subList(1, input.size());
            i += 1;
        } else if (opt == 4) {
            out.add(getaLong(localMemory, map.get(0), i + 1));
            i += 1;
        } else if (opt == 5) {
            if (getaLong(localMemory, map.get(0), i + 1) != 0) {

                i = Math.toIntExact(getaLong(localMemory, map.get(1), i + 2)) - 1;
                return Status.IN_PROGRESS;
            }
            i += 2;
        } else if (opt == 6) {
            if (getaLong(localMemory, map.get(0), i + 1) == 0) {
                i = Math.toIntExact(getaLong(localMemory, map.get(1), i + 2)) - 1;
                return Status.IN_PROGRESS;
            }
            i += 2;
        } else if (opt == 8) {
            localMemory.set(
                    Math.toIntExact(getaLong(localMemory, 1L, i + 3)),
                    Objects.equals(getaLong(localMemory, map.get(0), i + 1), getaLong(localMemory, map.get(1), i + 2)) ?
                            1L :
                            0L);
            i = i + 3;
        } else if (opt == 7) {
            localMemory.set(
                    Math.toIntExact(getaLong(localMemory, 1L, i + 3)),
                    getaLong(localMemory, map.get(0), i + 1) < getaLong(localMemory, map.get(1), i + 2) ? 1L : 0L);
            i = i + 3;
        } else if (opt == 99) {
            return Status.END;
        }

        i++;
        return Status.IN_PROGRESS;
    }

    private Long getaLong(List<Long> longs, Long aLong, int i) {
        if (aLong == null || aLong == 0) {
            return longs.get(Math.toIntExact(longs.get(i)));
        } else {
            return longs.get(i);
        }
    }

}
