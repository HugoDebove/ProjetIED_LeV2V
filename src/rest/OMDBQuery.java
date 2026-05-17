package rest;

import javax.xml.xpath.*;

import java.util.Locale;
import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import element.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class OMDBQuery {
	
	static String apiKey = "49fc2268";
	static String uriBase = "http://www.omdbapi.com/?apikey=" + apiKey;
	
	/**
	 * Permet de récuperer un film avec differente informations donnée (titre, année)
	 * 
	 * @param title - titre du film
	 * @param year - année de sortie du film (pas obligatoire pour trouver un film)
	 * @return un film avec son resume et la date de sortie
	 */
	public Film getMovie(String title, String year) {
		Film movie = new Film();
		
		String uri = createURI(title, year);

		String plot = (String) XPath(uri, "/root/movie/@plot", XPathConstants.STRING);
		String released = (String) XPath(uri, "/root/movie/@released", XPathConstants.STRING);
		
		if(!plot.isEmpty()) {
			movie.setTitre(title);
			movie.setResume(plot);
			if(!released.isEmpty()) {
				movie.setDateSortie(convertReleaseDate(released));				
			}
		}
		else {
			System.out.println("Aucun film trouvée avec OMDB");
		}
				
		return movie;
	}
	
	public static Object XPath(String uri, String query, QName returnType){
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
	
	/**
	 * Permet de créer l'uri complete pour interroger la source omdb
	 * 
	 * @param title - titre du film
	 * @param year - année de sorti du film
	 * @return une chaine de caractere correspondant a l'uri complete
	 */
	private String createURI(String title, String year) {
		String uri = uriBase + convertTitleIntoUri(title);
		
		if(!year.isEmpty()) {
			uri += convertYearIntoUri(year);
		}
		
		uri += getXMLUri();
		
		return uri;
	}
	
	/**
	 * Permet de convertir le titre pour pouvoir l'utiliser dans l'uri
	 * 
	 * @param title - titre du film
	 * @return chaine de caractere pouvant être utilisée dans l'uri
	 */
	private String convertTitleIntoUri(String title) {
		String[] words = title.split(" ");
		return "&t=" + String.join("+", words);
	}
	
	/**
	 * Permet de convertir l'année de sortie du film pour pouvoir l'utiliser dans l'uri
	 * 
	 * @param year - année de sortie du film
	 * @return chaine de caractere pouvant être utilisée dans l'uri
	 */
	private String convertYearIntoUri(String year) {
		return "&y=" + year;
	}
	
	/**
	 * Permet d'obtenir une chaine de caractere qui nous permet de récuperer le resultat sous le format XML
	 * 
	 * @return chaine de caractere pouvant être utilisée dans l'uri
	 */
	private String getXMLUri() {
		return "&r=xml";
	}
	
	/**
	 * Permet de modifier le format de la date de sortie
	 * 
	 * @param dateOriginal - date sous le mauvais format (dd MMM yyyy)
	 * @return la date sous le bon format (dd/MM/yyyu)
	 */
	private String convertReleaseDate(String dateOriginal) {
		DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
		DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDate date = LocalDate.parse(dateOriginal, inputFormat);
		String newDate = date.format(outputFormat);

		return newDate; 
	}
	

}
