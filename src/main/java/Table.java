import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Table {
    ArrayList<String> header;
    ArrayList<ArrayList<Double>> data;

    public Table() {
        this.header = new ArrayList<>();
        this.data = new ArrayList<>();
    }

    public Table(ArrayList<String> header, ArrayList<ArrayList<Double>> data) {
        this.header = header;
        this.data = data;
    }

    void normalizeData() {
        for(int i = 0; i < this.header.size(); ++i) {
            double min = this.getMinData().get(i);
            double max = this.getMaxData().get(i);

            for (ArrayList<Double> datum : this.data) {
                datum.set(i, (datum.get(i) - min) / (max - min));
            }
        }

    }

    ArrayList<Double> getAverageData() {
        ArrayList<Double> ans = new ArrayList<>();

        for(int i = 0; i < this.header.size(); ++i) {
            double x = 0.0;

            for (ArrayList<Double> datum : this.data) {
                x += datum.get(i);
            }

            ans.add(x / this.data.size());
        }

        return ans;
    }

    ArrayList<Double> getMinData() {
        ArrayList<Double> ans = new ArrayList<>();

        for(int i = 0; i < this.header.size(); ++i) {
            double x = Double.MAX_VALUE;

            for (ArrayList<Double> datum : this.data) {
                x = Math.min(datum.get(i), x);
            }

            ans.add(x);
        }

        return ans;
    }

    ArrayList<Double> getMaxData() {
        ArrayList<Double> ans = new ArrayList<>();

        for(int i = 0; i < this.header.size(); ++i) {
            double x = Double.MIN_VALUE;

            for (ArrayList<Double> datum : this.data) {
                x = Math.max(datum.get(i), x);
            }

            ans.add(x);
        }

        return ans;
    }

    ArrayList<Double> getStandardDeviationData() {
        ArrayList<Double> ans = new ArrayList<>();
        ArrayList<Double> average = this.getAverageData();

        for(int i = 0; i < this.header.size(); ++i) {
            double x = Double.MIN_VALUE;

            for (ArrayList<Double> datum : this.data) {
                x += Math.pow(datum.get(i) - average.get(i), 2.0);
            }

            ans.add(Math.sqrt(x / this.data.size()));
        }

        return ans;
    }

    ArrayList<Double> getQuantileData(double percentile) {
        ArrayList<Double> ans = new ArrayList<>();

        for(int col = 0; col < this.header.size(); ++col) {
            ArrayList<Double> columnData = new ArrayList<>();

            for (ArrayList<Double> datum : this.data) {
                columnData.add(datum.get(col));
            }

            Collections.sort(columnData);
            ans.add(columnData.get((int)(columnData.size() * percentile)));
        }

        return ans;
    }

    public void splitTable(Table train, Table test, int part) {
        int x = (int) (this.data.size() * (part / 100.0));
        train.header = this.header;

        for(int i = 0; i < x; ++i) {
            train.data.add(this.data.get(i));
        }

        test.header = this.header;

        for(int i = x; i < this.data.size(); ++i) {
            test.data.add(this.data.get(i));
        }

    }

    public void removeColumns(List<String> columnIndices) {
        for (String col : columnIndices) {
            this.removeColumn(col);
        }
    }

    /*
    * +
    * сколько зарабатываю
    * когда зарабатывал
    * -
    * если человек не знаком*/
    public void removeRowZero() {
        for (ArrayList<Double> row: data) {
            for (int i = 1; i < row.size() - 1; i++) {
                if (row.get(i) == 0) {
                    data.remove(row);
                    removeRowZero();
                    return;
                }
            }
        }
    }

    public void removeColumn(String columnIndices) {
        int col = this.header.indexOf(columnIndices);

        if (col < 0) {
            return;
        }
        for (int i = 0; i < this.data.size(); ++i) {
            this.data.get(i).remove(col);
        }

        this.header.remove(col);
    }

    public ArrayList<Double> getColumn(String name) {
        ArrayList<Double> ans = new ArrayList<>();
        int x = header.indexOf(name);

        for (int i = 0; i < data.size(); ++i) {
            ans.add(data.get(i).get(x));
        }

        return ans;
    }
}
