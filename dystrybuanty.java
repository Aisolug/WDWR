//Wczytywanie dystrybuant wygenerowanych w R

package wdwr;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author Aisolug
 */
public class dystrybuanty {

public dystrybuanty(){
    
}   
    public void dystryb(double[] scenarios) throws IOException{
        FileWriter fileWriter;
        fileWriter = new FileWriter("D:\\R\\dystrybuanta3.csv");
        
        
        Arrays.sort(scenarios);
        double[] skumulowana = new double[scenarios.length];
        int i;
         
        for (i=0; i < scenarios.length; i++){
            for (int k = 1; k<i+2; k++){
                skumulowana[i] += scenarios[scenarios.length-k];
            }
            fileWriter.append(String.valueOf(skumulowana[i]));
            fileWriter.append('\n');
        }
        fileWriter.flush();
        fileWriter.close();
        System.out.println(skumulowana[skumulowana.length-1]);

        
    }
}
