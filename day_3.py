import sys


def recreate_wire_path(wire_description):
    directions = {'U': 1, 'D': -1, 'L': -1j, 'R': 1j}
    path = []
    current_pos = 0
    for step in wire_description:
        direction = step[0]
        length = int(step.replace(direction, ''))
        while length:
            current_pos += directions[direction]
            path.append(current_pos)
            length -= 1
    return path


def closest_intersection(intersections):
    return int(min((abs(p.real) + abs(p.imag) for p in intersections)))


def fewest_combined_steps(wire_paths, intersections):
    steps = []
    for path in wire_paths:
        _steps = {}
        for i, point in enumerate(path):
            if point in intersections and point not in _steps:
                _steps[point] = i + 1
        steps.append(_steps)
    steps_combined = [steps[0][point] + steps[1][point] for point in intersections]
    return min(steps_combined)


if __name__ == '__main__':
    wire_descriptions = [row.split(',') for row in sys.stdin.read().split('\n')]
    wire_paths = [recreate_wire_path(wire_desc) for wire_desc in wire_descriptions]
    intersections = set(wire_paths[0]) & set(wire_paths[1])
    print(f'closest intersection: {closest_intersection(intersections)}')
    print(f'fewest combined steps: {fewest_combined_steps(wire_paths, intersections)}')
    