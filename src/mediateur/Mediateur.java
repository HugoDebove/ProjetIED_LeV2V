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
    	
    	// Initialisation des années pour la DB locale avant la fusion
    	if (moviesFromDB != null) {
    	    for (Film f : moviesFromDB) {
    	        if (f.getDateSortie() != null && f.getDateSortie().contains("/")) {
    	            f.getReleaseYearByReleaseDate();
    	        }
    	    }
    	}
    	
    	movies = mergeMoviesFromSources(moviesFromDB, moviesFromDBPedia, moviesFromOmdb);
    	
    	// Pour afficher en 1er le film qui a le plus d'infos, et ainsi de suite
    	if (movies != null && !movies.isEmpty()) {
            movies.sort((f1, f2) -> Integer.compare(f2.getInfoScore(false), f1.getInfoScore(false)));
        }
    	
    	return movies;
    }
    
    private ArrayList<Film> mergeMoviesFromSources(ArrayList<Film> moviesDB, ArrayList<Film> moviesDBPedia, ArrayList<Film> moviesOMDB){
    	// TODO : c'est pour toi hugo
    	// TODO : faire attention au cas ou la liste est vide
    	if (moviesDB == null) moviesDB = new ArrayList<>();
        if (moviesOMDB == null) moviesOMDB = new ArrayList<>();
        if (moviesDBPedia == null) moviesDBPedia = new ArrayList<>();
    	
    	// Merge DB + Omdb avec date / annee
    	ArrayList<Film> mergedMovies = new ArrayList<>(moviesDB);
        ArrayList<Film> omdbCopy = new ArrayList<>(moviesOMDB);
        ArrayList<Film> dbpediaCopy = new ArrayList<>(moviesDBPedia);
        
        boolean search;
        int count;
        
        for (Film movie : mergedMovies) {
            search = true;
            count = 0;

            while (search && omdbCopy.size() > count) {
                Film movieOMDB = omdbCopy.get(count);

                // Vérification Date de sortie
                if (checkTitle(movie, movieOMDB) && checkReleaseYear(movieOMDB, movie)) {
                    movie.setResume(movieOMDB.getResume());
                    if (movie.getRealisateur() == null || movie.getRealisateur().isEmpty()) {
                        movie.setRealisateur(movieOMDB.getRealisateur());
                    }
                    
                    search = false;
                    omdbCopy.remove(movieOMDB);
                }
                count++;
            }
        }
        
        // Si des films sont dans OMDB mais pas dans notre DB locale, on les ajoute
        if (!omdbCopy.isEmpty()) {
            mergedMovies.addAll(omdbCopy);
        }
    	
    	// Merge DBPedia + Omdb avec realisateur (+ annee si possible)
        for (Film movie : mergedMovies) {
            search = true;
            count = 0;

            while (search && dbpediaCopy.size() > count) {
                Film movieDBP = dbpediaCopy.get(count);

                // 1. Vérification Réalisateur
                boolean matchDirector = true;
                if (movie.getRealisateur() != null && !movie.getRealisateur().isEmpty() && !movie.getRealisateur().equalsIgnoreCase("Inconnu")) {
                    matchDirector = checkDirector(movieDBP, movie);
                }
                
                // 2. Vérification Année si DBPedia la fournit
                boolean bonusYearValid = true; 
                if (movieDBP.getAnneeSortie() != null && !movieDBP.getAnneeSortie().isEmpty()) {
                    bonusYearValid = checkReleaseYear(movieDBP, movie);
                }

                // Vérification complémentaire du titre nettoyé pour l'alignement DBpedia
                boolean matchTitle = checkTitle(movie, movieDBP);

                if (matchTitle && bonusYearValid && matchDirector) {
                    movie.setActeurs(movieDBP.getActeurs());
                    movie.setProducteur(movieDBP.getProducteur());
                    if (movie.getRealisateur() == null || movie.getRealisateur().isEmpty() || movie.getRealisateur().equalsIgnoreCase("Inconnu")) {
                        movie.setRealisateur(movieDBP.getRealisateur());
                    }

                    search = false;
                    dbpediaCopy.remove(movieDBP);
                }
                count++;
            }
        }
    	
        // Si DBPedia contient des films introuvables ailleurs, on les ajoute au résultat
        if (!dbpediaCopy.isEmpty()) {
            mergedMovies.addAll(dbpediaCopy);
        }

        return mergedMovies;
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
    			
    			if(canMerge(goodDirector, goodYear, haveYear) && checkTitle(movie, movieDBP)) {
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
    
    private boolean checkTitle(Film movie1, Film movie2) {
        if (movie1.getTitre() == null || movie2.getTitre() == null) return false;

        String t1 = movie1.getTitre().replaceAll("\\s*\\([^)]*\\)", "").trim().toLowerCase();
        String t2 = movie2.getTitre().replaceAll("\\s*\\([^)]*\\)", "").trim().toLowerCase();

        return t1.equals(t2);
    }
    
    private boolean checkReleaseYear(Film movie1, Film movie2) {
        if (movie1.getAnneeSortie() != null && !movie1.getAnneeSortie().isEmpty() && 
            movie2.getAnneeSortie() != null && !movie2.getAnneeSortie().isEmpty()) {
            
            String y1 = movie1.getAnneeSortie().trim();
            String y2 = movie2.getAnneeSortie().trim();
            
            return y1.contains(y2) || y2.contains(y1);
        }
        return false;
    }
    
    private boolean checkDirector(Film movieDBP, Film movie) {
        if (movieDBP.getRealisateur() != null && !movieDBP.getRealisateur().isEmpty() && 
            movie.getRealisateur() != null && !movie.getRealisateur().isEmpty()) {
            
            String realDBP = movieDBP.getRealisateur().toLowerCase().trim();
            String realMovie = movie.getRealisateur().toLowerCase().trim();
            
            return realMovie.contains(realDBP) || realDBP.contains(realMovie);
        }
        return false;
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
    
    /**
     * Permet de chercher tous les films dans lesquels un acteur / une actrice a joué
     * 
     * @param name - nom de l'acteur / l'actrice 
     */
    public void searchByActorName(String name) {
        ArrayList<Film> finalMoviesList = new ArrayList<>();
        
        // 1. Récupération des films depuis DBpedia par le nom de l'acteur
        DBPediaService dbpediaService = new DBPediaService();
        List<Film> dbpediaMovies = dbpediaService.getFilmsByActorName(name);
        
        if (dbpediaMovies != null && !dbpediaMovies.isEmpty()) {
            for (Film dbpMovie : dbpediaMovies) {
                
                // Nettoyage du titre DBpedia pour optimiser la recherche OMDB
                String cleanTitle = dbpMovie.getTitre()
                    .replaceAll("\\s*\\([^)]*\\)", "") // Enlève les "(film)", "(1997 film)", etc.
                    .trim();
                
                dbpMovie.setTitre(cleanTitle);
                
                // 2. OMDB sert de passerelle
                ArrayList<Film> omdbResults = getDataFromOMBD(cleanTitle);
                Film matchedOmdbMovie = null;
                
                if (omdbResults != null && !omdbResults.isEmpty()) {
                    // On cherche la bonne correspondance d'année entre DBPedia et OMDB
                    for (Film omdbMovie : omdbResults) {
                        if (checkReleaseYear(dbpMovie, omdbMovie)) {
                            matchedOmdbMovie = omdbMovie;
                            break;
                        }
                    }
                    // Si pas de correspondance d'année exacte trouvée, on prend le premier résultat par défaut
                    if (matchedOmdbMovie == null) {
                        matchedOmdbMovie = omdbResults.get(0);
                    }
                }
                
                // On privilégie le titre OMDB s'il existe, sinon le titre nettoyé de DBpedia
                String localSearchTitle = (matchedOmdbMovie != null) ? matchedOmdbMovie.getTitre() : cleanTitle;
                
                // 3. Interrogation de la Base de Données locale avec le titre
                ArrayList<Film> localDBResults = getDataFromDB(localSearchTitle.toLowerCase());
                Film matchedLocalMovie = null;
                
                if (localDBResults != null && !localDBResults.isEmpty()) {
                    // On cherche la correspondance dans notre DB locale
                    for (Film dbMovie : localDBResults) {
                        if (matchedOmdbMovie != null && checkReleaseYear(matchedOmdbMovie, dbMovie)) {
                            matchedLocalMovie = dbMovie;
                            break;
                        }
                    }
                    if (matchedLocalMovie == null) {
                        matchedLocalMovie = localDBResults.get(0);
                    }
                }
                
                Film enrichedMovie = dbpMovie;
                
                // Injection prioritaire des données de la BD locale (Date, Genre, Distributeur)
                if (matchedLocalMovie != null) {
                    if (matchedLocalMovie.getDateSortie() != null && !matchedLocalMovie.getDateSortie().equalsIgnoreCase("Inconnue")) {
                        enrichedMovie.setDateSortie(matchedLocalMovie.getDateSortie());
                    }
                    if (matchedLocalMovie.getGenre() != null && !matchedLocalMovie.getGenre().equalsIgnoreCase("Inconnu")) {
                        enrichedMovie.setGenre(matchedLocalMovie.getGenre());
                    }
                    if (matchedLocalMovie.getDistributeur() != null && !matchedLocalMovie.getDistributeur().equalsIgnoreCase("Inconnu")) {
                        enrichedMovie.setDistributeur(matchedLocalMovie.getDistributeur());
                    }
                }
                
                // Injection depuis OMDB si la BD locale n'a pas pu fournir l'information
                if (matchedOmdbMovie != null) {
                    if (enrichedMovie.getDateSortie() == null || enrichedMovie.getDateSortie().equalsIgnoreCase("Inconnue")) {
                        enrichedMovie.setDateSortie(matchedOmdbMovie.getDateSortie());
                    }
                    if (enrichedMovie.getGenre() == null || enrichedMovie.getGenre().equalsIgnoreCase("Inconnu")) {
                        enrichedMovie.setGenre(matchedOmdbMovie.getGenre());
                    }
                    if (enrichedMovie.getResume() == null || enrichedMovie.getResume().isEmpty()) {
                        enrichedMovie.setResume(matchedOmdbMovie.getResume());
                    }
                }
                
                // On ajoute le film à notre collection finale
                finalMoviesList.add(enrichedMovie);
            }
        }
        
        // Tri final par score de complétude décroissant (comme pour searchByTitle)
        if (!finalMoviesList.isEmpty()) {
            finalMoviesList.sort((f1, f2) -> Integer.compare(f2.getInfoScore(true), f1.getInfoScore(true)));
        }
        
        // Derniere verif : On verifie que l'acteur est bien dans la liste des acteurs
        ArrayList<Film> verifMoviesList = new ArrayList<>();
        for (Film movie : finalMoviesList) {
            if (movie.getActeurs() != null && !movie.getActeurs().isEmpty()) {
                boolean actorFound = false;
                for (String actor : movie.getActeurs()) {
                    if (actor.toLowerCase().contains(name.toLowerCase().trim())) {
                        actorFound = true;
                        break;
                    }
                }
                if (actorFound) {
                    verifMoviesList.add(movie);
                }
            } else {
                verifMoviesList.add(movie);
            }
        }
        finalMoviesList = verifMoviesList;
        
        this.filmsParActeur = finalMoviesList;
        this.listeFilmsTrouvee = !finalMoviesList.isEmpty();
        this.filmTrouve = false;
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