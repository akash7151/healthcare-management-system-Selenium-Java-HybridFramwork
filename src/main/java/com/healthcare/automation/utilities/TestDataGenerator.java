package com.healthcare.automation.utilities;

import com.healthcare.automation.constants.FrameworkConstants;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestDataGenerator {

    public static void main(String[] args) throws IOException {
        generateTestData();
        generateKeywordData();
        System.out.println("Test data Excel files generated.");
    }

    public static void generateAll() throws IOException {
        generateTestData();
        generateKeywordData();
    }

    private static void generateTestData() throws IOException {
        Path path = FrameworkConstants.TEST_DATA_PATH;
        Files.createDirectories(path.getParent());
        if (Files.exists(path)) {
            return;
        }
        try (Workbook workbook = new XSSFWorkbook()) {
            createLoginSheet(workbook);
            createReceptionistSheet(workbook);
            createBillingSheet(workbook);
            createAdminSheet(workbook);
            createRolePermissionSheet(workbook);
            try (OutputStream stream = Files.newOutputStream(path)) {
                workbook.write(stream);
            }
        }
    }

    private static void generateKeywordData() throws IOException {
        Path path = FrameworkConstants.KEYWORD_DATA_PATH;
        Files.createDirectories(path.getParent());
        if (Files.exists(path)) {
            return;
        }
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Keywords");
            row(sheet, 0, "TestCaseId", "Step", "Keyword", "LocatorType", "Locator", "Value");
            row(sheet, 1, "KW_LOGIN_001", "1", "OPEN", "", "", "${BASE_URL}login");
            row(sheet, 2, "KW_LOGIN_001", "2", "VERIFY_TEXT", "xpath", "//h1", "Sign in");
            row(sheet, 3, "KW_LOGIN_001", "3", "TYPE", "css", "input[type='email']", "${AUTH_ADMIN_EMAIL}");
            row(sheet, 4, "KW_LOGIN_001", "4", "TYPE", "css", "input[type='password']", "${AUTH_ADMIN_PASSWORD}");
            row(sheet, 5, "KW_LOGIN_001", "5", "CLICK", "xpath", "//button[normalize-space()='Sign in']", "");
            row(sheet, 6, "KW_LOGIN_001", "6", "VERIFY_URL", "", "", "localhost:5173");
            try (OutputStream stream = Files.newOutputStream(path)) {
                workbook.write(stream);
            }
        }
    }

    private static void createLoginSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("LoginData");
        row(sheet, 0, "TestId", "Scenario", "Email", "Password", "ExpectedResult");
        row(sheet, 1, "LOGIN_001", "Valid admin login", "${AUTH_ADMIN_EMAIL}", "${AUTH_ADMIN_PASSWORD}", "dashboard");
        row(sheet, 2, "LOGIN_002", "Invalid username", "invalid.user@test.com", "${AUTH_DEFAULT_PASSWORD}", "error");
        row(sheet, 3, "LOGIN_003", "Invalid password", "${AUTH_ADMIN_EMAIL}", "WrongPass999!", "error");
    }

    private static void createReceptionistSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("ReceptionistData");
        row(sheet, 0, "TestId", "Action", "SearchQuery", "FirstName", "LastName", "DOB", "Phone", "Expected");
        row(sheet, 1, "REC_001", "searchPatient", "P-10001", "", "", "", "", "found");
        row(sheet, 2, "REC_002", "registerPatient", "", "Auto", "Patient", "1990-05-15", "9876543210", "created");
    }

    private static void createBillingSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("BillingData");
        row(sheet, 0, "TestId", "Action", "SearchQuery", "Description", "UnitPrice", "PaymentAmount", "Expected");
        row(sheet, 1, "BILL_001", "searchInvoice", "INV", "", "", "", "listed");
        row(sheet, 2, "BILL_002", "createInvoice", "", "Automation consultation", "750.00", "", "created");
    }

    private static void createAdminSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("AdminData");
        row(sheet, 0, "TestId", "Action", "SearchQuery", "FirstName", "LastName", "Email", "Role", "Expected");
        row(sheet, 1, "ADM_001", "searchUser", "lata", "", "", "", "", "found");
        row(sheet, 2, "ADM_002", "viewAudit", "", "", "", "", "", "audit");
    }

    private static void createRolePermissionSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("RolePermissions");
        row(sheet, 0, "RoleKey", "Path", "MenuLabel", "ShouldAccess");
        row(sheet, 1, "AUTH_RECEPTIONIST", "/patients", "Patients", "true");
        row(sheet, 2, "AUTH_RECEPTIONIST", "/billing", "Billing", "false");
        row(sheet, 3, "AUTH_BILLING", "/billing", "Billing", "true");
        row(sheet, 4, "AUTH_BILLING", "/users", "Users", "false");
        row(sheet, 5, "AUTH_ADMIN", "/users", "Users", "true");
        row(sheet, 6, "AUTH_ADMIN", "/audit", "Audit log", "true");
        row(sheet, 7, "AUTH_BILLING", "/appointments/new", "", "false");
    }

    private static void row(Sheet sheet, int index, String... values) {
        Row row = sheet.createRow(index);
        for (int column = 0; column < values.length; column++) {
            row.createCell(column).setCellValue(values[column]);
        }
    }
}
