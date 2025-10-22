package utils;

import org.testng.annotations.DataProvider;
import utils.annotations.TestDataFile;
import utils.readers.ConfigReader;
import utils.readers.CsvReader;
import utils.readers.ExcelReader;

import java.lang.reflect.Method;

public class TestDataProvider {

    @DataProvider(name = "testData")
    public static Object[][] getData(Method testMethod) {
        TestDataFile annotation = testMethod.getAnnotation(TestDataFile.class);

        if (annotation == null) {
            throw new IllegalArgumentException(
                    "Missing @TestDataFile on test method: " + testMethod.getName()
            );
        }

        String filePath = resolveFilePath(annotation.file());
        String sheetName = annotation.sheet();

        if (filePath.endsWith(".xlsx") || filePath.endsWith(".xls")) {
            return ExcelReader.readExcel(filePath, sheetName);
        } else if (filePath.endsWith(".csv")) {
            return CsvReader.readCSV(filePath);
        } else {
            throw new IllegalArgumentException("Unsupported file: " + filePath);
        }
    }

    private static String resolveFilePath(String filePath) {
        if (filePath.startsWith("${") && filePath.endsWith("}")) {
            String key = filePath.substring(2, filePath.length() - 1);

            String sysValue = System.getProperty(key);
            if (sysValue != null && !sysValue.isEmpty()) return sysValue;

            String configValue = ConfigReader.getProperty(key);
            if (configValue != null && !configValue.isEmpty()) return configValue;

            throw new IllegalArgumentException("No value found for placeholder: " + key);
        }

        return filePath;
    }

}
