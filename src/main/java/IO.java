import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.max;

public class IO {
    public static void printTable(Table table) {
        int columnCount = table.header.size();
        int[] columnWidths = new int[columnCount];

        for(int i = 0; i < columnCount; ++i) {
            columnWidths[i] = table.header.get(i).length();

            for (ArrayList<Double> doubles : table.data) {
                String cell = doubles.get(i).toString();
                if (cell.length() > columnWidths[i]) {
                    columnWidths[i] = cell.length();
                }
            }
        }

        for(int i = 0; i < columnCount; ++i) {
            String cell = table.header.get(i);
            System.out.print(cell);
            printSpaces(columnWidths[i] - cell.length() + 2);
        }

        System.out.println();

        for(int i = 0; i < columnCount; ++i) {
            printLine(columnWidths[i] + 2);
        }

        System.out.println();

        for (ArrayList<Double> doubles : table.data) {
            for (int i = 0; i < columnCount; ++i) {
                String cell = doubles.get(i).toString();
                System.out.print(cell);
                printSpaces(columnWidths[i] - cell.length() + 2);
            }

            System.out.println();
        }

    }

    public static void printData(Table table, List<String> col) {
        int columnCount = table.header.size();
        int[] columnWidths = new int[columnCount];

        for(int i = 0; i < columnCount; ++i) {
            columnWidths[i] = table.header.get(i).length();

            for (ArrayList<Double> doubles : table.data) {
                String cell = doubles.get(i).toString();
                if (cell.length() > columnWidths[i]) {
                    columnWidths[i] = cell.length();
                }
            }
        }

        int name = 0;
        for (String s : col) {
            name = max(name, s.length());
        }

        printSpaces(name + 2);

        for(int j = 0; j < columnCount; ++j) {
            String cell = table.header.get(j);
            System.out.print(cell);
            printSpaces(columnWidths[j] - cell.length() + 2);
        }

        System.out.println();
        printLine(name + 2);

        for(int j = 0; j < columnCount; ++j) {
            printLine(columnWidths[j] + 2);
        }

        System.out.println();

        for(int j = 0; j < table.data.size(); ++j) {
            String cell1 = col.get(j);
            System.out.print(cell1);
            printSpaces(name - cell1.length() + 2);

            for(int i = 0; i < columnCount; ++i) {
                String cell = table.data.get(j).get(i).toString();
                System.out.print(cell);
                printSpaces(columnWidths[i] - cell.length() + 2);
            }

            System.out.println();
        }

    }

    public static void printSpaces(int count) {
        for(int i = 0; i < count; ++i) {
            System.out.print(" ");
        }

    }

    public static void printLine(int count) {
        for(int i = 0; i < count; ++i) {
            System.out.print("-");
        }

    }

    /*public static void printTableToFile(Table table, String filePath) {
        filePath = "src\\main\\resources\\" + filePath;

        try {
            FileWriter writer = new FileWriter(filePath);

            try {
                int columnCount = table.header.size();
                int[] columnWidths = new int[columnCount];

                for(int i = 0; i < columnCount; ++i) {
                    columnWidths[i] = table.header.get(i).length();

                    for (ArrayList<Double> doubles : table.data) {
                        String cell = doubles.get(i).toString();
                        if (cell.length() > columnWidths[i]) {
                            columnWidths[i] = cell.length();
                        }
                    }
                }

                for(int i = 0; i < columnCount; ++i) {
                    String cell = table.header.get(i);
                    writer.write(cell);
                    writeSpaces(writer, columnWidths[i] - cell.length() + 2);
                }

                writer.write(System.lineSeparator());

                int i = 0;
                label57:
                while(true) {
                    if (i >= columnCount) {
                        writer.write(System.lineSeparator());
                        Iterator var14 = table.data.iterator();

                        while(true) {
                            if (!var14.hasNext()) {
                                break label57;
                            }

                            ArrayList<Double> row = (ArrayList)var14.next();

                            for(int i = 0; i < columnCount; ++i) {
                                cell = ((Double)row.get(i)).toString();
                                writer.write(cell);
                                writeSpaces(writer, columnWidths[i] - cell.length() + 2);
                            }

                            writer.write(System.lineSeparator());
                        }
                    }

                    writeLine(writer, columnWidths[i] + 2);
                    ++i;
                }
            } catch (Throwable var10) {
                try {
                    writer.close();
                } catch (Throwable var9) {
                    var10.addSuppressed(var9);
                }

                throw var10;
            }

            writer.close();
        } catch (IOException var11) {
            var11.printStackTrace();
        }

    }*/

    public static void writeSpaces(FileWriter writer, int count) throws IOException {
        for(int i = 0; i < count; ++i) {
            writer.write(" ");
        }

    }

    public static void writeLine(FileWriter writer, int count) throws IOException {
        for(int i = 0; i < count; ++i) {
            writer.write("-");
        }

    }

    public static void inputCSV(Table table, String csvFile) {
        String csvSplitBy = ",";
        csvFile = "src\\main\\resources\\" + csvFile;

        try {
            BufferedReader br = new BufferedReader(new FileReader(csvFile));

            try {
                String line = br.readLine();
                table.header.addAll(List.of(line.split(csvSplitBy)));

                while((line = br.readLine()) != null) {
                    String[] values = line.split(csvSplitBy);
                    ArrayList<Double> row = new ArrayList();
                    String[] var9 = values;
                    int var10 = values.length;

                    for(int var11 = 0; var11 < var10; ++var11) {
                        String value = var9[var11];
                        double number;
                        try {
                            number = Double.parseDouble(value);
                        } catch (Exception e) {
                            if (value.equals("Yes")) {
                                number = 1.0;
                            } else if (value.equals("No")) {
                                number = 0.0;
                            } else {
                                number = -1.0;
                            }
                        }

                        row.add(number);
                    }

                    table.data.add(row);
                }
            } catch (Throwable var14) {
                try {
                    br.close();
                } catch (Throwable var13) {
                    var14.addSuppressed(var13);
                }

                throw var14;
            }

            br.close();
        } catch (IOException var15) {
            System.out.println("Input Error!!");
        }

    }
}
