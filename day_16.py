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

def solve():
    original_input = list(map(int, tuple(sys.stdin.read())))
    sequence = numpy.array(original_input)
    n = len(sequence)
    for _ in tqdm(range(100)):
        sequence = fft(sequence, n)
    print('part 1: ', ''.join(map(str, sequence[:8])))
    

if __name__ == '__main__':
    solve()
