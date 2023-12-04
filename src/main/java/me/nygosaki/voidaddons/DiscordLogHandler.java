package me.nygosaki.voidaddons;

// read log
import java.io.BufferedReader;
import java.io.FileReader;

// webhook
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

// lole
import java.util.concurrent.TimeUnit;

public class DiscordLogHandler implements Runnable {
	private long lastReadLine = 0;

	@Override
	public void run() {
	while (true) {
		try {
			String lines = readLines(lastReadLine);
			sendWebhook(lines);
			// java is a programming language in which sleep(); can throw an
			// exception, and you can't sleep(); instead of a try-catch, else
			// the compiler calls you a slur
			TimeUnit.SECONDS.sleep(2);
		} catch (Exception e) {
			// lole
		}
	}
	}

	private String readLines(long lastLine) throws IOException {
		StringBuilder newLines = new StringBuilder();
		String logFilePath = "logs/latest.log";

		try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
			long lineIndex = 0;
			String line;

			while ((line = reader.readLine()) != null) {
				if (lineIndex > lastLine) {
					newLines.append(line).append('\n');
				}
				lineIndex++;
			}
			lastReadLine = lineIndex - 1;
		}
		return newLines.toString();
	}

	public static void sendWebhook(String message) {
		if (message.trim().isBlank()) return;
		HttpClient client = HttpClient.newHttpClient();

		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("https://discord.com/api/webhooks/a webook")) # discord webhook link
			.POST(BodyPublishers.ofString("{\"content\": \"" + "```\\n" + message.trim().replace("\n", "\\n").replace("\"", "\\\"") + "\\n```" + "\"}"))
			.setHeader("Accept", "application/json")
			.setHeader("Content-Type", "application/json")
		.build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
