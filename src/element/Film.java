package element;
import java.util.ArrayList;
import java.util.List;

public class Film {
	// Base de données locale
    private String titre;
    private String dateSortie;
    private String genre;
    private String distributeur;
    private String budget;
    private String revenusUSA;
    private String revenusMondiaux;
    
    // DBpedia
    private String realisateur;
    private String producteur;
    private ArrayList<String> acteurs;
    
    // OMDB
    private String resume;
    
    // Autre
    private String anneeSortie;

    public Film() {}
    
    /**
     * Calcule le nombre d'informations valides (non nulles et non vides) présentes dans le film.
     * @return un score entre 0 et 11
     */
    public int getInfoScore(boolean rechecheActeur) {
        int score = 0;
        
        if (titre != null && !titre.trim().isEmpty()) score++;
        if (dateSortie != null && !dateSortie.trim().isEmpty()) score++;
        if (genre != null && !genre.trim().isEmpty()) score++;
        if (distributeur != null && !distributeur.trim().isEmpty()) score++;
        if (realisateur != null && !realisateur.trim().isEmpty() && !realisateur.equalsIgnoreCase("N/A") && !realisateur.equalsIgnoreCase("Inconnu")) score++;
        if (producteur != null && !producteur.trim().isEmpty() && !producteur.equalsIgnoreCase("N/A")) score++;
        
        if(!rechecheActeur) {
        	if (budget != null && !budget.trim().isEmpty()) score++;
            if (revenusUSA != null && !revenusUSA.trim().isEmpty()) score++;
            if (revenusMondiaux != null && !revenusMondiaux.trim().isEmpty()) score++;
            if (acteurs != null && !acteurs.isEmpty()) score++;
            if (resume != null && !resume.trim().isEmpty() && !resume.equalsIgnoreCase("N/A")) score++;	
        }
        
        return score;
    }

    @Override
    public String toString() {
        return "------------------------------------------\n" +
        	   "SCORE           : " + getInfoScore(false) + " / 11\n\n" +
        		
               "TITRE           : " + titre + "\n" +
               "SORTIE          : " + dateSortie + "\n" +
               "GENRE           : " + genre + "\n" +
               "DISTRIBUTEUR    : " + distributeur + "\n" +
               "BUDGET          : " + budget + "\n" +
               "REVENU USA      : " + revenusUSA + "\n" +
               "REVENU MONDIAUX : " + revenusMondiaux + "\n" +
               "RÉALISATEUR     : " + realisateur + "\n" +
               "PRODUCTEUR      : " + producteur + "\n" +
               "RESUME          : " + resume + "\n" +
               "Acteurs         : " + acteurs + "\n" +
               "------------------------------------------";
    }
    
    /**
     * Retourne une chaîne contenant uniquement les informations essentielles demandées.
     */
    public String toStringActeur() {
        return "------------------------------------------\n" +
        	   "SCORE        : " + getInfoScore(true) + " / 6\n\n" +
        		
               "TITRE        : " + (titre != null ? titre : "Inconnu") + "\n" +
               "SORTIE       : " + (dateSortie != null ? dateSortie : "Inconnue") + "\n" +
               "GENRE        : " + (genre != null ? genre : "Inconnu") + "\n" +
               "DISTRIBUTEUR : " + (distributeur != null ? distributeur : "Inconnu") + "\n" +
               "RÉALISATEUR  : " + (realisateur != null ? realisateur : "Inconnu") + "\n" +
               "PRODUCTEUR   : " + (producteur != null ? producteur : "Inconnu") + "\n" +
               "------------------------------------------";
    }

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getDateSortie() {
		return dateSortie;
	}

	public void setDateSortie(String dateSortie) {
		this.dateSortie = dateSortie;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getDistributeur() {
		return distributeur;
	}

	public void setDistributeur(String distributeur) {
		this.distributeur = distributeur;
	}

	public String getBudget() {
		return budget;
	}

	public void setBudget(String budget) {
		this.budget = budget;
	}

	public String getRevenusUSA() {
		return revenusUSA;
	}

	public void setRevenusUSA(String revenusUSA) {
		this.revenusUSA = revenusUSA;
	}

	public String getRevenusMondiaux() {
		return revenusMondiaux;
	}

	public void setRevenusMondiaux(String revenusMondiaux) {
		this.revenusMondiaux = revenusMondiaux;
	}

	public String getRealisateur() {
		return realisateur;
	}

	public void setRealisateur(String realisateur) {
		this.realisateur = realisateur;
	}

	public String getProducteur() {
		return producteur;
	}

	public void setProducteur(String producteur) {
		this.producteur = producteur;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public ArrayList<String> getActeurs() {
		return acteurs;
	}

	public void setActeurs(ArrayList<String> acteurs) {
		this.acteurs = acteurs;
	}
	
	public String getAnneeSortie() {
		return anneeSortie;
	}

	public void setAnneeSortie(String anneeSortie) {
		this.anneeSortie = anneeSortie;
	}

	public void getReleaseYearByReleaseDate() {
		String[] dates = dateSortie.split("/");
		setAnneeSortie(dates[2]);
	}
    
    
}