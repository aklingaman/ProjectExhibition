import main.java.util.LinAlg;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static main.java.util.LinAlg.computeTranspose;
import static main.java.util.LinAlg.matrixVectorMult;

public class TestLinAlg {

    @Test
    public static void testTransposeOrderDoesntMatter() {
        double[][] test = new double[5][3];
        double[][] test2 = new double[5][3];
        double[] vector = new double[5];
        Random rd = new Random(1);
        Random rd2 = new Random(2);
        for(int i = 0; i<test.length; i++) {
            for(int j = 0; j<test[0].length; j++) {
                test[i][j] = rd.nextDouble();
                test2[i][j] = rd2.nextDouble();
            }
        }
        for(int i = 0; i<vector.length; i++) {
            vector[i] = rd.nextDouble();
        }

        double[] ans = LinAlg.matrixVectorMultTranspose(test,vector);
        double[] ans2 = matrixVectorMult(computeTranspose(test2),vector);
        for(int i = 0; i<ans.length; i++){
            Assert.assertTrue(ans[i]==ans2[i]);
        }
    }
}
