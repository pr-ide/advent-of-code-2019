import sys
from itertools import permutations
from collections import deque


class Komputar:
    def __init__(self, _input):
        self.memory = _input
        self.ptr = 0
        self.opcodes = {
            1: self.opcode_1,
            2: self.opcode_2,
            3: self.opcode_3,
            4: self.opcode_4,
            5: self.opcode_5,
            6: self.opcode_6,
            7: self.opcode_7,
            8: self.opcode_8,
            99: self.opcode_99,
        }
        self.stdin = deque()
        self.stdout = deque()

    def parse_instruction(self, instruction):
        opcode = instruction % 100
        instruction = instruction // 10
        modes = []
        for _ in range(3):
            instruction = instruction // 10
            modes.append(instruction % 10)
        return modes, opcode

    def read_memory(self, index, mode):
        if mode == 0:
            return self.memory[self.memory[index]]
        elif mode == 1:
            return self.memory[index]

    def write_memory(self, index, val):
        self.memory[self.memory[index]] = val

    def opcode_1(self, modes):
        a = self.read_memory(self.ptr + 1, modes[0])
        b = self.read_memory(self.ptr + 2, modes[1])
        self.write_memory(self.ptr + 3, a + b)
        self.ptr += 4
        return 0

    def opcode_2(self, modes):
        a = self.read_memory(self.ptr + 1, modes[0])
        b = self.read_memory(self.ptr + 2, modes[1])
        self.write_memory(self.ptr + 3, a * b)
        self.ptr += 4
        return 0

    def opcode_3(self, modes):
        value = self.stdin.popleft()
        self.write_memory(self.ptr + 1, value)
        self.ptr += 2
        return 0

    def opcode_4(self, modes):
        value = self.read_memory(self.ptr + 1, modes[0])
        self.stdout.append(value)
        self.ptr += 2
        return 0

    def opcode_5(self, modes):
        if self.read_memory(self.ptr + 1, modes[0]):
            self.ptr = self.read_memory(self.ptr + 2, modes[1])
        else:
            self.ptr += 3
        return 0

    def opcode_6(self, modes):
        if self.read_memory(self.ptr + 1, modes[0]) == 0:
            self.ptr = self.read_memory(self.ptr + 2, modes[1])
        else:
            self.ptr += 3
        return 0

    def opcode_7(self, modes):
        if self.read_memory(self.ptr + 1, modes[0]) < self.read_memory(self.ptr + 2, modes[1]):
            self.write_memory(self.ptr + 3, 1)
        else:
            self.write_memory(self.ptr + 3, 0)
        self.ptr += 4
        return 0

    def opcode_8(self, modes):
        if self.read_memory(self.ptr + 1, modes[0]) == self.read_memory(self.ptr + 2, modes[1]):
            self.write_memory(self.ptr + 3, 1)
        else:
            self.write_memory(self.ptr + 3, 0)
        self.ptr += 4
        return 0
    
    def opcode_99(self, modes):
        return 1

    def execute(self):
        while True:
            modes, opcode = self.parse_instruction(self.memory[self.ptr])
            if self.opcodes[opcode](modes):
                break


def solve():
    _input = list(map(int, sys.stdin.readline().split(',')))
    max_output_signal = 0
    for p in permutations((0, 1, 2, 3, 4)):
        output_signal = 0
        for settings in p:
            komp = Komputar(_input.copy())
            komp.stdin.append(settings)
            komp.stdin.append(output_signal)
            komp.execute()
            output_signal = komp.stdout.popleft()
        max_output_signal = max(max_output_signal, output_signal)
    print(max_output_signal)


if __name__ == '__main__':
    solve()
