package com.prabal.anaphoraResolution;

import java.util.Scanner;

// a class designed to arrange the values of an integer array to give an
// arrangement that gives the maximum value
public class LargestInteger {
	
	public static void main(String args[]){
		// create the object of the class
		LargestInteger obj = new LargestInteger();
		
		// enter the values from the user
		Scanner scn = new Scanner(System.in);
		System.out.println("enter the size of array");
		int size = scn.nextInt();
		int array[] = new int[size];
		System.out.println("enter the array");
		for(int i = 0; i < size; i++)
			array[i] = scn.nextInt();
		scn.close();
		// apply the algorithm for the entered array
		array = obj.calculateLargestValue(array);
		for(int i = 0; i < size; i++)
			System.out.print(array[i]);
	}
	
	// a funtion to concatenate the two integer values
	public int concatNumber(int x, int y){
		String xx = String.format("%d", x);
		String yy = String.format("%d", y);
		return (Integer.parseInt(xx+yy));
	}
	
	// a funtion to implement the final algorithm to check which of the 
	// combination yields the greater number
	public int[] calculateLargestValue(int array[]){
		
		for(int i = 0; i < array.length-1; i++){
			for(int j = i+1; j < array.length; j++){
				if(concatNumber(array[i], array[j]) < 
						concatNumber(array[j], array[i])){
					int temp = array[i];
					array[i] = array[j];
					array[j] = temp;
				}
			}
		}
		return array;
	}
}
