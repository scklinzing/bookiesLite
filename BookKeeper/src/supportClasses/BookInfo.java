package supportClasses;

import java.util.Date;

public class BookInfo {
	//this class contains the information about each book and is used to populate the mainform/xml areas
	private int rating;
	private String bookName;
	private String bookAuthor;
	private int bookStatus;
	private int bookSecurity;
	private String userComment;
	private Date readDt;
	private int myStatus = -1; // for friends list to display if you have the book and what the status is
	private int myRating = -1;//same as above, so you can see your rating as well as theirs
		/*Status
		 * 1= Read
		 * 2= Want to Read/ wish list
		 * 3= Currently Reading
		 * 
		 */
	private String bookID;
	
	public int getMyStatus(){
		return myStatus;
	}
	public int getMyRating(){
		return myRating;
	}
	public int getBookSecurity(){
		return bookSecurity;
	}
	public String getUserComment(){
		return userComment;
	}
	public Date getDateRead(){
		return readDt;
	}
	public int getRating(){
		return rating;
	}
	public String getBookName(){
		return bookName;
	}
	public String getBookAuthor(){
		return bookAuthor;
	}
	public int getBookStatus(){
		return bookStatus;
	}
	public void setRating(int a){
		rating = a;
	}
	public void setStatus(int a){
		bookStatus = a;
	}
	public String getBookID(){
		return bookID;
	}
	public void setMyRating(int a){
		myRating = a;
	}
	public void setMyStatus(int a){
		myStatus = a;
	}
	
	public BookInfo(String id, String name, String author, int rating, int status ){
		bookID = id;
		bookName = name;
		bookAuthor = author;
		this.rating = rating;
		bookStatus = status; 
	}
	public BookInfo(String id, String name, String author, int rating, int status, Date date, String comment, int security ){
		bookID = id;
		bookName = name;
		bookAuthor = author;
		this.rating = rating;
		bookStatus = status; 
		bookSecurity = security;
		userComment = comment;
		readDt = date;
	}

}
