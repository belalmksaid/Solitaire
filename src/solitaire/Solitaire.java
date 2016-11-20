package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;
import java.util.NoSuchElementException;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		int aa[] = new int[12];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	
	void print() {
		CardNode ptr = deckRear.next;
		do {
			System.out.print(ptr.cardValue + " ");
			ptr = ptr.next;
		} while(ptr != deckRear.next);
		System.out.println();
		
	}
	
	void jokerA() {
		CardNode ptr = deckRear.next;
		CardNode prev = deckRear;
		
		while(ptr.cardValue != 27) {
			ptr = ptr.next;
			prev = prev.next;
		}
		CardNode post = ptr.next;
		ptr.next = post.next;
		post.next = ptr;
		prev.next = post;
		
		if(ptr == deckRear) {
			deckRear = post;
		}
		else if(deckRear == post) {
			deckRear = ptr;
		}
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
		CardNode ptr = deckRear.next;
		CardNode prev = deckRear;
		
		while(ptr.cardValue != 28) {
			ptr = ptr.next;
			prev = prev.next;
		}
		CardNode post1 = ptr.next, post2 = post1.next;
		ptr.next = post2.next;
		prev.next = post1;
		post2.next = ptr;
		
		if(post2 == deckRear) {
			deckRear = ptr;
		}
		else if(post1 == deckRear) {
			deckRear = post2;
		}
		else if(ptr == deckRear) {
			deckRear = post1;
		}
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		if((deckRear.cardValue == 28 || deckRear.cardValue == 27) && (deckRear.next.cardValue == 27 || deckRear.next.cardValue == 28)) {
			return;
		}
		else {
			CardNode j1 = null, j2 = null, p1 = deckRear, p2 = deckRear, ptr = deckRear.next;
			while(j1 == null || j2 == null) {
				if(ptr.cardValue == 27 || ptr.cardValue == 28) {
					if(j1 == null) j1 = ptr;
					else j2 = ptr;
				}
				ptr = ptr.next;
				if(j1 == null)
					p1 = p1.next;
				if(j2 == null)
					p2 = p2.next;
			}
			if(j1 == deckRear.next) {
				deckRear = j2;
			}
			else if(j2 == deckRear) {
				deckRear = p1;
			}
			else {
				CardNode post = j2.next;
				j2.next = deckRear.next;
				deckRear.next = j1;
				p1.next = post;
				deckRear = p1;				
			}
			
		}
	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {
		int count = deckRear.cardValue == 28 ? 27 : deckRear.cardValue;
		if(count == 27) return;
		CardNode prev = deckRear.next, ptr = null;
		while(prev.next != deckRear) {
			if(count == 1) ptr = prev;
			count--;
			prev = prev.next;
		}
		CardNode first = deckRear.next;
		deckRear.next = ptr.next;
		ptr.next = deckRear;
		prev.next = first;
	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		int key = 27;
		while(key == 27) {
			jokerA();
			jokerB();
			tripleCut();
			countCut();
			int loop = deckRear.next.cardValue == 28 ? 27 : deckRear.next.cardValue;
			CardNode ptr = deckRear;
			for(int i = 0; i < loop; i++) {
				ptr = ptr.next;
			}
			key = ptr.next.cardValue == 28 ? 27 : ptr.next.cardValue;
		}
	    return key;
	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {
		char c;
		String message2 = message.toUpperCase();
		String output = "";
		for(int index = 0; index < message2.length(); index++) {
			c = message2.charAt(index);
			if(!Character.isLetter(c)) continue;
			int key = getKey();
			char res = (char) (((c - 'A' + key) % 26) + 'A');
			output += res;
		}
	    return output;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {
		String message2 = message.toUpperCase();
		String output = "";
		char c;
		for(int i = 0; i < message2.length(); i++) {
			c = message2.charAt(i);
			if(!Character.isLetter(c)) continue;
			int key = getKey();
			char res = (char) (c - key);
			if(res < 'A')
				res += 26;
			output += res;
		}
		return output;
	}
}
