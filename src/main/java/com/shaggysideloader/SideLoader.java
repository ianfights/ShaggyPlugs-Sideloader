package com.shaggysideloader;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Shaggadelic.420 - May 2023.
 */

public class SideLoader extends JFrame {
    private final JTextField addressField;
    private final JTextField portField;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    private final JTextField clientsField;
    private final JButton loginButton;

    private final JButton installJagexPatchButton;

    private final JButton uninstallJagexPatchButton;


    private final JButton noproxyButton;

    public SideLoader() {
        setTitle("Shaggadelic - SideLoader");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set the darker UI design
        UIManager.put("Panel.background", new Color(35, 39, 42));
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("TextField.background", new Color(54, 57, 62));
        UIManager.put("TextField.foreground", Color.WHITE);
        UIManager.put("PasswordField.background", new Color(54, 57, 62));
        UIManager.put("PasswordField.foreground", Color.WHITE);
        UIManager.put("ClientsButton.background", new Color(54, 57, 62));
        UIManager.put("ClientsButton.foreground", Color.WHITE);
        UIManager.put("Button.background", new Color(47, 102, 153));
        UIManager.put("Button.foreground", Color.WHITE);


        // Create the address input field
        addressField = new JTextField();
        addressField.setPreferredSize(new Dimension(200, 30));

        // Create the port input field
        portField = new JTextField();
        portField.setPreferredSize(new Dimension(200, 30));

        // Create the username input field
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 30));

        // Create the password input field
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));

        clientsField = new JTextField();
        clientsField.setPreferredSize(new Dimension(200, 30));

        // Create the login button
        loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String address = addressField.getText();
            String port = portField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String clientsPerProxy = clientsField.getText();
            saveProxyToFile(address, port, username, password, clientsPerProxy);
