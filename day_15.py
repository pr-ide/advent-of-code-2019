import sys
from Komputar import Komputar

def move_north(pos):
    return (pos[0], pos[1] + 1)

def move_south(pos):
    return (pos[0], pos[1] - 1)

def move_west(pos):
    return (pos[0] - 1, pos[1])

def move_east(pos):
    return (pos[0] + 1, pos[1])

def bfs(actual_map, start):
    queue = set([start])
    distances = {start: 0}
    while queue:
        next_queue = set()
        for x, y in queue:
            possible_neighbors = [(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)]
            neighbors = [pn for pn in possible_neighbors if actual_map.get(pn, '#') == ' ' and pn not in distances]
            for n in neighbors:
                if n not in distances:
                    distances[n] = distances[(x, y)] + 1
                next_queue.add(n)
        queue = next_queue
    return distances

def solve():
    moves = {1: move_north, 2: move_south, 3: move_west, 4: move_east}
    reverse_moves = {1: 2, 2: 1, 3: 4, 4: 3}
    _input = list(map(int, sys.stdin.readline().split(',')))
    komp = Komputar(_input.copy())
    actual_map = {}
    current_pos, oxygen_system = (0, 0), None
    actual_map[current_pos] = ' '
    executed_commands = []
    while True:
        moved = False
        for command in (3, 1, 4, 2):
            if moves[command](current_pos) not in actual_map:
                komp.stdin.append(command)
                komp.execute()
                response = komp.stdout.popleft()
                if response in (1, 2):
                    moved = True
                    executed_commands.append(command)
                    current_pos = moves[command](current_pos)
                    actual_map[current_pos] = ' '
                    if response == 2:
                        oxygen_system = current_pos
                    break
                else:
                    actual_map[moves[command](current_pos)] = '#'
        if not moved:
            if not executed_commands:
                break
            latest_command = executed_commands.pop()
            komp.stdin.append(reverse_moves[latest_command])
            komp.execute()
            _ = komp.stdout.popleft()
            current_pos = moves[reverse_moves[latest_command]](current_pos)
    
    def part_1():
        return bfs(actual_map, (0, 0))[oxygen_system]
    
    def part_2():
        return max(bfs(actual_map, oxygen_system).values())

    print('part 1:', part_1())
    print('part 2:', part_2())


if __name__ == '__main__':
    solve()
