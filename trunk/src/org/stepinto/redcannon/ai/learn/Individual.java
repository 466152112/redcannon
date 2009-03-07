package org.stepinto.redcannon.ai.learn;

import java.util.*;

public class Individual {
	public static final int ELEMENT_NUM = 8;
	public static final int BIT_LENGTH_PER_ELEMENT = 8;
	public static final int TOTAL_LENGTH = ELEMENT_NUM * BIT_LENGTH_PER_ELEMENT;
	
	private BitSet value = new BitSet(TOTAL_LENGTH);
	
	private Individual(BitSet bits) {
		value = bits;
	}
	
	public Individual(int array[]) {
		assert(array.length == ELEMENT_NUM);
		
		for (int i = 0; i < array.length; i++) {
			int s = array[i];
			for (int j = (i+1) * BIT_LENGTH_PER_ELEMENT - 1; j >= i * BIT_LENGTH_PER_ELEMENT; j--) {
				value.set(j, (s & 1) == 1);
				s = s >> 1;
			}
		}
	}
	
	public int[] toIntArray() {
		int result[] = new int [ELEMENT_NUM];
		for (int i = 0, j = 0; i < ELEMENT_NUM; i++) {
			int s = 0;
			for (int k = 0; k < BIT_LENGTH_PER_ELEMENT; k++, j++)
				s = (s << 1) | (value.get(j) ? 1 : 0);
			
			result[i] = s;
		}
		return result;
	}
	
	
	public static Individual crossover(Random random, Individual a, Individual b) {
		BitSet bits = new BitSet(TOTAL_LENGTH);
		int p = random.nextInt(TOTAL_LENGTH);
		
		for (int i = 0; i <= p; i++)
			bits.set(i, a.value.get(i));
		for (int i = p+1; i < TOTAL_LENGTH; i++)
			bits.set(i, b.value.get(i));
		return new Individual(bits);
	}
	
	public Individual mutate(Random random) {
		int p = random.nextInt(TOTAL_LENGTH);
		BitSet bits = (BitSet)value.clone();
		bits.flip(p);
		return new Individual(bits);
	}
}
