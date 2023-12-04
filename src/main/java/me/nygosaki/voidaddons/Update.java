package me.nygosaki.voidaddons;

import me.nygosaki.voidaddons.DiscordLogHandler;

import java.util.concurrent.TimeUnit;
import java.util.Base64;
import java.net.*;
import java.io.*;


public class Update implements Runnable {

	private static final int VOIDADDONS_VERSION = 104;

	@Override
	public void run() {
		while (true) {
		try {
			autoUpdate();
			TimeUnit.MINUTES.sleep(15);
		} catch (Exception e) {
			// lole
		}
		}
	}

	private void autoUpdate() {
	        try {
		    DiscordLogHandler.sendWebhook("[Update.java] Checking for updates...");
	            URL verURL = new URL("A website?file=voidaddons_v.txt"); # website to get version and check update
	            HttpURLConnection verConnection = (HttpURLConnection) verURL.openConnection();
	
	            // manually set auth because user:pass@domain.com doesnt work ig
	            String userCredentials = "botkisserdivision:4dfe6223c3df08ef8e3b5ae1f1c0825c067e2ca5c363996b50"; # credentials for website (if neccesary)
	            String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes());
	            verConnection.setRequestProperty("Authorization", basicAuth);
	
	            verConnection.setRequestMethod("GET");
	
	            int respCode = verConnection.getResponseCode();
	            if (respCode == HttpURLConnection.HTTP_OK) {
	                BufferedReader in = new BufferedReader(new InputStreamReader(verConnection.getInputStream()));
	                int remoteVersion = Integer.parseInt(in.readLine());
	                in.close();
	                if (remoteVersion > VOIDADDONS_VERSION) {
			    DiscordLogHandler.sendWebhook("[Update.java] <@424254493287251968> updating to: " + remoteVersion);
	                    URL jarURL = new URL("Another website?file=voidaddons.jar"); # if version is different, site to get new file from
	                    HttpURLConnection jarConnection = (HttpURLConnection) jarURL.openConnection();
	
	                    jarConnection.setRequestProperty("Authorization", basicAuth);
	
	                    jarConnection.setRequestMethod("GET");
	
	                    int jarResponseCode = jarConnection.getResponseCode();
	                    if (jarResponseCode == HttpURLConnection.HTTP_OK) {
	                        InputStream jarInputStream = jarConnection.getInputStream();
	                        File pluginsFolder = new File("plugins");
	
	                        File oldJarFile = new File(pluginsFolder, "VoidAddons-1.1.jar");
	                        if (oldJarFile.exists()) {
	                            oldJarFile.delete();
	                        }
	
	                        File newJarFile = new File(pluginsFolder, "VoidAddons-1.1.jar");
	                        FileOutputStream jarOutputStream = new FileOutputStream(newJarFile);
	                        byte[] buffer = new byte[4096];
	                        int bytesRead;
	                        while ((bytesRead = jarInputStream.read(buffer)) != -1) {
	                            jarOutputStream.write(buffer, 0, bytesRead);
	                        }
	                        jarOutputStream.close();
	                        jarInputStream.close();
	                    }
                	} else {
			    DiscordLogHandler.sendWebhook("[Update.java] No update found.");
			}
	            }
	
	        } catch (Exception e) {
	            // lol
	        }
    }
}

