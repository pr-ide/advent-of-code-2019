from typing import List, Optional, Tuple
import operator


def get_digit(number: int, place: int) -> int:
    return number // 10**place % 10


def get_opcode(instruction: int) -> int:
    return instruction % 100


def parse_instruction(instruction: int) -> Tuple:
    opcode = get_opcode(instruction)
    if opcode in (1, 2, 7, 8):
        (
            first_parameter_mode,
            second_parameter_mode,
            third_parameter_mode,
        ) = [
            get_digit(instruction, place)
            for place in range(2, 5)
        ]
        return (
            opcode,
            first_parameter_mode,
            second_parameter_mode,
            third_parameter_mode,
        )
    if opcode in (3, 4):
        first_parameter_mode = get_digit(instruction, 2)
        return (
            opcode,
            first_parameter_mode,
        )
    if opcode in (5, 6):
        first_parameter_mode = get_digit(instruction, 2)
        second_parameter_mode = get_digit(instruction, 3)
        return (
            opcode,
            first_parameter_mode,
            second_parameter_mode,
        )
    if opcode == 99:
        return (0,)


def get_step(opcode: int) -> int:
    step_map = {
        1: 4,
        2: 4,
        3: 2,
        4: 2,
        5: 3,
        6: 3,
        7: 4,
        8: 4,
        99: 0,
    }
    return step_map[opcode%100]


def execute_instruction(input: int, instruction_pointer: int, memory: List) -> int:
    parsed_instruction = parse_instruction(memory[instruction_pointer])
    opcode = parsed_instruction[0]
    if len(parsed_instruction) == 1:  # 99
        return -1
    if len(parsed_instruction) == 4:
        step = get_step(opcode)
        instruction = memory[instruction_pointer: instruction_pointer+step]
        first_parameter, second_parameter = [
            instruction[i] if parsed_instruction[i] else memory[instruction[i]]
            for i in range(1, 3)
        ]
        if opcode == 1:  # add
            memory[instruction[3]] = first_parameter + second_parameter
        if opcode == 2:  # mul
            memory[instruction[3]] = first_parameter * second_parameter
        if opcode == 7:  # less than
            if first_parameter < second_parameter:
                memory[instruction[3]] = 1
            else:
                memory[instruction[3]] = 0
        if opcode == 8:  # equals
            if first_parameter == second_parameter:
                memory[instruction[3]] = 1
            else:
                memory[instruction[3]] = 0
        return instruction_pointer + step
    if len(parsed_instruction) == 3:
        step = get_step(opcode)
        instruction = memory[instruction_pointer: instruction_pointer+step]
        first_parameter, second_parameter = [
            instruction[i] if parsed_instruction[i] else memory[instruction[i]]
            for i in range(1, 3)
        ]
        if opcode == 5:  # jump if true
            return second_parameter if first_parameter else instruction_pointer + step
        if opcode == 6:  # jump if false
            return instruction_pointer + step if first_parameter else second_parameter
    if len(parsed_instruction) == 2:
        step = get_step(opcode)
        instruction = memory[instruction_pointer: instruction_pointer+step]
        first_parameter = instruction[1] if parsed_instruction[1] else memory[instruction[1]]
        if opcode == 4:  # output
            print(first_parameter)
        if opcode == 3:  # input
            memory[instruction[1]] = input
        return instruction_pointer + step



def parse_memory(input: int, memory: List) -> None:
    instruction_pointer = 0
    step = get_step(memory[instruction_pointer])
    while instruction_pointer != -1:
        instruction_pointer = execute_instruction(input, instruction_pointer, memory)


