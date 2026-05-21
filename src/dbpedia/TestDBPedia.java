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
            System.out.println("\n--- MENU DE RECHERCHE ---");
            System.out.println("1. Rechercher par titre de film");
            System.out.println("2. Rechercher les films d'un acteur");
            System.out.println("3. Quitter (ou tapez 'exit')");
            System.out.print("Votre choix : ");
            
            String choix = reader.nextLine().trim();

            if (choix.equalsIgnoreCase("exit") || choix.equals("3")) {
                break;
            }

            if (!choix.equals("1") && !choix.equals("2")) {
                System.out.println("Choix invalide. Veuillez entrer 1, 2 ou 3.");
                continue;
            }

            // Demande de la saisie selon le choix de l'utilisateur
            if (choix.equals("1")) {
                System.out.print("Entrez un titre de film : ");
            } else {
                System.out.print("Entrez le nom d'un acteur : ");
            }
            
            String input = reader.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("La saisie ne peut pas être vide.");
                continue;
            }

            System.out.println("Recherche en cours pour : " + input + "...");
            
            // Déclaration de la liste qui va accueillir les résultats
            List<Film> films;
            
            // On appelle la bonne méthode selon le choix initial
            if (choix.equals("1")) {
                films = dbpedia.getFilmDetailsByTitle(input);
            } else {
                films = dbpedia.getFilmsByActorName(input);
            }
            
            // Affichage des résultats (le bloc reste identique et générique)
            if (films == null || films.isEmpty()) {
                System.out.println("Aucun film trouvé sur DBpedia pour cette recherche.");
            } else {
                System.out.println("\n>>> [ " + films.size() + " ] RÉSULTAT(S) TROUVÉ(S) <<<");
                
                for (Film film : films) {
                    System.out.println("\n------------------------------------------");
                    System.out.println("TITRE       : " + film.getTitre());
                    System.out.println("DATE        : " + (film.getDateSortie() != null ? film.getDateSortie() : "Inconnue"));
                    System.out.println("RÉALISATEUR : " + (film.getRealisateur() != null ? film.getRealisateur() : "Inconnu"));
                    System.out.println("PRODUCTEUR  : " + (film.getProducteur() != null ? film.getProducteur() : "Inconnu"));
                    
                    // On n'affiche la ligne des acteurs que s'il y en a (la recherche par acteur n'en ramène pas par défaut avec votre requête SPARQL actuelle)
                    if (film.getActeurs() != null && !film.getActeurs().isEmpty()) {
                        System.out.println("ACTEURS     : " + String.join(", ", film.getActeurs()));
                    }
                    System.out.println("------------------------------------------");
                }
            }
        }

        System.out.println("Fin du programme. À bientôt !");
        reader.close();
    }
}