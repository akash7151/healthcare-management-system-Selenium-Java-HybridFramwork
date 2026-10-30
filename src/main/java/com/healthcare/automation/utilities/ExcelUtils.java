package com.healthcare.automation.utilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExcelUtils {

    private static final DataFormatter FORMATTER = new DataFormatter();

    private ExcelUtils() {
    }

    public static List<Map<String, String>> readSheet(Path filePath, String sheetName) {
        if (!Files.exists(filePath)) {
            throw new IllegalStateException("Excel file not found: " + filePath);
        }
        try (InputStream stream = Files.newInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(stream)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalStateException("Sheet not found: " + sheetName);
            }
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return List.of();
            }
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(FORMATTER.formatCellValue(cell).trim());
            }
            List<Map<String, String>> rows = new ArrayList<>();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                Map<String, String> data = new LinkedHashMap<>();
                boolean empty = true;
                for (int column = 0; column < headers.size(); column++) {
                    Cell cell = row.getCell(column);
                    String value = cell == null ? "" : FORMATTER.formatCellValue(cell).trim();
                    if (!value.isBlank()) {
                        empty = false;
                    }
                    data.put(headers.get(column), value);
                }
                if (!empty) {
                    rows.add(data);
                }
            }
            return rows;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read Excel file: " + filePath, exception);
        }
    }

    public static List<Map<String, String>> readSheetByTestCase(Path filePath, String sheetName, String testCaseId) {
        return readSheet(filePath, sheetName).stream()
                .filter(row -> testCaseId.equals(row.get("TestCaseId")))
                .toList();
    }

    public static Map<String, List<Map<String, String>>> groupByTestCase(Path filePath, String sheetName) {
        Map<String, List<Map<String, String>>> grouped = new HashMap<>();
        for (Map<String, String> row : readSheet(filePath, sheetName)) {
            grouped.computeIfAbsent(row.get("TestCaseId"), key -> new ArrayList<>()).add(row);
        }
        return grouped;
    }
}
