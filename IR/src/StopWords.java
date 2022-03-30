import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;



public class StopWords  {
	public static String remove(String text)throws Exception 
  {
	List<String> StopWord_list= new ArrayList<String>();
	//read the file that contains the stop words and add them into a list
	File file = new File("arabic-stop-words-list.txt");
	try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		String st; 
		 while ((st = br.readLine()) != null) {
			 StopWord_list.add(st);
		 }
	}
	//remove the stop words that we added to the list from the text 
	String newText = null;
	if (text==null) {
		System.out.println("TEXT IS NULL");
	}
	for (String word : text.split("\\s+")) {
		if (!StopWord_list.contains(word)) {
			newText += " " + word;
		}
	
	
	}
	return newText;
  }

}
