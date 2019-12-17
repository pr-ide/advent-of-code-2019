import sys
from itertools import permutations, cycle
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
    
    @property
    def HALT(self):
        return 1

    @property
    def PAUSE(self):
        return 2

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
        if self.stdin:
            value = self.stdin.popleft()
            self.write_memory(self.ptr + 1, value)
            self.ptr += 2
            return 0
        else:
            return self.PAUSE

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
        return self.HALT

    def execute(self):
        while True:
            modes, opcode = self.parse_instruction(self.memory[self.ptr])
            return_code = self.opcodes[opcode](modes)
            if return_code:
                return return_code


def solve():
    _input = list(map(int, sys.stdin.readline().split(',')))

    def part_1():
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
        return max_output_signal

    def part_2():
        max_output_signal = 0
        n_amplifiers = 5
        last_amplifier = n_amplifiers - 1
        for p in permutations((5, 6, 7, 8, 9)):
            komps = [Komputar(_input.copy()) for _ in range(n_amplifiers)]
            for i, settings in enumerate(p):
                komps[i].stdin.append(settings)
            output_signal = 0
            for i in cycle(range(n_amplifiers)):
                komps[i].stdin.append(output_signal)
                return_code = komps[i].execute()
                output_signal = komps[i].stdout.popleft()
                if i == last_amplifier and return_code == komps[i].HALT:
                    break
            max_output_signal = max(max_output_signal, output_signal)
        return max_output_signal
    
    print('part 1', part_1())
    print('part 2', part_2())


if __name__ == '__main__':
    solve()
