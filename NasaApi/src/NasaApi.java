import java.io.*;
import java.net.*;

public class NasaApi {

	private String prefix = "https://api.nasa.gov/planetary/apod?api_key=INSERT KEY HERE";
	private String url;
	private String filename;

	public NasaApi(String filename) {
		this.filename = filename;
	}

	public int getNasaApi(String start_date, String end_date) {
		URL server;
		HttpURLConnection service;
		BufferedReader input;
		BufferedWriter output;
		String line;
		int status;

		try {

			// combinazioni date accettabili:
			// data+data
			// data+null
			// data
			// null+null da il giorno attuale

			// NON ACCETTABILE:
			// null+data

			// creazione dell'url finale
			if (start_date.equals("") && end_date.equals("")) {
				url = prefix;

			} else if (start_date.equals("") && !end_date.equals("")) {
				url = prefix + "&date=" + URLEncoder.encode(end_date, "UTF-8");

			} else {
				url = prefix + "&start_date=" + URLEncoder.encode(start_date, "UTF-8") + "&end_date="
						+ URLEncoder.encode(end_date, "UTF-8");
			}

			System.out.println(url);
			server = new URL(url);
			service = (HttpURLConnection) server.openConnection();

			// ctrl-c ctrl-v
			service.setRequestProperty("Host", "maps.googleapis.com");
			service.setRequestProperty("Accept", "application/json");
			service.setRequestProperty("Accept-Charset", "UTF-8");
			service.setRequestMethod("GET");
			service.setDoInput(true);
			service.connect();

			// controllo dello stato della richiesta
			status = service.getResponseCode();
			if (status != 200) { // se non si riceve 200, c'è stato un errore
				return status; // errore
			}

			// apertura stream di ricezione da risorsa web
			input = new BufferedReader(new InputStreamReader(service.getInputStream(), "UTF-8"));
			// apertura stream per scrittura su file
			output = new BufferedWriter(new FileWriter(filename));
			// ciclo di lettura da web e scrittura su file
			while ((line = input.readLine()) != null) {
				output.write(line);
				output.newLine();
			}
			// chiusura dello stream di ricezione dal web
			input.close();
			// chiusura dello stream per la scrittura nel file
			output.close();
			return 1;

		} catch (IOException e) {
			System.out.println(e.getStackTrace());
			return -1;
		}
	}
}