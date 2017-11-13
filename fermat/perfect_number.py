#encode: utf8

# get factors exclude 1 and self
def factor2(x, num=0):
    factors = []
    i = 2
    while i*i <= x and len(factors) < num:
        if x % i == 0:
            factors.append(i)
            next = x/i
            if next > i and next < x:
                factors.append(next)
        i = i + 1
    return factors

if __name__ == '__main__':
    factors = factor2(128,20)
    print factors
    sum = 0
    for f in factors:
        sum = sum + f
    print sum
