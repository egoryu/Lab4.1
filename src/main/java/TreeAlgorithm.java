import java.util.*;

class DecisionTreeNode {
    public String featureName;
    public ArrayList<Double> featureValues;
    public ArrayList<DecisionTreeNode> children;
    public Double classLabel;

    public DecisionTreeNode(String featureName, ArrayList<Double> featureValues) {
        this.featureName = featureName;
        this.featureValues = featureValues;
        this.children = new ArrayList<>();
        this.classLabel = null;
    }

    public void addChild(DecisionTreeNode child) {
        this.children.add(child);
    }

    public void setClassLabel(Double classLabel) {
        this.classLabel = classLabel;
    }
}

class DecisionTree {
    public DecisionTreeNode root;

    public DecisionTree() {
        this.root = null;
    }

    public void buildTree(ArrayList<String> header, ArrayList<ArrayList<Double>> data, int maxLevel) {
        this.root = buildTreeHelper(new ArrayList<>(header), new ArrayList<>(data), null, 0, maxLevel);
    }

    private DecisionTreeNode buildTreeHelper(ArrayList<String> header, ArrayList<ArrayList<Double>> data, ArrayList<Double> values, int currentLevel, int maxLevel) {
        if (isSameClass(data)) {
            DecisionTreeNode leafNode = new DecisionTreeNode(null, values);
            leafNode.setClassLabel(data.get(0).get(header.size() - 1));
            return leafNode;
        }

        if (currentLevel == maxLevel || header.size() == 1) {
            DecisionTreeNode leafNode = new DecisionTreeNode(null, values);
            leafNode.setClassLabel(getMostFrequentClass(data, header));
            return leafNode;
        }

        String bestFeature = getBestFeature(data, header);
        DecisionTreeNode root = new DecisionTreeNode(bestFeature, values);

        ArrayList<Double> featureValues = getUniqueValues(data, header.indexOf(bestFeature));
        for (Double value : featureValues) {
            ArrayList<ArrayList<Double>> subset = getSubset(data, header.indexOf(bestFeature), value);

            ArrayList<String> newHeader = new ArrayList<>(header);
            ArrayList<Double> newValues = new ArrayList<>();

            newValues.add(value);
            newHeader.remove(bestFeature);
            removeColumn(subset, header.indexOf(bestFeature));

            DecisionTreeNode childNode = buildTreeHelper(newHeader, subset, newValues, currentLevel + 1, maxLevel);
            root.addChild(childNode);
        }

        return root;
    }

    private boolean isSameClass(ArrayList<ArrayList<Double>> data) {
        double firstClass = data.get(0).get(data.get(0).size() - 1);

        for (int i = 1; i < data.size(); i++) {
            double currentClass = data.get(i).get(data.get(i).size() - 1);

            if (currentClass != firstClass) {
                return false;
            }
        }

        return true;
    }

