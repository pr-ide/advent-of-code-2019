import sys
from Komputar import Komputar


def solve():
    _input = list(map(int, sys.stdin.readline().split(',')))
    # part 1
    komp = Komputar(_input.copy())
    komp.stdin.append(1)
    komp.execute()
    print('part 1 ', komp.stdout.pop())
    # part 2
    komp = Komputar(_input.copy())
    komp.stdin.append(5)
    komp.execute()
    print('part 2 ', komp.stdout.pop())


if __name__ == '__main__':
    solve()
