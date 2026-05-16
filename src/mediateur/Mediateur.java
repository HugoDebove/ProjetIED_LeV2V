package mediateur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import element.Film;

public class Mediateur {
    // Recherche par titre
    private String titreRecherche;
    private Film filmResultat;
    private boolean filmTrouve = false;
    
    // Recherche par acteur
    private String nomActeurRecherche;
    private List<Film> filmsParActeur;
    private boolean listeFilmsTrouvee = false;

    // Simulation recherche de film par titre
    public String rechercherFilm() {
        filmResultat = new Film();
        filmResultat.setTitre(titreRecherche);
        filmResultat.setDateSortie("2026-05-15");
        filmResultat.setGenre("Science-Fiction");
        filmResultat.setDistributeur("CY Films");
        filmResultat.setBudget("150000000");
        filmResultat.setRevenusUSA("200000000");
        filmResultat.setRevenusMondiaux("500000000");
        filmResultat.setRealisateur("Hugo Debove");
        filmResultat.setResume("Hugo donne une tarte divine...");
        filmResultat.setActeurs(Arrays.asList("Hugo Debove", "Theo Amedro", "Massin Kheloufi", "Ilyes Aghouiles", "Dominique Gon"));
        
        filmTrouve = true;
        listeFilmsTrouvee = false; // Pour cacher la recherche par acteur
        return null; // On ne change pas de pages
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