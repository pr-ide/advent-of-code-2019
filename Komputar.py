from collections import deque


class Komputar:
    def __init__(self, _input):
        self.memory_size = 10 ** 5
        self.memory = _input + [0 for _ in range(self.memory_size - len(_input))]
        self.ptr = 0
        self.relative_base = 0
        self.opcodes = {
            1: self.add,
            2: self.multiply,
            3: self.cin,
            4: self.cout,
            5: self.jump_if_true,
            6: self.jump_if_false,
            7: self.less_than,
            8: self.equals,
            9: self.relative_base_offset,
            99: self.halt_now,
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
        elif mode == 2:
            return self.memory[self.relative_base + self.memory[index]]

    def write_memory(self, index, val, mode):
        if mode == 0:
            self.memory[self.memory[index]] = val
        elif mode == 2:
            self.memory[self.relative_base + self.memory[index]] = val

    def add(self, modes):
        a = self.read_memory(self.ptr + 1, modes[0])
        b = self.read_memory(self.ptr + 2, modes[1])
        self.write_memory(self.ptr + 3, a + b, modes[2])
        self.ptr += 4
        return 0

    def multiply(self, modes):
        a = self.read_memory(self.ptr + 1, modes[0])
        b = self.read_memory(self.ptr + 2, modes[1])
        self.write_memory(self.ptr + 3, a * b, modes[2])
        self.ptr += 4
        return 0

    def cin(self, modes):
        if self.stdin:
            value = self.stdin.popleft()
            self.write_memory(self.ptr + 1, value, modes[0])
            self.ptr += 2
            return 0
        else:
            return self.PAUSE

    def cout(self, modes):
        value = self.read_memory(self.ptr + 1, modes[0])
        self.stdout.append(value)
        self.ptr += 2
        return 0

    def jump_if_true(self, modes):
        if self.read_memory(self.ptr + 1, modes[0]):
            self.ptr = self.read_memory(self.ptr + 2, modes[1])
        else:
            self.ptr += 3
        return 0

    def jump_if_false(self, modes):
        if self.read_memory(self.ptr + 1, modes[0]) == 0:
            self.ptr = self.read_memory(self.ptr + 2, modes[1])
        else:
            self.ptr += 3
        return 0

    def less_than(self, modes):
        if self.read_memory(self.ptr + 1, modes[0]) < self.read_memory(self.ptr + 2, modes[1]):
            self.write_memory(self.ptr + 3, 1, modes[2])
        else:
            self.write_memory(self.ptr + 3, 0, modes[2])
        self.ptr += 4
        return 0

    def equals(self, modes):
        if self.read_memory(self.ptr + 1, modes[0]) == self.read_memory(self.ptr + 2, modes[1]):
            self.write_memory(self.ptr + 3, 1, modes[2])
        else:
            self.write_memory(self.ptr + 3, 0, modes[2])
        self.ptr += 4
        return 0
    
    def relative_base_offset(self, modes):
        self.relative_base += self.read_memory(self.ptr + 1, modes[0])
        self.ptr += 2
        return 0
    
    def halt_now(self, modes):
        return self.HALT

    def execute(self):
        while True:
            modes, opcode = self.parse_instruction(self.memory[self.ptr])
            return_code = self.opcodes[opcode](modes)
            if return_code:
                return return_code