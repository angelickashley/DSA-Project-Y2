package model;

import java.util.function.BiFunction;

public class LeftJustifiedList<T> {
	private T[] array;
	private int count;
	private int capacity;
	private int growthStep;
	
	@SuppressWarnings("unchecked")
	public LeftJustifiedList(int capacity, int growthStep) {
		this.array      = (T[]) new Object[capacity];
		this.count      = 0;
		this.capacity   = capacity;
		this.growthStep = Math.max(growthStep, 1);
	}

	public int size() {
		return this.count;
	}

	public int capacity() {
		return this.capacity();
	}

	public boolean isEmpty() {
		return this.count == 0;
	}

	public T get(int index) {
		if (index >= this.capacity) return null;
		return this.array[index];
	}

	public void set(int index, T value) {
		if (index >= this.capacity) return;
		this.array[index] = value;
	}


	@SuppressWarnings("unchecked")
	void insert(int index, T value) {
		if (index >= this.capacity) return;
		if (index >  this.count) return;

		// if there's not enough space in the list, grow the list by the growth step, and then add it
		if (this.count >= this.capacity) {
			T[] newArray = (T[]) new Object[this.capacity + growthStep];

			for (int i = 0; i < this.capacity; i++) {
				newArray[i] = this.array[i];
			}
			this.array = newArray;
			this.capacity += growthStep;
			
		}

		for (int i = this.count; i > index; i--) {
			this.array[i] = this.array[i - 1];
		}
		this.array[index] = value;
		this.count++;
	}

	public void append(T value) {
		insert(this.size(), value);
	}

	public void remove(int index) {
		if (index >= this.capacity) return;
		if (index >= this.count) return;

		for (int i = index; i < this.count; i++) {
			this.array[i] = this.array[i + 1];
		}
		this.count--;
	}

	public int indexOf(T value) {
		for (int i = 0; i < this.count; i++) {
			if (this.array[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}

	public boolean contains(T value) {
		return indexOf(value) != -1;
	}

	/**
	 * uses bubble sort to sort arbitrary data, given a comparator function
	 * @param comparator the function used to decide whether one element is bigger than another<br/>
	 * for example <code>Student::compareNameTo</code> will sort by name alphabetically
	 * @param lowToHigh whether to sort low to high (true) or high to low (false)
	 */
	public void sort(BiFunction<T, T, Integer> comparator, boolean lowToHigh) {
		/* swap truth table
		a?b|dir|swap?
		---+---+-----
		a>b|l2h| yes 
		a>b|h2l|  no 
		a=b|l2h|  no 
		a=b|h2l|  no 
		a<b|l2h|  no 
		a<b|h2l| yes 
		*/
		boolean swapOccurred = false;
		do {
			swapOccurred = false;
			for (int i = 1; i < count; i++) {
				T a = array[i - 1];
				T b = array[i];
				int comparison = comparator.apply(a,b);
				boolean swap = (comparison > 0 && lowToHigh) || (comparison < 0 && !lowToHigh);
				if (swap) {
					T c          = array[i - 1];
					array[i - 1] = b;
					array[i]     = c;
					swapOccurred = true;
				}
			}
		} while (swapOccurred);
	}

	@Override 
	public String toString() {
		String str = "{";
		for (int i = 0; i < this.count; i++) {
			str += this.array[i].toString();
			if (i < this.count - 1) str += ", ";
			
		}
		str += "}";
		return str;
	}
}
