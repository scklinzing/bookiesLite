package supportClasses;

import java.util.ArrayList;
import java.util.Collections;

public class UserList {
	private ArrayList<User> userlist;

	public UserList(ArrayList<User> list){
	    userlist = list;
	}
	public ArrayList<User> getList(){
	    return userlist;
	}
	public User getID(int id){
		for(int i = 0; i < userlist.size(); i++){
			if(userlist.get(i).getID() == id){
				return userlist.get(i);
			}
		}
		return null;
	}

	public ArrayList<User> getList(char[] filter, char sort, String name, String email){
	    ArrayList<User> ret = new ArrayList<User>();
	    boolean add = true;
	    User element;
	    //filter 0 - status
	    //filter 1 - type
	    int status;
	    int type;
	    
	    if(filter[0] == '1') status = 1;
	    else if (filter[0] == '2') status = 2;
	    else status = 0;
	    if(filter[1] == '1') type =1;//admin
	    else if(filter[1] == '0') type = 0;
	    else type = -1;
	    	                
	    for(int i = 0; i < userlist.size(); i++){
	        add = true;
	        element = userlist.get(i);
	        //filter[0] status
	        if(add == true && status !=0){
	           if(element.getStatus() != status){
	               add = false;
	           }
	        }//end filter status

	        //filter[1] type
	        if(add == true && type != -1){
	           if(element.getUserType() != type){
	                add = false;
	           }
	       }//end filter rating

	       //filter Title
	       if(add == true && name != null){
	           if(!element.getUserName().toLowerCase().contains(name.toLowerCase())){
	               add = false;                  
	           }
	       }//end filter title

	       //filter Author
	       if(add == true && email != null){
	           if(!element.getEmail().toLowerCase().contains(email.toLowerCase())){
	                add = false;
	           }
	       }//end filter title
	       
	       if(add == true) ret.add(element);
	       }//end for
	                
	       sort(ret, sort);
	       return ret;
	}
	

	private ArrayList<User> sort(ArrayList<User> proc, char sort){
	      //select a comparitor and sort the collection.      
	      if(sort == 't') Collections.sort(proc, SortUserInfo.NAME_SORT);
	      else
	      if(sort == 'a') Collections.sort(proc, SortUserInfo.EMAIL_SORT);
	      else
	      if(sort == 'r') Collections.sort(proc, SortUserInfo.TYPE_SORT);
	      else
	      if(sort == 's') Collections.sort(proc, SortUserInfo.STATUS_SORT);
	      return proc;
	}

}
