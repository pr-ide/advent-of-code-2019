from typing import List
from multiprocessing import Pipe
from itertools import permutations


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


def execute_instruction(inputs: List, instruction_pointer: int, memory: List) -> int:
    parsed_instruction = parse_instruction(memory[instruction_pointer])
    opcode = parsed_instruction[0]
    if len(parsed_instruction) == 1:  # 99
        return -1, None
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
        return instruction_pointer + step, None
    if len(parsed_instruction) == 3:
        step = get_step(opcode)
        instruction = memory[instruction_pointer: instruction_pointer+step]
        first_parameter, second_parameter = [
            instruction[i] if parsed_instruction[i] else memory[instruction[i]]
            for i in range(1, 3)
        ]
        if opcode == 5:  # jump if true
            return (second_parameter, None) if first_parameter else (instruction_pointer + step, None)
        if opcode == 6:  # jump if false
            return (instruction_pointer + step, None) if first_parameter else (second_parameter, None)
    if len(parsed_instruction) == 2:
        step = get_step(opcode)
        instruction = memory[instruction_pointer: instruction_pointer+step]
        first_parameter = instruction[1] if parsed_instruction[1] else memory[instruction[1]]
        if opcode == 4:  # output
            return instruction_pointer + step, first_parameter
        if opcode == 3:  # input
            input = inputs.pop(0)
            memory[instruction[1]] = input
        return instruction_pointer + step, None



def parse_memory(inputs: List, memory: List) -> None:
    # inputs = inputs.copy()
    memory = memory.copy()
    instruction_pointer = 0
    step = get_step(memory[instruction_pointer])
    result = 0
    while instruction_pointer != -1:
        instruction_pointer, output = execute_instruction(inputs, instruction_pointer, memory)
        if output is not None:
            result = output
            yield output
    yield result


def find_higher_signal(memory: List) -> int:
    phase_settings = permutations(range(5))
    outputs = []
    for phase_setting_sequence in phase_settings:
        output = 0
        for phase_setting in phase_setting_sequence:
            inputs = [phase_setting, output]
            try:
                output = next(parse_memory(inputs, memory))
            except StopIteration:
                pass
        outputs.append(output)
    return max(outputs)


def find_higher_signal_part_2(memory: List) -> int:
    phase_settings = permutations(range(5, 10))
    outputs = []
    for phase_setting_sequence in phase_settings:
        inputs_0 = [phase_setting_sequence[0], 0]
        inputs_1 = [phase_setting_sequence[1]]
        inputs_2 = [phase_setting_sequence[2]]
        inputs_3 = [phase_setting_sequence[3]]
        inputs_4 = [phase_setting_sequence[4]]
        output_generator_0 = parse_memory(inputs_0, memory)
        output_generator_1 = parse_memory(inputs_1, memory)
        output_generator_2 = parse_memory(inputs_2, memory)
        output_generator_3 = parse_memory(inputs_3, memory)
        output_generator_4 = parse_memory(inputs_4, memory)
        while True:
            try:
                output = next(output_generator_0)
                inputs_1.append(output)
            except StopIteration:
                pass
            try:
                output = next(output_generator_1)
                inputs_2.append(output)
            except StopIteration:
                pass
            try:
                output = next(output_generator_2)
                inputs_3.append(output)
            except StopIteration:
                pass
            try:
                output = next(output_generator_3)
                inputs_4.append(output)
            except StopIteration:
                pass
            try:
                output = next(output_generator_4)
                inputs_0.append(output)
            except StopIteration:
                outputs.append(output)
                break
    return max(outputs)


def test_find(memory: List) -> int:
    inputs = [6, 0]
    output = parse_memory(inputs, memory)
    print(inputs, next(output))
    print(inputs)
    print(next(output))


def main():
    memory = [3,8,1001,8,10,8,105,1,0,0,21,38,59,76,89,106,187,268,349,430,99999,3,9,1002,9,3,9,101,2,9,9,1002,9,4,9,4,9,99,3,9,1001,9,5,9,1002,9,5,9,1001,9,2,9,1002,9,3,9,4,9,99,3,9,1001,9,4,9,102,4,9,9,1001,9,3,9,4,9,99,3,9,101,4,9,9,1002,9,5,9,4,9,99,3,9,1002,9,3,9,101,5,9,9,1002,9,3,9,4,9,99,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,2,9,4,9,99,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,101,1,9,9,4,9,99,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,102,2,9,9,4,9,99]
    test_memory = [3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10]
    # result = find_higher_signal(memory)
    result = find_higher_signal_part_2(memory)
    print(result)



if __name__ == '__main__':
    main()
