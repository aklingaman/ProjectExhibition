//This data structure I made holds up to <Capacity> reviews as a list in order from least to greatest. This exists in order to minimize the number of comparisons, as well as reduce the number of calls to computeDistance, to try to speed up the program to a reasonable runtime.  I had to make this myself because the list depends on what review you pass it as the testReview, so i cant bring down the number of computeDistance calls by comparing the double values when they are known by just implementing comparator. 
//This very likely doesnt work with a K value of one, but not bothering to fix that

import java.util.*;
public class OrderedReviewList {
    public class Node {
        public Review rev;
        public Node next;
        public Node prev;
        public double value;
        public int insertNumber; //the insertNumberTH element to be added to the list. 
        public Node(Review r, double value) {//A wrong value here will be very bad, lets make sure that doesnt happen. 
            this.rev = r;
            this.value = value;
        
        }
         
    }
    public int insertions;
    public Review testReview;
    public HashMap<String,Double> idfWeights;
    public Node head;
    public int size;
    public int capacity;
    public Node tail;   

    public OrderedReviewList(Review r, int capacity, HashMap<String,Double> idfWeights) {
        this.insertions = 0;
        this.size = 0;
        this.capacity = capacity;
        this.testReview = r;
        this.idfWeights = idfWeights;
    }
    private double getValue(Node n) {
        if(n.value<0) {
            n.value = KNN.computeDistance(testReview,n.rev,idfWeights);
            //System.out.println("List: distance not found, computed");
        }
        return n.value;
    }
    
    public double getMinValue() {
        return getValue(head);
    } 
    public double getMaxValue() {
         return getValue(tail);
    }      

    public void insert(Review newRev) {
        //printList();
        double newDist = KNN.computeDistance(newRev,testReview,idfWeights);
        //System.out.println("Trying to insert: " + newDist);
        if(this.head == null) {
            Node newNode = new Node(newRev,newDist);
            this.head = newNode;
            this.insertions++;
            newNode.insertNumber = this.insertions;
            this.tail = this.head;
            size++;
            return;
        }
        
        if(newDist<=getMinValue()) {
            if(this.size==this.capacity) {  //NewDist is more dissimilar than the least similar entry in our full list, decline this entry.
                return;
            }
            //Even though this entry is the least similar thing weve found, there is still room, so put it in the list. 
            Node newNode = new Node(newRev,newDist);
            this.insertions++;
            newNode.insertNumber = this.insertions;
            newNode.next = head;
            head.prev = newNode;
            head = newNode;   
            headTrim();         
            return;
        }
        
        
        
        //System.out.println("List: checking max: " + getMaxValue());
        if(newDist>=getMaxValue()) { //This node is the most similar weve seen so far, so put it at the end of the list.   
            Node newNode = new Node(newRev,newDist);
            this.insertions++;
            newNode.insertNumber = this.insertions;
            newNode.next = null;
            newNode.prev = this.tail;
            newNode.prev.next = newNode;
            this.tail = newNode;
            headTrim();
            return;
        }
        
        //We now know that minDist<newNode<maxDist, so we have to iterate to find its spot. 
        Node iterate = head;       
        while(newDist>getValue(iterate)) { 
            iterate = iterate.next;
        }
        //Iterate now points to the first element for which the condition is false, so the first element that bigger or equal in size to newNode. 
        
        if(iterate==null) { //We shouldnt manage to reach this because this happens when we are in the insert tail node case. 
            System.out.println("List error"); 
        }
        
        
        Node newNode = new Node(newRev,newDist);  //We have to make a newNode no matter what.  
        this.insertions++;
        newNode.insertNumber = this.insertions;
        newNode.prev = iterate.prev;  
        newNode.next = iterate; 
        iterate.prev.next = newNode;
        iterate.prev = newNode;    
        headTrim();       
        return; 
    }
    
    public void headTrim() {
        this.insertions++;
        if(this.size<this.capacity) {
            this.size++;
        } else {
            this.head = this.head.next;
            this.head.prev = null;
        }
    }
    public Boolean vote() {
        //printList();        
        int i = 0;
        int total = 0;
        Node copy = head;
        while(i<size) {
            int mod = (copy.rev.isPositive==true)? 1 : -1;    
            /*
            if(mod>0) {
               System.out.print("+1, ");
            } else {
               System.out.print("-1, ");
            }
            */
            total+=mod;
            i++;
            copy = copy.next;
        }
        System.out.printf("Voting: %+3d, ", total);
        //System.out.println();
        Boolean ret = (total>0)?(new Boolean(true)):(new Boolean(false));
        return ret;
    } 
    public void printList() {
        if(this.size==0) {
            System.out.println("List is empty");
            return;
        }
        Node copy = head;
        System.out.println("List: head: " + getValue(this.head));
        while(copy!=null) {
            System.out.printf("\tValue: %1.5f, Sentiment: %s, insertNo: %d\n", getValue(copy), copy.rev.isPositive,copy.insertNumber);
            copy = copy.next;
        }
        System.out.println("List: tail: "+getValue(this.tail));
    }

}
