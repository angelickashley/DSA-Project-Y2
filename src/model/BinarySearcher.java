package model;

import java.util.function.BiFunction;

public class BinarySearcher<T> {
	private int start;
	private int end;

	private T                    searchKey;
	private LeftJustifiedList<T> list;
	private BiFunction<T, T, Integer> comparator;

	private searchStateEnum currentState;


	public BinarySearcher(T key, LeftJustifiedList<T> list, BiFunction<T, T, Integer> comparator) {
		this.searchKey  = key;
		this.list       = list;
		this.comparator = comparator;

		this.start = 0;
		this.end   = list.size() - 1;

		this.currentState = searchStateEnum.SEARCHING;
	}

	public int start() { return this.start; }
	public int mid()   { return (this.start + this.end) / 2; }
	public int end()   { return this.end; }

	public boolean foundKey() { return currentState == searchStateEnum.FOUND; }
	public searchStateEnum internalState() { return currentState; }

	public searchStateEnum stepAlgorithm() {
		if (start() > end()) {
			this.currentState = searchStateEnum.NOT_IN_LIST;
		}
		else if (this.currentState == searchStateEnum.SEARCHING) {
			int comparison = this.comparator.apply(list.get(mid()), this.searchKey);
			
			if (comparison < 0) { // [mid] < key
				this.currentState = searchStateEnum.SEARCHING;
				this.start = mid() + 1;
			}
			else if (comparison == 0) { // [mid] == key
				this.currentState = searchStateEnum.FOUND;
			}
			else if (comparison > 0) { // [mid] > key
				this.currentState = searchStateEnum.SEARCHING;
				this.end = mid() - 1;
			}
		}

		return this.currentState;
	}

	public int getKeyIndex() {
		if (this.currentState == searchStateEnum.FOUND) {
			return mid();
		}
		return -1;
	}
}
