package wdwr;

import ilog.cplex.*;

import ilog.concert.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Aisolug
 */
public class model {

    double[] cost = {44.9745448834606, 35.2347110553482, 39.8285917033513};
    double[][] efficiency = {
        {0.85, 1.3, 0.65, 1.5, 0.4},
        {0.65, 0.8, 0.55, 1.5, 0.7},
        {1.2, 0.95, 0.35, 1.7, 0.4}
    };
    int M = 100;
    double[] costAvg;

    public model() throws IOException {

        ReadFile inFile = new ReadFile();
        ArrayList<ArrayList<Double>> scenarios = inFile.generateScenarios();
        ArrayList<ArrayList<Double>> results = new ArrayList();

        costAvg = inFile.averageScenarios();
        /*
        Test scenariuszy
        for (int k = 0; k < 3; k++) {
             System.out.println(cost[k] - costAvg[k]);
        }
         */
        //for (int step = 0; step <80 ; step++){
        try {

            IloCplex cplex = new IloCplex();
            /*
            
            i - numer podzespołu
            j - numer wymogu odnośnie ilości sztuk danego podzespołu
            k - numer maszyny
            hours - suma godzin danej maszyny
            efficiency - wydajność
            cost - koszty danej maszyny
            M - duża liczba (180-100)
             
            inicjalizacja zmiennych
            3-elementowy wektor boolean dla każdej z maszyn */
            IloNumVar[] bool = new IloNumVar[3];

            //macierz 3x5 sztuk
            IloNumVar[][] items = new IloNumVar[3][5];
            IloNumVar[] costExtra = cplex.numVarArray(3, 0.0, Double.MAX_VALUE);//jezeli to zakres przyjmowanych zmiennych to blad 
            IloIntVar[][] intItems = new IloIntVar[3][5];

            for (int k = 0; k < 3; k++) {
                items[k] = cplex.numVarArray(5, 0.0, Double.MAX_VALUE);
                intItems[k] = cplex.intVarArray(5, 0, Integer.MAX_VALUE);
                bool[k] = cplex.boolVar();
            }

            /*
            nadanie ograniczeń
            godz - suma godzin danej maszyny <= 180
            intemsEnough - ilość każdego z podzespołów A,B oraz C >= 60 oraz D+E >= 120
             */
            IloLinearNumExpr[] itemsEnough = new IloLinearNumExpr[4];
            IloLinearNumExpr[] allMachine = new IloLinearNumExpr[3];

            IloNumExpr model = cplex.numExpr();

            IloNumExpr[] hours = new IloNumExpr[3];

            for (int j = 0; j < 4; j++) {
                itemsEnough[j] = cplex.linearNumExpr();
            }
            for (int k = 0; k < 3; k++) {

                hours[k] = cplex.numExpr();

                allMachine[k] = cplex.linearNumExpr();

                for (int i = 0; i < 5; i++) {

                    hours[k] = cplex.sum(hours[k], cplex.prod(1 / efficiency[k][i], items[k][i]));
                    //wymuszanie zmiennej całkowitej liczby sztuk
                    cplex.addLe(intItems[k][i], items[k][i]);
                    cplex.addGe(intItems[k][i], cplex.sum(items[k][i], -1));
                    cplex.addEq(intItems[k][i], items[k][i]);
                    allMachine[k].addTerm(1, items[k][i]);
                    if (i < 3) {
                        //dla A,B oraz C
                        itemsEnough[i].addTerm(1, items[k][i]);
                    } else {
                        //D+E
                        itemsEnough[3].addTerm(1, items[k][i]);
                    }
                }

                //suma godzin <= 180
                cplex.addLe(hours[k], 180);
                cplex.addGe(allMachine[k], 1);
                //czy suma godzin <= 100 jeżeli tak to bool danej maszyny = 0

                cplex.addLe(hours[k], cplex.sum(100, cplex.prod(M, bool[k])));
                //godz<=100 + Mu
                //godz>=100u
                cplex.addGe(hours[k], cplex.prod(100, bool[k]));

                cplex.addGe(costExtra[k], cplex.prod(bool[k], 0.2 * costAvg[k]));
                cplex.addLe(costExtra[k], cplex.prod(bool[k], 36 * costAvg[k]));
                //z<=bool*50

                //z>=bool*5
                cplex.addLe(costExtra[k], cplex.prod(hours[k], 0.2 * costAvg[k]));
                //z<=0.2koszt + L(u-1)

                cplex.addGe(cplex.sum(costExtra[k], cplex.prod(36 * costAvg[k], cplex.sum(1, cplex.negative(bool[k])))), cplex.prod(hours[k], 0.2 * costAvg[k]));

                //cost -zad1, costAvg zad2
                model = cplex.sum(model, cplex.sum(cplex.prod(0.8 * costAvg[k], hours[k]), costExtra[k]));

            }

            //cd nadania ograniczen ilości podzespołu A,B,C min 60 sztuk
            for (int j = 0; j < 3; j++) {
                cplex.addGe(itemsEnough[j], 60);

            }
            //cd nadania ograniczen ilości podzespołu D+E min 120 sztuk
            cplex.addGe(itemsEnough[3], 120);

            //minimalizacja sumy kosztu dla każdej z maszyn (liczba godzin*cena), jeżeli bool danej maszyny =0 to koszty spadają o 20%
            IloNumExpr riskGini = cplex.numExpr();

            for (int l = 0; l < scenarios.size(); l++) {
                for (int n = 0; n < scenarios.size(); n++) {
                    for (int k = 0; k < 3; k++) {

                        riskGini = cplex.sum(riskGini, cplex.prod(cplex.abs(cplex.sum(cplex.sum(cplex.prod(hours[k], 0.8 * scenarios.get(l).get(k)), costExtra[k]),
                                cplex.negative(cplex.sum(cplex.prod(hours[k], scenarios.get(n).get(k)), costExtra[k])))), 1.0 / (2 * scenarios.size() * scenarios.size())));
                    }
                }

            }
            /* Test zmiennej binarnej
             cplex.addGe(hours[0],101);    
             cplex.addGe(hours[1],101);  
             cplex.addGe(hours[2],101);
             */
            cplex.addLe(riskGini, 1155);
            cplex.addMinimize(model);

            //cplex.addMinimize(cplex.prod(1/400, riskGini));
            //wyświetl koszt oraz sztuki
            if (cplex.solve()) {
                results.add(new ArrayList<>());
                results.get(results.size() - 1).add(cplex.getValue(model));
                results.get(results.size() - 1).add(cplex.getValue(riskGini));
                System.out.println(cplex.getValue(model));
                System.out.println(cplex.getValue(riskGini));

                for (int k = 0; k < 3; k++) {
                    System.out.println(cplex.getValue(hours[k]));

                    for (int i = 0; i < 5; i++) {
                        System.out.println("sztuki " + cplex.getValue(items[k][i]));
                    }
                    System.out.println(cplex.getValue(costExtra[k]));
                    System.out.println(cplex.getValue(bool[k]));
                }
                double[] costScenario = new double[scenarios.size()];
                for (int l = 0; l < scenarios.size(); l++) {
                    double suma = 0.0;
                    for (int k = 0; k < 3; k++) {
                        suma += cplex.getValue(hours[k]) * scenarios.get(l).get(k) * (0.8 + 0.2 * cplex.getValue(bool[k]));
                    }
                    costScenario[l] = suma / scenarios.size();
                }
                dystrybuanty fsd = new dystrybuanty();
                fsd.dystryb(costScenario);
            } else {
                System.out.println("Model not solved");
            }

        } catch (IloException exc) {
            System.out.print(exc);
        }
       
        inFile.saveFile(results);
    }

}
