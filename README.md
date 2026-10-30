# Healthcare Management — Hybrid Selenium Automation Framework

Enterprise-grade **Hybrid Automation Framework** (POM + Data-Driven + Keyword-Driven + TestNG) for the Healthcare Management web application at `http://localhost:5173/`.

## Application Analysis Summary

The target app is a React + Vite frontend with an Express/Prisma backend (`http://localhost:4000`).

| App Role | Framework Mapping | Seed Account | Your Account |
|---|---|---|---|
| **Receptionist** | `RECEPTIONIST` | `lata.kulkarni@example.test` | — |
| **Billing Staff** | `BILLING_STAFF` | `vikram.desai@example.test` | — |
| **Receptionist Admin** | `ADMIN` (Administrator) | `asha.menon@example.test` | `akashdake9@gmail.com` |

### Modules Automated (based on actual application)

| Module | Receptionist | Billing Staff | Admin |
|---|---|---|---|
| Login / Logout | ✅ | ✅ | ✅ |
| Dashboard | ✅ | ✅ | ✅ |
| Patients (search, register, view) | ✅ | Read only | ✅ |
| Appointments (list, book link) | ✅ | ❌ Blocked | ✅ |
| Billing (search, invoice, payment) | ❌ Blocked | ✅ | ✅ |
| Reports | ❌ | ✅ | ✅ |
| Users / Role management | ❌ | ❌ | ✅ |
| Audit log | ❌ | ❌ | ✅ |
| Unauthorized access checks | ✅ | ✅ | ✅ |

## Architecture

```
Hybrid Framework = Page Object Model (POM)
                 + Data-Driven (Apache POI / Excel)
                 + Keyword-Driven (KeywordEngine + testcases.xlsx)
                 + TestNG execution, listeners, parallel-ready design
                 + Reusable utilities (wait, screenshot, email, config)
```

### Project Structure

```
src/main/java/com/healthcare/automation/
├── base/           BaseTest, DriverFactory (ThreadLocal WebDriver)
├── pages/          LoginPage, DashboardPage, ReceptionistPage, BillingStaffPage, ReceptionistAdminPage
├── utilities/      ConfigReader, CredentialManager, ExcelUtils, WaitUtils, EmailNotifier, ...
├── listeners/      TestListener, SuiteListener
├── reports/        ExtentReportManager
├── keywords/       KeywordEngine
└── constants/      FrameworkConstants

src/test/java/com/healthcare/automation/tests/
├── LoginTests.java
├── ReceptionistTests.java
├── BillingStaffTests.java
├── ReceptionistAdminTests.java
└── RolePermissionTests.java

src/test/resources/
├── config.properties
├── testng.xml
├── log4j2.xml
├── testdata/TestData.xlsx      (auto-generated)
└── keywords/testcases.xlsx     (auto-generated)
```

## Prerequisites

| Tool | Version Used |
|---|---|
| Java JDK | 21+ |
| Maven | 3.9+ |
| Chrome / Firefox / Edge | Latest stable |
| Healthcare App | Frontend `:5173`, Backend `:4000`, MySQL |

### Start the Application

From `D:\Healthcare Management`:

```bash
npm install --prefix backend
npm install --prefix frontend
npm run dev:backend
npm run dev:frontend
```

Verify: `http://localhost:5173/login`

## Setup

1. **Clone / open this project** in your IDE.

2. **Configure credentials** (never commit real passwords):

```bash
copy credentials.properties.example credentials.properties
```

Edit `credentials.properties`:

```properties
AUTH_ADMIN_EMAIL=akashdake9@gmail.com
AUTH_ADMIN_PASSWORD=your-password
AUTH_RECEPTIONIST_EMAIL=lata.kulkarni@example.test
AUTH_RECEPTIONIST_PASSWORD=DemoPass123!
AUTH_BILLING_EMAIL=vikram.desai@example.test
AUTH_BILLING_PASSWORD=DemoPass123!
AUTH_DEFAULT_PASSWORD=DemoPass123!
```

Or use environment variables: `AUTH_ADMIN_EMAIL`, `AUTH_ADMIN_PASSWORD`, etc.

3. **Install dependencies and generate Excel test data**:

```bash
mvn clean compile
```

Excel files are auto-created under `src/test/resources/` on first build.

## Test Execution

