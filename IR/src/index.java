

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.CSVWriter;

import java.io.PrintWriter;
	
	public class index{
		@SuppressWarnings("unused")
		public static void main(String[] args)throws Exception 
		  {
			//create the emotional words index
			HashMap<String,ArrayList<String>> EWIndex = new HashMap<String,ArrayList<String>>();
			File file1 = new File("EmotionalWords.txt");
			try (BufferedReader br = new BufferedReader(new FileReader(file1))) {
				String st; 
				 while ((st = br.readLine()) != null) {
					 String[] words = st.split(" ");
					 for (int i=0; i<words.length;i++) {
						 if(words[i].length()>0)
					         EWIndex.put(words[i], null);
					 }
					
				 }
			}
			System.out.println(EWIndex.keySet());
			
			//create the trust words index
			HashMap<String,ArrayList<String>> TWIndex = new HashMap<String,ArrayList<String>>();
			File file2 = new File("TrustWords.txt");
			try (BufferedReader br2 = new BufferedReader(new FileReader(file2))) {
				String st2; 
				 while ((st2 = br2.readLine()) != null) {
					 String[] words2 = st2.split(" ");
					 for (int i=0; i<words2.length;i++) 
					     TWIndex.put(words2[i], null);
					 
					
				 }
			}


		File dir = new File("Files/Arabic/");
		int FileNum=1;
		int sum=0;

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
			Boolean flag =false;
			

			while (bi.next() != BreakIterator.DONE) {

				String sentence = newText.substring(index, bi.current());
				String doc = "D"+Integer.toString(FileNum)+"S"+Integer.toString(Snum);
				sentence = sentence.replaceAll("[؟،]", " ");
				sentence = sentence.replaceAll("\\p{Punct}", " ");
				//trust words index
				for (String tw : TWIndex.keySet()) {
					String[] w2 = sentence.split("\\s+");
					for (String e : w2) {
						
						if(e.equals(tw)) {
							//insert the doc and the sentence into the posting 
							ArrayList<String> TrustPosting =  TWIndex.get(tw);
							
							//String doc = "D1S"+Integer.toString(Snum);
							if (TrustPosting!=null) {
							     TrustPosting.add(doc);
							}
							else if (TrustPosting==null) {
								 TrustPosting = new ArrayList<String>();
								 TrustPosting.add(doc);
							}
							
							TWIndex.put(tw, TrustPosting);
							
					     }
					}
						
				}
				
				//Emotional words index
				for (String w : EWIndex.keySet()) {
					String[] w1 = sentence.split(" ");
					for (String e : w1) {
						
						if(e.equals(w)) {
							//insert the doc and the sentence into the posting 
							ArrayList<String> posting =  EWIndex.get(w);
							
							
							if (posting!=null) {
							     posting.add(doc);
							}
							else if (posting==null) {
								 posting = new ArrayList<String>();
								 posting.add(doc);
							}
							
							EWIndex.put(w, posting);
							
					    
					     }
					}
						
				}
				
				index = bi.current();
				sum+=Snum;
				Snum++;

			}  
		    }
		    FileNum++;
		    s.close();
		}
		System.out.println(FileNum-1);
		System.out.println(sum);
		 //TODO Intersection
		ArrayList<String> Em_results = new ArrayList<String>();
		 for (String emotionalWord: EWIndex.keySet()) {
			 if (EWIndex.get(emotionalWord)!=null) {
				 ArrayList<String> t = EWIndex.get(emotionalWord);
		     	 Set<String> sen = new HashSet<String>(t);
		     	Set<String> unique_doc = new HashSet<String>();
		     	 for (int i=0;i<t.size();i++) {
		     		
		     	  String st=t.get(i).substring(0, t.get(i).indexOf('S'));
		     	  
		     	  unique_doc.add(st);
		     		
		     	 }
		     	Set<String> doc= new HashSet<String>(t);
			    Em_results.add(emotionalWord+","+EWIndex.get(emotionalWord).size()+","+sen.size()+","+unique_doc.size());
		 
			 }
			 else {
				 Em_results.add(emotionalWord+","+ 0+","+0+","+0);
			 }
		 }
		  String csv = "emotionalResults.csv";
	      CSVWriter writer = new CSVWriter(new FileWriter(csv));
	      String[] header1 = {"Emotional Word","Number of all occurrences","Number of occurrences in Sentences","Number of occurrences in documents"};
	      writer.writeNext(header1); 
	      //Create record
	      for(int i=0;i<Em_results.size();i++) {
	      String [] record = Em_results.get(i).split(",");
	      //Write the record to file
	      writer.writeNext(record);
	      }
	      //close the writer
	      writer.close();
	      
	      //////////////////////////write to csv file
	      ArrayList<String> trust_results = new ArrayList<String>();
			 for (String trustWord: TWIndex.keySet()) {
				 if (TWIndex.get(trustWord)!=null) {
					 ArrayList<String> t = TWIndex.get(trustWord);
			     	 Set<String> sen = new HashSet<String>(t);
			     	Set<String> unique_doc = new HashSet<String>();
			     	 for (int i=0;i<t.size();i++) {
			     		
			     	  String st=t.get(i).substring(0, t.get(i).indexOf('S'));
			     	  
			     	  unique_doc.add(st);
			     		
			     	 }
			     	Set<String> doc= new HashSet<String>(t);
				     trust_results.add(trustWord+","+TWIndex.get(trustWord).size()+","+sen.size()+","+unique_doc.size());
			 
				 }
				 else {
					 trust_results.add(trustWord+","+ 0+","+0+","+0);
				 }
			 }
			 String csv1 = "trustWords.csv";
		      CSVWriter writer1 = new CSVWriter(new FileWriter(csv1));
		      String[] header = {"Trust Word","Number of all occurrences","Number of occurrences in Sentences","Number of occurrences in documents"};
		      writer1.writeNext(header);
		        
		      //Create record
		      for(int i=0;i<trust_results.size();i++) {
		      String [] record = trust_results.get(i).split(",");
		      //Write the record to file
		        writer1.writeNext(record);
		      }
		      //close the writer
		      writer1.close();
	
	
		 System.out.println("+++++++++++++++++++++++++++++++++++++++");
	
		// for each key in the trust words index, find the intersection with the emotional words index
	
	 	 int counter=0;
		 ArrayList<String> results = new ArrayList<String>();
		 for (String trustWord:TWIndex.keySet()) {
			ArrayList<String> Tmap = TWIndex.get(trustWord);
			// Set<String> trust_doc = new HashSet<String>(Tmap);

			System.out.println(trustWord+": ");
			
			 if (Tmap!=null) {
			 //System.out.println(trustWord+" "+ Tmap.keySet());
				 System.out.println(Tmap);
			 for (String emotionalWord: EWIndex.keySet()) {
				 ArrayList<String> Emap = EWIndex.get(emotionalWord);
				 ArrayList<String> list = new ArrayList<String>();
				 if(Emap!=null) {
				for (String t : Tmap) {
					if (Emap.contains(t))
						list.add(t);
				}
				if (list.size()>0) {
					
					String size =Integer.toString(list.size());
					System.out.println(size);
					results.add(trustWord+","+emotionalWord+","+size);
					
					counter++;
					System.out.println(counter);

			System.out.println("list: "+list);
				
								 
					System.out.println(emotionalWord+": "+Emap);
				}
			 	
				 }
			 }
			 }
			 System.out.println("----------------------------------------------------");
			 }
		//write results into a csv file
		 String csv2 = "InterSection.csv";
	      CSVWriter writer2 = new CSVWriter(new FileWriter(csv2));
	      String[] header2 = {"Trust Word","Emotional Word","Number of intersections"};
	      writer2.writeNext(header2);
	        
		 
		    //Create record
	      for(int i=0;i<results.size();i++) {
	      String [] record = results.get(i).split(",");
	      //Write the record to file
	        writer2.writeNext(record);
	      }
	      //close the writer
	      writer2.close();
		 
	
		


	      }
	
	}



