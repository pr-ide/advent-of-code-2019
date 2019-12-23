import sys
from Komputar import Komputar


def is_network_idle(NAT, network):
    if not NAT:
        return False
    idle = True
    for komp in network:
        idle = idle and komp.stdin[0] == -1
    return idle

def solve():
    _input = list(map(int, sys.stdin.readline().split(',')))
    n_komp = 50
    network = [Komputar(_input.copy()) for _ in range(n_komp)]
    for i, komp in enumerate(network):
        komp.stdin.append(i)
    NAT, latest_NAT, first_NAT_Y = None, None, None
    while True:
        for komp in network:
            komp.execute()
        for komp in network:
            while komp.stdout:
                address, X, Y = komp.stdout.popleft(), komp.stdout.popleft(), komp.stdout.popleft()
                if address == 255:
                    if not first_NAT_Y:
                        first_NAT_Y = Y
                    NAT = (X, Y)
                else:
                    network[address].stdin.extend((X, Y))
        for komp in network:
            if not komp.stdin:
                komp.stdin.append(-1)
        idle = is_network_idle(NAT, network)
        if idle:
            network[0].stdin.extend(NAT)
            if latest_NAT == NAT:
                break
            latest_NAT = NAT

    print('part 1:', first_NAT_Y)
    print('part 2:', latest_NAT[1])


if __name__ == '__main__':
    solve()
