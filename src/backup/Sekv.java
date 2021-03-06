
public class Sekv {

	/** N.B. Sorterer a[] stigende - antar at: 0 < a[i]) < 2**32    */

	  // viktig konstant 
	   final static int NUM_BIT =8; // eller 6,8,9,10..
		
	    int []  radixMulti(int [] a) {
			  long tt = System.nanoTime();
			  // 1-5 digit radixSort of : a[]
			  int max = a[0], numBit = 2, numDigits, n =a.length;
			  int [] bit ;

			 // a) finn max verdi i a[]
			  for (int i = 1 ; i < n ; i++)
				   if (a[i] > max) max = a[i];
			  while (max >= (1L<<numBit) )numBit++; // antall siffer i max
			 
			  // bestem antall bit i numBits sifre
			  numDigits = Math.max(1, numBit/NUM_BIT);
			  bit = new int[numDigits];
			  int rest = numBit%NUM_BIT, sum =0;
			  System.out.println(max + " " + numBit + " " + numDigits);
			  // fordel bitene vi skal sortere paa jevnt
			  for (int i = 0; i < bit.length; i++){
				  bit[i] = numBit/numDigits;
			      if ( rest-- >0) bit[i]++;
			  }

			  int[] t=a, b = new int [n];
			  System.out.println("Bit length = " + bit.length);
			  for (int i =0; i < bit.length; i++) {
				  radixSort( a,b,bit[i],sum );    // i-te siffer fra a[] til b[]
				  sum += bit[i];
				  // swap arrays (pointers only)
				  t = a;
				  a = b;
				  b = t;
			  }
			  if ((bit.length&1) != 0 ) {
				  // et odde antall sifre, kopier innhold tilbake til original a[] (n b)
				  System.arraycopy (a,0,b,0,a.length);
			  }

			  double tid = (System.nanoTime() -tt)/1000000.0;
			  System.out.println("\nSorterte "+n+" tall paa:" + tid + "millisek.");
			  testSort(a);
			  
			  for (int i = a.length - 1; i > a.length - 30; i--) {
					System.out.println(i + " " + a[i]);
				}
			
			  return a;
		 } // end radix2


		/** Sort a[] on one digit ; number of bits = maskLen, shiftet up 'shift' bits */
		 void radixSort ( int [] a, int [] b, int maskLen, int shift){
	                  System.out.println(" radixSort maskLen:"+maskLen+", shift :"+shift);
			  int  acumVal = 0, j, n = a.length;
			  int mask = (1<<maskLen) -1;
			  int [] count = new int [mask+1];
			//  System.out.println("Sekv count length = " + count.length);
			 // b) count=the frequency of each radix value in a
			  for (int i = 0; i < n; i++) {
				 count[(a[i]>>> shift) & mask]++;
			  }
			  
			
			 // c) Add up in 'count' - accumulated values, i.e pointers
			  for (int i = 0; i <= mask; i++) {
				   j = count[i];
				   count[i] = acumVal;
				   acumVal += j;
				  // System.out.println(acumVal);
			   }
			  
			 
			 // d) move numbers in sorted order a to b
			  for (int i = 0; i < n; i++) {
				 b[count[(a[i]>>>shift) & mask]++] = a[i];
			  }
		}// end radixSort
		void testSort(int [] a){
			for (int i = 0; i< a.length-1;i++) {
			   if (a[i] > a[i+1]){
			      System.out.println("SorteringsFEIL paa plass: "+
	                                              i +" a["+i+"]:"+a[i]+" > a["+(i+1)+"]:"+a[i+1]);
			      return;
			  }
		  }
		 }// end simple sorteingstest
}
