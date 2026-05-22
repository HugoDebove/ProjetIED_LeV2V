package mediateur;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import element.Film;

public class CLI {

    public static void main(String[] args) {
        Mediateur mediateur = new Mediateur();
        Scanner reader = new Scanner(System.in);
        
        System.out.println("MÉDIATEUR DE FILMS");

        while (true) {
            System.out.println("\n--- MENU DE RECHERCHE ---");
            System.out.println("1. Rechercher un film par son titre");
            System.out.println("2. Rechercher les films d'un acteur/actrice");
            System.out.println("3. Quitter");
            System.out.print("Votre choix : ");
            
            String choix = reader.nextLine().trim();

            if (choix.equalsIgnoreCase("exit") || choix.equals("3")) {
                break;
            }

            if (!choix.equals("1") && !choix.equals("2")) {
                System.out.println("Choix invalide. Veuillez entrer 1, 2 ou 3.");
                continue;
            }
            
            // RECHERCHE PAR TITRE
            if (choix.equals("1")) {
                System.out.print("Entrez le titre du film à rechercher : ");
                String input = reader.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("La saisie ne peut pas être vide.");
                    continue;
                }

                System.out.println("\n[Médiateur] Lancement des requêtes simultanées...");
                System.out.println("[Médiateur] Croisement des données (BD locale + OMDB + DBpedia)...");
                
                ArrayList<Film> filmsFusionnes = mediateur.searchByTitle(input);
                
                if (filmsFusionnes == null || filmsFusionnes.isEmpty()) {
                    System.out.println("Aucun film trouvé, même après tentative de fusion des sources.");
                } else {
                    System.out.println("\n " + filmsFusionnes.size() + " FILM(S) RECONSTITUÉ(S) PAR LE MÉDIATEUR");
                    for (Film film : filmsFusionnes) {
                        System.out.println(film.toString());
                    }
                }
            } 
            // RECHERCHE PAR ACTEUR
            else if (choix.equals("2")) {
                System.out.print("Entrez le nom de l'acteur / actrice : ");
                String input = reader.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("La saisie ne peut pas être vide.");
                    continue;
                }

                System.out.println("\n[Médiateur] Enchaînement des sources (DBpedia -> OMDB -> BD locale)...");
                
                List<Film> filmsActeur = mediateur.searchByActorName(input);
                
                if (filmsActeur == null || filmsActeur.isEmpty()) {
                    System.out.println("Aucun film trouvé pour cet acteur.");
                } else {
                    System.out.println("\n " + filmsActeur.size() + " FILM(S) TROUVÉ(S) POUR " + input.toUpperCase());
                    for (Film film : filmsActeur) {
                        System.out.println(film.toStringActeur());
                    }
                }
            }
        }

        System.out.println("Fin du programme");
        reader.close();
    }
}