import sys
from tqdm import tqdm


def deal_into_new_stack(deck, **params):
    return list(reversed(deck))

def cut(deck, **params):
    n = int(params['n'])
    return deck[n:] + deck[:n]

def deal_with_increment(deck, **params):
    n = int(params['n'])
    new_deck = [0 for _ in range(len(deck))]
    idx = 0
    for card in deck:
        new_deck[idx] = card
        idx = (idx + n) % len(deck)
    return new_deck

def solve():
    techniques = {
        'deal with increment': deal_with_increment,
        'cut': cut,
        'deal into new stack': deal_into_new_stack,
    }
    shuffles = [line for line in sys.stdin.read().split('\n')]

    def part_1():
        deck = list(range(10007))
        for line in shuffles:
            for t in techniques:
                if line.startswith(t):
                    deck = techniques[t](deck, n=line.replace(t, ''))
        return deck.index(2019)
    
    print('part 1:', part_1())


if __name__ == '__main__':
    solve()
