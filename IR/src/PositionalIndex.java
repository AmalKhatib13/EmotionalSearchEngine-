import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.opencsv.CSVWriter;

public class PositionalIndex {
	@SuppressWarnings("unused")
	public static void main(String[] args)throws Exception 
	  {
	// create positional index for trust words 
		HashMap<String,HashMap<String,ArrayList<Integer>>> TWIndex = new HashMap<String,HashMap<String,ArrayList<Integer>>>();
		File trustWords = new File("TrustWords.txt");
		try (BufferedReader br2 = new BufferedReader(new FileReader(trustWords))) {
			String st2; 
			 while ((st2 = br2.readLine()) != null) {
				 String[] words2 = st2.split(" ");
				 for (int i=0; i<words2.length;i++) 
				     TWIndex.put(words2[i], null);
			 }
		}	
		
		//Create positional index for emotional words
		HashMap<String,HashMap<String,ArrayList<Integer>>> EWIndex = new HashMap<String,HashMap<String,ArrayList<Integer>>>();
		File emWords = new File("EmotionalWords.txt");
		try (BufferedReader br = new BufferedReader(new FileReader(emWords))) {
			String st ;
			
			 while ((st = br.readLine()) != null) {
				// st= st.replaceAll("_", " ");
			     
				 String[] words = st.split(" ");
				 for (int i=0; i<words.length;i++) {
					 if(words[i].length()>0)
				         EWIndex.put(words[i], null);
				 }
				 
			 }
		}
		System.out.println(EWIndex.keySet()+"\n"+EWIndex.size());

	File dir = new File("Files/Arabic/");
	int FileNum=1;
	

	for (File file : dir.listFiles()) {
	    Scanner s = new Scanner(file);
	    //Tokenization process 
	    //remove html tags

	    
	    String text = RemoveHTMLTags.main(file,FileNum);
	    if (text!=null) {
	    //remove stop words
	    String newText = StopWords.remove(text);
	    //TODO stemming(?)
	         
		BreakIterator bi = BreakIterator.getSentenceInstance();
	    bi.setText(newText);
		int index = 0;
		int Snum = 1;
		while (bi.next() != BreakIterator.DONE) {
			String sentence = newText.substring(index, bi.current());
			sentence = sentence.replaceAll("[؟،]", " ");
			sentence = sentence.replaceAll("\\p{Punct}", " ");
			//trust words index
			for (String tw : TWIndex.keySet()) {
				String[] w2 = sentence.split(" ");
				for (int i=0;i<w2.length;i++) {
					if(w2[i].equals(tw)) {
						//insert the doc and the sentence into the posting 
						HashMap<String,ArrayList<Integer>> map = TWIndex.get(tw);
						String doc = "D"+Integer.toString(FileNum)+"S"+Integer.toString(Snum);
						 if (map==null) {
							map= new HashMap<String,ArrayList<Integer>>();
						}
						ArrayList<Integer> positions =  map.get(doc);
						
						map.put(doc, null);
						if (positions!=null) {
							positions.add(i+1);
						}
						else if (positions==null) {
							positions = new ArrayList<Integer>();
							positions.add(i+1);
						}
						map.put(doc, positions);
						TWIndex.put(tw, map);
				
				     }
				}		
			}

			//Emotional words index
			for (String ew : EWIndex.keySet()) {
				String[] w1 = sentence.split(" ");
				//for (String e : w2) {
				for (int i=0;i<w1.length;i++) {
					if(w1[i].equals(ew)) {
						//insert the doc and the sentence into the posting 
						HashMap<String,ArrayList<Integer>> Emap = EWIndex.get(ew);
						String doc = "D"+Integer.toString(FileNum)+"S"+Integer.toString(Snum);
						 if (Emap==null) {
							Emap= new HashMap<String,ArrayList<Integer>>();
						}
						ArrayList<Integer> Epositions =  Emap.get(doc);
						
						Emap.put(doc, null);
						if (Epositions!=null) {
							Epositions.add(i+1);
						}
						else if (Epositions==null) {
							Epositions = new ArrayList<Integer>();
							Epositions.add(i+1);
						}
						Emap.put(doc, Epositions);
						EWIndex.put(ew, Emap);
					//	System.out.println(ew+" sentence:"+sentence);
					//	System.out.println(EWIndex.get(ew));
				     }
				}	
			}
			//System.out.println("Sentence: " + sentence);
			index = bi.current();
			Snum++;
		} 
	    }
	    FileNum++;
	    s.close();
	}
	System.out.println(FileNum-1);
	 //TODO Intersection
	
	// for each key in the trust words index, find the intersection with the emotional words index
	 ArrayList<String> totalDistance = new ArrayList<String>();
	 for (String trustWord:TWIndex.keySet()) {
		 HashMap<String,ArrayList<Integer>> Tmap = TWIndex.get(trustWord);
		 
		
		 if (Tmap!=null) {
			
		 //System.out.println(trustWord+" "+ Tmap.keySet());
		 for (String emotionalWord: EWIndex.keySet()) {
			 HashMap<String,ArrayList<Integer>> Emap = EWIndex.get(emotionalWord);
			 
			 if(Emap!=null) {
			 
			// System.out.println(emotionalWord+" "+Emap.keySet());
			 ArrayList<String> interSection =new ArrayList<String>();
			 ArrayList<Integer> tpos = new ArrayList<Integer>();
			 ArrayList<Integer> epos = new ArrayList<Integer>();
			
			 for (String t: Tmap.keySet() ) {
				 if (Emap.keySet().contains(t)) {
			           interSection.add(t);
				 tpos = Tmap.get(t);
				 epos = Emap.get(t);
				 
				 for(int i=0;i<tpos.size();i++) {
					 
					 int min = tpos.get(i);
					 for (int j=0;j<epos.size();j++) {
						 
						 int distance= Math.abs(tpos.get(i) - epos.get(j));
						 if (min>distance)
							 min=distance;
						 
					 }
					 totalDistance.add(trustWord+","+emotionalWord+","+Integer.toString(min));
					 
				 } 
				 }
			 }
			 
			 if (interSection.size()>0)
				 
		           System.out.println(emotionalWord+" "+trustWord+": intersections: "+interSection + " size:"+interSection.size());
			 }
		 }
		
		 }
	 }
	 //write results into a csv file
	 String csv2 = "Distance.csv";
     CSVWriter writer2 = new CSVWriter(new FileWriter(csv2));
     String[] header2 = {"Trust Word","Emotional Word","Distance"};
     writer2.writeNext(header2);
       
	 
	    //Create record
     for(int i=0;i<totalDistance.size();i++) {
       String [] record = totalDistance.get(i).split(",");
     //Write the record to file
       writer2.writeNext(record);
     }
     //close the writer
       writer2.close();
	
	
   

      }
}
