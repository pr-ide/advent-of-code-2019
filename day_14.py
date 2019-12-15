import sys
input = sys.stdin.readline
read_tuple = lambda _type: map(_type, input().split(' '))
from collections import defaultdict
from math import ceil


def read_input():
    reactions = sys.stdin.read().split('\n')
    dependency_graph = defaultdict(set)
    quantity_map = {}
    prod_quantity = {}
    for r in reactions:
        _in, _out = r.split(' => ')
        out_quantity, out_type = _out.split(' ')
        for item in _in.split(', '):
            in_quantity, in_type = item.split(' ')
            dependency_graph[out_type].add(in_type)
            quantity_map[(in_type, out_type)] = (int(in_quantity), int(out_quantity))
            prod_quantity[out_type] = int(out_quantity)
    return dependency_graph, quantity_map, prod_quantity

def solve():
    dependency_graph, quantity_map, prod_quantity = read_input()
    # part 1
    leftover, created_total = defaultdict(int), defaultdict(int)
    
    def create(material, quantity):
        if material == 'ORE':
            leftover['ORE'] += quantity
            created_total['ORE'] += quantity
        else:
            while leftover[material] < quantity:
                times = ceil((quantity - leftover[material]) / prod_quantity[material])
                for ingridient in dependency_graph[material]:
                    create(ingridient, times * quantity_map[(ingridient, material)][0])
                enough_materials = True
                for ingridient in dependency_graph[material]:
                    enough_materials = enough_materials and leftover[ingridient] >= times * quantity_map[(ingridient, material)][0]
                if enough_materials:
                    for ingridient in dependency_graph[material]:
                        leftover[ingridient] -= times * quantity_map[(ingridient, material)][0]
                    leftover[material] += times * quantity_map[(ingridient, material)][1]


    create('FUEL', 1)
    print(f"part 1: {created_total['ORE']}")
    # part 2
    lower_bound, upper_bound = 1, 10 ** 9
    while upper_bound - lower_bound > 1:
        leftover, created_total = defaultdict(int), defaultdict(int)
        mid = (lower_bound + upper_bound) // 2
        create('FUEL', mid)
        if created_total['ORE'] < 10 ** 12:
            lower_bound = mid
        else:
            upper_bound = mid
    print(f"part 2:", lower_bound)


if __name__ == '__main__':
    solve()
