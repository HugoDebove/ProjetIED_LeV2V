package mediateur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dbpedia.DBPediaService;
import element.Film;
import jdbc.DBQuery;
import rest.OMDBQuery;

public class Mediateur {
    // Recherche par titre
    private String titreRecherche;
    private Film filmResultat;
    private boolean filmTrouve = false;
    
    // Recherche par acteur
    private String nomActeurRecherche;
    private List<Film> filmsParActeur;
    private boolean listeFilmsTrouvee = false;
    
    /**
     * Permet de chercher tous les films venant des 3 sources avec un titre
     * 
     * @param title - Titre du film 
     * @return Tous les films trouvée par les sources
     */
    public ArrayList<Film> searchByTitle(String title) {
    	ArrayList<Film> movies = new ArrayList<>();
    	
    	// Récupération des données venant de bd
    	ArrayList<Film> moviesFromDB = getDataFromDB(title.toLowerCase());
    	System.out.println("On a trouver " + moviesFromDB.size() + " filme");
    	
    	ArrayList<Film> moviesFromDBPedia = getDataFromDBBedia(title);
    	
    	if(moviesFromDB.size() > 0) {
    		// Récupération des données venant de omdb avec le titre et les années de production pour pouvoir fusionner les données
    		searchDataFromOMBDWithDBData(moviesFromDB, title);
    		movies.addAll(moviesFromDB);
    	}
    	else {
    		// Cas où on a rien trouver dans la bd donc on cherche les données sans l'année de production
    		Film movieFromOMBD = getDataFromOMBD(title, "");
    		movies.add(movieFromOMBD);
    	}
    	
    	return movies;
    }
    
    private ArrayList<Film> mergeDBAndDBPedia(ArrayList<Film> moviesFromDB, ArrayList<Film> moviesFromDBPedia){
    	ArrayList<Film> movies = new ArrayList<>(moviesFromDB);
    	
    	for(Film movieDB : movies) {
    		for(Film movieDBP : moviesFromDBPedia) {
    			//TODO : ajouter la condition de merge (pour l'instant seulement l'année de sortie si il y a
    			// Faire attention au cas ou l'année est la meme (rajouter un second critere dans tous les cas
    			if(movieDBP.getAnneeSortie() != null || !movieDBP.getAnneeSortie().isEmpty()) {
    				if(movieDB.getAnneeSortie() == movieDBP.getAnneeSortie()) {
    					movieDB.setActeurs(movieDBP.getActeurs());
    					movieDB.setRealisateur(movieDBP.getRealisateur());
    					movieDB.setProducteur(movieDBP.getProducteur());
    				}
    				
    				// TODO : supprimer le film de la list de dbpedia
    			}
    			else {
    				// On doit choisir un critere dans le cas ou le film est unique
    			}
    		}
    	}
    	
    	return movies;
    }
    
    public void searchByTitleTest(String title) {
    	ArrayList<Film> movies = new ArrayList<>();
    	
    	// Récupération des données venant de bd
    	ArrayList<Film> moviesFromDB = getDataFromDB(title.toLowerCase());
    	System.out.println("On a trouver " + moviesFromDB.size() + " filme");
    	
    	ArrayList<Film> moviesFromDBPedia = getDataFromDBBedia(title);
    	
    	if(moviesFromDBPedia.size() > 1) {
    		for(Film movie : moviesFromDB) {
    			boolean research = true;
    			while(research) {
    				
    			}
        	}
    	}
    	
    	showMovies(moviesFromDB);
    	showMovies(moviesFromDBPedia);
    	
    }
    
    private void showMovies(ArrayList<Film> movies) {
    	//TODO : A supprimer après
    	for(Film movie : movies) {
    		System.out.println(movie.toString());
    	}
    }
    
    /**
     * Permet de chercher tous les films dans les quelles un acteur / une actrice à jouer
     * 
     * @param name - nom de l'acteur / l'actrice 
     * @return Tous les films trouver par les sources
     */
    public void searchByActorName(String name) {
    	
    }
    
    /**
     * Permet de récupérer les données venant de la source base de données
     *
     * @param title - Titre du film
     * @return Liste de tous les films trouvées avec (...)
     */
    private ArrayList<Film> getDataFromDB(String title) {
    	DBQuery dbq = new DBQuery();
    	return dbq.getMoviesInformations(title);
    }
    
    private ArrayList<Film> getDataFromDBBedia(String title) {
    	DBPediaService dbp = new DBPediaService();
    	return dbp.getFilmDetailsByTitle(title);
    }
    
    /**
     * Permet de récupérer les données venant de la source OMBD en fonction des films trouver avec la source base de données
     * 
     * @param moviesFromDB - Liste de tous les films trouvées
     * @param title - Titre du film
     */
    private void searchDataFromOMBDWithDBData(ArrayList<Film> moviesFromDB, String title) {
    	for(Film movie : moviesFromDB) {
			movie.setTitre(title);
			Film movieFromOMBD = getDataFromOMBD(title, movie.getAnneeSortie());
			if(movieFromOMBD != null) {
				movie.setResume(movieFromOMBD.getResume());
			}
		}
    }
    
    /**
     * Permet de récupérer les données venant de la source OMDB
     * 
     * @param title - titre du film
     * @param year - année de sortie du film
     * @return un Film avec le résumé et la date de sortie
     */
    private Film getDataFromOMBD(String title, String year) {
    	OMDBQuery omdbq = new OMDBQuery();
    	
    	return omdbq.getMovie(title, year);
    }

    // Simulation recherche de film par acteur
    public String rechercherFilmsParActeur() {
        filmsParActeur = new ArrayList<>();
        
        Film f1 = new Film();
        f1.setTitre("Le retour du giga beau gosse");
        f1.setDateSortie("2024");
        f1.setGenre("Action");
        f1.setDistributeur("Universal");
        f1.setRealisateur("Nolan");
        f1.setProducteur("Spielberg");
        
        filmsParActeur.add(f1);
        
        listeFilmsTrouvee = true;
        filmTrouve = false; // Pour cacher la recherche par titre
        return null; // On ne change pas de pages
    }

	public String getTitreRecherche() {
		return titreRecherche;
	}

	public void setTitreRecherche(String titreRecherche) {
		this.titreRecherche = titreRecherche;
	}

	public Film getFilmResultat() {
		return filmResultat;
	}

	public void setFilmResultat(Film filmResultat) {
		this.filmResultat = filmResultat;
	}

	public boolean isFilmTrouve() {
		return filmTrouve;
	}

	public void setFilmTrouve(boolean filmTrouve) {
		this.filmTrouve = filmTrouve;
	}

	public String getNomActeurRecherche() {
		return nomActeurRecherche;
	}

	public void setNomActeurRecherche(String nomActeurRecherche) {
		this.nomActeurRecherche = nomActeurRecherche;
	}

	public List<Film> getFilmsParActeur() {
		return filmsParActeur;
	}

	public void setFilmsParActeur(List<Film> filmsParActeur) {
		this.filmsParActeur = filmsParActeur;
	}

	public boolean isListeFilmsTrouvee() {
		return listeFilmsTrouvee;
	}

	public void setListeFilmsTrouvee(boolean listeFilmsTrouvee) {
		this.listeFilmsTrouvee = listeFilmsTrouvee;
	}

    
}