import java.util.ArrayList;
import java.util.Arrays;

public class LogisticRegression {
    private ArrayList<Double> weights;
    private double learningRate;
    private int numIterations;

    public LogisticRegression(double learningRate, int numIterations) {
        this.learningRate = learningRate;
        this.numIterations = numIterations;
    }

    public double sigmoid(double z) {
        return 1 / (1 + Math.exp(-z));
    }

    /*public double logLoss(double[] features, double label) {
        double[] predictions = predict(features);
        double loss = -label * Math.log(predictions[1]) - (1 - label) * Math.log(predictions[0]);
        return loss;
    }*/

    public double predict(ArrayList<Double> features, double percent) {
        double z = weights.get(0);

        for (int i = 1; i < weights.size(); i++) {
            z += weights.get(i) * features.get(i - 1);
        }

        return sigmoid(z);
    }

    public ArrayList<Double> predictAll(ArrayList<ArrayList<Double>> data) {
        ArrayList<Double> ans = new ArrayList<>();

        for (ArrayList<Double> datum : data) {
            ans.add(predict(datum, 0.5));
        }

        return ans;
    }

    public void fit(Table table) {
        weights = new ArrayList<>();
        fillZero(weights, table.header.size());

        for (int iteration = 0; iteration < numIterations; iteration++) {
            ArrayList<Double> gradients = new ArrayList<>();
            fillZero(gradients, weights.size());

            for (int i = 0; i < table.data.size(); i++) {
                double predictions = predict(table.data.get(i), 0.5);
                double error = predictions - table.data.get(i).get(table.header.size() - 1);

                gradients.set(0, gradients.get(0) + error);

                for (int j = 1; j < gradients.size(); j++) {
                    gradients.set(j, error * table.data.get(i).get(j - 1) + gradients.get(j));
                }
            }

            for (int j = 0; j < weights.size(); j++) {
                weights.set(j, weights.get(j) - learningRate * gradients.get(j));
            }
        }
    }

    private void fillZero(ArrayList<Double> x, int n) {
        while (n-- > 0) {
            x.add(0.0);
        }
    }

    public static void solve(Table table) {
        Table train = new Table();
        Table test = new Table();

        table.splitTable(train, test, 85);

        double learningRate = 0.0001;
        int numIterations = 10000;

        LogisticRegression logisticRegression = new LogisticRegression(learningRate, numIterations);
        logisticRegression.fit(train);

        ArrayList<Double> predictions = logisticRegression.predictAll(test.data);
        int[][] conf = TreeAlgorithm.getConfusionMatrix(predictions, test.getColumn("Outcome"), 0.5);

        double P = (conf[1][1]) * 1.0 / (conf[1][1] + conf[0][1]), R = (conf[1][1]) * 1.0 / (conf[1][1] + conf[1][0]);

        System.out.println("Accuracy: " + ((conf[0][0] + conf[1][1]) * 1.0 / predictions.size()));
        System.out.println("Precision: " + P);
        System.out.println("Recall: " + R);
        System.out.println("F1 score: " + (2 * P * R / (P + R)));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.print(conf[i][j] + " ");
            }
            System.out.println();
        }
    }
}

