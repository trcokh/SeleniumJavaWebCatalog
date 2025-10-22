package utils.readers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {
    private Workbook workbook;
    private Sheet sheet;
    private Cell cell;
    private String excelFilePath;
    private final Map<String, Integer> columns = new HashMap<>();

    public void setExcelFile(String excelPath, String sheetName) {
        try {
            File file = new File(excelPath);

            if (!file.exists()) {
                file.createNewFile();

                System.out.println("File does not exist, so it were auto created!");
            }

            FileInputStream fileIn = new FileInputStream(excelPath);

            workbook = WorkbookFactory.create(fileIn);

            sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }

            this.excelFilePath = excelPath;

            sheet.getRow(0).forEach(cell -> {
                columns.put(cell.getStringCellValue(), cell.getColumnIndex());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getCellData(int rowNum, int colNum) throws Exception {
        try {
            cell = sheet.getRow(rowNum).getCell(colNum);

            String cellData = null;

            switch (cell.getCellType()) {
                case STRING:
                    cellData = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellData = String.valueOf(cell.getDateCellValue());
                    } else {
                        cellData = String.valueOf((long) cell.getNumericCellValue());
                    }
                    break;
                case BOOLEAN:
                    cellData = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case BLANK:
                    cellData = "";
                    break;
            }

            return cellData;
        } catch (Exception e) {
            return "";
        }
    }

    public String getCellData(String columnName, int rowNum) throws Exception {
        return getCellData(rowNum, columns.get(columnName));
    }

    public void setCellData(String text, int rowNum, int colNum) throws Exception {
        try {
            Row row = sheet.getRow(rowNum);

            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            cell = row.getCell(colNum);

            if (cell == null) {
                cell = row.createCell(colNum);
            }

            cell.setCellValue(text);

            FileOutputStream fileOut = new FileOutputStream(excelFilePath);
            workbook.write(fileOut);

            fileOut.flush();
            fileOut.close();
        } catch (Exception e) {
            throw (e);
        }
    }

    public static Object[][] readExcel(String filePath, String sheetName) {
        List<Object[]> dataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            int colCount = sheet.getRow(0).getPhysicalNumberOfCells();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, colCount)) {
                    continue; // skip empty rows
                }

                Object[] rowData = new Object[colCount];
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    rowData[j] = getCellValue(cell);
                }

                dataList.add(rowData);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        }

        return dataList.toArray(new Object[0][]);
    }

    private static boolean isRowEmpty(Row row, int colCount) {
        for (int i = 0; i < colCount; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValue(cell).toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}
