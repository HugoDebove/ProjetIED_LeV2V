package mediateur;

import java.util.Scanner;

public class CLI {
    
    public static void main(String[] args) {
        DBPediaService dbpedia = new DBPediaService();
        Scanner reader = new Scanner(System.in);
        
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

            System.out.println("Recherche des infos DBpedia pour : " + input + "...");
            
            try {
                // Appel de la nouvelle méthode par titre
                Film film = dbpedia.getFilmDetailsByTitle(input);
                
                // On vérifie si DBpedia a trouvé quelque chose (ex: le réalisateur est rempli)
                if (film == null) {
                    System.out.println("Aucune information trouvée sur DBpedia pour ce titre.");
                } else {
                    System.out.println("\n--- FICHE MÉDIATEUR (SOURCE DBPEDIA) ---");
                    System.out.println("TITRE       : " + film.getTitre());
                    System.out.println("RÉALISATEUR : " + film.getRealisateur());
                    System.out.println("PRODUCTEUR  : " + (film.getProducteur() != null ? film.getProducteur() : "Inconnu"));
                    
                    // Affichage de la liste des acteurs
                    System.out.print("ACTEURS     : ");
                    if (film.getActeurs() != null && !film.getActeurs().isEmpty()) {
                        System.out.println(String.join(", ", film.getActeurs()));
                    } else {
                        System.out.println("Aucun acteur listé.");
                    }
                    System.out.println("------------------------------------------");
                    System.out.println("[Note: Le résumé (OMDb) et les revenus (SQL) seront intégrés ici]");
                }
            } catch (Exception e) {
                System.err.println("Erreur : " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Fermeture du médiateur. Au revoir !");
        reader.close();
    }
}