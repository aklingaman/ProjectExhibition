import edu.stanford.nlp.process.*;
import java.util.*;
import java.io.*;
public class Review {
    public HashMap<String,Integer> bag; //Stores word frequencies directly. 
    public boolean isPositive; //True for positive reviews false for negative.
    public int length; 
    public Set<String> words;
    public Review() {
        bag = new HashMap<String,Integer>();
    }
    public Review(String review) {
        bag = new HashMap<String,Integer>();
        analyze(review);
        makeWordSet();
    }
    public int getFrequency(String word) {
        if(bag.get(word)==null) {
            return 0;
        } else {
            return bag.get(word);
        }
    }
    //Makes a set containing all the words. We look into this set when computing the distance
    private void makeWordSet() {
        words = new HashSet<String>();
        words.addAll(bag.keySet());
    }


    //Returns success.
    public boolean analyze(String review) {
        /* 1.String tokenize each word out
           2.Throw out the word if its A stop word ????????
           3.Stem the word. 
           4.Search for a map entry for the word
           5A. Add new entry if not found
           5B. Increment the value if it is. 
        */
        if(review==null||review.equals("")) {
            System.out.println("null review passed, returning");
            return false; 
        } 
     
        StringTokenizer tokenizer = new StringTokenizer(review, "\"*`()?!<>-:;/,.'%+{}[] "); //Removing the sentiment manually lets me use dashes as a extra delimiter which is super needed ( see first review )
        StopList stop = new StopList(new File(System.getProperty("user.dir")+File.pathSeparator+"data"+File.pathSeparator+"stopwords.txt"));
        Stemmer stem = new Stemmer();
        while(tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken();
            if(word.equals("#EOF")) {
                //System.out.println("EOF found.");
                return true;
            }
            if(word.length()==1) { //Cases like nickel'n'dime will make a word thats just n, skipping.
                continue;
            }

            word = word.toLowerCase();

            if(stop.contains(word)) {
                //System.out.println("Skipped: " + word);
                continue;
            }
        
            word = stem.stem(word);
            //System.out.println(word);        

            Integer current = bag.putIfAbsent(word, new Integer(1));              
            if(current!=null) {
                bag.replace(word,current.intValue()+1);
            } else {
                this.length++;
            } 
        }
        return false; //Unreachable under the assumption that the review ends with a #EOF, but regardless java needs it. 
    }

    //Here we convert the hashmap to a csv file. This allows us to run the trainer and the tester seperately by just loading the file. Returns success or fail.

    
    public static boolean writeHashMapToFile(HashMap<String, Integer> wordbag) {
        
        
        try {
            File bag = new File("BagOfWords.csv");
            FileWriter writer = new FileWriter(bag);
            Iterator it = wordbag.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                String line = pair.getKey() + ", " + pair.getValue()+"\n";
                writer.write(line);
            }
            writer.flush();
            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static HashMap<String,Double> processReviews(ArrayList<Review> reviews) {
        int trainingSize = reviews.size();
        HashMap<String,Integer> collection = new HashMap<String,Integer>();
        //Collection stores word frequencies across entire training data. 
        System.out.println("Calculating idf");    
        for(Review r : reviews) {
            for(String word : r.bag.keySet()) {
                Integer current = collection.putIfAbsent(word, new Integer(1));
                if(current!=null) {
                    collection.replace(word,current.intValue()+1);
                }
            }
        }
        /*
        List<String> removals = new LinkedList<String>();
        for(String s : collection.keySet()) {
            System.out.println("Collection: " + s + ": " + collection.get(s));
            if(collection.get(s).intValue()==1) {
               removals.add(s); //Have to not get a concurrentModificationException
            }
        }
        for(String s : removals) {
            collection.remove(s);  
        }
        */
        
        
        
        
        System.out.println("Finished combining review maps");
        HashMap<String,Double> idfWeights = new HashMap<String,Double>();
        final double log2base10 = Math.log10(2);
        for(String word : collection.keySet() ) {
            double x = trainingSize/collection.get(word).intValue();
            x = Math.log10(x)/log2base10;
            //So im not going to be normalizing for the length of the document.  This has the added effect of making all word weights identical regardless of what document they are appearing in.        
            idfWeights.putIfAbsent(word, new Double(x));
        }        
        System.out.println("Finished calculating idf");
        return idfWeights; 
    }
}
