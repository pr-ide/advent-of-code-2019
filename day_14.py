import sys
input = sys.stdin.readline
read_tuple = lambda _type: map(_type, input().split(' '))
from collections import defaultdict


def read_input():
    reactions = sys.stdin.read().split('\n')
    dependency_graph = defaultdict(set)
    quantity_map = {}
    for r in reactions:
        _in, _out = r.split(' => ')
        out_quantity, out_type = _out.split(' ')
        for item in _in.split(', '):
            in_quantity, in_type = item.split(' ')
            dependency_graph[out_type].add(in_type)
            quantity_map[(in_type, out_type)] = (int(in_quantity), int(out_quantity))
    return dependency_graph, quantity_map

def solve():
    dependency_graph, quantity_map = read_input()
    leftover, created_total = defaultdict(int), defaultdict(int)
    
    def create(material, quantity):
        if material == 'ORE':
            leftover['ORE'] += quantity
            created_total['ORE'] += quantity
        else:
            while leftover[material] < quantity:
                for ingridient in dependency_graph[material]:
                    create(ingridient, quantity_map[(ingridient, material)][0])
                enough_materials = True
                for ingridient in dependency_graph[material]:
                    enough_materials = enough_materials and leftover[ingridient] >= quantity_map[(ingridient, material)][0]
                if enough_materials:
                    for ingridient in dependency_graph[material]:
                        leftover[ingridient] -= quantity_map[(ingridient, material)][0]
                    leftover[material] += quantity_map[(ingridient, material)][1]


    create('FUEL', 1)
    print(f"part 1: {created_total['ORE']}")


if __name__ == '__main__':
    solve()