def main():
    memory = [
        3,225,1,225,6,6,1100,1,238,225,104,0,1101,90,
        60,224,1001,224,-150,224,4,224,1002,223,8,223,
        1001,224,7,224,1,224,223,223,1,57,83,224,1001,
        224,-99,224,4,224,1002,223,8,223,1001,224,5,224,
        1,223,224,223,1102,92,88,225,101,41,187,224,1001,
        224,-82,224,4,224,1002,223,8,223,101,7,224,224,1,
        224,223,223,1101,7,20,225,1101,82,64,225,1002,183,
        42,224,101,-1554,224,224,4,224,102,8,223,223,1001,
        224,1,224,1,224,223,223,1102,70,30,224,101,-2100,
        224,224,4,224,102,8,223,223,101,1,224,224,1,224,
        223,223,2,87,214,224,1001,224,-2460,224,4,224,1002,
        223,8,223,101,7,224,224,1,223,224,223,102,36,180,
        224,1001,224,-1368,224,4,224,1002,223,8,223,1001,
        224,5,224,1,223,224,223,1102,50,38,225,1102,37,14,
        225,1101,41,20,225,1001,217,7,224,101,-25,224,224,
        4,224,1002,223,8,223,101,2,224,224,1,224,223,223,
        1101,7,30,225,1102,18,16,225,4,223,99,0,0,0,677,
        0,0,0,0,0,0,0,0,0,0,0,1105,0,99999,1105,227,247,
        1105,1,99999,1005,227,99999,1005,0,256,1105,1,
        99999,1106,227,99999,1106,0,265,1105,1,99999,
        1006,0,99999,1006,227,274,1105,1,99999,1105,1,
        280,1105,1,99999,1,225,225,225,1101,294,0,0,
        105,1,0,1105,1,99999,1106,0,300,1105,1,99999,1,
        225,225,225,1101,314,0,0,106,0,0,1105,1,99999,7,
        226,226,224,102,2,223,223,1006,224,329,101,1,223,
        223,1107,677,226,224,102,2,223,223,1006,224,344,
        1001,223,1,223,8,677,226,224,1002,223,2,223,1005,
        224,359,101,1,223,223,107,677,677,224,1002,223,2,
        223,1006,224,374,101,1,223,223,7,677,226,224,1002,
        223,2,223,1006,224,389,101,1,223,223,108,677,226,
        224,1002,223,2,223,1005,224,404,101,1,223,223,1108,
        677,226,224,102,2,223,223,1005,224,419,101,1,223,
        223,8,226,677,224,102,2,223,223,1006,224,434,1001,
        223,1,223,1008,677,677,224,1002,223,2,223,1005,224,
        449,1001,223,1,223,1107,226,677,224,102,2,223,223,
        1006,224,464,101,1,223,223,107,226,677,224,1002,223,
        2,223,1006,224,479,1001,223,1,223,7,226,677,224,102,
        2,223,223,1005,224,494,1001,223,1,223,8,677,677,224,
        102,2,223,223,1006,224,509,1001,223,1,223,1108,677,
        677,224,102,2,223,223,1005,224,524,1001,223,1,223,
        1108,226,677,224,1002,223,2,223,1005,224,539,101,1,
        223,223,107,226,226,224,102,2,223,223,1006,224,554,
        1001,223,1,223,1007,226,226,224,102,2,223,223,1005,
        224,569,1001,223,1,223,1008,226,226,224,102,2,223,
        223,1005,224,584,101,1,223,223,1007,677,677,224,1002,
        223,2,223,1005,224,599,1001,223,1,223,108,677,677,
        224,1002,223,2,223,1006,224,614,1001,223,1,223,1007,
        226,677,224,1002,223,2,223,1006,224,629,101,1,223,
        223,1008,677,226,224,102,2,223,223,1005,224,644,101,
        1,223,223,1107,226,226,224,1002,223,2,223,1005,224,
        659,1001,223,1,223,108,226,226,224,1002,223,2,223,
        1005,224,674,101,1,223,223,4,223,99,226
    ]
    test = [
        3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,
        1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,
        999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99
    ]
    parse_memory(5, memory)


if __name__ == '__main__':
    main()
