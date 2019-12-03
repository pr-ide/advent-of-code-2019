from typing import List, Optional
import operator


def calculate(command: List, memory: List) -> bool:
    opcode_map = {
        1: operator.add,
        2: operator.mul,
        99: False
    }
    func = opcode_map[command[0]]
    if func:
        memory[command[3]] = func(memory[command[1]], memory[command[2]])
        return True
    else:
        return False


def parse_ints(ints: List) -> None:
    for i in range(0, len(ints), 4):
        if not calculate(ints[i:i+4], ints):
            return


def init(memory: List, noun: int, verb: int) -> None:
    memory[1], memory[2] = noun, verb


def brute(memory: List) -> Optional[(int, int)]:
    for i in range(99):
        for e in range(99):
            temp = memory.copy()
            init(temp, i, e)
            parse_ints(temp)
            if temp[0] == 19690720:
                return i, e


def main():
    memory = [1, 0, 0, 3, 1, 1, 2, 3, 1, 3, 4, 3, 1, 5, 0, 3, 2, 13, 1, 19, 1,
              9, 19, 23, 2, 23, 13, 27, 1, 27, 9, 31, 2, 31, 6, 35, 1, 5, 35, 39,
              1, 10, 39, 43, 2, 43, 6, 47, 1, 10, 47, 51, 2, 6, 51, 55, 1, 5, 55,
              9, 1, 59, 9, 63, 1, 13, 63, 67, 2, 6, 67, 71, 1, 5, 71, 75, 2, 6,
              75, 79, 2, 79, 6, 83, 1, 13, 83, 87, 1, 9, 87, 91, 1, 9, 91, 95, 1,
              5, 95, 99, 1, 5, 99, 103, 2, 13, 103, 107, 1, 6, 107, 111, 1, 9,
              111, 115, 2, 6, 115, 119, 1, 13, 119, 123, 1, 123, 6, 127, 1,
              127, 5, 131, 2, 10, 131, 135, 2, 135, 10, 139, 1, 13, 139, 143,
              1, 10, 143, 147, 1, 2, 147, 151, 1, 6, 151, 0, 99, 2, 14, 0, 0]
    noun, verb = brute(memory)
    print(noun, verb)
    print(100*noun+verb)


if __name__ == '__main__':
    main()
