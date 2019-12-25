import sys
import string
from collections import defaultdict


def parse_map():
    graph = defaultdict(set)
    portals = defaultdict(set)
    inner, outer = set(), set()
    _input = [list(' ' + row + ' ') for row in sys.stdin.read().split('\n')]
    h, w = len(_input) + 2, len(_input[0])
    _input.append([' ' for _ in range(w)])
    _input.insert(0, [' ' for _ in range(w)])
    chars = set(string.ascii_uppercase)
    for i in range(1, h - 1):
        for j in range(1, w - 1):
            if _input[i][j] == '.' and _input[i + 1][j] == '.':
                graph[(i, j)].add((i + 1, j))
                graph[(i + 1, j)].add((i, j))
            if _input[i][j] == '.' and _input[i][j + 1] == '.':
                graph[(i, j)].add((i, j + 1))
                graph[(i, j + 1)].add((i, j))
            if _input[i][j] in chars and _input[i + 1][j] in chars:
                portal = ''.join(sorted(_input[i][j] + _input[i + 1][j]))
                if _input[i + 2][j] == '.':
                    portals[portal].add((i + 2, j))
                    if i - 1 == 0:
                        outer.add((i + 2, j))
                    else:
                        inner.add((i + 2, j))
                elif _input[i - 1][j] == '.':
                    portals[portal].add((i - 1, j))
                    if i + 2 == h - 1:
                        outer.add((i - 1, j))
                    else:
                        inner.add((i - 1, j))
            if _input[i][j] in chars and _input[i][j + 1] in chars:
                portal = ''.join(sorted(_input[i][j] + _input[i][j + 1]))
                if _input[i][j + 2] == '.':
                    portals[portal].add((i, j + 2))
                    if j - 1 == 0:
                        outer.add((i, j + 2))
                    else:
                        inner.add((i, j + 2))
                elif _input[i][j - 1] == '.':
                    portals[portal].add((i, j - 1))
                    if j + 2 == w - 1:
                        outer.add((i, j - 1))
                    else:
                        inner.add((i, j - 1))
    for portal, points in portals.items():
        if len(points) == 2:
            _in, _out = points.pop(), points.pop()
            graph[_in].add(_out)
            graph[_out].add(_in)
    start = portals['AA'].pop()
    end = portals['ZZ'].pop()
    outer -= set((start, end))
    return graph, start, end, inner, outer


def bfs(graph, start):
    queue = set([start])
    distances = {start: 0}
    while queue:
        next_queue = set()
        for elem in queue:
            neighbors = graph[elem]
            for n in neighbors:
                if n not in distances:
                    distances[n] = distances[elem] + 1
                    next_queue.add(n)
        queue = next_queue
    return distances


def recursive_maze_bfs(graph, start, end, inner, outer):
    level = 0
    queue = {(start, level)}
    visited = set()
    distance = 0
    while queue:
        next_queue = set()
        for elem, level in queue:
            visited.add((elem, level))
            neighbors = [(n, level) for n in graph[elem] if n not in visited]
            for n, n_lvl in neighbors:
                if n == end:
                    if n_lvl == 0:
                        return distance + 1
                    else:
                        continue
                if elem in outer and n in inner:
                    n_lvl -= 1
                if elem in inner and n in outer:
                    n_lvl += 1
                if n_lvl < 0:
                    continue
                if (n, n_lvl) not in visited:
                    next_queue.add((n, n_lvl))
        queue = next_queue
        distance += 1


def solve():
    graph, start, end, inner, outer = parse_map()
    print('part 1:', bfs(graph, start)[end])
    print('part 2:', recursive_maze_bfs(graph, start, end, inner, outer))


if __name__ == '__main__':
    solve()
