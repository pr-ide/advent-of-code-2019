import math


def calculate_fuel(mass: int) -> int:
    return math.floor(mass // 3) - 2


def total_fuel(mass: int) -> int:
    fuel = calculate_fuel(mass)
    if fuel <= 0:
        return 0
    return fuel + total_fuel(fuel)


def need_fuel(filename: str) -> int:
    need = 0
    with open(filename, encoding='utf-8') as stream:
        line = stream.readline()
        while line:
            need += total_fuel(int(line))
            line = stream.readline()
    return need


if __name__ == '__main__':
    print(need_fuel('0001_input.txt'))
