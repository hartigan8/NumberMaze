package com.company;
public class Stack {
		private int top;
		private Object[] elements;
		
		Stack(int capacity){
			elements=new Object[capacity];
			top=-1;
		}
		
		void push(Object objectPush) {
			if (!isFull()) {
				top++;
				elements[top]=objectPush;
			}else {
				System.out.println("Stack overflow");
			}

		}
		
		Object pop() {
			if (!isEmpty()) {
				Object returnObject=elements[top];
				elements[top]=null;
				top--;
				return returnObject;	
			}else {
				System.out.println("Stack is empty");
				return null;
			}

		}
		
		Object peek() {
			if (!isEmpty()) {
				return elements[top];
			}else {
				System.out.println("Stack is empty");
				return null;
			}
		}
		
		boolean isEmpty() {
			return (top==-1);
		}
		
		boolean isFull() {
			return (top+1==elements.length);
		}
		
		int size() {
			return (top+1);
		}
		
		
		
}


