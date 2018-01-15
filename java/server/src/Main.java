package net.gwendallas.overkill.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Main {
	
	private static ServerSocket serverSocket;

	public static void main(String[] args) throws IOException {
		
		if (args.length > 0)
		{
			System.out.println(dateString() + "\t# Initializing Overkill Decryption Server...");
			int port = Integer.valueOf(args[0].trim());
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() { try {
			    	System.out.println(dateString() + "\t# Server termination signal received.");
			    	serverSocket.close();
			    	System.out.println(dateString() + "\t# Server terminated.");
				} catch (IOException e) {
					e.printStackTrace();
				} }
			 });
			String validCharSet = "abcdefghijklmnopqrstuvwxyz";
			String separator = "_";
			
			if (args.length > 1)
			{
				validCharSet = args[1].trim();				
				if (args.length > 2)
				{
					separator = args[2].trim();
				}
			}
			
			System.out.println(dateString() + "\t# Valid Characters:\t"+validCharSet);
			System.out.println(dateString() + "\t# Separator:\t\t"+separator);
			validCharSet=validCharSet+separator;
			int[] validCharCount = new int[validCharSet.length()];			
			
			if (port > 1000 && port < 65536)
			{
				try {
					serverSocket = new ServerSocket(port);
					System.out.println(dateString() + "\t# Opened port "+port+".");
				} catch (IOException e) {
					e.printStackTrace();
				}			
			}
			else 
			{
				System.out.println("ERROR: Invalid port specified (port < 1000 or > 65536). Exiting.");
				return;
			}

			Socket socket = null;
			
			while(true)
			{
				
				System.out.println(dateString() + "\t# Server listening.");
				for (int i = 0; i < validCharSet.length(); i++)
				{
					validCharCount[i]=0;
				}

				try {
					socket = serverSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
				}
				InetSocketAddress socketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
				System.out.println(dateString() + "\t# Connection opened from "+socketAddress.getHostString()+".");
				
				BufferedReader inputFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
				
				outputToClient.writeChars(dateString() + "\t<# Connected to Overkill Decrypt Server.\n\r");
				outputToClient.writeChars(dateString() + "\t<# Hit Return to Send New Line.\n\r");
				outputToClient.writeChars(dateString() + "\t<# Ctrl-Z (and then) New Line will hangup.\n\r");
				outputToClient.writeChars(dateString() + "\t<# ---------------------------------------\n\r");
				outputToClient.writeChars(dateString() + "\t># ");
				
				boolean connected = true;

				while(connected)
				{

					String inputLine = inputFromClient.readLine();
					if (inputLine == null || inputLine.startsWith("\u001A"))
					{
						connected = false;
						outputToClient.writeChars(dateString()+ "\t<# Ctrl-Z received. Hanging up.\n\r");
						socket.close();
						System.out.println(dateString()+ "\t# Closed connection.");
						break;
					}
					else
					{
						System.out.println(dateString() + "\t# Received:\t" + inputLine);
						validCharCount = countValidChars(inputLine, validCharSet, validCharCount);
						int[] burnCharCount = validCharCount.clone();
						String plaintext = "";
						for (int k = 0; k < validCharSet.length(); k++)
						{
							int maxCount=0;
							for (int i = 0; i < validCharSet.length(); i++)
							{
								if (burnCharCount[i] > maxCount)
								{
									maxCount = burnCharCount[i];
								}
							}
							for (int i = 0; i < validCharSet.length(); i++)
							{
								if (validCharCount[i] == maxCount && maxCount > 0 && validCharCount[i] > validCharCount[validCharCount.length - 1])
								{
									plaintext = plaintext + validCharSet.charAt(i);
									burnCharCount[i]=0;
								}
							}							
						}
						System.out.println(dateString() + "\t# Processed:\t"+plaintext);
						outputToClient.writeChars(dateString()+ "\t<# Processed:\t"+plaintext+"\n\r" + dateString() + "\t># ");
					}
					
				}	
				
			}
		
			
		}
		
		else
		{
			System.out.println("ERROR: No port specified. Exiting.");
			return;
		}
	}
	
	private static int[] countValidChars (String iText, String vSet, int[] vCharCount)
	{	
		for (int i = 0; i < iText.length(); i++)
		{
			int index = vSet.indexOf(iText.charAt(i));
			if (index >= 0)
			{
				vCharCount[index]++;				
			}
		}	
		return vCharCount;
	}
	
	private static String dateString()
	{
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		String dateString = Calendar.getInstance().getTime().toString();
		return dateString;
	}
}	