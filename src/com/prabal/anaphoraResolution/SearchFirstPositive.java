package com.prabal.anaphoraResolution;

// A class designed to search for the first value of x for which f(x) becomes 
// positive
public class SearchFirstPositive {

	public static void main(String args[]){
		SearchFirstPositive obj = new SearchFirstPositive();
		System.out.println(obj.findFirstPositive());
	}
	
	// a funtion to simulate the required funtion (monotonically increasing)
	int function(int x) {
		return (x*x - 10*x - 20);
	}

	// a funtion to define the upper limit for the binary search
	public int findFirstPositive() {

		if (function(0) > 0)
			return 0;

		int i = 1;
		while (function(i) <= 0)
			i = i * 2;

		return binarySearch(i/2, i);
	}

	// a standard binary search algorithm
	int binarySearch(int low, int high) {
		if (high >= low) {
			int mid = low + (high - low) / 2;
			if (function(mid) > 0 && (mid == low || 
					function(mid - 1) <= 0))
				return mid;

			if (function(mid) <= 0)
				return binarySearch((mid + 1), high);
			else
				return binarySearch(low, (mid - 1));
		}
		return -1;
	}
}