package mediateur;

import java.util.ArrayList;
import java.util.Scanner;

import dbpedia.DBPediaService;
import element.Film;

public class CLI {
    
    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        Mediateur med = new Mediateur();
        
        System.out.println("******************************************");
        System.out.println("   MÉDIATEUR CINÉMA - VERSION TERMINAL   ");
        System.out.println("******************************************");

        while (true) {
            // Modification pour demander un titre de film
            System.out.print("\nEntrez le titre d'un film (ou 'exit' pour quitter) : ");
            String input = reader.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.println("Recherche des infos pour : " + input + "...");
            
            med.searchByTitleTest(input);
            /*
            try {
                ArrayList<Film> movies = med.searchByTitle(input);
                
                for(Film movie : movies) {
                	System.out.println(movie.toString());
                }
            } catch (Exception e) {
                System.err.println("Erreur : " + e.getMessage());
                e.printStackTrace();
            }
            */
        }

        System.out.println("Fermeture du médiateur. Au revoir !");
        reader.close();
    }
}