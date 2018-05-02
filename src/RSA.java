import java.math.BigInteger;
import java.util.ArrayList;

public class RSA {

	private char[] characters = {' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z', '.', ':', '\''};
	private String encryptedMessage;
	private BigInteger pubKey_N, pubKey_E;
	private int privKey_D;
	private ArrayList<Integer> primes;
	private ArrayList<Integer[]> primePairs;
	private ArrayList<Integer[]> possiblePairs;
	
	public RSA(String encrypted, BigInteger N_key, BigInteger E_key) {
		encryptedMessage = encrypted;
		pubKey_N = N_key;
		pubKey_E = E_key;
		primePairs = new ArrayList<Integer[]>();
		possiblePairs = new ArrayList<Integer[]>();
		primes = new ArrayList<Integer>();
		sieveOfEratosthenes(N_key.intValue()/2);
		generatePrimePairs();
		checkPrimePairs();
		//solveLinearCongruence(693647,4074, 874164);
		System.out.println("\nPlain Text:  "+toPlainText());
	}
	/*public int[]  getLetters(String e) {
		int[] letters = new int[e.length()/2];
		int index = 0;
		for(int i = 0; i < e.length(); i+=2) {
			letters[index] = Integer.parseInt(e.substring(i, i+2));
			index++;
		}
		return letters;
	}*/
	public String decrypt() {
		String decryptedMessage = "";
		String[] letters = encryptedMessage.split(" ");
		
		for(int i = 0; i < letters.length; i++) {
			String str = ""+ new BigInteger(""+letters[i]).pow(privKey_D).mod(pubKey_N);
			int num = str.length();
			for(int j = 0; j < 6 - num ; j++)
				str = "0"+str;
			decryptedMessage += str;
			System.out.println("Decrypt "+ str);
		}
		System.out.println("Decrypted Message");
		for(int i = 0; i < decryptedMessage.length(); i+=6) {
			System.out.print(decryptedMessage.substring(i, i+6) + "  ");
		}
		return decryptedMessage;
	}

	/*private String encrypt() {
		String encrypt = "";
		String[] letters =encryptedMessage.split(" ");
		for(int i = 0; i < letters.length; i++) {
			String str = ""+ new BigInteger(""+letters[i]).pow(pubKey_E.intValue()).mod(pubKey_N);
			System.out.println("Mes "+letters[i]);
			int num = str.length();
			for(int j = 0; j < 6 - num ; j++)
				str += "0";
			System.out.println("Str "+str);
			encrypt = encrypt+str+ " ";
		}
		return encrypt;
	}*/
	 public void sieveOfEratosthenes(int n)
	 {
	     boolean prime[] = new boolean[n+1];
	     for(int i=0;i<n;i++)
           prime[i] = true;  
	       for(int p = 2; p*p <=n; p++){
	          if(prime[p] == true){
	             for(int i = p*2; i <= n; i += p)
	               prime[i] = false;
	          }
	        }
	        for(int i = 2; i <= n; i++){
	            if(prime[i] == true)
	               primes.add(i);
	        }
	} 
	public void generatePrimePairs() {
		System.out.println("Prime Size "+ primes.size());
		int n = pubKey_N.intValue();
		
		for(int i = 0; i < primes.size(); i++) {
			int remainder = n%primes.get(i);
			int quotient = n/primes.get(i);
			if(remainder == 0 && primes.contains(quotient)) {
				Integer[] pair = {quotient, primes.get(i)};
				primePairs.add(pair);
				
				primes.remove(i);
				i -=1;
			}
		}
		for(int i = 0; i < primePairs.size(); i++)
			System.out.println("Prime pairs "+ primePairs.get(i)[0] + " "+ primePairs.get(i)[1]);
	}
	
	public void checkPrimePairs() {
		for(int i = 0; i < primePairs.size(); i++) {
			int totient = (primePairs.get(i)[0] -1) * (primePairs.get(i)[1] -1);
			
			int gcd = computeGCD(pubKey_E.intValue(), totient);
			if(gcd == 1 && pubKey_E.intValue() < totient) {
				possiblePairs.add(primePairs.get(i));
				modInverse(pubKey_E.intValue(), totient);
			}
		}
	}
	public int computeGCD(int num1, int num2) {
		if(num2 == 0){
			return num1; 
		} 
		return computeGCD(num2, num1%num2);
	}
	public int modInverse(int a, int m) {

		System.out.println("GCD "+computeGCD(a, m));
		
	    int m0 = m;
	    int y = 0, x =1;
	 
	    if (m == 1)
	       return 0;
	 
	     while (a > 1){
	    	 int q = a / m;
	    	 int t = m;
	    	 
	         m = a % m;
	         a = t;
	         t = y;
	         y = x - q * y;
	         x = t;
	     }
	     if (x < 0)
	       x += m0;
	     privKey_D = x;
	     return x;
	}
	
	public int modularInverse(int a , int m) {
		int x = 0, y = 1, lastx = 1, lasty = 0, temp;
        while (m != 0)
        {
            int q = a / m;
            int r = a % m;
 
            a = m;
            m = r;
 
            temp = x;
            x = lastx - q * x;
            lastx = temp;
 
            temp = y;
            y = lasty - q * y;
            lasty = temp;            
        }
        System.out.println("Roots  x : "+ lastx +" y :"+ lasty);
      
        return lastx;
	}
	public void solveLinearCongruence(int a, long b,int m) {
		int gcd = computeGCD(a, m);

		if (b%gcd == 0) {
			int a1 = a/gcd;
			int m1 = m/gcd;
			long b1 = b/gcd;
			int x = modInverse(a1,m1);
			
			long num1 = x*b1;
			long sol = num1 - (m1)*(num1/m1);
			
			long[] solutions = new long[gcd];
			
			System.out.println("\nSolutions less than " + m);
			for(int i = 0; i < solutions.length; i++) {
				solutions[i] = sol + m1*i;
				System.out.println("\tSolution " + (i+1) + ": "+ solutions[i]);
			}
		}
		else {
			System.out.println("No solutions can be found");
		}
	}
	public String toPlainText() {
		String toConvert = decrypt();
		String plainText = "";
		for(int i = 0; i < toConvert.length(); i+=2) {
			String str = toConvert.substring(i, i+2);
			int index = Integer.parseInt(str);
			plainText += characters[index];
		}
		
		return plainText;
	}
	public static void main(String[] args) {
		String encrypt = "082976 371981 814231 505650 853440 353277 596004 250518 "
				+ "494162 922046 540928 633792 779152 973836 494176 019498 "
				+ "125267 683832 244888 922046 522776 395123 915899 132032 "
				+ "620457 568301 878543 623328 746341 710542";
		new RSA(encrypt, new BigInteger("999797"), new BigInteger("123457"));
	}
}
