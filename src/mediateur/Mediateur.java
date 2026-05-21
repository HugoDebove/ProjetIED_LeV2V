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
    	int typeMerge = 0; // 1 = OMDB avec BD / 2 = OMDB avec DBPedia / 0 = Pas de merge
    	
    	// On recuperer les source DB local et DBPedia
    	ArrayList<Film> moviesFromDB = getDataFromDB(title.toLowerCase());
    	ArrayList<Film> moviesFromDBPedia = getDataFromDBBedia(title);
    	ArrayList<Film> moviesFromOmdb = getDataFromOMBD(title);
    	
    	movies = mergeMoviesFromSources(moviesFromDB, moviesFromDBPedia, moviesFromOmdb);
    	
    	return movies;
    }
    
    private ArrayList<Film> mergeMoviesFromSources(ArrayList<Film> moviesDB, ArrayList<Film> moviesDBPedia, ArrayList<Film> moviesOMDB){
    	// TODO : c'est pour toi hugo
    	// TODO : faire attention au cas ou la liste est vide
    	
    	ArrayList<Film> movies = new ArrayList<>();
    	
    	// Merge DB + Omdb avec date / annee
    	
    	// Merge DBPedia + Omdb avec annee + realisateur (premier merge peut etre fais avant mais faire attention au cas exceptionel
    	
    	return movies;
    }
    
    private ArrayList<Film> mergeMoviesListFromSources(ArrayList<Film> moviesDBAndOMDB, ArrayList<Film> moviesFromDBPedia){
    	ArrayList<Film> movies = new ArrayList<>(moviesDBAndOMDB);
    	ArrayList<Film> moviesDBPCopy = new ArrayList<>(moviesFromDBPedia);
    	boolean search, goodYear, goodDirector, haveYear, merge;
    	int count;
    	
    	
    	for(Film movie : movies) {
    		search = true;
    		merge = false;
    		count = 0;
    		
    		while(search && moviesDBPCopy.size() > count) {
    			goodYear = goodDirector = haveYear = false;
    			Film movieDBP = moviesDBPCopy.get(count);
    			
    			if(moviesFromDBPedia.size() > 1) {
    				haveYear = true;
    				goodYear = checkReleaseYear(movieDBP, movie);
    			}
    			
    			goodDirector = checkDirector(movieDBP, movie);
    			
    			if(canMerge(goodDirector, goodYear, haveYear)) {
    				movie.setActeurs(movieDBP.getActeurs());
    				movie.setRealisateur(movieDBP.getRealisateur());
    				movie.setProducteur(movieDBP.getProducteur());
					
    				search = false;
    				moviesDBPCopy.remove(movieDBP);
    			}
    			
    			count++;
    		}
    	}
    	
    	if(!moviesDBPCopy.isEmpty()) {
    		movies.addAll(moviesDBPCopy);
    	}
    	
    	return movies;
    }
    
    
    private boolean checkReleaseYear(Film movieDBP, Film movie) {
    	boolean goodYear = false;
    	if((movieDBP.getAnneeSortie() != null || !movieDBP.getAnneeSortie().isEmpty()) && (movie.getAnneeSortie() != null || !movie.getAnneeSortie().isEmpty())) {
			if(movie.getAnneeSortie().equals(movieDBP.getAnneeSortie())) {
				goodYear = true;
			}
		}
    	
    	return goodYear;
    }
    
    private boolean checkDirector(Film movieDBP, Film movie) {
    	boolean goodDirector = false;
    	if((movieDBP.getRealisateur() != null && !movieDBP.getRealisateur().isEmpty()) && (movie.getRealisateur() != null && !movie.getRealisateur().isEmpty())) {
			// TODO : gerer le cas ou le real de OMDB soit plusieurs personne
			if(movie.getAnneeSortie().contains(movieDBP.getAnneeSortie())) {
				goodDirector = true;
			}
		}
    	
    	return goodDirector;
    }
    
    private boolean canMerge(boolean goodDirector, boolean goodYear, boolean haveYear) {
    	boolean canMerge = false;
    	if(goodDirector) {
			if(haveYear) {
				if(goodYear) {
					canMerge = true;
				}
			}
			else {
				canMerge = true;
			}
		}
    	
    	return canMerge;
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
    
    // TODO : peut etre a supprimer
    /*
    private void mergeDataFromDBPAndOMDB(ArrayList<Film> moviesFromDBP, String title) {
    	for(Film movie : moviesFromDBP) {
			movie.setTitre(title);
			Film movieFromOMBD = getDataFromOMBD(title, movie.getAnneeSortie());
			if(movieFromOMBD != null) {
				movie.setResume(movieFromOMBD.getResume());
				movie.setRealisateur(movieFromOMBD.getRealisateur());
			}
		}
    }
    */
    
    /**
     * Permet de récupérer les données venant de la source OMBD en fonction des films trouver avec la source base de données
     * 
     * @param moviesFromDB - Liste de tous les films trouvées
     * @param title - Titre du film
     */
    private void mergeDataFromDBAndOMDB(ArrayList<Film> moviesFromDB, String title) {
    	/*
    	for(Film movie : moviesFromDB) {
			movie.setTitre(title);
			Film movieFromOMBD = getDataFromOMBD(title, movie.getAnneeSortie());
			if(movieFromOMBD != null) {
				movie.setResume(movieFromOMBD.getResume());
				movie.setRealisateur(movieFromOMBD.getRealisateur());
			}
		}
		*/
    }
    
    /**
     * Permet de récupérer les données venant de la source OMDB
     * 
     * @param title - titre du film
     * @param year - année de sortie du film
     * @return un Film avec le résumé et la date de sortie
     */
    private ArrayList<Film> getDataFromOMBD(String title) {
    	OMDBQuery omdbq = new OMDBQuery();
    	
    	return omdbq.getMovies(title);
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