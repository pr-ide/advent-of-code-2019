from collections import defaultdict


class AdjList:
    def __init__(self):
        self.graph = defaultdict(list)

    def add_edge(self, src, dest):
        self.graph[src].append(dest)
        self.graph[dest].append(src)

    def bfs(self, src, dest):
        level = {src: 0}
        parent = {src: None}
        frontier = [src]
        i = 1
        while frontier:
            n = []
            for u in frontier:
                for v in self.graph[u]:
                    if v not in level:
                        level[v] = i
                        parent[v] = u
                        n.append(v)
                        if v == dest:
                            return i
            i += 1
            frontier = n
        return -1

    def dfs_visit(self, s, parent):
        for v in self.graph[s]:
            if v not in parent:
                parent[v] = s
                self.dfs_visit(v, parent)

    def dfs(self, s):
        parent = {s: None}
        self.dfs_visit(s, parent)
        print(parent)

    def get_total(self):
        src = 'COM'
        total = 0
        for dest in self.graph:
            total += self.bfs(src, dest)
        return total + 1


def main():
    graph = AdjList()
    with open('task_06.txt', encoding='utf-8') as stream:
        for line in stream:
            src, dest = line.rstrip().split(')')
            graph.add_edge(src, dest)
    print(graph.get_total())
    print(graph.bfs('YOU', 'SAN') - 2)


if __name__ == '__main__':
    main()
