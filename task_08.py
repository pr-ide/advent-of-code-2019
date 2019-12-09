from typing import List
from termcolor import colored, cprint


def decode_part_1(wide: int, tall: int, image: List) -> str:
    layer_size = wide*tall
    less_corrupted = [layer_size]
    for i in range(0, len(image) - layer_size, layer_size):
        layer = image[i:i+layer_size]
        layer_zero = layer.count('0')
        if layer_zero < less_corrupted[0]:
            less_corrupted = [layer_zero, layer]
    return less_corrupted


def decode_part_2(wide: int, tall: int, image: List) -> str:
    layer_size = wide*tall
    layer = image[len(image)-layer_size:len(image)]
    for i in range(len(image)-layer_size, -1, -layer_size):
        new_layer = image[i:i+layer_size]
        for idx, value in enumerate(new_layer):
            if value == '2':
                new_layer = new_layer[:idx] + layer[idx] + new_layer[idx+1:]
        layer = new_layer
    return layer


def main_part_1() -> None:
    wide = 25
    tall = 6
    with open('task_08.txt', encoding='utf-8') as stream:
        data = stream.readlines()
        encoded_image = str(data[0])
        result = decode_part_1(wide, tall, encoded_image)
        print(result[1].count('1') * result[1].count('2'))


def print_result(result: str, wide: int) -> None:
    for i in range(0, len(result), wide):
        for e in result[i:i+wide]:
            if e == '0':
                cprint(e, 'grey', 'on_grey', end='')
            if e == '1':
                cprint(e, 'white', 'on_white', end='')
        print()


def main_part_2() -> None:
    wide = 25
    tall = 6
    with open('task_08.txt', encoding='utf-8') as stream:
        data = stream.readlines()
        encoded_image = str(data[0])
        result = decode_part_2(wide, tall, encoded_image)
        print_result(result, wide)


if __name__ == '__main__':
    main_part_1()
    main_part_2()
