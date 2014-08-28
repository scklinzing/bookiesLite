package supportClasses;

import java.util.Date;

public class RecBook {
	//this class contains the information about each book and is used to populate the mainform/xml areas
	private String bookName;
	private String bookAuthor;
	private String recommenderName;
	private int recommenderID;
	private String userComment;
	private int recID;
		/*Status
		 * 1= Read
		 * 2= Want to Read/ wish list
		 * 3= Currently Reading
		 * 
		 */
	private String bookID;
	public int getRecID(){
		return recID;
	}
	public int getRecommenderID(){
		return recommenderID;
	}
	public String getRecommenderName(){
		return recommenderName;
	}
	public String getUserComment(){
		return userComment;
	}
	public String getBookName(){
		return bookName;
	}
	public String getBookAuthor(){
		return bookAuthor;
	}
	public String getBookID(){
		return bookID;
	}
	
	public RecBook(int recID, String id, String name, String author, String recommenderName,
			int recommender, String comment){
		this.recID = recID;
		bookID = id;
		bookName = name;
		bookAuthor = author;
		this.recommenderName = recommenderName;
		this.recommenderID = recommender;
		userComment = comment; 
	}

}
