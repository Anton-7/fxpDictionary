package file;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.HashMap;
import java.util.Map;

class Translate {

    private final Map<String, String> groupMap = new HashMap<>();
    private final Map<String, String> unitMap = new HashMap<>();

    public Translate() {
        groupMap.put("Eksootika", "Exotic");
        groupMap.put("Juurvili", "Vegetables");
        groupMap.put("Puuviljad", "Fruits");
        groupMap.put("Marjad", "Berries");
        groupMap.put("Salatid / maitsetaimed", "Salads/herbs");
        groupMap.put("Töödeldud", "Cooked");
        unitMap.put("Kilogramm", "kg");
        unitMap.put("Tükk", "tk");
        unitMap.put("Kast", "box");
    }

    void startTranslation(XSSFSheet sheet) {
        translateGroupAndUnit(sheet);
        new NameTranslate().translateName(sheet);
    }

    private void translateGroupAndUnit(XSSFSheet sheet) {
        for (int x = 1; x <= sheet.getLastRowNum(); x++) {
            XSSFCell groupCell =  sheet.getRow(x).getCell(0);
            groupCell.setCellValue(groupMap.get(groupCell.getStringCellValue()));
            XSSFCell unitCell =  sheet.getRow(x).getCell(2);
            unitCell.setCellValue(unitMap.get(unitCell.getStringCellValue()));
        }
        sheet.setColumnWidth(0, sheet.getColumnWidth(0) + 1500);
    }

}
