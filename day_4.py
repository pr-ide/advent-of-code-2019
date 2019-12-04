def is_non_decreasing(sequence):
    for i in range(1, len(sequence)):
        if sequence[i] < sequence[i - 1]:
            return False
    return True

def has_double(sequence):
    for i in range(len(sequence)):
        if sequence[i] == sequence[i - 1]:
            return True
    return False

def fits_adjacency_rules(sequence):
    adjacent_splitted = [[sequence[0]]]
    for i in range(1, len(sequence)):
        if sequence[i] == adjacent_splitted[-1][-1]:
            adjacent_splitted[-1].append(sequence[i])
        else:
            adjacent_splitted.append([sequence[i]])
    adjacent_lengths = [len(subsequence) for subsequence in adjacent_splitted]
    return 2 in adjacent_lengths

def is_correct(password, use_adjacency_rules=False):
    digits = list(str(password))
    result = is_non_decreasing(digits) and has_double(digits)
    if use_adjacency_rules:
        result = result and fits_adjacency_rules(digits)
    return result


if __name__ == '__main__':
    pass_range = (353096, 843212 + 1)
    n_passwords = sum((is_correct(p) for p in range(*pass_range)))
    print(f'different passwords: {n_passwords}')
    n_passwords_with_adj_rules = sum((is_correct(p, use_adjacency_rules=True) for p in range(*pass_range)))
    print(f'different passwords (w/ adjacency rules): {n_passwords_with_adj_rules}')
