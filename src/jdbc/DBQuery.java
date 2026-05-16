package jdbc;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import element.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBQuery {

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
	
	public static ArrayList<Film> getMoviesInformations(String title) {
		ArrayList<Film> movies = new ArrayList<>();
		try {
			String selectFilmsQuery = "SELECT * FROM films Where titre = ?";
			
			Connection dbConnection = JdbcConnection.getConnection();
			PreparedStatement preparedStatement = dbConnection.prepareStatement(selectFilmsQuery);
			preparedStatement.setString(1, title);
			
			ResultSet res = preparedStatement.executeQuery();
			
			while(res.next()) {
				Film movie = new Film();
				movie.setTitre(res.getString("titre"));
				movie.setDateSortie(res.getString("date_de_sortie"));
				movie.setGenre(res.getString("genre"));
				movie.setDistributeur(res.getString("distributeur"));
				movie.setBudget(res.getString("budget"));
				movie.setRevenusUSA(res.getString("revenus_etats_unis"));
				movie.setRevenusMondiaux(res.getString("revenus_mondiaux"));
				movies.add(movie);
			}
			
			preparedStatement.close();
		} catch (SQLException se) {
			System.err.println(se.getMessage());
		}
		return movies;
	}
}
