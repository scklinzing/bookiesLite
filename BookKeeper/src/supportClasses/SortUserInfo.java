package supportClasses;

import android.annotation.SuppressLint;
import java.util.Comparator;

@SuppressLint("DefaultLocale")
public class SortUserInfo{
	static final Comparator<User> NAME_SORT = new Comparator<User>(){
		public int compare(User a, User b){
			return a.getUserName().toLowerCase().compareTo(b.getUserName().toLowerCase());
        }
    };
    static final Comparator<User> EMAIL_SORT = new Comparator<User>(){
          public int compare(User a, User b){
                int comp = a.getEmail().toLowerCase().compareTo(b.getEmail().toLowerCase());
                return comp;
          }
   };
   static final Comparator<User> TYPE_SORT = new Comparator<User>(){
          public int compare(User a, User b){
                if(a.getUserType() > b.getUserType()) return 1;
                if(a.getUserType() < b.getUserType()) return -1;
                return a.getUserName().toLowerCase().compareTo(b.getUserName().toLowerCase());
          }
  };
  static final Comparator<User> STATUS_SORT = new Comparator<User>(){
         public int compare(User a, User b){
                if(a.getStatus() > b.getStatus()) return 1;
                if(a.getStatus() < b.getStatus()) return -1;
                return a.getUserName().toLowerCase().compareTo(b.getUserName().toLowerCase());
        }
 };
                
}
