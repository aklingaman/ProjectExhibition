import java.util.Random;
public class Miscellanious {

   static Random rd = new Random();


   public static double triangular(double a, int b, double c) {
      //min 20 mode 25 max 30
      double u = Math.random();
      if(u<(c-a)/(b-a)) {
         return a + Math.sqrt(u*(b-a)*(c-a)); 
      } else {
         return b - Math.sqrt((1-u)*(b-a)*(b-c));
      }  
   }
   
   public static double expo(double mean) {
      double lambda = 1.0/mean;
      return (-1.0/lambda)*Math.log(Math.random());
   }


   //Computes normal dist. Use same units for both params.
   public static double normalDist(double mean, double stdev) {
      return rd.nextGaussian()*stdev+mean;
   }

}