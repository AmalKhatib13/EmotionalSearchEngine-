import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.*;

public class RemoveHTMLTags {
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
	
	public static String main(File file,int num)throws Exception 
	  { 
	  try (
		  
	BufferedReader br = new BufferedReader(new FileReader(file))) {
		 
		String text = null;
		  String st; 
		  while ((st = br.readLine()) != null) {
			
			 // st=Jsoup.parse(st).text();
		//    System.out.println(Jsoup.parse(st).text()); 
		    text +=" "+Jsoup.parse(st).text();
		  }
		
		
		 // System.out.println(text);
		    return text;
	}
	  } 
	

}

