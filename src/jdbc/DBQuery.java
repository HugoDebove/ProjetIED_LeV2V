package jdbc;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import element.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBQuery {
	
	// Méthode a enlever plus tard
	
	// TODO : Ajouter une recherche pas année pour etre plus précis ???

	public static void main(String[] args) {
		ArrayList<Film> movies = getMoviesInformations("king kong");
		showAllMovies(movies);
	}
	
	public static void showAllMovies(ArrayList<Film> movies) {
		String info = "";
		
		for(Film movie : movies) {
			info = movie.toString();
			System.out.println(info);
		}
	}
	
	// Méthode a garder
	
	public static ArrayList<Film> getMoviesInformations(String title) {
		ArrayList<Film> movies = new ArrayList<>();
		try {
			String selectFilmsQuery = "SELECT * FROM films Where titre = ?";
			
			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(selectFilmsQuery);
			preparedStatement.setString(1, title);
			
			ResultSet res = preparedStatement.executeQuery();
			
			if (!res.next()) {
			    System.out.println("Aucun résultat trouvé dans la base de données pour le film : " + title);
			} else {
			    do {
			        Film movie = new Film();
			        movie.setTitre(res.getString("titre"));
			        movie.setDateSortie(converteReleaseDate(res.getString("date_de_sortie")));
			        movie.setGenre(res.getString("genre"));
			        movie.setDistributeur(res.getString("distributeur"));
			        movie.setBudget(res.getString("budget"));
			        movie.setRevenusUSA(res.getString("revenus_etats_unis"));
			        movie.setRevenusMondiaux(res.getString("revenus_mondiaux"));
			        movies.add(movie);
			    } while (res.next());
			}
			
			preparedStatement.close();
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
		return movies;
	}
	
	private static String converteReleaseDate(String dateOriginal) {
		DateTimeFormatter formatEntree = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		DateTimeFormatter formatSortie = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		// Conversion
		LocalDateTime dateTime = LocalDateTime.parse(dateOriginal, formatEntree);
		String dateFormatee = dateTime.format(formatSortie);

		return dateFormatee;
	}
	
	
}