    private Double getMostFrequentClass(ArrayList<ArrayList<Double>> data, ArrayList<String> header) {
        Map<Double, Integer> classCounts = new HashMap<>();

        for (ArrayList<Double> instance : data) {
            double classLabel = instance.get(header.size() - 1);

            classCounts.put(classLabel, classCounts.getOrDefault(classLabel, 0) + 1);
        }

        double mostFrequentClass = 0.0;
        int maxCount = 0;

        for (Map.Entry<Double, Integer> entry : classCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostFrequentClass = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        if (mostFrequentClass == 1.0) {
            return maxCount * 1.0 / data.size();
        } else {
            return (data.size() - maxCount) * 1.0 / data.size();
        }
    }

    private String getBestFeature(ArrayList<ArrayList<Double>> data, ArrayList<String> header) {
        double maxInfoGain = Double.MAX_VALUE;
        String bestFeature = header.get(0);

        for (int i = 0; i < header.size() - 2; i++) {
            double infoGain = calculateInfoGain(data, header, header.get(i));

            if (infoGain > maxInfoGain) {
                maxInfoGain = infoGain;
                bestFeature = header.get(i);
            }
        }
        return bestFeature;
    }

    private double calculateInfoGain(ArrayList<ArrayList<Double>> data, ArrayList<String> header, String feature) {
        int featureIndex = header.indexOf(feature);
        double entropy = calculateEntropy(data, header.size() - 1);
        double featureEntropy = 0.0;
        ArrayList<Double> featureValues = getUniqueValues(data, featureIndex);

        for (Double value : featureValues) {
            ArrayList<ArrayList<Double>> subset = getSubset(data, featureIndex, value);
            double subsetEntropy = calculateEntropy(subset, header.size() - 1);
            double subsetProbability = (double) subset.size() / data.size();

            featureEntropy += subsetProbability * subsetEntropy;
        }

        return entropy - featureEntropy;
    }

    private double calculateEntropy(ArrayList<ArrayList<Double>> data, int classIndex) {
        Map<Double, Integer> classCounts = new HashMap<>();

        for (ArrayList<Double> instance : data) {
            double classLabel = instance.get(classIndex);

            classCounts.put(classLabel, classCounts.getOrDefault(classLabel, 0) + 1);
        }

        double entropy = 0.0;

        for (Map.Entry<Double, Integer> entry : classCounts.entrySet()) {
            double probability = (double) entry.getValue() / data.size();

            entropy -= probability * (Math.log(probability) / Math.log(2));
        }

        return entropy;
    }

    private ArrayList<Double> getUniqueValues(ArrayList<ArrayList<Double>> data, int columnIndex) {
        ArrayList<Double> uniqueValues = new ArrayList<>();

        for (ArrayList<Double> instance : data) {
            double value = instance.get(columnIndex);

            if (!uniqueValues.contains(value)) {
                uniqueValues.add(value);
            }
        }

        return uniqueValues;
    }

    private ArrayList<ArrayList<Double>> getSubset(ArrayList<ArrayList<Double>> data, int columnIndex, double value) {
        ArrayList<ArrayList<Double>> subset = new ArrayList<>();

        for (ArrayList<Double> instance : data) {
            if (instance.get(columnIndex) == value) {
                subset.add(new ArrayList<>(instance));
            }
        }
        return subset;
    }

    private void removeColumn(ArrayList<ArrayList<Double>> data, int columnIndex) {
        for (int i = 0; i < data.size(); i++) {
            data.get(i).remove(columnIndex);
        }
    }

    public void printDecisionTree(DecisionTreeNode node, int indent) {
        String indentation = "";
        for (int i = 0; i < indent; i++) {
            indentation += "  ";
        }

        System.out.println(indentation + "Feature Name: " + node.featureName);
        System.out.println(indentation + "Feature Values: " + node.featureValues);
        System.out.println(indentation + "Class Label: " + node.classLabel);

        if (node.children != null) {
            for (int i = 0; i < node.children.size(); i++) {
                System.out.println(indentation + "Child " + indent + ":");
                printDecisionTree(node.children.get(i), indent + 1);
            }
        }
    }

    public Double predict(ArrayList<Double> instance, ArrayList<String> header) {
        DecisionTreeNode cur = root;
        while (cur.classLabel == null) {
            double diff = Double.MAX_VALUE;
            int best = 0;

            for (int i = 0; i < cur.children.size(); i++) {
                for (int j = 0; j < cur.children.get(i).featureValues.size(); j++) {
                    if (Math.abs(instance.get(header.indexOf(cur.featureName)) - cur.children.get(i).featureValues.get(j)) < diff) {
                        diff = Math.abs(instance.get(header.indexOf(cur.featureName)) - cur.children.get(i).featureValues.get(j));
                        best = i;
                    }
                }
            }
            cur = cur.children.get(best);
        }

        return cur.classLabel;
    }

    public ArrayList<Double> predictAll(ArrayList<ArrayList<Double>> data, ArrayList<String> header) {
        ArrayList<Double> predicted = new ArrayList<>();

        for (ArrayList<Double> row : data) {
            predicted.add(predict(row, header));
        }

        return predicted;
    }
}
public class TreeAlgorithm {
    public static double build(Table table) {

        for (int i = 0; i < table.data.size(); i++) {
            if (table.data.get(i).get(table.header.size() - 1) > 4) {
                table.data.get(i).set(table.header.size() - 1, 1.0);
            } else {
                table.data.get(i).set(table.header.size() - 1, 0.0);
            }
        }

        Set<String> selected = new HashSet<>();
        while (selected.size() < Math.sqrt(table.header.size())) {
            selected.add(table.header.get((int) (Math.random() * (table.header.size() - 3) + 1.0)));
        }
        selected.add(table.header.get(table.header.size() - 1));

        ArrayList<String> tmp = new ArrayList<>(table.header);
        tmp.removeAll(selected.stream().toList());
        table.removeColumns(tmp);

        System.out.print("Selected column: ");
        for (String str : selected) {
            System.out.print(str + " ");
        }
        System.out.println();

        Table train = new Table();
        Table test = new Table();

        table.splitTable(train, test, 70);

        DecisionTree decisionTree = new DecisionTree();
        decisionTree.buildTree(train.header, train.data, 3);

        ArrayList<Double> predicted = decisionTree.predictAll(test.data, test.header);
        int[][] conf = getConfusionMatrix(predicted, test.getColumn("GRADE"), 0.5);

        System.out.println("Accuracy: " + (conf[0][0] + conf[1][1]) * 1.0 / predicted.size());
        System.out.println("Precision: " + (conf[1][1]) * 1.0 / (conf[1][1] + conf[0][1]));
        System.out.println("Recall: " + (conf[1][1]) * 1.0 / (conf[1][1] + conf[1][0]));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                System.out.print(conf[i][j] + " ");
            }
            System.out.println();
        }
        ArrayList<Double> x = new ArrayList<>();
        ArrayList<Double> y = new ArrayList<>();

