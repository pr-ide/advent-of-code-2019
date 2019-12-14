import sys
input = sys.stdin.readline
read_tuple = lambda _type: map(_type, input().split(' '))
import numpy
from itertools import combinations
from math import gcd


class Simulation:
    
    def __init__(self, points, velocities=numpy.zeros((4, 3), dtype=int)):
        self.points = points
        self.velocities = velocities

    def step(self):
        self.apply_gravity()
        self.apply_velocity()

    def total_energy(self):
        return numpy.sum(self.potential_energy() * self.kinetic_energy())

    def apply_gravity(self):
        for i, j in combinations(range(4), 2):
            delta = numpy.sign(self.points[j] - self.points[i])
            self.velocities[i] += delta
            self.velocities[j] -= delta

    def apply_velocity(self):
        self.points += self.velocities

    def potential_energy(self):
        return numpy.sum(numpy.abs(self.points), axis=1)

    def kinetic_energy(self):
        return numpy.sum(numpy.abs(self.velocities), axis=1)


def least_common_multiple(p_1, p_2, p_3):
    lcm2 = p_2 * p_3 // gcd(p_2, p_3)
    return p_1 * lcm2 // gcd(p_1, lcm2)


def solve():
    initial_coords = numpy.array([
        [3, 15, 8],
        [5, -1, -2],
        [-10, 8, 2],
        [8, 4, -5],
    ])
    simulation = Simulation(initial_coords, numpy.zeros((4, 3), dtype=int))
    for _ in range(1000):
        simulation.step()
    print('part 1: ', simulation.total_energy())
    print('part 2: ', least_common_multiple(p_1=231614, p_2=144624, p_3=102356))

    
if __name__ == '__main__':
    solve()
