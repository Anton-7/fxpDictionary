package file;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class NameTranslate {

    private final String DICTINARY_FILE_NAME = "fruits.txt";
    private final String DICTONARY_DELIMITER = "-";

    void translateName(XSSFSheet sheet) {
        Path dictionaryFile = Paths.get(DICTINARY_FILE_NAME);
        try {
            Files.write(dictionaryFile, getAbsentLines(sheet, Files.readAllLines(dictionaryFile)), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getAbsentLines(XSSFSheet sheet, List<String> dicLines) {
        List<String> ret = new ArrayList<>();
        for (int x = 1; x <= sheet.getLastRowNum(); x++) {
            XSSFCell cell = sheet.getRow(x).getCell(1);
            XSSFCell engCell = sheet.getRow(x).createCell(4, CellType.STRING);
            String cellValue = cell.getStringCellValue();
            Optional<String> translatedLine = dicLines.stream().filter(l -> l.contains(cellValue)).findAny();
            if (!translatedLine.isPresent()) {
                ret.add(cellValue + " " + DICTONARY_DELIMITER + " " );
                System.out.println("NameTranslate: new line without translation: " + cellValue);
            } else {
                String[] split = translatedLine.get().split(DICTONARY_DELIMITER);
                engCell.setCellValue(split[split.length - 1].trim());
            }
        }
        return ret;
    }

    public void fillDictionary(Path path) {
        File file = path.toFile();
        try (XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file))/*;
             FileOutputStream fileOut = new FileOutputStream(file)*/) {
            fillDictionary(wb.getSheetAt(0));
//            wb.write(fileOut);
        } catch (Exception e) {
            System.out.println("Path: " + path);
            e.printStackTrace();
        }
    }

    private void fillDictionary(XSSFSheet sheet) {
        Set<String> lines = new HashSet<>();
        try {
            lines.addAll(Files.readAllLines(new File(DICTINARY_FILE_NAME).toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int x = 1; x <= sheet.getLastRowNum(); x++) {
            String estCell = null;
            String engCell = null;
            XSSFRow row = sheet.getRow(x);
            try {
                estCell = row.getCell(1).getStringCellValue();
                engCell = row.getCell(4).getStringCellValue();
            } catch (IllegalStateException ise) {
                engCell = row.getCell(2).getStringCellValue();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
            lines.add(estCell + DICTONARY_DELIMITER + engCell);
        }
        try {
            if (!lines.isEmpty()) {
//                Files.write(Paths.get(DICTINARY_FILE_NAME), lines, StandardOpenOption.APPEND);
                Files.write(Paths.get(DICTINARY_FILE_NAME), lines);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
