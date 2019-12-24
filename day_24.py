import sys


def next_state(eris):
    n = len(eris)
    new_eris = [row[:] for row in eris]
    for i in range(n):
        for j in range(n):
            adjacent = 0
            if i > 0:
                adjacent += eris[i - 1][j] == '#'
            if i < n - 1:
                adjacent += eris[i + 1][j] == '#'
            if j > 0:
                adjacent += eris[i][j - 1] == '#'
            if j < n - 1:
                adjacent += eris[i][j + 1] == '#'
            if eris[i][j] == '#' and adjacent != 1:
                new_eris[i][j] = '.'
            elif eris[i][j] == '.' and adjacent in (1, 2):
                new_eris[i][j] = '#'
    return new_eris

def biodiversity_rating(eris):
    n = len(eris)
    total = 0
    for i, row in enumerate(eris):
        for j, elem in enumerate(row):
            if elem == '#':
                total += 2 ** (i * n + j)
    return total

def solve():
    _input = [list(row) for row in sys.stdin.read().split('\n')]

    def part_1():
        eris = [row[:] for row in _input]
        various_ratings = set()
        while True:
            rating = biodiversity_rating(eris)
            if rating in various_ratings:
                return rating
            various_ratings.add(rating)
            eris = next_state(eris)
        
    
    print('part 1:', part_1())

if __name__ == '__main__':
    solve()
