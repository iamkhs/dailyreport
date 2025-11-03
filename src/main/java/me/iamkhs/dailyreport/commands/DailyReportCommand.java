package me.iamkhs.dailyreport.commands;

import me.iamkhs.dailyreport.service.AiService;
import org.jetbrains.annotations.NotNull;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@ShellComponent
public class DailyReportCommand {

    private final AiService  aiService;

    public DailyReportCommand(AiService aiService) {
        this.aiService = aiService;
    }

    @ShellMethod(key = "report", value = "Generate today's work report from Git commits")
    public String report(
            @ShellOption(value = "--path", help = "Path to the local git repository") String path,
            @ShellOption(value = "--author", help = "Filter commits by author email") String author) {

        String todayCommits = runCommand(path, author);
        return this.aiService.summarizeCommits(todayCommits);
    }


    private String runCommand(String path, String authorEmail) {
        StringBuilder builder = new StringBuilder();
        ProcessBuilder processBuilder = getProcessBuilder(path, authorEmail);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Command failed with exit code: " + exitCode);
            }

        } catch (Exception e) {
            System.out.println("Error running command: " + e.getMessage());
        }

        return builder.toString();
    }

    @NotNull
    private static ProcessBuilder getProcessBuilder(String path, String authorEmail) {
        ProcessBuilder processBuilder = new ProcessBuilder();

        List<String> command = new ArrayList<>();
        command.add("git");
        command.add("log");
        command.add("--since=midnight");
        command.add("--pretty=format:%s");
        if (authorEmail != null && !authorEmail.isEmpty()) {
            command.add("--author=" + authorEmail);
        }
        processBuilder.command(command);

        processBuilder.directory(new File(path));
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

}
