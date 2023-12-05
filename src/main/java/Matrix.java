//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.ArrayList;
import java.util.Collection;

public class Matrix {
    public static ArrayList<ArrayList<Double>> multiplyMatrices(ArrayList<ArrayList<Double>> A, ArrayList<ArrayList<Double>> B) {
        int rowsA = A.size();
        int colsA = A.get(0).size();
        int colsB = B.get(0).size();
        ArrayList<ArrayList<Double>> result = new ArrayList<>();

        for (int i = 0; i < rowsA; ++i) {
            ArrayList<Double> row = new ArrayList<>();

            for (int j = 0; j < colsB; ++j) {
                double sum = 0.0;

                for (int k = 0; k < colsA; ++k) {
                    sum += A.get(i).get(k) * B.get(k).get(j);
                }

                row.add(sum);
            }

            result.add(row);
        }

        return result;
    }

    public static ArrayList<ArrayList<Double>> getTransposeMatrix(ArrayList<ArrayList<Double>> A) {
        int rows = A.size();
        int cols = A.get(0).size();
        ArrayList<ArrayList<Double>> result = new ArrayList<>();

        for(int j = 0; j < cols; ++j) {
            ArrayList<Double> row = new ArrayList<>();

            for (int i = 0; i < rows; ++i) {
                row.add(A.get(i).get(j));
            }

            result.add(row);
        }

        return result;
    }

    public static ArrayList<ArrayList<Double>> getInverse(ArrayList<ArrayList<Double>> a) {
        int n = a.size();
        ArrayList<ArrayList<Double>> identity = new ArrayList<>();

        for(int i = 0; i < n; ++i) {
            ArrayList<Double> row = new ArrayList<>();

            for(int j = 0; j < n; ++j) {
                if (i == j) {
                    row.add(1.0);
                } else {
                    row.add(0.0);
                }
            }

            identity.add(row);
        }

        ArrayList<ArrayList<Double>> A = new ArrayList<>();

        for(int i = 0; i < n; ++i) {
            A.add(a.get(i));
        }

        for(int i = 0; i < n; ++i) {
            double pivot = A.get(i).get(i);

            for(int k = 0; k < n; ++k) {
                A.get(i).set(k, A.get(i).get(k) / pivot);
                identity.get(i).set(k, identity.get(i).get(k) / pivot);
            }

            for(int k = 0; k < n; ++k) {
                if (k != i) {
                    double factor = A.get(k).get(i);

                    for(int j = 0; j < n; ++j) {
                        A.get(k).set(j, A.get(k).get(j) - factor * A.get(i).get(j));
                        identity.get(k).set(j, identity.get(k).get(j) - factor * identity.get(i).get(j));
                    }
                }
            }
        }

        return identity;
    }

    public static ArrayList<ArrayList<Double>> chooseY(Table table, String name) {
        ArrayList<ArrayList<Double>> result = new ArrayList<>();
        int x = table.header.indexOf(name);

        result.add(new ArrayList<>());

        for(int i = 0; i < table.data.size(); ++i) {
            result.get(0).add(table.data.get(i).get(x));
            table.data.get(i).set(x, 1.0);
        }

        return result;
    }

    public static double R2(Table table, ArrayList<Double> coef, String name) {
        int x = table.header.indexOf(name);
        double average = table.getAverageData().get(x);
        double sum1 = 0.0, sum2 = 0.0, val;

        for(int i = 0; i < table.data.size(); ++i) {
            val = coef.get(x);

            for(int j = 0; j < coef.size(); ++j) {
                if (j != x) {
                    val += table.data.get(i).get(j) * coef.get(j);
                }
            }

            sum1 += Math.pow(table.data.get(i).get(x) - average, 2.0);
            sum2 += Math.pow(table.data.get(i).get(x) - val, 2.0);
        }

        return 1.0 - sum2 / sum1;
    }
}
