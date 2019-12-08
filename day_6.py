import sys
input = sys.stdin.readline
read_tuple = lambda _type: map(_type, input().split(' '))


def read_graph():
    graph = {}
    for line in sys.stdin.read().split('\n'):
        obj1, obj2 = line.split(')')
        if obj1 not in graph:
            graph[obj1] = set()
        if obj2 not in graph:
            graph[obj2] = set()
        graph[obj1].add(obj2)
        graph[obj2].add(obj1)
    return graph

def part1(graph):
    visited = set()
    orbits = 0
    current_distance = 1
    queue = set(['COM'])
    while queue:
        next_queue = set()
        for obj in queue:
            visited.add(obj)
            neighbours = graph.get(obj, set())
            neighbours = set((n for n in neighbours if n not in visited))
            orbits += len(neighbours) * current_distance
            next_queue = next_queue | neighbours
        current_distance += 1
        queue = next_queue
    print(f'orbits: {orbits}')

def part2(graph):
    visited = set()
    distances = {}
    curr_distance = 0
    queue = set(['YOU'])
    while queue:
        next_queue = set()
        for obj in queue:
            if obj not in distances:
                distances[obj] = curr_distance
            visited.add(obj)
            neighbours = graph.get(obj, set())
            neighbours = set((n for n in neighbours if n not in visited))
            next_queue = next_queue | neighbours
        queue = next_queue
        curr_distance += 1
    print(f"distance: {distances['SAN'] - 2}")

def solve():
    graph = read_graph()
    part1(graph)
    part2(graph)


if __name__ == '__main__':
    solve()
