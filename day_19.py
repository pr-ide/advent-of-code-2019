import sys
from Komputar import Komputar
from tqdm import tqdm
from functools import lru_cache

def solve():
    _input = list(map(int, sys.stdin.readline().split(',')))

    def deploy_drone(i, j):
        komp = Komputar(_input.copy())
        komp.stdin.append(i)
        komp.stdin.append(j)
        komp.execute()
        return komp.stdout.popleft()

    def part_1():
        affected_area = 0
        for i in tqdm(range(50)):
            for j in range(50):
                affected_area += deploy_drone(i, j)
        return affected_area
    
    def part_2():

        def linear_search(start_i, stop_i):
            j = 1
            for i in tqdm(range(start_i, stop_i)):
                while deploy_drone(i, j - 1):
                    j -= 1
                while not deploy_drone(i, j):
                    j += 1
                if deploy_drone(i - 99, j) and deploy_drone(i, j + 99) and deploy_drone(i - 99, j + 99):
                    return (i - 99, j)
        
        i, j = linear_search(500, 1500)
        return i * 10000 + j

    print('part 1:', part_1())
    print('part 2:', part_2())


if __name__ == '__main__':
    solve()
