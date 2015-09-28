package server;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ListeDesMatchs {
	List<Match> matches = Collections.synchronizedList(new ArrayList<Match>());
	
	//http://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#synchronizedList%28java.util.List%29
	 //synchronized (list) {
	      //Iterator i = list.iterator(); // Must be in synchronized block
	      //while (i.hasNext())
	         //foo(i.next());
	  //}
}
