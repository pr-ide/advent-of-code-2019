import sys
from itertools import permutations, cycle
from Komputar import Komputar


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
