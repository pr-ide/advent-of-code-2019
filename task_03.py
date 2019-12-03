from typing import Union
from dataclasses import dataclass


@dataclass
class Point:
    x: int
    y: int

    def calculate_distance(self, other: 'Point') -> int:
        return abs(self.x - other.x) + abs(self.y - other.y)


@dataclass
class Line:
    start: 'Point'
    end: 'Point'

    def on_segment(self, p: 'Point', q: 'Point', r: 'Point') -> bool:
        if (q.x <= max(p.x, r.x) and q.x >= min(p.x, r.x) and
            q.y <= max(p.y, r.y) and q.y >= min(p.y, r.y)):
            return True

        return False

    def orientation(self, p: 'Point', q: 'Point', r: 'Point') -> int:
        val = ((q.y - p.y) * (r.x - q.x) -
              (q.x - p.x) * (r.y - q.y))

        if val == 0:
            return 0

        return 1 if val > 0 else 2

    def do_intersect(self, other: 'Line') -> bool:
        p1 = self.start
        p2 = other.start
        q1 = self.end
        q2 = other.end
        o1 = self.orientation(p1, q1, p2)
        o2 = self.orientation(p1, q1, q2)
        o3 = self.orientation(p2, q2, p1)
        o4 = self.orientation(p2, q2, q1)

        if (o1 != o2 and o3 != o4):
            return True

        if (o1 == 0 and self.on_segment(p1, p2, q1)):
            return True

        if (o2 == 0 and self.on_segment(p1, q2, q1)):
            return True

        if (o3 == 0 and self.on_segment(p2, p1, q2)):
            return True

        if (o4 == 0 and self.on_segment(p2, q1, q2)):
            return True

        return False

    def find_intersection(self, other: 'Line') -> Union['Point', bool]:
        if self.do_intersect(other):
            a1 = self.end.y - self.start.y
            a2 = other.end.y - other.start.y

            b1 = self.start.x - self.end.x
            b2 = other.start.x - other.end.x

            c1 = a1*self.start.x + b1*self.start.y
            c2 = a2*other.start.x + b2*other.start.y

            det = a1*b2 - a2*b1
            if det == 0:
                return False
            else:
                x = (b2*c1 - b1*c2) / det
                y = (a1*c2 - a2*c1) / det
                return Point(x, y)

    def find_steps_to_point(self, intersection: 'Point') -> int:
        point = intersection
        return (
            abs(point.x - self.start.x)
            if point.y == self.start.y
            else abs(point.y - self.start.y)
        )

    def __hash__(self):
        return hash((self.start.x, self.start.y, self.end.x, self.end.y))


class Map:
    def __init__(self):
        self.start = Point(0, 0)
        self.current = self.start
        self.lines = []
        self.steps = 0
        self.lines_n_steps = dict()

    def left(self, value: int) -> None:
        self.steps += value
        new = Point(self.current.x - value, self.current.y)
        self.lines.append(Line(self.current, new))
        self.lines_n_steps[Line(self.current, new)] = (value, self.steps)
        self.current = new

    def up(self, value: int) -> None:
        self.steps += value
        new = Point(self.current.x, self.current.y + value)
        self.lines.append(Line(self.current, new))
        self.lines_n_steps[Line(self.current, new)] = (value, self.steps)
        self.current = new

    def right(self, value: int) -> None:
        self.steps += value
        new = Point(self.current.x + value, self.current.y)
        self.lines.append(Line(self.current, new))
        self.lines_n_steps[Line(self.current, new)] = (value, self.steps)
        self.current = new

    def down(self, value: int) -> None:
        self.steps += value
        new = Point(self.current.x, self.current.y - value)
        self.lines.append(Line(self.current, new))
        self.lines_n_steps[Line(self.current, new)] = (value, self.steps)
        self.current = new

    def process_command(self, command: str):
        operation_map = {
            'L': self.left,
            'U': self.up,
            'R': self.right,
            'D': self.down,
        }
        operation = command[:1]
        value = command[1:]
        operation_map[operation](int(value))

    def find_closest_intersection(self, other: 'Map') -> int:
        lengths = []
        first_line: 'Line'
        for first_line in self.lines:
            second_line: 'Line'
            for second_line in other.lines:
                point = first_line.find_intersection(second_line)
                if point:
                    lengths.append(point.calculate_distance(self.start))
        return min(lengths[1:])

    def find_lowest_steps_intersection(self, other: 'Map') -> int:
        steps = []
        first_line: 'Line'
        for first_line in self.lines:
            second_line: 'Line'
            for second_line in other.lines:
                point = first_line.find_intersection(second_line)
                if point:
                    first_steps = (
                        self.lines_n_steps[first_line][1] - self.lines_n_steps[first_line][0]
                        + first_line.find_steps_to_point(point)
                    )
                    second_steps = (
                        other.lines_n_steps[second_line][1] - other.lines_n_steps[second_line][0]
                        + second_line.find_steps_to_point(point)
                    )
                    steps.append(first_steps + second_steps)
        return min(steps[1:])


