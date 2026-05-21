package dbpedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.util.Context;

import element.Film;

public class DBPediaService {

    private static final String SERVICE_URL =
            "https://dbpedia.org/sparql";

    public List<Film> getFilmDetailsByTitle(String filmTitle) {

        Map<String, Film> filmsTrouves = new HashMap<>();

        String titreRecherche = filmTitle.trim();

        String sparqlQuery =

                "PREFIX dbo: <http://dbpedia.org/ontology/> \n"
              + "PREFIX dbp: <http://dbpedia.org/property/> \n"
              + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
              + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n"

              + "SELECT DISTINCT ?f ?label "
              + "?directorName ?producerName ?actorName "
              + "WHERE { \n"

              + "  ?f a dbo:Film . \n"

              + "  { \n"
              + "     ?f rdfs:label ?label . \n"
              + "     FILTER(lang(?label)='en') \n"
              + "     FILTER(LCASE(STR(?label)) = LCASE(\""
              + titreRecherche
              + "\")) \n"
              + "  } \n"

              + "  UNION \n"

              + "  { \n"
              + "     ?f foaf:name ?label . \n"
              + "     FILTER(lang(?label)='en') \n"
              + "     FILTER(LCASE(STR(?label)) = LCASE(\""
              + titreRecherche
              + "\")) \n"
              + "  } \n"

              + "  OPTIONAL { \n"
              + "      ?f dbo:director ?d . \n"
              + "      ?d rdfs:label ?directorName . \n"
              + "      FILTER(lang(?directorName)='en') \n"
              + "  } \n"

              + "  OPTIONAL { \n"
              + "      ?f dbo:producer ?p . \n"
              + "      ?p rdfs:label ?producerName . \n"
              + "      FILTER(lang(?producerName)='en') \n"
              + "  } \n"

              + "  OPTIONAL { \n"
              + "      ?f dbo:starring ?a . \n"
              + "      ?a rdfs:label ?actorName . \n"
              + "      FILTER(lang(?actorName)='en') \n"
              + "  } \n"

              + "} ORDER BY ?f";

        Context context = new Context();
        context.set(ARQ.queryTimeout, "30000");

        try (QueryExecution qexec =
                     QueryExecution.service(SERVICE_URL)
                             .query(sparqlQuery)
                             .context(context)
                             .build()) {

            ResultSet results = qexec.execSelect();

            if (!results.hasNext()) {
                return new ArrayList<>();
            }

            while (results.hasNext()) {

                QuerySolution soln = results.nextSolution();

                if (!soln.contains("f")) {
                    continue;
                }

                String filmURI =
                        soln.getResource("f").getURI();

                // =========================
                // CREATION FILM
                // =========================

                if (!filmsTrouves.containsKey(filmURI)) {

                    Film f = new Film();

                    String shortName =
                            filmURI.substring(
                                    filmURI.lastIndexOf("/") + 1
                            ).replace("_", " ");

                    f.setTitre(shortName);

                    f.setActeurs(new ArrayList<>());

                    filmsTrouves.put(filmURI, f);
                }

                Film currentFilm =
                        filmsTrouves.get(filmURI);

                // =========================
                // REALISATEUR
                // =========================

                if (currentFilm.getRealisateur() == null
                        && soln.contains("directorName")) {

                    currentFilm.setRealisateur(
                            soln.getLiteral("directorName")
                                    .getString()
                    );
                }

                // =========================
                // PRODUCTEUR
                // =========================

                if (currentFilm.getProducteur() == null
                        && soln.contains("producerName")) {

                    currentFilm.setProducteur(
                            soln.getLiteral("producerName")
                                    .getString()
                    );
                }

                // =========================
                // ACTEURS
                // =========================

                if (soln.contains("actorName")) {

                    String acteur =
                            soln.getLiteral("actorName")
                                    .getString();

                    if (!currentFilm.getActeurs()
                            .contains(acteur)) {

                        currentFilm.getActeurs()
                                .add(acteur);
                    }
                }
            }
            
            List<Film> listeFilms = new ArrayList<>(filmsTrouves.values());

            if (listeFilms.size() > 1) {
                // Expression régulière pour chercher 4 chiffres consécutifs entre parenthèses : (1997)
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\((\\d{4})");
                
                for (Film f : listeFilms) {
                    if (f.getTitre() != null) {
                        java.util.regex.Matcher matcher = pattern.matcher(f.getTitre());
                        if (matcher.find()) {
                            // On récupère le groupe 1 (les 4 chiffres)
                            String annee = matcher.group(1);
                            
                            // On suppose que ta classe Film possède un setter pour la date/année
                            // Ajuste le nom de la méthode selon ton code (ex: setAnnee ou setDate)
                            f.setDateSortie(annee);
                        }
                    }
                }
            }

            return listeFilms;

        } catch (Exception e) {

            System.err.println("Erreur SPARQL : "
                    + e.getMessage());

            e.printStackTrace();

            return new ArrayList<>();
        }
    }

}