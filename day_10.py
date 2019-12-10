import sys
from collections import defaultdict
from math import gcd, atan2, pi
from itertools import cycle


def read_data():
    data = [list(row) for row in sys.stdin.read().split('\n')]
    asteroids = []
    for i, row in enumerate(data):
        for j, elem in enumerate(row):
            if elem == '#':
                asteroids.append((i, j))
    return asteroids

def kinda_normalized_vector(x, y):
    if y == 0:
        return (x // abs(x), 0)
    elif x == 0:
        return (0, y // abs(y))
    else:
        _gcd = gcd(abs(x), abs(y))
        return (x // _gcd, y // _gcd)

def get_optimal_coords(kinda_normalized_vectors):
    _max = max((len(v) for v in kinda_normalized_vectors.values()))
    coords = next((k for k in kinda_normalized_vectors if len(kinda_normalized_vectors[k]) == _max))
    return coords, _max

def build_asteroid_death_queues(vectors, lazer):
    vectors_left = defaultdict(list)
    for vector in vectors[lazer]:
        vectors_left[kinda_normalized_vector(*vector)].append(vector)
    for k in vectors_left:
        vectors_left[k].sort(key=lambda x: x[0] ** 2 + x[1] ** 2, reverse=True)
    return vectors_left

def build_angle_generator(vectors_left):
    all_angles = cycle(sorted(vectors_left.keys(), key=lambda x: -atan2(x[1], x[0])))
    curr_angle = next(all_angles)
    while curr_angle != (0, 1):
        curr_angle = next(all_angles)
    yield curr_angle
    while True:
        yield next(all_angles)

def solve():
    asteroids = read_data()
    # part 1
    kinda_normalized_vectors = defaultdict(set)
    vectors = defaultdict(list)
    for i, j in asteroids:
        for k, l in asteroids:
            if (i, j) != (k, l):
                dx, dy = l - j, -(k - i) # flip Y axis for convenience
                kinda_normalized_vectors[(i, j)].add(kinda_normalized_vector(dx, dy))
                vectors[(i, j)].append((dx, dy))
    lazer, _max = get_optimal_coords(kinda_normalized_vectors)
    print(f'part 1: {_max}')
    # part 2
    vectors_left = build_asteroid_death_queues(vectors, lazer)
    angle_generator = build_angle_generator(vectors_left)
    asteroids_destroyed = 0
    while asteroids_destroyed < 200:
        curr_angle = next(angle_generator)
        if vectors_left[curr_angle]:
            latest = vectors_left[curr_angle].pop()
            asteroids_destroyed += 1
    part_2 = (lazer[1] + latest[0]) * 100 + (lazer[0] - latest[1])
    print(f'part 2: {part_2}')


if __name__ == '__main__':
    solve()