```bash
# Full regression (38 tests)
mvn clean test

# Headless Chrome
mvn clean test -Dheadless=true

# Cross-browser
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge

# Suite-specific
mvn clean test -Dtest=LoginTests
mvn clean test -Dtest=ReceptionistTests
mvn clean test -Dtest=BillingStaffTests
mvn clean test -Dtest=ReceptionistAdminTests

# Maven profiles
mvn clean test -Plogin
mvn clean test -Preceptionist
mvn clean test -Pbilling
mvn clean test -Padmin
mvn clean test -Psmoke
```

### Latest Execution Result

```
Tests run: 38, Failures: 0, Errors: 0, Skipped: 0
Duration: ~141 seconds (headless Chrome)
```

## Reports & Logs

| Output | Location |
|---|---|
| Extent HTML Report | `reports/ExtentReport.html` |
| TestNG XML Reports | `target/surefire-reports/` |
| Automation Log | `logs/automation.log` |
| Failure Screenshots | `screenshots/` |

## Email Notifications

Set environment variables before running tests:

```bash
set SMTP_HOST=smtp.gmail.com
set SMTP_PORT=587
set SMTP_USERNAME=your@gmail.com
set SMTP_PASSWORD=your-app-password
set EMAIL_FROM=your@gmail.com
set EMAIL_TO=team@example.com
set EMAIL_CC=manager@example.com
```

**Gmail setup:** Enable 2FA → Google Account → Security → App passwords → generate SMTP password.

If SMTP is not configured, tests still pass; email is skipped with a log warning.

## Jenkins CI/CD

### Initial Jenkins Setup

1. Install Jenkins: https://www.jenkins.io/download/
2. Open `http://localhost:8080/` → initial admin password from console log
3. Install plugins: **Pipeline**, **JUnit**, **Email Extension**, **Credentials Binding**
4. Add credentials (Secret text):
   - `healthcare-admin-email`, `healthcare-admin-password`
   - `healthcare-receptionist-email`, `healthcare-receptionist-password`
   - `healthcare-billing-email`, `healthcare-billing-password`
5. Create **Pipeline** job → Pipeline script from SCM → point to this repo → `Jenkinsfile`

### Pipeline Parameters

| Parameter | Options |
|---|---|
| BROWSER | chrome, firefox, edge |
| ENVIRONMENT | local, qa, staging |
| TEST_SUITE | all, login, receptionist, billing, admin, smoke |
| HEADLESS | true / false |
| THREAD_COUNT | 1–5 |

### Jenkins Jobs to Create

1. **Healthcare-Full-Regression** — `TEST_SUITE=all`
2. **Healthcare-Smoke** — `TEST_SUITE=smoke`
3. **Healthcare-Receptionist** — `TEST_SUITE=receptionist`
4. **Healthcare-Billing** — `TEST_SUITE=billing`
5. **Healthcare-Admin** — `TEST_SUITE=admin`

Schedule example: `H 2 * * *` (nightly at 2 AM)

## Excel Test Data

| Sheet | Purpose |
|---|---|
| LoginData | Valid/invalid login scenarios |
| ReceptionistData | Patient search & registration |
| BillingData | Invoice search & creation |
| AdminData | User search & audit log |
| RolePermissions | Role-based menu & URL access |
| Keywords | Keyword-driven login smoke (`KW_LOGIN_001`) |

Sensitive passwords are **not** stored in Excel — use `${AUTH_ADMIN_EMAIL}` placeholders resolved at runtime.

## Troubleshooting

| Issue | Solution |
|---|---|
| `Connection refused :5173` | Start frontend: `npm run dev:frontend` |
| Login API fails | Start backend: `npm run dev:backend` |
| `Missing credential` | Create `credentials.properties` or set env vars |
| `NoSuchElementException` | Increase `explicitWait` in `config.properties` |
| Stale element | Framework waits are built-in; retry analyzer re-runs once |
| ChromeDriver mismatch | WebDriverManager auto-downloads matching driver |
| Email not sent | Expected if SMTP env vars are unset |

## Security Notes

- Passwords are **never** logged, hardcoded in test classes, or committed to Git
- `credentials.properties` is gitignored
- Jenkins uses Credentials Binding plugin for secrets
- Use Gmail App Passwords, not your primary password

## Technology Stack

Java 21 · Selenium 4.27 · TestNG 7.10 · Maven · WebDriverManager · Apache POI · Log4j2 · ExtentReports 5 · Jakarta Mail · Rest Assured · Jenkins Pipeline
