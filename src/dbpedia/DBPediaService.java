package dbpedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.util.Context;

import element.Film;

public class DBPediaService {

    private static final String SERVICE_URL =
            "https://dbpedia.org/sparql";
    
    // Requête SPARQL pour trouver les infos de films à partir d'un titre
    private static final String SPARQL_TITLE =
    		"PREFIX dbo: <http://dbpedia.org/ontology/> \n"
    	  + "PREFIX dbp: <http://dbpedia.org/property/> \n"
    	  + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
    	  + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n"

    	  + "SELECT DISTINCT ?f ?label "
    	  + "?directorName ?producerName ?actorName "
    	  + "WHERE { \n"

    	  + "  ?f a dbo:Film . \n"

    	  // 1. Recherche par label principal
    	  + "  { \n"
    	  + "     ?f rdfs:label ?label . \n"
    	  + "     FILTER(lang(?label)='en') \n"
    	  + "     FILTER(LCASE(STR(?label)) = LCASE(\"%s\")) \n"
    	  + "  } \n"

    	  + "  UNION \n"

    	  // 2. Alternative de recherche via la propriété foaf:name
    	  + "  { \n"
    	  + "     ?f foaf:name ?label . \n"
    	  + "     FILTER(lang(?label)='en') \n"
    	  + "     FILTER(LCASE(STR(?label)) = LCASE(\"%s\")) \n"
    	  + "  } \n"

    	  // 3. On récupère le réalisateur
    	  + "  OPTIONAL { \n"
    	  + "      ?f dbo:director ?d . \n"
    	  + "      ?d rdfs:label ?directorName . \n"
    	  + "      FILTER(lang(?directorName)='en') \n"
    	  + "  } \n"

    	  // 4. On récupère le producteur            
    	  + "  OPTIONAL { \n"
    	  + "      ?f dbo:producer ?p . \n"
    	  + "      ?p rdfs:label ?producerName . \n"
    	  + "      FILTER(lang(?producerName)='en') \n"
    	  + "  } \n"

    	  // 5. On récupère les acteurs
    	  + "  OPTIONAL { \n"
    	  + "      ?f dbo:starring ?a . \n"
    	  + "      ?a rdfs:label ?actorName . \n"
    	  + "      FILTER(lang(?actorName)='en') \n"
    	  + "  } \n"

    	  + "} ORDER BY ?f";
    
    // Requête SPARQL pour trouver les films d'un acteur donné
    private static final String SPARQL_ACTOR =
            "PREFIX dbo: <http://dbpedia.org/ontology/> \n"
          + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"

          + "SELECT DISTINCT ?f ?filmLabel ?directorName ?producerName \n"
          + "WHERE { \n"
          
          // 1. On cherche la ressource de l'acteur à partir de son nom
          + "  ?actorResource rdfs:label ?actName . \n"
          + "  FILTER(lang(?actName)='en') \n"
          + "  FILTER(LCASE(STR(?actName)) = LCASE(\"%s\")) \n"
          
          // 2. On lie l'acteur aux films dans lesquels il joue
          + "  ?f a dbo:Film . \n"
          + "  ?f dbo:starring ?actorResource . \n"
          
          // 3. On récupère le titre du film
          + "  OPTIONAL { \n"
          + "     ?f rdfs:label ?filmLabel . \n"
          + "     FILTER(lang(?filmLabel)='en') \n"
          + "  } \n"

          // 4. On récupère le réalisateur
          + "  OPTIONAL { \n"
          + "      ?f dbo:director ?d . \n"
          + "      ?d rdfs:label ?directorName . \n"
          + "      FILTER(lang(?directorName)='en') \n"
          + "  } \n"

          // 5. On récupère le producteur
          + "  OPTIONAL { \n"
          + "      ?f dbo:producer ?p . \n"
          + "      ?p rdfs:label ?producerName . \n"
          + "      FILTER(lang(?producerName)='en') \n"
          + "  } \n"

          + "} ORDER BY ?f";
    
    private static final Pattern ANNEE_PATTERN = Pattern.compile("\\((\\d{4})");

    public List<Film> getFilmDetailsByTitle(String filmTitle) {

        Map<String, Film> filmsTrouves = new HashMap<>();

        String titreRecherche = filmTitle.trim();

        String sparqlQuery = String.format(SPARQL_TITLE, titreRecherche, titreRecherche);                

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

                // CREATION FILM
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

                // REALISATEUR
                if (currentFilm.getRealisateur() == null
                        && soln.contains("directorName")) {
                    currentFilm.setRealisateur(
                            soln.getLiteral("directorName")
                                    .getString()
                    );
                }
                // PRODUCTEUR
                if (currentFilm.getProducteur() == null
                        && soln.contains("producerName")) {
                    currentFilm.setProducteur(
                            soln.getLiteral("producerName")
                                    .getString()
                    );
                }
                // ACTEURS
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
            	extraireAnneeDepuisTitre(listeFilms);
            }

            return listeFilms;

        } catch (Exception e) {
            System.err.println("Erreur SPARQL : "
                    + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Film> getFilmsByActorName(String actorName) {

        Map<String, Film> filmsTrouves = new HashMap<>();
        String acteurRecherche = actorName.trim();

        String sparqlQuery = String.format(SPARQL_ACTOR, acteurRecherche);

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
                
                String filmURI = soln.getResource("f").getURI();

                // Création du film s'il n'existe pas encore dans notre Map
                if (!filmsTrouves.containsKey(filmURI)) {
                    Film f = new Film();
                    
                    if (soln.contains("filmLabel")) {
                        f.setTitre(soln.getLiteral("filmLabel").getString());
                    } else {
                        String shortName = filmURI.substring(filmURI.lastIndexOf("/") + 1).replace("_", " ");
                        f.setTitre(shortName);
                    }
                    
                    // On initialise la liste des acteurs
                    f.setActeurs(new ArrayList<>());
                    filmsTrouves.put(filmURI, f);
                }

                Film currentFilm = filmsTrouves.get(filmURI);

                // Réalisateur
                if (currentFilm.getRealisateur() == null && soln.contains("directorName")) {
                    currentFilm.setRealisateur(soln.getLiteral("directorName").getString());
                }
                
                // Producteur
                if (currentFilm.getProducteur() == null && soln.contains("producerName")) {
                    currentFilm.setProducteur(soln.getLiteral("producerName").getString());
                }
            }
            
            List<Film> listeFilms = new ArrayList<>(filmsTrouves.values());

            extraireAnneeDepuisTitre(listeFilms);

            return listeFilms;

        } catch (Exception e) {
            System.err.println("Erreur SPARQL : " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private void extraireAnneeDepuisTitre(List<Film> listeFilms) {
        for (Film f : listeFilms) {
            if (f.getTitre() != null) {
                Matcher matcher = ANNEE_PATTERN.matcher(f.getTitre());
                if (matcher.find()) {
                    String annee = matcher.group(1);
                    f.setDateSortie(annee);
                }
            }
        }
    }

}