import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Ex3Client {
	public static void main(String[] args) {
		try(Socket socket = new Socket("codebank.xyz", 38103)) {
          		System.out.println("Connected to server.");

          		//Create byte streams to communicate to Server
          		InputStream is = socket.getInputStream();
		  	OutputStream os = socket.getOutputStream();
		  
		 	 //Number of bytes to be Sent
		  	int numSent = is.read();
		  	System.out.println("Reading " + numSent + " bytes.");
		  
		 	 //Create Byte Array
		  	byte[] bArray = new byte[numSent];
		  	byte value;
		  	System.out.println("Data received: ");

		  	//Store bytes in Byte Array
		  	for(int i=0; i < numSent; i++) {
				value = (byte) is.read();
				bArray[i] = value;
			  
				if(i%10 == 0) {
					System.out.println();
			  	}
			  	System.out.printf("%02X", bArray[i]);
		 	 }
		  
		  	//Call checkSum
		  	short get = checkSum(bArray);
		  	System.out.println();
		  	System.out.print("Checksum calculated: ");
		  	System.out.printf("0x%02X", get);
		  
			//Send checkSum to Server
		  	for(int i=1; i>= 0; i--) {
				os.write(get >>(8*i));
		  	}
		  
			//Determine if Server response is good
		 	int check = is.read();
		  	if(check == 1) {
				System.out.println("\nResponse good.");
		  	} else {
				System.out.println("\nResponse bad.");
			}
		  
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* checkSum method - Each iteration 1. Takes two bytes to form a 16-bit number then adds to the sum
	* 				   2. Checks for overflow:
	*					If overflow occurs, it is cleared and added back in to the sum
	*				   3. When the sum is calculated we perform ones’ complement 
	*				   4. Return rightmost 16 bits of the sum
	*/
	public static short checkSum(byte[] b) {
		int sum = 0;
		
		//for(int i=0; i < (b.length/2); i++) {
		int i = 0;
		while(i < b.length-1) {
			byte first = b[i];
			byte second = b[i+1];

			//Shift first byte by 8
			//AND upper/lower bytes to mask values
			//Concatenate to create short
			sum += ((first<<8 & 0xFF00) | (second & 0xFF));

			//Check for overflow
			if((sum & 0xFFFF0000) > 0) {
				sum &= 0xFFFF;
				sum++;
			}
			i = i + 2;
		}
		
		//When bArray size is odd
		if((b.length)%2 == 1) {
			byte last = b[(b.length-1)];
			sum += ((last<<8) & 0xFF00);
			
			if((sum & 0xFFFF0000) > 0) {
				sum &= 0xFFFF;
				sum++;
			}
		}
		
		return (short) ~(sum & 0xFFFF);
	}
}
