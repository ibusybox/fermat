#encode: utf8

def factor(x):
    factors = []
    i = 1
    while i*i <= x:
        if x % i == 0:
            factors.append(i)
            next = x/i
            if next > i and next < x:
                factors.append(next)
        i = i + 1
    return factors

if __name__ == '__main__':
    factors = factor(128)
    print factors
    sum = 0
    for f in factors:
        sum = sum + f
    print sum
