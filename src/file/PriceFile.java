package file;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PriceFile {

    private final String COLUMN_NAME_TO_REMOVE = "Hind KM-ta";

    private final List<String> newHeaders = new ArrayList<>();

    public PriceFile() {
        newHeaders.add("Group");
        newHeaders.add("Name");
        newHeaders.add("Unit");
        newHeaders.add("Price");
        newHeaders.add("English name");
    }

    public void processFile(Path path) {
        read(path.toFile());
    }

    public void read(File file) {
        if (file.exists() && file.isFile()) {
            readXsl(file);
        } else {
            String exception = "ERROR: the file doesn't exist or is not a file: " + file;
            System.out.println(exception);
        }
    }

    private void readXsl(File file) {
        try {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet sheet = wb.getSheetAt(0);

            if (!Objects.equals(sheet.getRow(0).getCell(3).getStringCellValue(), COLUMN_NAME_TO_REMOVE)) {
                throw new IllegalAccessException();
            }
            deleteColumn(sheet, 3);
            renameHeaders(sheet);
            new Translate().startTranslation(sheet);

            //Write changes.
            FileOutputStream fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            fileOut.close();
        } catch (IllegalAccessException iae) {
            System.err.println("The file " + file + " has already been processed! Please use a fresh copy of this file or process it manually.");
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    private void deleteColumn(XSSFSheet sheet, int columnToDelete) {
        int maxColumn = 0;
        for (int r = 0; r < sheet.getLastRowNum() + 1; r++) {
            XSSFRow row = sheet.getRow(r);

            // if no row exists here; then nothing to do; next!
            if (row == null)
                continue;

            // if the row doesn't have this many columns then we are good; next!
            int lastColumn = row.getLastCellNum();
            if (lastColumn > maxColumn)
                maxColumn = lastColumn;

            if (lastColumn < columnToDelete)
                continue;

            for (int x = columnToDelete + 1; x < lastColumn + 1; x++) {
                XSSFCell oldCell = row.getCell(x - 1);
                if (oldCell != null) {
                    row.removeCell(oldCell);
                }
                XSSFCell nextCell = row.getCell(x);
                if (nextCell != null) {
                    XSSFCell newCell = row.createCell(x - 1, nextCell.getCellType());
                    cloneCell(newCell, nextCell);
                }
            }
        }
        // Adjust the column widths
//        for (int c = 0; c < maxColumn; c++) {
//            sheet.setColumnWidth(c, sheet.getColumnWidth(c + 1));
//        }
    }

    private void cloneCell(XSSFCell cNew, XSSFCell cOld) {
        cNew.setCellComment(cOld.getCellComment());
        cNew.setCellStyle(cOld.getCellStyle());
        if (cOld.getCellType() == CellType.STRING) {
            cNew.setCellValue(cOld.getStringCellValue());
        } else if (cOld.getCellType() == CellType.NUMERIC) {
            cNew.setCellValue(cOld.getNumericCellValue());
        }
    }

    private void renameHeaders(XSSFSheet sheet) {
        XSSFRow headerRow = sheet.getRow(0);
        int nameWidth = -1;
        for (int x = 0; x < 5; x++) {
            XSSFCell cell = headerRow.getCell(x);
            if (x == 1) {
                nameWidth = sheet.getColumnWidth(x);
            }
            if (cell != null) {
                cell.setCellValue(newHeaders.get(x));
            } else {
                XSSFCell createdCell = headerRow.createCell(x, CellType.STRING);
                createdCell.setCellValue(newHeaders.get(4));
                sheet.setColumnWidth(x, nameWidth);
            }
        }
    }

}
