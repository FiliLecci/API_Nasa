import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Parser {

	public ArrayList<Immagine> parseDocument(String filename)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		Document document;
		Element root;
		NodeList elements = null;
		ArrayList<Immagine> imgArray = null;
		// creazione dell'albero DOM dal documento XML
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		document = builder.parse(filename);
		root = document.getDocumentElement();

		// prendo tutti gli oggetti dal file XML
		if ((elements = root.getElementsByTagName("array")) != null && elements.getLength() > 0) {
			// creo l'ArrayList
			imgArray = new ArrayList<Immagine>();
			// for per scorrere la NodeList
			for (int i = 0; i < elements.getLength(); i++) {
				Element immagine = (Element) elements.item(i);
				// ritorna null se l'elemento non è un'immagine
				Immagine tImg = getImage(immagine);
				if (tImg != null) {
					// aggiungo all'array una nuova immagine
					imgArray.add(tImg);
				}
			}

			return imgArray;
		} else {
			imgArray = new ArrayList<Immagine>();
			imgArray.add(getImage(root));
		}
		return imgArray;
	}

	private Immagine getImage(Element element) {
		Immagine img = null;
		String[] par = new String[2];

		// controllo prima di tutto se ho un'immagine
		if (getTextValue(element, "media_type").equals("image")) {
			// prendo l'URL dell'immagine e creo un titolo
			par[0] = getTextValue(element, "hdurl");

			String title = getTextValue(element, "title");
			title = title.replace(":", ";");
			title = title.replace(".", "");
			String copyright = getTextValue(element, "copyright");
			if (copyright != null) {
				copyright = copyright.replace(".", "");
				copyright = copyright.replace(":", ";");
			}
			par[1] = title + "~" + copyright + "~" + getTextValue(element, "date");

			// creazione oggetto Immagine
			img = new Immagine(par[0], par[1]);
		}

		return img;
	}

	private String getTextValue(Element element, String tag) {
		String value = null;
		NodeList nodelist;
		// si prendono tutti i nodi con tag "tag"
		nodelist = element.getElementsByTagName(tag);
		// si prende il primo
		if (nodelist != null && nodelist.getLength() > 0) {
			Text testo = (Text) nodelist.item(0).getFirstChild();
			if (testo != null)
				value = testo.getNodeValue();
			else
				return "";
		}
		return value;
	}
}