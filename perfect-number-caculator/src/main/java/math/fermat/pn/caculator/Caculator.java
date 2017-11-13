package math.fermat.pn.caculator;

import java.util.ArrayList;
import java.util.List;

public class Caculator {	
	public static final List<Long> factor2(long x, int times) {
		List<Long> factors = new ArrayList<Long>();
		long i = 2;
		for (; i*i<x && factors.size()<times; i++) {
			if (x%i == 0) {
				factors.add(i);
				long next = x/i;
				if (next > i && next < x) {
					factors.add(next);
				}
			}
		}
		return factors;
	}
	
	public static void main(String args[]) {
		Caculator.factor2(128, 10).forEach(f -> {System.out.println(f);});
		
	}
}
