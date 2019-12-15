import sys
input = sys.stdin.readline
read_tuple = lambda _type: map(_type, input().split(' '))
from collections import defaultdict
from math import ceil


class Kitchen:
    def __init__(self):
        self.dependency_graph = defaultdict(set)
        self.quantity_map = {}
        self.prod_quantity = {}
        self.leftover = defaultdict(int)
        self.total_ore_used = 0
        for reaction in sys.stdin.read().split('\n'):
            _in, _out = reaction.split(' => ')
            out_quantity, out_type = _out.split(' ')
            for item in _in.split(', '):
                in_quantity, in_type = item.split(' ')
                self.dependency_graph[out_type].add(in_type)
                self.quantity_map[(in_type, out_type)] = (int(in_quantity), int(out_quantity))
                self.prod_quantity[out_type] = int(out_quantity)

    def flush(self):
        self.leftover = defaultdict(int)
        self.total_ore_used = 0

    def create(self, material, quantity):
        if material == 'ORE':
            self.leftover['ORE'] += quantity
            self.total_ore_used += quantity
        else:
            while self.leftover[material] < quantity:
                times = ceil((quantity - self.leftover[material]) / self.prod_quantity[material])
                for ingridient in self.dependency_graph[material]:
                    self.create(ingridient, times * self.quantity_map[(ingridient, material)][0])
                enough_materials = True
                for ingridient in self.dependency_graph[material]:
                    enough_materials = enough_materials and self.leftover[ingridient] >= times * self.quantity_map[(ingridient, material)][0]
                if enough_materials:
                    for ingridient in self.dependency_graph[material]:
                        self.leftover[ingridient] -= times * self.quantity_map[(ingridient, material)][0]
                    self.leftover[material] += times * self.quantity_map[(ingridient, material)][1]

def solve():
    kitchen = Kitchen()
    # part 1
    kitchen.create('FUEL', 1)
    print("part 1:", kitchen.total_ore_used)
    # part 2
    lower_bound, upper_bound = 1, 10 ** 9
    while upper_bound - lower_bound > 1:
        kitchen.flush()
        mid = (lower_bound + upper_bound) // 2
        kitchen.create('FUEL', mid)
        if kitchen.total_ore_used <= 10 ** 12:
            lower_bound = mid
        else:
            upper_bound = mid
    print(f"part 2:", lower_bound)


if __name__ == '__main__':
    solve()
