import dictionary.FruitDictionary;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        FruitDictionary fd = new FruitDictionary();
        if (args.length != 1) {
            if (args.length == 2 && args[1].equals("fill")) {
                if (args[0].equals("-")) {
                    File[] files = new File(".").listFiles((dir, name) -> name.contains("FruitXpress-"));
                    try {
                        if (files != null && files.length > 0) {
                            Arrays.stream(files).forEach(f -> fd.fillDictionary(f.toPath()));
                        } else {
                            throw new NullPointerException();
                        }
                    } catch (NullPointerException e) {
                        System.err.println("No processed price-list files found.");
                    }
                } else {
                    fd.fillDictionary(Paths.get(args[0]));
                }
                System.exit(0);
            } else if (args.length == 0) {

                File[] priceFiles = new File(".").listFiles((dir, name) -> name.contains("Hinnakiri "));
                if (priceFiles != null && priceFiles.length != 0) {
                    Path priceFile = priceFiles[0].toPath();
                    try {
                        fd.start(Files.move(priceFile, priceFile.resolveSibling(generateFileName())));
                        System.exit(0);
                    } catch (IOException e) {
                        System.err.println("Couldn't find any default price-list files.");
                    }
                }
            }
            System.err.println("Wrong arguments! Use price-list file name. Or <filled-price-list name> <fill> to fill the dictionary.");
            System.exit(-1);
        }
        if (args[0].equals("help")) {
            System.out.println("===FruitXpress price-list translator===");
            System.out.println("Arguments to use:" +
                    "\n1) help - shows brief info about the program, including arguments you can use;" +
                    "\n2) <file name> - translates Estonian names in the file to English using the dictionary (fruits.txt). Will leave untranslated those fields, which are absent" +
                    "from the dictionary. In this case translate manually and then update the dictionary;" +
                    "\n3) <file name> fill - updates dictionary (fruits.txt) with already translated file's values;" +
                    "\n4) - fill - updates dictionary (fruits.txt) with all already translated files, which are in the same directory and have names starting with 'FruitXpress-';" +
                    "\n\nFor questions contact the developer - Anton Matskevich");
        } else {
            fd.start(Paths.get(args[0]));
        }
    }

    private static String generateFileName() {
        String dateTag = "<date>";
        String ret = "FruitXpress-" + dateTag + ".xlsx";
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;
        String date = (month < 10 ? "0" + month : month) + "." + (day < 10 ? "0" + day : day);
        return ret.replace(dateTag, date);
    }

}
