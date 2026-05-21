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
		
	/**
	 * Permet de récupérer des données de film avec un titre donnée
	 * 
	 * @param title - titre du film donnée
	 * @return List de tous les films trouvées
	 */
	public ArrayList<Film> getMoviesInformations(String title) {
		ArrayList<Film> movies = new ArrayList<>();
		try {
			Connection dbConnection = JdbcConnection.getConnection(); // Connection à la bd
			
			// Préparation de la requête pour la bd
			String selectFilmsQuery = "SELECT * FROM films Where titre = ?";
			PreparedStatement preparedStatement = dbConnection.prepareStatement(selectFilmsQuery);
			preparedStatement.setString(1, title);
			
			ResultSet res = preparedStatement.executeQuery(); // Résultat de la requête
			
			if (!res.next()) {
				// Cas où on ne trouve rien dans la bd
			    // System.out.println("Aucun résultat trouvé dans la base de données pour le film : " + title);
			} else {
				
			    do {
			        Film movie = new Film();
			        movie.setTitre(res.getString("titre"));
			        movie.setDateSortie(converteReleaseDate(res.getString("date_de_sortie")));
			        movie.getReleaseYearByReleaseDate();
			        movie.setGenre(res.getString("genre"));
			        movie.setDistributeur(res.getString("distributeur"));
			        movie.setBudget(res.getString("budget"));
			        movie.setRevenusUSA(res.getString("revenus_etats_unis"));
			        movie.setRevenusMondiaux(res.getString("revenus_mondiaux"));
			        movies.add(movie);
			    } while (res.next());
			    // System.out.println("On a trouvée " + movies.size() + " avec jdbc");
			}
			
			preparedStatement.close();
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
		return movies;
	}
	
	
	/**
	 * Permet de modifier le format de la date de sortie
	 * 
	 * @param dateOriginal - date sous le mauvais format (yyyy-MM-dd HH:mm:ss)
	 * @return la date sous le bon format (dd/MM/yyyu)
	 */
	private String converteReleaseDate(String dateOriginal) {
		DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		LocalDateTime dateTime = LocalDateTime.parse(dateOriginal, inputFormat);
		String newDate = dateTime.format(outputFormat);

		return newDate;
	}
	
	
}
