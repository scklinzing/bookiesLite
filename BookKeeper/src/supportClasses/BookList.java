package supportClasses;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BookList {
	private ArrayList<BookInfo> booklist;

	public BookList(ArrayList<BookInfo> list){
	                booklist = list;
	}
	public ArrayList<BookInfo> getList(){
	                return booklist;
	}
	public BookInfo getISBN(String isbn){
		for(int i = 0; i < booklist.size(); i++){
			if(booklist.get(i).getBookID().equals(isbn)){
				return booklist.get(i);
			}
		}
		return null;
	}

	public ArrayList<BookInfo> getList(char[] filter, char sort, String title, String author){
	                ArrayList<BookInfo> ret = new ArrayList<BookInfo>();
	                boolean add = true;
	                BookInfo element;
	                //filter is a character array representing each possible filter == (int) 0 represents filter is not active
	                                //filter[0] char 1 2 3 0
	                                //filter[1] (int)rating -1 unrated, else 1-5
	                                //title, author will = null if no filter
	                //Sort – 0 – standard title sort a- author, r- rating, s- status.

	                int status;
	                int rating;
	        		/*1= wishlist
	        		 * 2= read
	        		 * 3= reading
	        		 * 0= view all
	        		 */
	                if(filter[0] == '2') status = 2;
	                else if (filter[0] == '1') status = 1;
	                else if(filter[0] == '3') status = 3;
	                else status = 0;
	                
	                switch(filter[1]){
	                case '1':
	                	rating = 1;
	                	break;
	                case '2':
	                	rating = 2;
	                	break;
	                case '3':
	                	rating = 3;
	                	break;
	                case'4':
	                	rating = 4;
	                	break;
	                case'5':
	                	rating = 5;
	                	break;
	                case '0':
	                	rating = 0;
	                	default:
	                		//will not use rating in this case because it == 'n'
	                		//but this shuts the compiler up
	                		rating = -1;
	                }
	                
	                for(int i = 0; i < booklist.size(); i++){
	                   add = true;
	                   element = booklist.get(i);
	                   //filter[0] status
	                   if(add == true && status !=0){
	                	   if(element.getBookStatus() != status){
	                		   add = false;
	                       }
	                   }//end filter status

	                   //filter[1] rating
	                   if(add == true && filter[1] != 'n'){
	                                if(element.getRating() != rating){
	                                                add = false;
	                                }
	                   }//end filter rating

	                   //filter Title
	                   if(add == true && title != null){
	                                if(!element.getBookName().toLowerCase().contains(title.toLowerCase())){
	                                                add = false;
	                                }
	                   }//end filter title

	                   //filter Author
	                   if(add == true && author != null){
	                                if(!element.getBookAuthor().toLowerCase().contains(author.toLowerCase())){
	                                                add = false;
	                                }
	                   }//end filter title
	                   if(add == true) ret.add(element);
	                }//end for
	                
	                sort(ret, sort);
	                return ret;
	}
	

	private ArrayList<BookInfo> sort(ArrayList<BookInfo> proc, char sort){
	                //select a comparitor and sort the collection.      
	                if(sort == 't') Collections.sort(proc, SortBookInfo.TITLE_SORT);
	                else
	                if(sort == 'a') Collections.sort(proc, SortBookInfo.AUTHOR_SORT);
	                else
	                if(sort == 'r') Collections.sort(proc, SortBookInfo.RATING_SORT);
	                else
	                if(sort == 's') Collections.sort(proc, SortBookInfo.STATUS_SORT);

	                return proc;
	}
}



