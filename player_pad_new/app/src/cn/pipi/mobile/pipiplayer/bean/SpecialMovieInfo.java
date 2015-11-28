package cn.pipi.mobile.pipiplayer.bean;

import java.io.Serializable;
/**
 * 专题javabean
 * @author qiny
 *
 */
public class SpecialMovieInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private String  movieId;
    
    private String  movieName;
    
    private String movieImgPath;
    
    private String movieShowName;

	public String getMovieShowName() {
		return movieShowName;
	}

	public void setMovieShowName(String movieShowName) {
		this.movieShowName = movieShowName;
	}

	public String getMovieId() {
		return movieId;
	}

	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public String getMovieImgPath() {
		return movieImgPath;
	}

	public void setMovieImgPath(String movieImgPath) {
		this.movieImgPath = movieImgPath;
	}

}
