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

def bfs(graph, start_planet):
    visited = set()
    orbits = 0
    distances = {}
    curr_distance = 0
    queue = set([start_planet])
    while queue:
        next_queue = set()
        for obj in queue:
            if obj not in distances:
                distances[obj] = curr_distance
            visited.add(obj)
            neighbours = graph.get(obj, set())
            neighbours = set((n for n in neighbours if n not in visited))
            orbits += len(neighbours) * (curr_distance + 1)
            next_queue = next_queue | neighbours
        queue = next_queue
        curr_distance += 1
    return orbits, distances

def solve():
    graph = read_graph()
    # part 1
    orbits, distances = bfs(graph, 'COM')
    print(f"orbits: {orbits}")
    # part 2
    orbits, distances = bfs(graph, 'YOU')
    # -2 because we should measure min distance from 'YOU' and 'SAN' neighbours
    print(f"distance: {distances['SAN'] - 2}")


if __name__ == '__main__':
    solve()
