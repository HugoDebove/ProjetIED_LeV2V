package element;
import java.util.List;

public class Film {
    private String titre;
    private String dateSortie;
    private String genre;
    private String distributeur;
    private String budget;
    private String revenusUSA;
    private String revenusMondiaux;
    private String realisateur;
    private String producteur;
    private String resume;
    private List<String> acteurs;

    public Film() {}

    @Override
    public String toString() {
        return "------------------------------------------\n" +
               "TITRE : " + titre + "\n" +
               "SORTIE : " + dateSortie + "\n" +
               "GENRE : " + genre + "\n" +
               "DISTRIBUTEUR : " + distributeur + "\n" +
               "BUDGET : " + budget + "\n" +
               "REVENU USA : " + revenusUSA + "\n" +
               "REVENU MONDIAUX : " + revenusMondiaux + "\n" +
               "RÉALISATEUR : " + realisateur + "\n" +
               "PRODUCTEUR : " + producteur + "\n" +
               "RESUME : " + resume + "\n" +
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

	public List<String> getActeurs() {
		return acteurs;
	}

	public void setActeurs(List<String> acteurs) {
		this.acteurs = acteurs;
	}
    
    
}