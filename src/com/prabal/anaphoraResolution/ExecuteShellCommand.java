package com.prabal.anaphoraResolution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
 
public class ExecuteShellCommand {
 
	public static void main(String[] args) throws IOException {
 
		ExecuteShellCommand obj = new ExecuteShellCommand();
 
		String domainName = "google.com";
 
		//in mac oxs
		//String command = "ping 10.1.1.19";
		String command = "sh /home/prabal/hinilmt/sampark/bin/sl/fullparser/fullparser_hin_run.sh /home/prabal/hinilmt/sampark/bin/sl/fullparser/input_data.in";
 
		//in windows
		//String command = "ping -n 3 " + domainName;
 
		//String output = obj.executeCommand(command);
 
		//System.out.println(output);
 
		obj.executeScript(command);
	}
	
	private void executeScript(String command) throws IOException {
		String errOutput = "";
		Process process = Runtime.getRuntime().exec(command);
		String s = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(process
					.getInputStream()));
		while ((s = br.readLine()) != null)
		{
		   s += s + "\n";
		}    
		System.out.println(s);

		BufferedReader br2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		while (br2.ready() && (s = br2.readLine()) != null)
		{
		  errOutput += s;
		}
		System.out.println(errOutput);  
		/*try {
		    ProcessBuilder pb = new ProcessBuilder(
		      command);
		    Process p = pb.start();     // Start the process.
		    p.waitFor();                // Wait for the process to finish.
		    System.out.println("Script executed successfully");
		  } catch (Exception e) {
		    e.printStackTrace();
		  }*/
		}
 
	private String executeCommand(String command) {
 
		StringBuffer output = new StringBuffer();
 
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			//p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
 
                        String line = "";
                        
			while ((line = reader.readLine())!= null) {
				System.out.println(line.toString());
				output.append(line + "\n");
			}
 
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		return output.toString();
 
	}
 
}