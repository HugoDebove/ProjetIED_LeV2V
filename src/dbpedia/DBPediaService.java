package dbpedia;

import java.util.ArrayList;
import java.util.List;
import org.apache.jena.query.*;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTP;

import element.Film;

public class DBPediaService {
    private static final String SERVICE_URL = "http://dbpedia.org/sparql";

    public Film getFilmDetailsByTitle(String filmTitle) {
        Film film = new Film();
        film.setTitre(filmTitle);
        List<String> listeActeurs = new ArrayList<>();

        // Utilisation de variables identiques à ton test web : directorName, producerName, actorName
        String sparqlQuery = 
              "PREFIX dbo: <http://dbpedia.org/ontology/> "
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
            + "SELECT DISTINCT ?directorName ?producerName ?actorName WHERE { "
            + "  ?f a dbo:Film ; "
            + "     rdfs:label \"" + filmTitle + "\"@en . "
            + "  ?f dbo:director ?d . "
            + "  ?d rdfs:label ?directorName . "
            + "  OPTIONAL { ?f dbo:producer ?p. ?p rdfs:label ?producerName. FILTER (lang(?producerName)='en') } "
            + "  OPTIONAL { ?f dbo:starring ?a. ?a rdfs:label ?actorName. FILTER (lang(?actorName)='en') } "
            + "  FILTER (lang(?directorName) = 'en') "
            + "}";

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", sparqlQuery)) {
            ResultSet results = qexec.execSelect();
            
            if (!results.hasNext()) return null;

            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                
                // Extraction en utilisant les noms exacts de la requête ci-dessus
                if (film.getRealisateur() == null && soln.contains("directorName")) {
                    film.setRealisateur(soln.getLiteral("directorName").getString());
                }
                if (film.getProducteur() == null && soln.contains("producerName")) {
                    film.setProducteur(soln.getLiteral("producerName").getString());
                }
                if (soln.contains("actorName")) {
                    String actor = soln.getLiteral("actorName").getString();
                    if (!listeActeurs.contains(actor)) {
                        listeActeurs.add(actor);
                    }
                }
            }
            film.setActeurs(listeActeurs);
            return film;
        } catch (Exception e) {
            System.err.println("Erreur technique : " + e.getMessage());
            return null;
        }
    }
}