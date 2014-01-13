package restresource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		URL url = new URL("https://api:secret4321@pedidos.cgsmoveis.com.br/clients/ahead.json?term=CA");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true); 
		connection.setInstanceFollowRedirects(false); 
		connection.setRequestMethod("GET"); 
		connection.setRequestProperty("Content-Type", "application/json"); 

		Scanner scan = new Scanner(connection.getInputStream()).useDelimiter("\\A");
		String body = scan.next();
		
		connection.getResponseCode();
		connection.disconnect(); 
		
		System.out.println(body);
	}
}
