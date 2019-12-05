"""
6 digit number
should have double
from left to right the digits never decrease
"""
from collections import defaultdict


def get_digit(number: int, place: int) -> int:
    return (number // 10**place) % 10


def brute(start: int, end: int) -> int:
    numbers = []
    for number in range(start, end+1):
        has_decrease = False
        count = defaultdict(int)  # count pairs in a number
        for place in range(1, 6):
            digit_1 = get_digit(number, place)
            digit_2 = get_digit(number, place-1)
            if digit_1 > digit_2:
                has_decrease = True
                break
            if digit_1 == digit_2:
                count[digit_1] += 1
        if not has_decrease and 1 in count.values():
            numbers.append(number)
    return len(numbers)


def main():
    start = 138307
    end = 654504
    print(brute(start, end))


if __name__ == '__main__':
    main()
