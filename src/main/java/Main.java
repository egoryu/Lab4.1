import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*Table table = new Table();
        IO.inputCSV(table, "diabetes.csv");
        Chart.drawHistograms(table);
        Algorithm.statist(table);

        TreeAlgorithm.build(table);

        Algorithm.kNNPredictAllSomeK("diabetes.csv", "Model 1", 70, "Outcome", List.of("Pregnancies", "Insulin"), 10);

        Algorithm.kNNPredictAllSomeKRandomColumn("diabetes.csv", "Model 2", 70, "Outcome", 10);*/

        Table table = new Table();
        IO.inputCSV(table, "DATA.csv");
        TreeAlgorithm.build(table);
        /*IO.inputCSV(table, "diabetes.csv");
        table.removeRowZero();
        LogisticRegression.solve(table);*/
    }
}
