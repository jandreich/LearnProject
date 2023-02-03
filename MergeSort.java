import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;

public class MergeSort {

    private static boolean direction = true;
    private static boolean mistakeInSort = false;

    public static void main(String[] args)  {
        String type = null;
        String outFile = null;
        LinkedList <String> files = new LinkedList<>();

        try {
            if (args.length<3) throw new Exception("Задано недостаточно входных параметров");

            if (args[0].equals("-a") || args[0].equals("-d")) {
                if (args[0].equals("-d")) { direction = false; }
                if (args[1].equals("-s")) {
                    type = "s";
                }
                if (args[1].equals("-i")) {
                    type = "i";
                }
                outFile = args[2];
                for (int i=0; i< args.length-3; i++){
                    files.addLast(args[i+3]);
                }
            }

            if (args[0].equals("-s") || args[0].equals("-i")) {
                if (args[0].equals("-s")) {
                    type = "s";
                }
                if (args[0].equals("-i")) {
                    type = "i";
                }
                outFile = args[1];
                for (int i=0; i< args.length-2; i++){
                    files.addLast(args[i+2]);
                }
            }

            if (type == null) throw new Exception("Задан неправильный тип входных данных");
            if (outFile == null && !Files.exists(Paths.get(outFile))) throw new Exception("Не задан выходной файл");

            Iterator<String> iterInFile = files.iterator();
            while (iterInFile.hasNext())  {
                String s = iterInFile.next();
                if (!Files.exists(Paths.get(s))) {
                    iterInFile.remove();
                    System.out.println(s+ " не существует");
                }
            }
            if (files.size() == 0) throw new Exception("Входные файлы не существуют");
            if (files.size() == 1) {
                sort(files.pollFirst(), outFile, type);
            } else {
                int i = 0;
                while (files.size() != 1) {
                    try {
                        String tempOut = Files.createTempFile("tempOut"+i, "txt").toString();
                        sort(files.pollFirst(), files.pollFirst(), tempOut, type);
                        files.addLast(tempOut);
                        i++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                sort(files.pollFirst(), outFile, type);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void sort (String inF1, String outF, String type) {
        mistakeInSort = false;
        try (
                BufferedReader reader1 = new BufferedReader(new FileReader(inF1));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outF))
        ) {
            switch (type){
                case "s":
                    mString(reader1, writer);
                    break;
                case "i":
                    mInt(reader1, writer);
                    break;
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
        if (mistakeInSort) System.out.println("Сортировка файла "+ inF1 +
                " проведена с учетом наличия в нем ошибки сортировки.");
    }


    public static void sort (String inF1, String inF2, String outF, String type) {
        mistakeInSort = false;
        try (
                BufferedReader reader1 = new BufferedReader(new FileReader(inF1));
                BufferedReader reader2 = new BufferedReader(new FileReader(inF2));
                BufferedWriter writer = new BufferedWriter(new FileWriter(outF))
        ) {

            switch (type){
                case "s":
                    mString(reader1, reader2, writer);
                    break;
                case "i":
                    mInt(reader1, reader2, writer);
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mistakeInSort) System.out.println("Сортировка файлов "+ inF1 + " и " + inF2 +
                " проведена с учетом наличия ошибки сортировки в одном из них.");
    }

    private static String read (BufferedReader reader, String last) throws IOException {
        String readed;
        while (reader.ready()) {
            readed = reader.readLine();
            if (readed != null && !readed.contains(" ")) {
                if (last!=null) {
                    if (direction) {
                        if (last.compareTo(readed) <= 0) {
                            return readed;
                        }/* Так как условие непонятное, я предполагаю, что ошибка в сортировке в
                    нескольких символах, остальные символы в порядке, и я их ищу. Но можно было тут прервать выполнение,
                    вернув null
                    */
                        mistakeInSort = true;
                    } else {
                        if (last.compareTo(readed) >= 0) {
                            return readed;
                        }
                        mistakeInSort = true;
                    }
                }
                else return readed;
            }
        }
        return null;
    }

    private static String canReadInt (BufferedReader reader, int last) throws IOException {
        String readed;
        while (reader.ready()) {
            readed = read(reader, null);
            if (readed != null) {
                if (readed.matches("-?\\d+")) {
                    if (direction && Integer.parseInt(readed) >= last) {
                        return readed;
                    }
                    if (!direction && Integer.parseInt(readed) <= last) {
                        return readed;
                    }
                    mistakeInSort = true;
                }
            }
        }
        return null;
    }

    private static void mString (BufferedReader reader1, BufferedReader reader2,
                                 BufferedWriter writer) throws IOException {
        String current1 = null;
        String current2 = null;

        String last1 = null;
        String last2 = null;

        boolean nChange1 = true;
        boolean nChange2 = true;

        while (true) {
            if (nChange1) {
                current1 = read(reader1, last1);
                if (current1 == null) break;
                nChange1 = false;
                last1 = current1;
            }

            if (nChange2) {
                current2 = read(reader2, last2);
                if (current2 == null) break;
                nChange2 = false;
                last2 = current2;
            }

            if (direction) {
                if (current1.compareTo(current2) <= 0) {
                    writer.write(current1 + "\n");
                    nChange1 = true;
                    current1 = null;
                }
                else {
                    writer.write(current2 + "\n");
                    nChange2 = true;
                    current2 = null;
                }
            }
            else {
                if (current1.compareTo(current2) >= 0) {
                    writer.write(current1 + "\n");
                    nChange1 = true;
                    current1 = null;
                }
                else {
                    writer.write(current2 + "\n");
                    nChange2 = true;
                    current2 = null;
                }
            }
        }

        if (current1 != null) {
            writer.write(current1+"\n");
            stReadOne(reader1, writer, last1);
        }
        if (current2 != null) {
            writer.write(current2+"\n");
            stReadOne(reader2, writer, last2);
        }
    }

    private static void mString (BufferedReader reader, BufferedWriter writer) throws IOException {
        stReadOne(reader, writer, null);
    }

    private static void stReadOne (BufferedReader reader, BufferedWriter writer, String last) throws IOException {
        String current;
        String lastN = last;

        while (true) {
            current = read(reader, lastN);
            if (current == null) break;
            writer.write(current+"\n");
            lastN = current;
        }
    }



    private static void mInt (BufferedReader reader1, BufferedReader reader2,
                              BufferedWriter writer) throws IOException {

        boolean nChange1 = true;
        boolean nChange2 = true;

        String can1 = null;
        String can2 = null;

        int current1 = 0;
        int current2 = 0;

        int last1;
        int last2;

        if (direction) {
            last1 = Integer.MIN_VALUE;
            last2 = Integer.MIN_VALUE;
        } else {
            last1 = Integer.MAX_VALUE;
            last2 = Integer.MAX_VALUE;
        }

        while (true) {
            if (nChange1) {
                can1 = canReadInt(reader1, last1);
                if (can1 == null) break;
                current1 = Integer.parseInt(can1);
                nChange1 = false;
                last1 = current1;

            }

            if (nChange2) {
                can2 = canReadInt(reader2, last2);
                if (can2 == null) break;
                current2 = Integer.parseInt(can2);
                nChange2 = false;
                last2 = current2;

            }

            if (direction) {
                if (current1<=current2) {
                    writer.write(current1 + "\n");
                    nChange1 = true;
                    can1 = null;

                }
                else {
                    writer.write(current2 + "\n");
                    nChange2 = true;
                    can2 = null;
                }
            }
            else {
                if (current1 > current2) {
                    writer.write(current1 + "\n");
                    nChange1 = true;
                    can1 = null;

                }
                else {
                    writer.write(current2 + "\n");
                    nChange2 = true;
                    can2 = null;
                }
            }
        }

        if (can1 != null) {
            writer.write(current1+"\n");
            inReadOne(reader1, writer, last1);
        }
        if (can2 != null) {
            writer.write(current2+"\n");
            inReadOne(reader2, writer, last2);
        }
    }

    private static void mInt (BufferedReader reader, BufferedWriter writer) throws IOException {

        int last;
        if (direction) {
            last = Integer.MIN_VALUE;
        } else {
            last = Integer.MAX_VALUE;
        }
        inReadOne(reader, writer, last);

    }

    private static void inReadOne (BufferedReader reader, BufferedWriter writer, int last) throws IOException {
        String can;
        int current;
        int lastN = last;

        while (true) {
            can = canReadInt(reader, lastN);
            if (can == null) break;
            current = Integer.parseInt(can);
            writer.write(current+"\n");
            lastN = current;
        }
    }
}
