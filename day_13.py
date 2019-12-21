import sys
from Komputar import Komputar


def decision(paddle_coords, ball_coords, ball_direction):
    if paddle_coords and ball_coords and ball_direction:
        if paddle_coords[0] < ball_coords[0] and ball_direction == 1:
            return 1
        elif paddle_coords[0] > ball_coords[0] and ball_direction == -1:
            return -1
    return 0

def update_ball(old_coords, coords):
    direction = 0
    if old_coords:
        if coords[0] > old_coords[0]:
            direction = 1
        elif coords[0] < old_coords[0]:
            direction = -1
    return coords, direction

def solve():
    _input = list(map(int, sys.stdin.readline().split(',')))

    def part_1():
        komp = Komputar(_input.copy())
        komp.execute()
        block_tiles = 0
        while komp.stdout:
            x = komp.stdout.popleft()
            y = komp.stdout.popleft()
            tile_id = komp.stdout.popleft()
            if tile_id == 2:
                block_tiles += 1
        return block_tiles

    def part_2():
        modified_input = _input.copy()
        modified_input[0] = 2
        komp = Komputar(modified_input)
        score = None
        paddle_coords, ball_coords, ball_direction = None, None, None
        while True:
            status = komp.execute()
            while komp.stdout:
                x = komp.stdout.popleft()
                y = komp.stdout.popleft()
                if x == -1 and y == 0:
                    score = komp.stdout.popleft()
                else:
                    tile_id = komp.stdout.popleft()
                    if tile_id == 4:
                        ball_coords, ball_direction = update_ball(ball_coords, (x, y))
                    if tile_id == 3:
                        paddle_coords = x, y
            if status == komp.PAUSE:
                joystick = decision(paddle_coords, ball_coords, ball_direction)
                komp.stdin.append(joystick)
            elif status == komp.HALT:
                break
        return score

    print('part 1:', part_1())
    print('part 2:', part_2())


if __name__ == '__main__':
    solve()
