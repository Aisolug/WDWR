//Zapisywanie otrzymanych wyników do pliku

package wdwr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 *
 * @author Aisolug
 */
final class ReadFile {

    public ReadFile() throws FileNotFoundException, IOException {
    }
    ArrayList<ArrayList<Double>> scenarios;

    //wczytanie scenariuszy
    public ArrayList<ArrayList<Double>> generateScenarios() throws FileNotFoundException {

        try (Scanner fileScanner = new Scanner(new File("D:\\R\\data100.txt")).useLocale(Locale.US)) {
            scenarios = new ArrayList<>();
            while (fileScanner.hasNextLine()) {
                scenarios.add(new ArrayList<>());
                for (int k = 0; k < 3; k++) {

                    scenarios.get(scenarios.size() - 1).add(fileScanner.nextDouble());
                }

            }
            fileScanner.close();
        }
        return scenarios;
    }
//liczenie średniej ze scenariuszy
    public double[] averageScenarios() {
        double[] costAvg = new double[3];
        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < scenarios.size(); l++) {
                costAvg[k] += scenarios.get(l).get(k);
            }
            costAvg[k] /= scenarios.size();
        }

        return costAvg;
    }
//zapis danych do pliku
    public void saveFile(ArrayList<ArrayList<Double>> lista) throws IOException {
        FileWriter fileWriter;
        fileWriter = new FileWriter("D:\\R\\result2.csv");
        //zapis r "cost"
        fileWriter.append("cost");
        fileWriter.append(',');
        //zapisa r max
        fileWriter.append(String.valueOf("risk"));
        //oddzielenie od reszty
        fileWriter.append('\n');
        fileWriter.append('\n');
        for (int j = 0; j < lista.size(); j++) {

            for (int i = 0; i < 2; i++) {
                //zapisywanie kolejnych wartości, tak, że odpowiednie wartości zmiennych w odpowiednich kolumnach
                fileWriter.append(String.valueOf(lista.get(j).get(i)));
                fileWriter.append(",");
            }
            fileWriter.append('\n');
        }
        fileWriter.flush();
        fileWriter.close();

    }
}
