package listeners;

import org.testng.IExecutionListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AllureReportListener implements IExecutionListener {

    @Override
    public void onExecutionStart() {
        cleanAllureResults();
    }

    @Override
    public void onExecutionFinish() {
        generateAllureReport();
    }

    private void generateAllureReport() {
        try {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String time = new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
            String outputDir = String.format("test-output/allure-report/%s/TestRun_%s", date, time);

            String allurePath = System.getProperty("os.name").toLowerCase().contains("win")
                    ? ".allure\\allure-2.20.1\\bin\\allure.bat"
                    : ".allure/allure-2.20.1/bin/allure";

            File allureExec = new File(allurePath);
            if (!allureExec.exists()) {
                System.out.println("❌ Allure executable not found at: " + allureExec.getAbsolutePath());
                return;
            }

            new ProcessBuilder(allurePath, "generate", "target/allure-results", "--clean", "-o", outputDir)
                    .inheritIO()
                    .start()
                    .waitFor();

            System.out.println("✅ Allure report generated in: " + outputDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanAllureResults() {
        File resultsDir = new File("target/allure-results");
        if (resultsDir.exists()) {
            for (File f : resultsDir.listFiles()) f.delete();
        }
    }
}