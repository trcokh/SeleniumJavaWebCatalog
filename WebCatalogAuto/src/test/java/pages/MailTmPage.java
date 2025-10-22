package pages;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import base.DriverFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.LogUtils;
import utils.MailTmException;
import utils.readers.ConfigReader;

public class MailTmPage {

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String address;
    private final String password;
    private String token;

    public record MailAccount(String address, String password) {
    }

    public MailTmPage(String address, String password) {
        try {
            this.address = address;
            this.password = password;

            createAccount();
            login();
        } catch (Exception e) {
            throw new MailTmException("Invalid email or password", e);
        }
    }

    public MailTmPage() {
        try {
            MailAccount acc = generateRandomAccount();
            this.address = acc.address();
            this.password = acc.password();
            createAccount();
            login();
        } catch (Exception e) {
            throw new MailTmException("Failed to initialize MailTmPage", e);
        }
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public static MailAccount generateRandomAccount() {
        try {
            int retries = 5;
            for (int i = 0; i < retries; i++) {
                HttpRequest domainRequest = HttpRequest.newBuilder()
                        .uri(URI.create(ConfigReader.getProperty("mailTmUrl") + "domains"))
                        .timeout(Duration.ofSeconds(10))
                        .GET()
                        .build();

                HttpResponse<String> domainResponse = client.send(domainRequest, HttpResponse.BodyHandlers.ofString());
                if (domainResponse.statusCode() != 200) continue;

                JSONObject json = new JSONObject(domainResponse.body());
                JSONArray domains = json.getJSONArray("hydra:member");

                if (!domains.isEmpty()) {
                    String domain = domains.getJSONObject(0).getString("domain");
                    String prefix = "auto_" + UUID.randomUUID().toString().substring(0, 8);
                    String password = UUID.randomUUID().toString().substring(0, 10);
                    return new MailAccount(prefix + "@" + domain, password);
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new MailTmException("Interrupted while waiting for domain", e);
                }
            }
            throw new MailTmException("No domain available from Mail.tm after retries");
        } catch (Exception e) {
            throw new MailTmException("Failed to generate random Mail.tm account", e);
        }
    }

    private void createAccount() {
        try {
            String jsonBody = String.format("{\"address\":\"%s\",\"password\":\"%s\"}", address, password);
            int maxRetries = 5;
            long delay = 2000L;

            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ConfigReader.getProperty("mailTmUrl") + "accounts"))
                        .timeout(Duration.ofSeconds(10))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                int status = response.statusCode();

                if (status == 201 || status == 422) {
                    LogUtils.info("✅ Mail.tm account created (or already exists): " + address);
                    return;
                }

                LogUtils.warn("⚠️ Attempt " + attempt + " failed (" + status + "): " + response.body());

                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new MailTmException("Interrupted while creating account", e);
                }

