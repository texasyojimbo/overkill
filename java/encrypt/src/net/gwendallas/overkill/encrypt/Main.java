package net.gwendallas.overkill.encrypt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {

	public static void main(String[] args) {

		String ciphertext = "";
		String randomValid = "";
		String burnList = "";
		int highWaterMark = -1;
		int lastCount = 0;
		int randomCount = 0;
		int randomIndex = 0;
		int minLength = 0;
		
		Random rand = new Random();
		System.out.println(!(args.length == 3) ? "Please enter the following parameters: <valid_character_set_not_including_separator> <separator_token> <plaintext_in_double_quotes>" : "Processing . . .");
		if (args.length == 3) {
			String validCharSet = args[0].trim();
			String separatorToken = args[1].trim().substring(0,1);
			String plaintext = args[2].trim();
			System.out.println("\nValid Character Set:\t" + validCharSet);
			System.out.println("Separator Token:\t" + separatorToken);
			System.out.println("Plaintext:\t\t" + plaintext);
			for (int g=0; g < plaintext.length() + 1; g++) { minLength = minLength + (plaintext.length() - g + 1); }
			System.out.println("\nMinimum Valid Length:\t" + minLength + "\n");
			
			if (!plaintext.contains(separatorToken) && !validCharSet.contains(separatorToken)) {				
				for (int i=1; i<plaintext.length()+1; i++) {
					if (!validCharSet.contains(plaintext.substring(i - 1 ,i))) {
						validCharSet = validCharSet + plaintext.substring(i - 1,i);
						System.out.println("\n** Warning: Adding " + plaintext.substring(i-1,i) + " to the valid character set.\n");
					}
					
					if (plaintext.lastIndexOf(plaintext.substring(i-1,i)) != i-1)
					{
						System.out.println("\n** Error: Plaintext contains the same character ("+ plaintext.substring(i-1,i) +") more than once. This will result in an ambiguous decode. Exiting . . .");
						System.exit(1);
					}
					
					if (highWaterMark == -1) {
						while (highWaterMark < plaintext.length() + 1) { 
							highWaterMark = ((10 * (rand.nextInt(plaintext.length()) + 1) + rand.nextInt(plaintext.length() + 1)));
							randomCount=highWaterMark;
							lastCount=highWaterMark;
						}
					}
					else {
						do {
							randomCount = lastCount - 1 - rand.nextInt(plaintext.length() + 1 - i); 
						} while ((randomCount < plaintext.length() + 2 - i ));
					}
					
					for (int j=1; j < randomCount + 1; j++) {
						ciphertext=ciphertext + plaintext.substring(i-1,i);
						
					}
					System.out.println(plaintext.substring(i-1,i) + "\t\t\t" + randomCount);
					lastCount=randomCount;
				}
				
				do {
					randomCount = lastCount - 1 - rand.nextInt(Math.round(lastCount/2)+1); 
				} while (randomCount < 1 );
				
				for (int k=1; k < randomCount; k++) {
					ciphertext=ciphertext+separatorToken;				
				}	
				
				System.out.println(separatorToken + "\t\t\t" + randomCount);
				burnList = plaintext;

				do {
					randomCount = lastCount - 1 - rand.nextInt(Math.round(lastCount/2)+1);
				} while (randomCount < 1 );

				
				for (int m=1; m < randomCount; m++) {
					
					if (burnList.length() < validCharSet.length()) {
						do {
							randomIndex = rand.nextInt(validCharSet.length());
							randomValid = validCharSet.substring(randomIndex, randomIndex + 1);
						} while (burnList.contains(randomValid));

						burnList = burnList + randomValid;

						for (int n=1; n < rand.nextInt(randomCount); n++) {
							ciphertext=ciphertext+randomValid;												
						}
					}
				}

				List<String> ciphertextList = Arrays.asList(ciphertext.split(""));
				Collections.shuffle(ciphertextList);
				String ciphertextString = "";
				for (int q = 0; q < ciphertextList.size(); q++) {
					ciphertextString = ciphertextString + ciphertextList.get(q);
				}
				System.out.println("\nActual Length:\t\t" + ciphertextString.length());
				System.out.println("\nCiphertext:\t\t" + ciphertextString);
			}
			else {
				System.out.println("\n** Error: Plaintext and valid character set parameters must not contain the separator token. Exiting . . .");
				System.exit(2);
			}
		}
	}
	
}

