import sys
from Komputar import Komputar


def parse_grid(komp_stdout):
    grid = ''.join((chr(char_code) for char_code in komp_stdout)).split('\n')
    grid = [row for row in grid if row != '' and row != 'Main:']
    return grid, len(grid), len(grid[0])

def grid_intersections(grid, nrows, ncols):
    intersections = set()
    for i in range(1, nrows - 1):
        for j in range(1, ncols - 1):
            if grid[i][j] == grid[i][j + 1] == grid[i][j - 1] == grid[i + 1][j] == grid[i - 1][j] == '#':
                intersections.add((i, j))
    return intersections

def full_sequence(grid, nrows, ncols):
    directions = ['^', 'v', '<', '>']
    directions_map = {
        ('^', '>'): 'R',
        ('>', 'v'): 'R',
        ('v', '<'): 'R',
        ('<', '^'): 'R',
        ('>', '^'): 'L',
        ('v', '>'): 'L',
        ('<', 'v'): 'L',
        ('^', '<'): 'L',
    }

    def needed_direction(curr_pos, next_pos):
        diff = (next_pos[0] - curr_pos[0], next_pos[1] - curr_pos[1])
        if diff[0] > 0:
            return 'v'
        if diff[0] < 0:
            return '^'
        if diff[1] > 0:
            return '>'
        if diff[1] < 0:
            return '<'
    
    def next_pos(curr_pos, curr_dir):
        if curr_dir == '>':
            return (curr_pos[0], curr_pos[1] + 1)
        if curr_dir == '<':
            return (curr_pos[0], curr_pos[1] - 1)
        if curr_dir == '^':
            return (curr_pos[0] - 1, curr_pos[1])
        if curr_dir == 'v':
            return (curr_pos[0] + 1, curr_pos[1])

    unvisited_scaffolds = set(((i, j) for i in range(nrows) for j in range(ncols) if grid[i][j] in ('#', '^')))
    intersections = grid_intersections(grid, nrows, ncols)
    curr_pos = next(((i, j) for i in range(nrows) for j in range(ncols) if grid[i][j] in directions))
    curr_dir = grid[curr_pos[0]][curr_pos[1]]
    moves_sequence = []
    while unvisited_scaffolds:
        unvisited_scaffolds.discard(curr_pos)
        i, j = curr_pos
        possible_neighbors = ((i, j + 1), (i, j - 1), (i + 1, j), (i - 1, j))
        neighbor = next((pn for pn in possible_neighbors if pn in unvisited_scaffolds))
        next_dir = needed_direction(curr_pos, neighbor)
        moves_sequence.append(directions_map[(curr_dir, next_dir)])
        curr_dir = next_dir
        steps = 0
        while next_pos(curr_pos, curr_dir) in (unvisited_scaffolds | intersections):
            curr_pos = next_pos(curr_pos, curr_dir)
            steps += 1
            unvisited_scaffolds.discard(curr_pos)
        moves_sequence.append(steps)
    return moves_sequence

def compress_moves_sequence(moves):
    functions = {
        'A': ['R', 4, 'L', 10, 'L', 10],
        'B': ['L', 8, 'R', 12, 'R', 10, 'R', 4],
        'C': ['L', 8, 'L', 8, 'R', 10, 'R', 4],
    }
    main = []
    idx = 0
    while idx < len(moves):
        for seq_letter, seq in functions.items():
            if moves[idx:idx + len(seq)] == seq:
                main.append(seq_letter)
                idx += len(seq)
    return main, functions

def solve():
    _input = list(map(int, sys.stdin.readline().split(',')))

    def part_1():
        komp = Komputar(_input.copy())
        komp.execute()
        grid, nrows, ncols = parse_grid(komp.stdout)
        intersections = grid_intersections(grid, nrows, ncols)
        return sum((i * j for i, j in intersections))
    
    def part_2():
        modified_input = _input.copy()
        modified_input[0] = 2
        komp = Komputar(modified_input)
        komp.execute()
        grid, nrows, ncols = parse_grid(komp.stdout)
        moves = full_sequence(grid, nrows, ncols)
        main, functions = compress_moves_sequence(moves)
        for char in ','.join(main):
            komp.stdin.append(ord(char))
        komp.stdin.append(10)
        for _, function in functions.items():
            for char in ','.join(map(str, function)):
                komp.stdin.append(ord(char))
            komp.stdin.append(10)
        komp.stdin.append(ord('n'))
        komp.stdin.append(10)
        komp.execute()
        return komp.stdout.pop()

    print('part 1:', part_1())
    print('part 2:', part_2())


if __name__ == '__main__':
    solve()
