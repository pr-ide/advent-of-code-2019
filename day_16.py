import sys
from tqdm import tqdm
from itertools import cycle
from functools import lru_cache
import numpy


@lru_cache(maxsize=None)
def pattern(pos, iters):
    pattern = []
    for item in cycle((0, 1, 0, -1)):
        for _ in range(pos + 1):
            pattern.append(item)
            iters -= 1
            if iters == -1:
                return numpy.array(pattern[1:])

def fft(sequence, n):
    return numpy.array([abs(numpy.dot(sequence, pattern(i, n))) % 10 for i in range(n)])

def fft_second_half(sequence, n):
    for i in range(n - 2, -1, -1):
        sequence[i] += sequence[i + 1]
    return sequence % 10

def solve():
    original_input = list(map(int, tuple(sys.stdin.read())))
    # part 1
    sequence = numpy.array(original_input)
    n = len(sequence)
    for _ in tqdm(range(100)):
        sequence = fft(sequence, n)
    print('part 1: ', ''.join(map(str, sequence[:8])))
    # part 2
    offset = int(''.join(map(str, original_input[:7])))
    sequence = numpy.tile(numpy.array(original_input), 10000)[offset:]
    n = len(sequence)
    for _ in tqdm(range(100)):
        sequence = fft_second_half(sequence, n)
    print('part 2: ', ''.join(map(str, sequence[:8])))



if __name__ == '__main__':
    solve()
