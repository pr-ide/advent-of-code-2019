import sys
from Komputar import Komputar
from tqdm import tqdm
from functools import lru_cache

def solve():
    _input = list(map(int, sys.stdin.readline().split(',')))

    def deploy_drone(x, y):
        komp = Komputar(_input.copy())
        komp.stdin.append(x)
        komp.stdin.append(y)
        komp.execute()
        return komp.stdout.popleft()

    def part_1():
        affected_area = 0
        for x in tqdm(range(50)):
            for y in range(50):
                affected_area += deploy_drone(x, y)
        return affected_area
    
    def part_2():

        def linear_search(start_y, stop_y):
            x = 1
            for y in tqdm(range(start_y, stop_y)):
                while deploy_drone(x - 1, y):
                    x -= 1
                while not deploy_drone(x, y):
                    x += 1
                if deploy_drone(x, y - 99) and deploy_drone(x + 99, y) and deploy_drone(x + 99, y - 99):
                    return (x, y - 99)
        
        x, y = linear_search(1500, 2000)
        return x * 10000 + y

    print('part 1:', part_1())
    print('part 2:', part_2())


if __name__ == '__main__':
    solve()