                delay *= 2;
            }
            throw new MailTmException("Create account failed after retries for: " + address);
        } catch (Exception e) {
            throw new MailTmException("Error while creating Mail.tm account for " + address, e);
        }
    }

    private void login() {
        int retries = 3;
        long delay = 2000L;

        for (int i = 1; i <= retries; i++) {
            try {
                String jsonBody = String.format("{\"address\":\"%s\",\"password\":\"%s\"}", address, password);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ConfigReader.getProperty("mailTmUrl") + "token"))
                        .timeout(Duration.ofSeconds(10))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JSONObject json = new JSONObject(response.body());
                    this.token = json.getString("token");
                    return;
                } else {
                    LogUtils.warn("⚠️ Login attempt " + i + " failed (" + response.statusCode() + "): " + response.body());
                }
            } catch (Exception e) {
                LogUtils.warn("⚠️ Login attempt " + i + " exception: " + e.getMessage());
            }

            try {
                TimeUnit.MILLISECONDS.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MailTmException("Interrupted while creating account", e);
            }

            delay *= 2;
        }
        throw new MailTmException("Login failed after retries for " + address);
    }

    private String getInbox() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ConfigReader.getProperty("mailTmUrl") + "messages"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Get inbox failed: " + response.statusCode() + " → " + response.body());
            }
            return response.body();
        } catch (Exception e) {
            throw new MailTmException("Failed to fetch inbox after re-login", e);
        }
    }

    public String waitForLatestMailBody(int maxTries, int delaySeconds) {
        long startTime = System.currentTimeMillis();
        long timeout = maxTries * delaySeconds * 1000L;

        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                String inboxJson = getInbox();
                JSONObject inbox = new JSONObject(inboxJson);
                JSONArray items = inbox.getJSONArray("hydra:member");

                if (!items.isEmpty()) {
                    String id = items.getJSONObject(0).getString("id");
                    String body = getMailBodyById(id);
                    if (body != null && !body.isBlank()) {
                        return body;
                    }
                }

                TimeUnit.SECONDS.sleep(1); // poll every 1s

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for latest mail", e);
            } catch (Exception e) {
                LogUtils.warn("Failed to fetch inbox: " + e.getMessage());
            }
        }

        throw new RuntimeException("No email received within timeout (" + timeout / 1000 + "s)");
    }

    private String getMailBodyById(String id) {
        int retries = 5;
        long delay = 1000L;

        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ConfigReader.getProperty("mailTmUrl") + "messages/" + id))
                        .timeout(Duration.ofSeconds(10))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200)
                    throw new RuntimeException("Get mail body failed: " + response.statusCode());

                JSONObject mail = new JSONObject(response.body());
                String body = mail.optString("text", mail.optString("html", ""));

                if (body != null && !body.isBlank()) return body;

            } catch (Exception e) {
                LogUtils.warn("Attempt " + attempt + " failed to fetch mail body: " + e.getMessage());
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                delay *= 2;
                if (attempt == retries) {
                    throw new MailTmException("Failed to fetch mail body after retries", e);
                }
            }
        }

        return null;
    }

    public String extractOtpFromMail() {
        long startTime = System.currentTimeMillis();
        long timeout = 30 * 1000L;
        int pollInterval = 1000;

        while (System.currentTimeMillis() - startTime < timeout) {
            try {
                String body = waitForLatestMailBody(1, 1);
                if (body != null && !body.isBlank()) {
                    Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
                    Matcher matcher = pattern.matcher(body);
                    if (matcher.find()) return matcher.group();
                }
            } catch (Exception e) {
                LogUtils.warn("Polling for OTP: " + e.getMessage());
            }

            try {
                TimeUnit.MILLISECONDS.sleep(pollInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MailTmException("Interrupted while waiting for OTP", e);
            }
        }

        throw new MailTmException("Failed to extract OTP within timeout");
    }

    public String extractVerificationLink() {
        String body = waitForLatestMailBody(15, 3);

        if (body == null || body.isBlank()) return null;

        Pattern pattern = Pattern.compile("https?://[\\w\\-\\.\\?\\=\\&/%#]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(body);

        while (matcher.find()) {
            String link = matcher.group();
            if (link.toLowerCase().contains("verify") || link.toLowerCase().contains("activation")) {
                return link;
            }
        }

        matcher = pattern.matcher(body);

        return matcher.find() ? matcher.group() : null;
    }

    public void clickOnVerifyLink(String link) {
        try {
            if (link == null)
                throw new RuntimeException("❌ No verification link found in email!");

            DriverFactory.openVerifyLinkAndReturnToCurrentTab(link);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            throw new MailTmException("Failed to open verification link: " + link, e);
        }
    }

    public void clearInbox() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ConfigReader.getProperty("mailTmUrl") + "messages"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Get inbox for clear failed: " + response.statusCode() + " → " + response.body());
            }

            JSONObject inbox = new JSONObject(response.body());
            JSONArray items = inbox.getJSONArray("hydra:member");

            for (int i = 0; i < items.length(); i++) {
                String id = items.getJSONObject(i).getString("id");

                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(URI.create(ConfigReader.getProperty("mailTmUrl") + "messages/" + id))
                        .timeout(Duration.ofSeconds(10))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();

                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                if (deleteResponse.statusCode() != 204) {
                    LogUtils.warn("⚠️ Failed to delete message: " + id + " → " + deleteResponse.body());
                }
            }

            LogUtils.info("📭 Inbox cleared successfully (" + items.length() + " messages deleted)");
        } catch (Exception e) {
            throw new MailTmException("Clear inbox failed: ", e);
        }

    }
}