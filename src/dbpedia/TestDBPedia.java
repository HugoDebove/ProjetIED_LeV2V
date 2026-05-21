package dbpedia;

import java.util.List;
import java.util.Scanner;
import element.Film;

public class TestDBPedia {

    public static void main(String[] args) {
    	DBPediaService dbpedia = new DBPediaService();
        Scanner reader = new Scanner(System.in);
        
        System.out.println("MÉDIATEUR DBPEDIA");

        while (true) {
            System.out.print("\nEntrez un titre de film (ou 'exit') : ");
            String input = reader.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.println("Recherche en cours pour : " + input + "...");
            
            // On récupère TOUS les films correspondants
            List<Film> films = dbpedia.getFilmDetailsByTitle(input);
            
            if (films == null || films.isEmpty()) {
                System.out.println("Aucun film trouvé sur DBpedia pour ce titre.");
            } else {
                System.out.println("\n>>> [ " + films.size() + " ] RÉSULTAT(S) TROUVÉ(S) <<<");
                
                // On boucle sur chaque film trouvé
                for (Film film : films) {
                    System.out.println("\n------------------------------------------");
                    System.out.println("TITRE       : " + film.getTitre());
                    System.out.println("DATE        : " + (film.getDateSortie() != null ? film.getDateSortie() : "Inconnue"));                    System.out.println("RÉALISATEUR : " + (film.getRealisateur() != null ? film.getRealisateur() : "Inconnu"));
                    System.out.println("PRODUCTEUR  : " + (film.getProducteur() != null ? film.getProducteur() : "Inconnu"));
                    
                    System.out.print("ACTEURS     : ");
                    if (film.getActeurs() != null && !film.getActeurs().isEmpty()) {
                        System.out.println(String.join(", ", film.getActeurs()));
                    } else {
                        System.out.println("Aucun acteur répertorié.");
                    }
                    System.out.println("------------------------------------------");
                }
            }
        }

        System.out.println("Fin du programme.");
        reader.close();
    }
}