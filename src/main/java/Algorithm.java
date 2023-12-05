import java.io.PrintStream;
import java.util.*;

public class Algorithm {
    public static void statist(Table table) {
        ArrayList<ArrayList<Double>> metric = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>(List.of("Min", "Max", "Average", "StandardDeviation", "Quantile 0.25", "Quantile 0.50", "Quantile 0.75", "Quantile 0.95"));
        metric.add(table.getMinData());
        metric.add(table.getMaxData());
        metric.add(table.getAverageData());
        metric.add(table.getStandardDeviationData());
        metric.add(table.getQuantileData(0.25));
        metric.add(table.getQuantileData(0.5));
        metric.add(table.getQuantileData(0.75));
        metric.add(table.getQuantileData(0.95));
        IO.printData(new Table(table.header, metric), names);
    }

    public static ArrayList<ArrayList<Double>> liner(Table train, String name) {
        ArrayList<ArrayList<Double>> Y = Matrix.chooseY(train, name);
        Y = Matrix.getTransposeMatrix(Y);
        ArrayList<ArrayList<Double>> coef = Matrix.multiplyMatrices(Matrix.multiplyMatrices(Matrix.getInverse(Matrix.multiplyMatrices(Matrix.getTransposeMatrix(train.data), train.data)), Matrix.getTransposeMatrix(train.data)), Y);
        return Matrix.getTransposeMatrix(coef);
    }

    public static void printLinerResult(Table table, String modelName, int percent, String predict, List<String> removed) {
        Table train = new Table();
        Table test = new Table();

        System.out.println("\n\n\t" + modelName + "\n");

        table.normalizeData();
        table.removeColumns(removed);
        table.splitTable(train, test, percent);

        ArrayList<ArrayList<Double>> coef = Algorithm.liner(train, predict);
        IO.printData(new Table(train.header, coef), List.of("Coefficient"));

        System.out.println("R2: " + Matrix.R2(test, coef.get(0), predict));
    }
    public static void kNNPredictAllSomeKRandomColumn(String tableName, String modelName, int percent, String predict, int c) {
        int x = (int) ((Math.random() * 7.0) + 1.0);
        Set<String> removed = new HashSet<>();
        Table table = new Table();

        IO.inputCSV(table, tableName);
        for (int i = 0; i < x; i++) {
            removed.add(table.header.get((int) (Math.random() * 7.0)));
        }


        table.removeColumns(removed.stream().toList());
        System.out.println("\n\n\t" + modelName);
        System.out.println("headers:" + table.header);
        System.out.println("Removed headers:" + removed.stream().toList());
        kNNPredictAllSomeK(tableName, modelName, percent, predict, removed.stream().toList(), c);
    }

    public static void kNNPredictAllSomeK(String tableName, String modelName, int percent, String predict, List<String> removed, int c) {
        int x = 1, y = 1;
        for (int i = 0; i < c; i++) {
            int t = x + y;
            Table table = new Table();

            IO.inputCSV(table, tableName);
            kNNPredictAll(table, modelName, percent, predict, removed, t);

            x = y;
            y = t;
        }
    }
    public static void kNNPredictAll(Table table, String modelName, int percent, String predict, List<String> removed, int k) {
        Table train = new Table();
        Table test = new Table();

        System.out.println("\n\n\t" + modelName + "\n");

        table.normalizeData();
        table.removeColumns(removed);
        table.splitTable(train, test, percent);

        ArrayList<Double> trainVal = train.getColumn(predict);
        ArrayList<Double> testVal = test.getColumn(predict);
        ArrayList<Double> result = new ArrayList<>();

        table.removeColumns(List.of(predict));

        for (int i = 0; i < test.data.size(); i++) {
            result.add(kNNPredict(train, trainVal, test.data.get(i), k));
        }

        int[][] matrix = new int[2][2];
        
        for (int i = 0; i < test.data.size(); i++) {
            if (testVal.get(i) == 1.0 && result.get(i) == 1.0) {
                matrix[0][0]++;
            } else if (testVal.get(i) == 0.0 && result.get(i) == 0.0) {
                matrix[1][1]++;
            } else if (testVal.get(i) == 1.0 && result.get(i) == 0.0) {
                matrix[0][1]++;
            } else {
                matrix[1][0]++;
            }
        }

        System.out.println("k=" + k);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("Accuracy: " + (matrix[0][0] + matrix[1][1]) * 1.0 / test.data.size());
    }
    public static Double kNNPredict(Table table, ArrayList<Double> category, ArrayList<Double> instance, int k) {
        List<Map.Entry<Double, Double>> nearestNeighbors = new ArrayList<>();

        for (int i = 0; i < table.data.size(); i++) {
            double distance = calculateDistance(instance, table.data.get(i));
            nearestNeighbors.add(Map.entry(distance, category.get(i)));
        }

        nearestNeighbors.sort(Map.Entry.comparingByKey());

        Map<Double, Integer> classCounts = new HashMap<>();
        for (int i = 0; i < k; i++) {
            double label = nearestNeighbors.get(i).getValue();
            classCounts.put(label, classCounts.getOrDefault(label, 0) + 1);
        }

        Double predictedClass = null;
        int maxCount = 0;
        for (Map.Entry<Double, Integer> entry : classCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                predictedClass = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return predictedClass;
    }

    public static double calculateDistance(ArrayList<Double> instance1, ArrayList<Double> instance2) {
        if (instance1.size() != instance2.size()) {
            throw new IllegalArgumentException("Instances have different dimensions");
        }

        double distance = 0.0;
        for (int i = 0; i < instance1.size(); i++) {
            double diff = instance1.get(i) - instance2.get(i);
            distance += diff * diff;
        }

        return Math.sqrt(distance);
    }
}
