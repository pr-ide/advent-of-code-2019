import sys
input = sys.stdin.readline
read_tuple = lambda _type: map(_type, input().split(' '))


def read_input(width, height):
    data = input()
    layers = []
    i = 0
    while i < len(data):
        layer = []
        for _ in range(height):
            layer.append(data[i:i + width])
            i += width
        layers.append(layer)
    return layers

def count(digit, layer):
    return sum((row.count(digit) for row in layer))

def check_control_sum(layers, width, height):
    digit_0_counts = [sum((row.count('0') for row in layer)) for layer in layers]
    digit_0_min_idx = digit_0_counts.index(min(digit_0_counts))
    control_sum = count('1', layers[digit_0_min_idx]) * count('2', layers[digit_0_min_idx])
    print(control_sum)

def decode(layers, width, height):
    decoded = [list(row) for row in layers[0]]
    for layer in layers:
        for i in range(height):
            for j in range(width):
                if decoded[i][j] == '2':
                    decoded[i][j] = layer[i][j]
    for row in decoded:
        print(''.join(row).replace('1', 'X').replace('0', ' '))

def solve():
    width, height = 25, 6
    layers = read_input(width, height)
    check_control_sum(layers, width, height)
    decode(layers, width, height)


if __name__ == '__main__':
    solve()
