import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.json.*;

public class App {
    public static void main(String[] args) throws Exception {
        final String jsonFileName = "C:\\Users\\user\\Desktop\\data\\Nasa.json", XMLFileName = "C:\\Users\\user\\Desktop\\data\\NasaXML.xml";
        File f, xmlFile, dataDirectory;
        FileWriter fw;
        Scanner scan = null;
        String jsonString = "";
        String start_date = "", end_date = "";
        int returnApi;

        dataDirectory = new File("C:\\Users\\user\\Desktop\\data");
        if (!dataDirectory.exists() && dataDirectory.mkdirs()) {
            System.out.println("directory 'data' creata");
        } else {
            System.out.println("impossibile creare directory 'data' / già esitente");
        }
        // stabilisco la connessione
        NasaApi n = new NasaApi(jsonFileName);
        if ((returnApi = n.getNasaApi(start_date, end_date)) != 1) {
            // in caso di errore ritorna il codice che corrisponde all'errore
            System.out.println("Ritornato codice " + returnApi);
            return;
        }
        System.out.println("ok");

        // leggo il file json
        f = new File(jsonFileName);
        scan = new Scanner(f);
        while (scan.hasNextLine()) {
            jsonString += scan.nextLine();
        }

        Object json;
        // non posso sapere se il mio JSON sarà composto da un array di elementi oppure
        // da un solo elemento
        // siccome la libreria li tratta in modo diverso, uso JSONArray per gli array di
        // elementi e JSONObject
        // per un singolo elemento.
        // Dal momento che le sintassi sono diverse, se provo a creare un JSONArray da
        // un codice che non è un array
        // mi viene lanciata un'eccezione.
        try {
            // controllo se è un array
            json = new JSONArray(jsonString);

        } catch (JSONException e) {
            // se non è un array controllo se è un oggetto singolo
            try {
                json = new JSONObject(jsonString);

            } catch (JSONException ex) {
                // se arrivo qua il codice JSON non è valido
                e.printStackTrace();
                scan.close();
                return;
            }
        }
        xmlFile = new File(XMLFileName);

        // se il file xml è già esistente lo elimino perché non me lo fa sovrascrivere (bastardo)
        if (xmlFile.exists()) {
            if (xmlFile.delete()) {
                System.out.println("file eliminato");
            } else {
                System.out.println("<Err> errore durante l'eliminazione del file xml");
                scan.close();
                return;
            }
        }

        // creo file xml
        if (xmlFile.createNewFile()) {
            System.out.println("File creato con successo");
        } else {
            System.out.println("File non creato");
        }

        // creo il file writer e scrivo il file
        fw = new FileWriter(xmlFile);
        // effettiva conversione da json a xml
        try {
            String xml = XML.toString(json);
            // la conversione da json a xml non aggiunge una radice quindi ne aggiungo una
            fw.write("<NASA>");
            fw.write(xml);
            fw.write("</NASA>");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        // chiudo il file
        fw.close();

        File directory = new File("C:\\Users\\user\\Desktop\\immagini");
        if (!directory.exists() && directory.mkdirs()) {
            System.out.println("directory creata");
        } else {
            System.out.println("impossibile creare directory / già esitente");
        }

        Parser parser = new Parser();

        ArrayList<Immagine> imgArray = parser.parseDocument(XMLFileName);

        for (Immagine temp : imgArray) {
            BufferedImage image = null;
            try {

                URL url = new URL(temp.getHdURL());
                image = ImageIO.read(url);
                ImageIO.write(image, "jpg", new File("C:\\Users\\user\\Desktop\\immagini\\" + temp.getTitolo() + ".jpg"));
                System.out.println("<OK> Immagine: " + temp.getTitolo() + " creata");

            } catch (Exception e) {
                System.out.println("<Err> Immagine: " + temp.getTitolo() + " non creata:");
                e.printStackTrace();
            }
        }
        scan.close();
    }
}