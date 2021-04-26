public class FireSimulation {
   public static void main(String[] args) {
      int numReplications = 5;
      StatDump[] stats = new StatDump[numReplications];
      int i = 0;
      while(i<numReplications) {
         Simulation s = new Simulation();
         while(stats[i]==null) {
            s.tick();
         }
         i++;
      }
      
      
      
      
      
      
         
   }
}