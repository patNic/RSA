import java.math.BigInteger;
import java.util.HashMap;

public class RSABigInteger {

	private char[] characters = {' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z', '.', ':', '\''};
	private String encryptedMessage;
	private BigInteger pubKey_N, pubKey_E;
	private BigInteger privKey_D;
	private HashMap<BigInteger, BigInteger> primes1;
	private BigInteger[] primePairs;
	
	public RSABigInteger(String encrypted, BigInteger N_key, BigInteger E_key) {
		encryptedMessage = encrypted;
		pubKey_N = N_key;
		pubKey_E = E_key;
		
		primes1 = new HashMap<BigInteger, BigInteger>();
		primePairs = new BigInteger[2];
		sieveOfEratosthenes1(pubKey_N.divide(BigInteger.valueOf(2)));
		generateP_and_Q();
		checkPrimePairs();
		System.out.println("\nPlain Text:  "+toPlainText());
	}
	public String decrypt() {
		String decryptedMessage = "";
		String[] letters = encryptedMessage.split(" ");
		
		for(int i = 0; i < letters.length; i++) {
			String str = ""+ new BigInteger(""+letters[i]).pow(privKey_D.intValue()).mod(pubKey_N);
			int num = str.length();
			for(int j = 0; j < 6 - num ; j++)
				str = "0"+str;
			System.out.println("Decrypt "+ (i+1) + ": "+ str);
			decryptedMessage += str;
		}
		System.out.println("Decrypted Message");
		for(int i = 0; i < decryptedMessage.length(); i+=6) {
			System.out.print(decryptedMessage.substring(i, i+6) + "  ");
		}
		return decryptedMessage;
	}
	 public void sieveOfEratosthenes1(BigInteger n)
	 {
	     HashMap<BigInteger, Boolean> prime = new HashMap<BigInteger, Boolean>();
	     for(BigInteger i= BigInteger.valueOf(0); i.compareTo(n) < 0 ; i = i.add(BigInteger.ONE))
             prime.put(i, true); 
	        for(BigInteger p= BigInteger.valueOf(2); p.multiply(p).compareTo(n) <= 0;p = p.add(BigInteger.ONE)){
	          if(prime.get(p) == true){
	             for(BigInteger i = p.multiply(BigInteger.valueOf(2)); i.compareTo(n) <= 0; i = i.add(p))
	               prime.put(i, false);;
	          }
	        }
	        BigInteger index = BigInteger.valueOf(0);
	        for(BigInteger i= BigInteger.valueOf(2); i.compareTo(n) <= 0;  i = i.add(BigInteger.ONE)){
	            if(prime.get(i) == true)
	            {
	            	 primes1.put(index, i);
		             index = index.add(BigInteger.ONE);
	            }
	        }
	} 
	public void generateP_and_Q() {
		for(int i = 0; i < primes1.size(); i++) {
			BigInteger remainder = (pubKey_N.mod(new BigInteger(primes1.get(BigInteger.valueOf(i))+"")));
			BigInteger quotient =(pubKey_N.divide(new BigInteger(primes1.get(BigInteger.valueOf(i))+"")));
			if(remainder == BigInteger.valueOf(0) && primes1.containsValue(quotient)) {
				primePairs[0] = quotient;
				primePairs[1] = primes1.get(BigInteger.valueOf(i));
				break;
			}
		}
		System.out.println("Prime pairs: "+ primePairs[0] + " "+ primePairs[1]);
	}
	
	public void checkPrimePairs() {
		BigInteger totient = (primePairs[0].subtract(BigInteger.valueOf(1))).multiply(primePairs[1].subtract(BigInteger.valueOf(1)));
			
		BigInteger gcd = computeGCD1(pubKey_E, totient);
			if(gcd.equals(BigInteger.valueOf(1))&& pubKey_E.compareTo(totient) < 0) {
				modInverse(pubKey_E, totient);
			}
	}
	public BigInteger computeGCD1(BigInteger num1, BigInteger num2) {
		if(num2 == BigInteger.valueOf(0)){
			return num1; 
		} 
		return computeGCD1(num2, num1.mod(num2));
	}
	
	public BigInteger modInverse(BigInteger a, BigInteger m) {

	    BigInteger m0 = m;
	    BigInteger y = BigInteger.valueOf(0), x = BigInteger.valueOf(1);
	 
	    if (m.equals(BigInteger.valueOf(1)))
	       return BigInteger.valueOf(0);
	    
	     while (a.compareTo(BigInteger.valueOf(1)) > 0){
	    	 BigInteger q = a.divide(m);
	    	 BigInteger t = m;
	    	 
	         m = a.mod(m);
	         a = t;
	         t = y;
	         y = x.subtract(q.multiply(y));
	         x = t;
	     }
	     if (x.compareTo(BigInteger.valueOf(0)) < 0)
	       x = x.add(m0);
	     
	     privKey_D = x;
	     System.out.println("D "+x);
	     return x;
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
		new RSABigInteger(encrypt, new BigInteger("999797"), new BigInteger("123457"));
	}
}
