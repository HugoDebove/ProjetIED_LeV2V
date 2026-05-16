package rest;

import javax.xml.xpath.*;

import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class OMDBService {
	
	static String apiKey = "49fc2268";
	static String uriBase = "http://www.omdbapi.com/?apikey=" + apiKey;
	
	public static void main(String[] args) {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Titre du film : ");
			String title = scanner.nextLine();
			
			String uri = uriBase + convertTitleIntoUri(title) + getXMLUri();
			String plot = (String) XPath(uri, "/root/movie/@plot", XPathConstants.STRING);
			
			System.out.println(plot);
		}
	}
	
	public static Object XPath(String uri, String query, QName returnType){
		//Le dernier paramètre indique le type de résultat souhaité
		//XPathConstants.STRING: chaîne de caractères (String)
		//XPathConstants.NODESET: ensemble de noeuds DOM (NodeList)
		//XPathConstants.NODE: noeud DOM (Node) - le premier de la liste
		//XPathConstants.BOOLEAN: booléen (Boolean) - vrai si la liste n'est pas vide
		//XPathConstants.NUMBER: numérique (Double) - le contenu du noeud sélectionné transformé en Double

		try{
				//Transformation en document DOM du contenu XML
				DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
				DocumentBuilder parseur = fabrique.newDocumentBuilder();
				Document document = parseur.parse(uri);

				//création de l'objet XPath 
	        	XPathFactory xfabrique = XPathFactory.newInstance();
	        	XPath xpath = xfabrique.newXPath();
	        
	        	//évaluation de l'expression XPath
	        	XPathExpression exp = xpath.compile(query);
	        	return exp.evaluate(document, returnType);
	        
	        } catch(Exception e){
	        	System.out.println(e.getMessage());
	        }
	        return null;
    }
	
	private static String convertTitleIntoUri(String title) {
		String[] words = title.split(" ");
		return "&t=" + String.join("+", words);
	}
	
	private static String getXMLUri() {
		return "&r=xml";
	}
	
	

}