//                try {
//                    dispose();
//                    launchRuneLite();
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }
        });

        noproxyButton = new JButton("Login without Proxy");
        noproxyButton.addActionListener(e -> {
         deleteProxyFile();
//         try {
//                    dispose();
//                    launchRuneLite();
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }

            });

        installJagexPatchButton = new JButton("Install Jagex Patch");
        installJagexPatchButton.addActionListener(e -> {
            try {
                downloadJagexPatch();
                dispose();
                String message = "<html><font color='white'>Patch installed, Please do not press this again.</font> <font color='white'>Use Jagex Launcher to launch RuneLite</font></html>";

                JOptionPane.showMessageDialog(SideLoader.this, message, "Success", JOptionPane.OK_OPTION);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Install Jagex Patch button clicked!");
        });
        uninstallJagexPatchButton = new JButton("Uninstall Jagex Patch");
        uninstallJagexPatchButton.addActionListener(e -> {
            try {
                downloadDefaultRuneLite();
                dispose();
                String message = "<html><font color='white'>Patch uninstalled, Please do not press this again.</font> <font color='white'>Use Jagex Launcher to launch RuneLite</font></html>";

                JOptionPane.showMessageDialog(SideLoader.this, message, "Success", JOptionPane.OK_OPTION);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Install Jagex Patch button clicked!");
        });

        // Create the panel and add components
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);



        gbc.gridy++;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Port:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Clients Per Proxy:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;


        gbc.gridy++;
        panel.add(addressField, gbc);
        gbc.gridy++;
        panel.add(portField, gbc);
        gbc.gridy++;
        panel.add(usernameField, gbc);
        gbc.gridy++;
        panel.add(passwordField, gbc);
        gbc.gridy++;
        panel.add(clientsField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(loginButton, gbc);

        add(panel, BorderLayout.CENTER);
        gbc.gridx = 2;
        panel.add(noproxyButton, gbc);
        gbc.gridy = 2;
        panel.add(installJagexPatchButton, gbc);
        gbc.gridy++;
        panel.add(uninstallJagexPatchButton, gbc);
        // Load the saved auth key and proxy configuration
        try {
            JSONObject proxy = readProxyFromFile(System.getProperty("user.home") + File.separator + "jdk" + File.separator + "proxy.json");
            addressField.setText(proxy.getString("host"));
            portField.setText(proxy.getString("port"));
            usernameField.setText(proxy.getString("username"));
            passwordField.setText(proxy.getString("password"));
            clientsField.setText(proxy.getJSONObject("config").getString("clients-per-proxy"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void dispose() {
        super.dispose();
    }
    private static JSONObject readProxyFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        System.out.println(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));


        StringBuffer buffer = new StringBuffer();
        while (true) {
            String line;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (line == null) {
                break;
            } else {
                buffer.append(line);
                buffer.append("\n");
            }
        }

        JSONObject json = new JSONObject(buffer.toString());
        JSONArray proxies = json.getJSONArray("proxies");
        JSONObject proxy = proxies.getJSONObject(0);
        proxy.put("config",json.getJSONObject("config"));

//        System.out.println(reader.lines().toString());
        reader.close();
        return proxy;
//        return void;
    }
    private void deleteProxyFile() {
        // We need to check for the old proxy in order to prevent some weird issues
        String oldProxyFilePath = System.getProperty("user.home") + File.separator + File.separator + "jdk" + File.separator + "proxy.txt";
        String proxyFilePath = System.getProperty("user.home") + File.separator + File.separator + "jdk" + File.separator + "proxy.json";

        File oldProxyFile = new File(oldProxyFilePath);
        File proxyFile = new File(proxyFilePath);

        if (oldProxyFile.exists()) {
            boolean deleted = oldProxyFile.delete();
            if (!deleted) {
                System.out.println("Failed to delete the depricated proxy file.");
            }
        }

        if (proxyFile.exists()) {
            boolean deleted = proxyFile.delete();
            if (!deleted) {
                System.out.println("Failed to delete the depricated proxy file.");
            }
        }


    }

    private void launchRuneLite() throws IOException {
        String injectorURL = "http://shaggyplugs.xyz/RuneLiteHijack.jar";
        String runeliteDownloadUrl = "http://shaggyplugs.xyz/RuneLite.jar";

        String downloadDir = System.getProperty("user.home") + File.separator + "/jdk/";
        String pluginDownloadPath = downloadDir + "RuneLiteHijack.jar";
        String runeliteDownloadPath = downloadDir + "RuneLite.jar";

        // Create the download directory if it doesn't exist
        File directory = new File(downloadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Download the runelite JAR file
        URL runeliteURL = new URL(runeliteDownloadUrl);
        HttpURLConnection runeliteConnection = (HttpURLConnection) runeliteURL.openConnection();
        int runeliteFileSize = runeliteConnection.getContentLength();
        int runeliteDownloadSize = 0;
        try (InputStream pluginIn = runeliteConnection.getInputStream();
             FileOutputStream pluginOut = new FileOutputStream(runeliteDownloadPath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = pluginIn.read(buffer)) != -1) {
                pluginOut.write(buffer, 0, bytesRead);
                runeliteDownloadSize += bytesRead;
                int percentage = (runeliteDownloadSize * 100) / runeliteFileSize;
                System.out.println("Downloading RuneLite... " + percentage + "%");
            }
        }


        // Download the plugin JAR file
        URL injectorurl = new URL(injectorURL);
        HttpURLConnection injectorConnection = (HttpURLConnection) injectorurl.openConnection();
        int injectorFileSize = injectorConnection.getContentLength();
        int injectorDownloadSize = 0;
        try (InputStream injectorIN = injectorConnection.getInputStream();
             FileOutputStream injectorOut = new FileOutputStream(pluginDownloadPath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = injectorIN.read(buffer)) != -1) {
                injectorOut.write(buffer, 0, bytesRead);
                injectorDownloadSize += bytesRead;
                int percentage = (injectorDownloadSize * 100) / injectorFileSize;
                System.out.println("Downloading injector... " + percentage + "%");
            }
        }
        downloadAndExtractOpenJDK();
    }

    private static final String WINDOWS_JDK_DOWNLOAD_URL = "http://shaggyplugs.xyz/openjdk/OpenJDK11U-jdk_x64_windows_hotspot_11.0.19_7.zip";
    private static final String MAC_JDK_DOWNLOAD_URL = "http://shaggyplugs.xyz/openjdk/OpenJDK11U-jdk_x64_mac_hotspot_11.0.19_7.tar.gz";
    private static final String LINUX_JDK_DOWNLOAD_URL = "http://shaggyplugs.xyz/openjdk/OpenJDK11U-jdk_x64_linux_hotspot_11.0.19_7.tar.gz";
    private static final String JDK_ARCHIVE_FILE = "openjdk-11.tar.gz";

    private static void executeCommandWindows() {
        String tmpDir = System.getProperty("user.home");
        String launcherDir = tmpDir + File.separator + "jdk";
        File jdkDirectory = new File(launcherDir + "\\jdk-11.0.19+7\\jdk-11.0.19+7");
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    jdkDirectory + "\\bin\\java",
                    "-Ddisable.telemetry=true",
                    "-cp",
                    "\"" + launcherDir + "\\RuneLiteHijack.jar;" + launcherDir + "\\RuneLite.jar\"",
                    "ca.arnah.runelite.LauncherHijack"
            );
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT); // Redirect process output to the current process output
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT); // Redirect process errors to the current process errors

            Process process = processBuilder.start();
            process.waitFor(); // Wait for the process to finish
            int exitCode = process.exitValue(); // Get the exit code
            System.out.println("Process exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static void executeCommandMac() {
        try {
            String launcherDir = "/Library/Java/JavaVirtualMachines/";
            String tmpDir = System.getProperty("user.home");
            String clientDir = tmpDir + File.separator + "jdk";
            File jdkDirectory = new File(launcherDir + "/jdk-11.0.19+7/jdk-11.0.19+7/Contents/Home");

            try {
                ProcessBuilder processBuilder = new ProcessBuilder(
                        jdkDirectory + "/bin/java",
                        "-Ddisable.telemetry=true",
                        "-cp",
                        clientDir + "/RuneLiteHijack.jar:" + clientDir + "/RuneLite.jar",
                        "ca.arnah.runelite.LauncherHijack"
                );

                processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT); // Redirect process output to the current process output
                processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT); // Redirect process errors to the current process errors

                Process process = processBuilder.start();
                int exitCode = process.waitFor(); // Wait for the process to finish
                System.out.println("Process exited with code: " + exitCode);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeCommandUnix() {
        try {
            String tmpDir = System.getProperty("user.home");
            String launcherDir = tmpDir + File.separator + "jdk";
            File jdkDirectory = new File(launcherDir + "\\jdk-11.0.19+7\\jdk-11.0.19+7");

            try {
                ProcessBuilder processBuilder = new ProcessBuilder(
                        jdkDirectory + "\\bin\\java",
                        "-Ddisable.telemetry=true",
                        "-cp",
                        "\"" + launcherDir + "\\RuneLiteHijack.jar;" + launcherDir + "\\RuneLite.jar\"",
                        "ca.arnah.runelite.LauncherHijack"
                );
                processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT); // Redirect process output to the current process output
                processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT); // Redirect process errors to the current process errors

                Process process = processBuilder.start();
                int exitCode = process.waitFor(); // Wait for the process to finish
                System.out.println("Process exited with code: " + exitCode);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static void executeCommands() throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        String tmpDir = System.getProperty("user.home");
        File jdkDirectory;

        if (osName.contains("win")) {
            jdkDirectory = new File(tmpDir + File.separator + "jdk" + File.separator + "jdk-11.0.19+7" + File.separator + "jdk-11.0.19+7");
        } else if (osName.contains("mac")) {
            jdkDirectory = new File("/Library/Java/JavaVirtualMachines/jdk-11.0.19+7");
        } else if (osName.contains("nix") || osName.contains("nux")) {
            jdkDirectory = new File(tmpDir + File.separator + "jdk" + File.separator + "jdk-11.0.19+7");
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }
        if (jdkDirectory.exists()) {
        if (osName.contains("win")) {
            executeCommandWindows();
        } else if (osName.contains("mac")) {
            executeCommandMac();
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("bsd")) {
            executeCommandUnix();
        } else {
            downloadAndExtractOpenJDK();
        }
      }
    }
    private static void downloadAndExtractOpenJDK() throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        String tmpDir = System.getProperty("user.home");
        File jdkDirectory;

        if (osName.contains("win")) {
           // jdkDirectory = new File(tmpDir + File.separator + "jdk" + File.separator + "jdk-11.0.19+7" + File.separator + "jdk-11.0.19+7");
            File jdkDirectory1 = new File(tmpDir + File.separator + "jdk" + File.separator + "jdk-11.0.19+7" + File.separator + "jdk-11.0.19+7");
            File jdkDirectory2 = new File(tmpDir + File.separator + "jdk" + File.separator + "jdk-11.0.19+7");

            if (jdkDirectory1.exists() && isJdkDirectoryValid(jdkDirectory1)) {
                jdkDirectory = jdkDirectory1;
            } else if (jdkDirectory2.exists() && isJdkDirectoryValid(jdkDirectory2)) {
                jdkDirectory = jdkDirectory2;
            } else {
                throw new UnsupportedOperationException("JDK directory not found.");
            }
        } else if (osName.contains("mac")) {
            File jdkDirectory1 = new File("/Library/Java/JavaVirtualMachines/jdk-11.0.19+7/jdk-11.0.19+7");
            File jdkDirectory2 = new File("/Library/Java/JavaVirtualMachines/jdk-12.0.1+7");

            if (jdkDirectory1.exists() && isJdkDirectoryValid(jdkDirectory1)) {
                jdkDirectory = jdkDirectory1;
            } else if (jdkDirectory2.exists() && isJdkDirectoryValid(jdkDirectory2)) {
                jdkDirectory = jdkDirectory2;
            } else {
                throw new UnsupportedOperationException("JDK directory not found.");
            }
        } else if (osName.contains("nix") || osName.contains("nux")) {
            jdkDirectory = new File(tmpDir + File.separator + "jdk" + File.separator + "jdk-11.0.19+7");
            File jdkDirectory1 = new File(tmpDir + File.separator + "jdk" + File.separator + "jdk-11.0.19+7");
            File jdkDirectory2 = new File(tmpDir + File.separator + "jdk" + File.separator + "jdk-11.0.19+7" + File.separator + "jdk-11.0.19+7");

            if (jdkDirectory1.exists() && isJdkDirectoryValid(jdkDirectory1)) {
                jdkDirectory = jdkDirectory1;
            } else if (jdkDirectory2.exists() && isJdkDirectoryValid(jdkDirectory2)) {
                jdkDirectory = jdkDirectory2;
            } else {
                throw new UnsupportedOperationException("JDK directory not found.");
            }
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + osName);
        }
        if (!jdkDirectory.exists() || !isJdkDirectoryValid(jdkDirectory)) {
            jdkDirectory.mkdirs();
            String jdkArchiveFilePath = jdkDirectory.getAbsolutePath() + File.separator + JDK_ARCHIVE_FILE;
            URL jdkDownloadUrl;

            if (osName.contains("win")) {
                jdkDownloadUrl = new URL(WINDOWS_JDK_DOWNLOAD_URL);
            } else if (osName.contains("mac")) {
                jdkDownloadUrl = new URL(MAC_JDK_DOWNLOAD_URL);
            } else if (osName.contains("nix") || osName.contains("nux")) {
                jdkDownloadUrl = new URL(LINUX_JDK_DOWNLOAD_URL);
            } else {
                throw new UnsupportedOperationException("Unsupported operating system: " + osName);
            }

            File jdkArchiveFile = new File(jdkArchiveFilePath);
            try (InputStream in = jdkDownloadUrl.openStream()) {
                Files.copy(in, jdkArchiveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // Extract the JDK archive
            if (osName.contains("win")) {
                try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(jdkArchiveFile))) {
                    ZipEntry entry = zipIn.getNextEntry();
                    while (entry != null) {
                        String filePath = jdkDirectory.getAbsolutePath() + File.separator + entry.getName();
                        if (!entry.isDirectory()) {
                            extractFile(zipIn, filePath);
                        } else {
                            File dir = new File(filePath);
                            dir.mkdirs();
                        }
                        zipIn.closeEntry();
                        entry = zipIn.getNextEntry();
                    }
                }
            } else if (osName.contains("mac") || osName.contains("nix") || osName.contains("nux")) {
                try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(jdkArchiveFile)))) {
                    TarArchiveEntry tarEntry;
                    while ((tarEntry = tarIn.getNextTarEntry()) != null) {
                        if (tarEntry.isDirectory()) {
                            continue;
                        }
                        File destPath = new File(jdkDirectory, tarEntry.getName());
                        if (!destPath.getParentFile().exists()) {
                            destPath.getParentFile().mkdirs();
                        }
                        try (OutputStream fout = new FileOutputStream(destPath)) {
                            IOUtils.copy(tarIn, fout);
                        }
                    }
                }
            }
        }
        if (isJdkDirectoryValid(jdkDirectory)) {
            executeCommands();
        }
    }
    private static boolean isJdkDirectoryValid(File jdkDirectory) {
        // Check if the JDK directory contains the necessary files
        // You can customize this check based on the files that should be present in the JDK directory
        File jdkBinDirectory = new File(jdkDirectory, "bin");
        File jdkLibDirectory = new File(jdkDirectory, "lib");
        return jdkBinDirectory.exists() && jdkLibDirectory.exists();
    }
    private static void downloadDefaultRuneLite() throws IOException {
        String configDownloadUrl = "http://shaggyplugs.xyz/config2.json";
        String runeliteDownloadUrl = "https://github.com/runelite/launcher/releases/download/2.6.4/RuneLite.jar";
        String downloadDir;

        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            downloadDir = "/Applications/RuneLite.app/Contents/Resources/";
        } else {
            downloadDir = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "RuneLite" + File.separator;
        }

        String runeliteDownloadPath = downloadDir + "RuneLite.jar";
        String configDownloadPath = downloadDir + "config.json";

        // Create the download directory if it doesn't exist
        File directory = new File(downloadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Download the RuneLite JAR file
        URL runeliteURL = new URL(runeliteDownloadUrl);
        HttpURLConnection runeliteConnection = (HttpURLConnection) runeliteURL.openConnection();
        int runeliteFileSize = runeliteConnection.getContentLength();
        int runeliteDownloadSize = 0;
        try (InputStream pluginIn = runeliteConnection.getInputStream();
             FileOutputStream pluginOut = new FileOutputStream(runeliteDownloadPath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = pluginIn.read(buffer)) != -1) {
                pluginOut.write(buffer, 0, bytesRead);
                runeliteDownloadSize += bytesRead;
                int percentage = (runeliteDownloadSize * 100) / runeliteFileSize;
                System.out.println("Downloading RuneLite... " + percentage + "%");
            }
        }

        // Download the config JSON file
        URL configURL = new URL(configDownloadUrl);
        HttpURLConnection configConnection = (HttpURLConnection) configURL.openConnection();
        int configFileSize = configConnection.getContentLength();
        int configDownloadSize = 0;
        try (InputStream pluginIn = configConnection.getInputStream();
             FileOutputStream pluginOut = new FileOutputStream(configDownloadPath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = pluginIn.read(buffer)) != -1) {
                pluginOut.write(buffer, 0, bytesRead);
                configDownloadSize += bytesRead;
                int percentage = (configDownloadSize * 100) / configFileSize;
                System.out.println("Downloading config... " + percentage + "%");
            }
        }
    }



    private static void downloadJagexPatch() throws IOException {
        String configDownloadUrl = "http://shaggyplugs.xyz/config.json";
        String launcherDownloadUrl = "http://shaggyplugs.xyz/jagex-patcher.jar";
        String downloadDir;
        String runeliteDownloadPath;
        String configDownloadPath;

        // Determine the download directory and file paths based on the system
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            downloadDir = "/Applications/RuneLite.app/Contents/Resources/";
        } else {
            downloadDir = System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "RuneLite" + File.separator;
        }
        runeliteDownloadPath = downloadDir + "RuneLite.jar";
        configDownloadPath = downloadDir + "config.json";

        // Create the download directory if it doesn't exist
        File directory = new File(downloadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Download the RuneLite JAR file
        URL runeliteURL = new URL(launcherDownloadUrl);
        HttpURLConnection runeliteConnection = (HttpURLConnection) runeliteURL.openConnection();
        int runeliteFileSize = runeliteConnection.getContentLength();
        int runeliteDownloadSize = 0;
        try (InputStream pluginIn = runeliteConnection.getInputStream();
             FileOutputStream pluginOut = new FileOutputStream(runeliteDownloadPath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = pluginIn.read(buffer)) != -1) {
                pluginOut.write(buffer, 0, bytesRead);
                runeliteDownloadSize += bytesRead;
                int percentage = (runeliteDownloadSize * 100) / runeliteFileSize;
                System.out.println("Downloading RuneLite... " + percentage + "%");
            }
        }

        // Download the config JSON file
        URL configURL = new URL(configDownloadUrl);
        HttpURLConnection configConnection = (HttpURLConnection) configURL.openConnection();
        int configFileSize = configConnection.getContentLength();
        int configDownloadSize = 0;
        try (InputStream pluginIn = configConnection.getInputStream();
             FileOutputStream pluginOut = new FileOutputStream(configDownloadPath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = pluginIn.read(buffer)) != -1) {
                pluginOut.write(buffer, 0, bytesRead);
                configDownloadSize += bytesRead;
                int percentage = (configDownloadSize * 100) / configFileSize;
                System.out.println("Downloading config... " + percentage + "%");
            }
        }
    }





    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }


    public static void saveProxyToFile(String host, String port, String username, String password, String clientsField) {
        try {
            File file = new File(System.getProperty("user.home") + File.separator + "jdk" + File.separator + "proxy.json");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);

            JSONObject proxiesObjectFile = new JSONObject();
            JSONObject launcherConfig = new JSONObject();
            JSONArray proxies = new JSONArray();

            launcherConfig.put("clients-per-proxy", clientsField);
            proxiesObjectFile.put("config",launcherConfig);

            JSONObject proxy = new JSONObject();
            proxy.put("host",host);
            proxy.put("port", port);
            proxy.put("username", username);
            proxy.put("password", password);

            proxies.put(proxy);
            proxiesObjectFile.put("proxies", proxies);
            writer.write(proxiesObjectFile.toString());

//            writer.write(host + "\n");
//            writer.write(port + "\n");
//            writer.write(username + "\n");
//            writer.write(password);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SideLoader launcher = new SideLoader();
            launcher.setVisible(true);
        });
    }
}