        x.add(0.0);
        y.add(0.0);

        getAUC_ROC(predicted, test.getColumn("GRADE"), x, y);

        x.add(1.0);
        y.add(1.0);

        Chart.drawAUC_ROC(x, y);

        x.clear();
        y.clear();

        x.add(1.0);
        y.add(0.0);

        getAUC_PR(predicted, test.getColumn("GRADE"), x, y);

        x.add(0.0);
        y.add(1.0);

        Chart.drawAUC_PR(x, y);

        return (conf[0][0] + conf[1][1]) * 1.0 / test.data.size();
    }

    public static void buildAll(String name, double percent) {
        while (true) {
            Table table = new Table();
            IO.inputCSV(table, name);
            if (build(new Table(new ArrayList<>(table.header), new ArrayList<>(table.data))) > percent) {
                break;
            }
        }
    }

    public static int[][] getConfusionMatrix(ArrayList<Double> predicted, ArrayList<Double> data, double proc) {
        int[][] conf = new int[2][2];

        for (int i = 0; i < predicted.size(); i++) {
            if (predicted.get(i) >= proc && data.get(i) >= proc) {
                conf[1][1]++;
            } else if (predicted.get(i) < proc && data.get(i) < proc) {
                conf[0][0]++;
            } else if (predicted.get(i) < proc && data.get(i) >= proc) {
                conf[1][0]++;
            } else {
                conf[0][1]++;
            }
        }

        return conf;
    }

    public static void getAUC_ROC(ArrayList<Double> predicted, ArrayList<Double> data, ArrayList<Double> x, ArrayList<Double> y) {
        for (double i = 0; i < 1.0; i += 0.01) {
            int[][] conf = getConfusionMatrix(predicted, data, i);

            x.add(conf[1][1] * 1.0 / (conf[1][1] + conf[0][1]));
            y.add(conf[0][1] * 1.0 / (conf[0][1] + conf[0][0]));
        }
    }

    public static void getAUC_PR(ArrayList<Double> predicted, ArrayList<Double> data, ArrayList<Double> x, ArrayList<Double> y) {
        for (double i = 0; i < 1.0; i += 0.01) {
            int[][] conf = getConfusionMatrix(predicted, data, i);

            x.add(conf[1][1] * 1.0 / (conf[1][1] + conf[0][1]));
            y.add(conf[1][1] * 1.0 / (conf[1][1] + conf[1][0]));
        }
    }
}



/*1 13 2 14 4 7 GRADE - 80
* 23 5 7 29 9 GRADE 30 - 90 */
