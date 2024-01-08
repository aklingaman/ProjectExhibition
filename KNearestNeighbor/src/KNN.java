import java.util.*;
import java.io.*;
import java.math.BigDecimal;
public class KNN {
        
    
    public static void main(String[] args) {
        //TODO: Read in the map to ram    
    
        //TODO: Read in each review, pass to analyzer. 
        
        //TODO: Find Good values of K
        //Ideas: pick a subset of say 25 reviews, plot K and pick whatever works... 
        //Note: K must be odd so you dont have to handle ties. 
        ArrayList<Review> trainingData = new ArrayList<Review>();
        long startTime = System.currentTimeMillis();
        try{
            Scanner s = new Scanner(new File(System.getProperty("user.dir")+File.pathSeparator+"data"+File.pathSeparator+"data.txt"));
            int i = 1;        
            while(s.hasNextLine()) {       
                String words = s.nextLine();
                while(words.equals("")) {
                    words = s.nextLine();
                } //File ends in #EOF anyway so this shouldnt have an exception
                boolean sentiment = (words.charAt(0) == '+') ? true:false; 
                words = words.substring(3); //Removes sentiment , the 1, and the ^I character at the beginning of the review.
                
                Review r = new Review(words);
                r.isPositive = sentiment;
                boolean success = r.analyze(words);
                if(success) { 
                    trainingData.add(r);
                }
                if(i%100==0) {
                    System.out.println("Training Review #" + i + " processed");
                }
                i++;
            }
        



        }catch(Exception e) {}
        
        long totalTime = System.currentTimeMillis()-startTime;
        System.out.println("Raw training took total of " + (1.0*totalTime/1000) + " seconds"); 
        HashMap<String,Double> idfWeights = Review.processReviews(trainingData);            
        System.out.println("Beginning tests, estimation for completion at 50");
        try {
            Scanner testDataScan = new Scanner(new File(System.getProperty("user.dir")+File.pathSeparator+"data"+File.pathSeparator+"test.txt"));
            int i = 1;
            int k = 159;
            File results = new File(System.getProperty("user.dir")+File.pathSeparator+"data"+File.pathSeparator+"results.txt");
            FileWriter write = new FileWriter(results);
            long time = System.currentTimeMillis();
            while(testDataScan.hasNextLine()) {
                String s = testDataScan.nextLine();
                if(s == "") {
                    continue;
                }
                Review r = new Review(s);
                for(String word : r.bag.keySet()) {
                  //System.out.println("Test review " + i +", " + word +": " + r.bag.get(word));
                }
                Boolean sentiment = analyzeReview(r, k, trainingData, idfWeights);
                String line = (sentiment.booleanValue()==true)? "+1":"-1"; 
                write.write(line); 
                write.write(System.lineSeparator());
                System.out.print("Review #" + i + " tested: "+sentiment.booleanValue()+"\n");
                i++;
                if(i==50) {
                  System.out.println("Predicting time to completion: ");
                  long time2 = System.currentTimeMillis()-time;
                  long timeTotal = time2/120;
                  System.out.println("Estimation: " + timeTotal + " minutes");
                }       
            }
        write.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }           
    //So What ive noticed is that when an element is not present in either vector, it doesnt change the calculation. Ergo, instead of going through all the words in the IDF set, i can just combine the keys of the 2 maps into a set, and iterate over that.
    //On Second thought, its way faster to iterate through both and just check if ive already gone through a word. No sense in leaving the realm of O(1) hashing operations.
    public static double computeDistance(Review a, Review b, HashMap<String,Double> idfWeights){
        double ab = 0;
        double aSquare = 0;
        double bSquare = 0;
         
        for(String word : a.words) {
            
            int tfA = a.bag.get(word);        
            Integer tfBInt = b.bag.get(word);
            int tfB = 0;
            if(tfBInt!=null) {
               tfB = tfBInt.intValue();
            }
            
            
            double tfidfA = tfA;
            double tfidfB = tfB;
            Double weight = idfWeights.get(word);
            double mod = 0;
            if(weight!=null) {
               mod = weight.doubleValue();
            }
            tfidfA*=mod;
            tfidfB*=mod;
            ab+=(tfidfA*tfidfB);
            aSquare+=(tfidfA*tfidfA);
            bSquare+=(tfidfB*tfidfB);
        }   
        for(String word : b.words) {
            if(!a.words.contains(word)) {  //Dont want to count a word twice. 
                Integer tfAInt = a.bag.get(word);
                int tfA = 0;
                if(tfAInt!=null) {
                    tfA = tfAInt.intValue();
                }
                int tfB =  b.bag.get(word);          
                double tfidfA = tfA;
                double tfidfB = tfB;
                Double weight = idfWeights.get(word);
                double mod = 0;
                if(weight!=null) {
                    mod = weight.doubleValue();
                }
                tfidfA*=mod;
                tfidfB*=mod;
                ab+=(tfidfA*tfidfB);
                aSquare+=(tfidfA*tfidfA);
                bSquare+=(tfidfB*tfidfB);
            }     
        } 
        
        return (double)(ab / ((Math.sqrt(aSquare)*Math.sqrt(bSquare))));
        //System.out.println(ret);
        //return ret;
    }
    
    //Boolean wrapper gets us a ternary, true for +1, false for -1 and null for error. 
    public static Boolean analyzeReview(Review test, int k, ArrayList<Review> train, HashMap<String,Double> idfWeights) {
        if(k < 1||test == null||test.bag==null||test.bag.size()==0||train==null||train.size()==0||idfWeights==null||idfWeights.size()==0) {
            System.out.println("Error, bad pass to analyzeReview");
            if(k<1) {
               System.out.println("bad k");
            }
            if(test==null) {
               System.out.println("test==null");
            }
            if(test.bag==null) {
               System.out.println("test.bag==null");
            }
            if(test.bag.size()==0) {
               System.out.println("bag size = 0");
            }
            if(train==null) {
               System.out.println("train==null");
            }
            if(train.size()==0) {
               System.out.println("train.size()==0");
            }
            
            if(idfWeights==null) {
               System.out.println("idfWeights==null");
            }
             if(idfWeights.size()==0) {
               System.out.println("idfWeights.size()==0");
            }
            System.exit(1);
        } 
        OrderedReviewList list = new OrderedReviewList(test,k,idfWeights);
        for(int i = 0; i<train.size(); i++) {
            list.insert(train.get(i));      
        }
        return list.vote();
    }
}
