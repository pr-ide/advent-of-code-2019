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
    start, end, fits = 0, 0, False
    while end < len(sequence):
        if sequence[start] == sequence[end]:
            end += 1
        else:
            fits = max(fits, end - start == 2)
            start = end
    return fits or (end - start == 2)

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