def main():
    map_1 = Map()
    map_2 = Map()

    path_1 = 'R1004,U520,R137,D262,L403,U857,R50,U679,R788,D98,L717,D1,R367,U608,L125,U703,L562,D701,L718,U357,R742,D860,R557,D117,R950,U546,L506,U836,R951,D460,L38,U893,L1,D217,R262,D950,R239,U384,R971,D289,R323,U878,L525,U687,L831,U523,R94,D33,L879,D318,R633,D775,R879,D351,L120,D8,R31,U49,R328,D598,L380,D160,R261,D716,R459,U533,L444,U412,L326,U93,L193,D621,R236,U769,L319,D885,L559,U509,L62,U321,L667,D505,R556,U159,L5,U126,L262,D946,L168,U491,L56,D831,R926,U926,R562,D270,R785,U436,R852,D629,R872,U716,R549,U435,R462,U191,R318,U91,L637,D682,R647,D53,L789,D725,R312,D366,L287,U29,R85,D657,R88,U300,R795,U378,R800,D391,L594,U791,R205,U352,L510,D975,R47,D311,R319,U579,R214,D112,R996,U874,R328,D578,R37,U689,L543,U16,L580,D230,L714,D58,L580,D658,R218,U535,R149,U996,L173,D316,L90,D372,L364,U700,L60,D70,L250,U276,R580,U505,L682,U943,R336,U847,R810,U963,R874,D740,R732,D328,R926,D447,R638,D102,R696,U211,L594,D354,R384,U81,L884,U916,L168,U759,R631,D702,L598,D382,L647,U642,R537,U53,R897,U954,R263,U445,L41,D91,L51,D338,R219,U269,L689,D172,R627,D287,L440,D504,L253,D252,R815,D108,L282,U835,L243,U638,R910,D306,R755,D202,R69,D862,L537,D947,L180,D835,L111,U832,R939,D449,R180,U105,R892,D837,L153,U215,L695,U957,R923,U496,R608,U739,L711,U700,L838,D117,R479,U852,R795,D955,L386,D70,R728,D40,R580,U777,L877,U284,R414,D300,R105,D372,L317,D91,R653,U920,R956,D496,L543,D363,R374,D283,L696,U466,R467,D878,R660,U590,L962,U619,R991,U848,L648,D191,R459,U125,L998,U19,L214,U947,R188,U103,R916'
    commands_1 = path_1.split(',')
    for command in commands_1:
        map_1.process_command(command)

    path_2 = 'L1008,U717,R288,D770,R270,U514,R109,D538,L719,U179,R466,D792,R421,U723,L22,U705,L284,U14,L478,U367,R727,U880,R620,D46,R377,U897,L731,U840,L910,D385,L257,U311,L596,D991,L668,D730,L707,D816,R47,U948,R84,D700,R299,U707,R261,D928,R358,D504,R309,U369,R931,U20,L940,U326,L362,D52,R98,D475,L907,D918,R931,D468,R279,D586,R592,U973,R753,D365,R694,U278,R934,U712,R441,U996,L989,D693,L211,D561,R105,D425,R53,U168,L451,U865,L585,D412,L857,U988,R724,U774,R295,U588,R329,D810,L698,D118,R277,U193,R309,U933,R186,D535,R409,U322,L849,U606,R590,U892,L542,D237,R475,D920,R679,U602,L477,D634,L988,D540,L323,U791,L375,U625,L621,U567,L943,U512,L239,D90,L66,U151,R83,U435,R612,D865,L177,U368,R326,U574,L241,U197,R499,U419,R297,U207,L311,D243,L559,D281,R513,U748,L884,U207,R71,D441,R133,D993,L4,D977,L669,U523,L564,U186,R477,U737,L685,U338,L456,U939,R774,U674,L97,D827,R237,D451,R618,D143,R750,U196,L559,D178,L693,D916,R334,U231,L651,U249,R620,U283,L387,U352,L915,U959,L693,U909,R320,U119,L617,U177,L993,D265,R667,U204,R59,D601,L579,U483,R155,D484,L44,D751,R915,U510,L552,U308,R505,U394,R585,U872,L617,U202,R928,U941,R235,U768,R666,D547,L244,D270,R353,D612,R384,U430,L685,D536,R103,U147,R794,D621,L52,U96,L557,D455,L635,D58,R265,U545,R938,D266,L173,U746,L672,D237,R286,U131,R487,U837,R394,D702,R49,U579,L699,U819,L448,D223,L982,D906,L397,U807,L737,D223,L791,D965,R436,U29,R908,D273,R194,U91,R232,U591,L336,D70,R467,U505,L341,U989,R278,U387,L442,U950,R487,D384,L534,D514,L433,U627,R381,U54,L847,U231,L590'
    commands_2 = path_2.split(',')
    for command in commands_2:
        map_2.process_command(command)

    result = map_1.find_closest_intersection(map_2)
    print(result)
    steps = map_1.find_lowest_steps_intersection(map_2)
    print(steps)


if __name__ == '__main__':
    main()
