import sys
from Komputar import Komputar


def apply_turn(direction, turn):
    if turn == 0:
        return (-1 * direction[1], direction[0])
    elif turn == 1:
        return (direction[1], -1 * direction[0])

def step_forward(pos, direction):
    return (pos[0] + direction[0], pos[1] + direction[1])

def paint(komp, white_coords):
    painted_coords = set()
    status = 0
    pos = (0, 0)
    direction = (0, 1)
    while status != komp.HALT:
        if pos in white_coords:
            komp.stdin.append(1)
        else:
            komp.stdin.append(0)
        status = komp.execute()
        if komp.stdout:
            painted_coords.add(pos)
            curr_color = komp.stdout.popleft()
            turn = komp.stdout.popleft()
            if curr_color:
                white_coords.add(pos)
            else:
                white_coords.discard(pos)
            direction = apply_turn(direction, turn)
            pos = step_forward(pos, direction)
    return white_coords, painted_coords

def solve():
    _input = list(map(int, sys.stdin.readline().split(',')))

    def part_1():
        komp = Komputar(_input.copy())
        white_coords = set()
        white_coords, painted_coords = paint(komp, white_coords)
        return len(painted_coords)

    def part_2():
        komp = Komputar(_input.copy())
        white_coords = set([(0, 0)])
        white_coords, _ = paint(komp, white_coords)
        x_dim, y_dim = 100, 6
        grid = [[' ' for _ in range(x_dim)] for _ in range(y_dim)]
        for x, y in white_coords:
            grid[-y][x_dim // 2 + x] = '#'
        for row in grid:
            print(''.join(row))
    
    print('part 1: ', part_1())
    print('part 2:')
    part_2()


if __name__ == '__main__':
    solve()
