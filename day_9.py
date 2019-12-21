import sys
from Komputar import Komputar


def solve():
    _input = list(map(int, sys.stdin.readline().split(',')))

    def part_1():
        komp = Komputar(_input.copy())
        komp.stdin.append(1)
        komp.execute()
        return komp.stdout.popleft()

    def part_2():
        komp = Komputar(_input.copy())
        komp.stdin.append(2)
        komp.execute()
        return komp.stdout.popleft()
    
    print('part 1', part_1())
    print('part 2', part_2())


if __name__ == '__main__':
    solve()
