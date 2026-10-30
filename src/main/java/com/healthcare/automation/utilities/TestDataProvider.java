package com.healthcare.automation.utilities;

import com.healthcare.automation.constants.FrameworkConstants;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TestDataProvider {

    private TestDataProvider() {
    }

    @DataProvider(name = "loginData")
    public static Object[][] loginData() {
        List<Map<String, String>> rows = ExcelUtils.readSheet(FrameworkConstants.TEST_DATA_PATH, "LoginData");
        return toArray(rows);
    }

    @DataProvider(name = "receptionistData")
    public static Object[][] receptionistData() {
        return toArray(ExcelUtils.readSheet(FrameworkConstants.TEST_DATA_PATH, "ReceptionistData"));
    }

    @DataProvider(name = "billingData")
    public static Object[][] billingData() {
        return toArray(ExcelUtils.readSheet(FrameworkConstants.TEST_DATA_PATH, "BillingData"));
    }

    @DataProvider(name = "adminData")
    public static Object[][] adminData() {
        return toArray(ExcelUtils.readSheet(FrameworkConstants.TEST_DATA_PATH, "AdminData"));
    }

    @DataProvider(name = "rolePermissionData")
    public static Object[][] rolePermissionData() {
        return toArray(ExcelUtils.readSheet(FrameworkConstants.TEST_DATA_PATH, "RolePermissions"));
    }

    private static Object[][] toArray(List<Map<String, String>> rows) {
        Object[][] data = new Object[rows.size()][1];
        for (int index = 0; index < rows.size(); index++) {
            data[index][0] = rows.get(index);
        }
        return data;
    }
}
