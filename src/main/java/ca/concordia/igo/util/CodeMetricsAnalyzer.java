package ca.concordia.igo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enhanced analyzer for code metrics including Physical SLOC, Logical SLOC, and Comment Ratio.
 *
 * <p><b>Metric Definitions:</b></p>
 * <ul>
 *   <li><b>Physical SLOC (PSLOC)</b>: Total lines - (blank lines + comment-only lines)</li>
 *   <li><b>Logical SLOC (LSLOC)</b>: Number of executable statements</li>
 *   <li><b>Comment Ratio (CR%)</b>: (Comment Lines / Total Lines) × 100</li>
 * </ul>
 *
 * <p><b>Key Improvements:</b></p>
 * <ul>
 *   <li>Excludes test code automatically (src/test, *Test.java)</li>
 *   <li>Improved logical SLOC counting accuracy</li>
 *   <li>Better handling of mixed code/comment lines</li>
 *   <li>Configurable exclusion patterns</li>
 *   <li>Public static methods for easy unit testing</li>
 * </ul>
 *
 * @author iGo Team
 * @version 2.0 (Refined)
 */
public class CodeMetricsAnalyzer {

    // Configuration
    private boolean excludeTests = true;
    private boolean excludeGenerated = true;
    private boolean verboseOutput = false;

    /**
     * Set whether to exclude test files from analysis
     */
    public void setExcludeTests(boolean excludeTests) {
        this.excludeTests = excludeTests;
    }

    /**
     * Set whether to exclude generated files from analysis
     */
    public void setExcludeGenerated(boolean excludeGenerated) {
        this.excludeGenerated = excludeGenerated;
    }

    /**
     * Set verbose output mode
     */
    public void setVerboseOutput(boolean verbose) {
        this.verboseOutput = verbose;
    }

    /**
     * Metrics for a single Java file
     */
    public static class FileMetrics {
        private final String fileName;
        private final int totalLines;
        private final int blankLines;
        private final int commentLines;
        private final int physicalSLOC;
        private final int logicalSLOC;
        private final double commentRatio;

        public FileMetrics(String fileName, int totalLines, int blankLines,
                           int commentLines, int physicalSLOC, int logicalSLOC) {
            this.fileName = fileName;
            this.totalLines = totalLines;
            this.blankLines = blankLines;
            this.commentLines = commentLines;
            this.physicalSLOC = physicalSLOC;
            this.logicalSLOC = logicalSLOC;
            this.commentRatio = totalLines > 0 ? (commentLines * 100.0 / totalLines) : 0.0;
        }

        public String getFileName() { return fileName; }
        public int getTotalLines() { return totalLines; }
        public int getBlankLines() { return blankLines; }
        public int getCommentLines() { return commentLines; }
        public int getPhysicalSLOC() { return physicalSLOC; }
        public int getLogicalSLOC() { return logicalSLOC; }
        public double getCommentRatio() { return commentRatio; }

        @Override
        public String toString() {
            return String.format(
                    "╔═══════════════════════════════════════════════════════════════\n" +
                            "║ FILE METRICS: %s\n" +
                            "╠═══════════════════════════════════════════════════════════════\n" +
                            "║ Total Lines:          %6d\n" +
                            "║ Blank Lines:          %6d\n" +
                            "║ Comment Lines:        %6d\n" +
                            "║ ─────────────────────────────────────────────────────────────\n" +
                            "║ Physical SLOC:        %6d  (Total - Blanks - Comments)\n" +
                            "║ Logical SLOC:         %6d  (Executable statements)\n" +
                            "║ ─────────────────────────────────────────────────────────────\n" +
                            "║ Comment Ratio:        %6.2f%%\n" +
                            "║   └─ Status: %s\n" +
                            "╚═══════════════════════════════════════════════════════════════\n",
                    fileName,
                    totalLines,
                    blankLines,
                    commentLines,
                    physicalSLOC,
                    logicalSLOC,
                    commentRatio,
                    getCommentRatioAssessment()
            );
        }

        private String getCommentRatioAssessment() {
            if (commentRatio < 10) {
                return "Poor documentation (<10%)";
            } else if (commentRatio <= 20) {
                return "Below recommended (10-20%)";
            } else if (commentRatio <= 40) {
                return "Well documented (20-40%) ✓";
            } else if (commentRatio <= 50) {
                return "Adequately documented (40-50%)";
            } else {
                return "Over-commented (>50%)";
            }
        }
    }

    /**
     * Aggregated metrics for an entire project
     */
    public static class ProjectMetrics {
        private final List<FileMetrics> fileMetrics;
        private final int totalFiles;
        private final int totalLines;
        private final int totalBlankLines;
        private final int totalCommentLines;
        private final int totalPhysicalSLOC;
        private final int totalLogicalSLOC;
        private final double averageCommentRatio;

        public ProjectMetrics(List<FileMetrics> fileMetrics) {
            this.fileMetrics = new ArrayList<>(fileMetrics);
            this.totalFiles = fileMetrics.size();
            this.totalLines = fileMetrics.stream().mapToInt(FileMetrics::getTotalLines).sum();
            this.totalBlankLines = fileMetrics.stream().mapToInt(FileMetrics::getBlankLines).sum();
            this.totalCommentLines = fileMetrics.stream().mapToInt(FileMetrics::getCommentLines).sum();
            this.totalPhysicalSLOC = fileMetrics.stream().mapToInt(FileMetrics::getPhysicalSLOC).sum();
            this.totalLogicalSLOC = fileMetrics.stream().mapToInt(FileMetrics::getLogicalSLOC).sum();
            this.averageCommentRatio = totalLines > 0 ? (totalCommentLines * 100.0 / totalLines) : 0.0;
        }

        public List<FileMetrics> getFileMetrics() { return fileMetrics; }
        public int getTotalFiles() { return totalFiles; }
        public int getTotalLines() { return totalLines; }
        public int getTotalBlankLines() { return totalBlankLines; }
        public int getTotalCommentLines() { return totalCommentLines; }
        public int getTotalPhysicalSLOC() { return totalPhysicalSLOC; }
        public int getTotalLogicalSLOC() { return totalLogicalSLOC; }
        public double getAverageCommentRatio() { return averageCommentRatio; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("╔═══════════════════════════════════════════════════════════════\n");
            sb.append("║           PROJECT METRICS SUMMARY\n");
            sb.append("╠═══════════════════════════════════════════════════════════════\n");
            sb.append(String.format("║ Total Files Analyzed: %6d\n", totalFiles));
            sb.append("║ ─────────────────────────────────────────────────────────────\n");
            sb.append(String.format("║ Total Lines:          %6d\n", totalLines));
            sb.append(String.format("║ Blank Lines:          %6d\n", totalBlankLines));
            sb.append(String.format("║ Comment Lines:        %6d\n", totalCommentLines));
            sb.append("║ ─────────────────────────────────────────────────────────────\n");
            sb.append(String.format("║ Physical SLOC:        %6d\n", totalPhysicalSLOC));
            sb.append(String.format("║ Logical SLOC:         %6d\n", totalLogicalSLOC));
            sb.append("║ ─────────────────────────────────────────────────────────────\n");
            sb.append(String.format("║ Comment Ratio:        %6.2f%%\n", averageCommentRatio));
            sb.append("╚═══════════════════════════════════════════════════════════════\n");
            return sb.toString();
        }

        public String toDetailedString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.toString());
            sb.append("\n");
            sb.append("╔═══════════════════════════════════════════════════════════════\n");
            sb.append("║           INDIVIDUAL FILE METRICS\n");
            sb.append("╠═══════════════════════════════════════════════════════════════\n");

            // Sort files by Physical SLOC (largest first)
            List<FileMetrics> sorted = fileMetrics.stream()
                    .sorted((a, b) -> Integer.compare(b.getPhysicalSLOC(), a.getPhysicalSLOC()))
                    .collect(Collectors.toList());

            for (int i = 0; i < sorted.size(); i++) {
                FileMetrics fm = sorted.get(i);
                sb.append(String.format("║ %2d. %-40s PSLOC: %5d  LSLOC: %5d\n",
                        i + 1,
                        truncate(fm.getFileName(), 40),
                        fm.getPhysicalSLOC(),
                        fm.getLogicalSLOC()));
            }
            sb.append("╚═══════════════════════════════════════════════════════════════\n");
            return sb.toString();
        }

        private String truncate(String str, int length) {
            return str.length() <= length ? str : str.substring(0, length - 3) + "...";
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // PUBLIC STATIC METHODS FOR UNIT TESTING
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Check if a line is blank (contains only whitespace)
     * Made public and static for easy unit testing
     */
    public static boolean isBlankLine(String line) {
        return line.trim().isEmpty();
    }

    /**
     * Check if a line is a comment-only line
     * Made public and static for easy unit testing
     */
    public static boolean isCommentOnlyLine(String line) {
        String trimmed = line.trim();
        return trimmed.startsWith("//") ||
                trimmed.startsWith("/*") ||
                trimmed.startsWith("*") ||
                trimmed.equals("*/");
    }

    /**
     * Check if a line contains executable code (not just comments or whitespace)
     * Made public and static for easy unit testing
     */
    public static boolean hasExecutableCode(String line) {
        String trimmed = line.trim();

        // Blank or comment-only
        if (isBlankLine(line) || isCommentOnlyLine(line)) {
            return false;
        }

        // Just closing brace
        if (trimmed.equals("}") || trimmed.equals("};")) {
            return false;
        }

        // Package or import statements (optional to exclude)
        if (trimmed.startsWith("package ") || trimmed.startsWith("import ")) {
            return false;
        }

        // Has actual code
        return true;
    }

    /**
     * Count logical statements in a single line
     * Improved algorithm that handles multiple statements per line
     * Made public and static for easy unit testing
     */
    public static int countLogicalStatementsInLine(String line) {
        String trimmed = line.trim();

        if (!hasExecutableCode(line)) return 0;

        String codeOnly = removeInlineComments(trimmed);

        int count = 0;

        // ✨ NEW: strip out the for(...) header so its semicolons don't count
        String codeForSemicolonCount = codeOnly.replaceAll("for\\s*\\([^)]*\\)", "for(/*header*/)");

        long semicolonCount = codeForSemicolonCount.chars().filter(ch -> ch == ';').count();
        if (semicolonCount > 0) count += semicolonCount;

        // Control flow keywords (no double-count for `for`, we still add 1 here)
        String[] controlKeywords = {
                "\\bif\\b", "\\belse\\b", "\\bfor\\b", "\\bwhile\\b", "\\bdo\\b",
                "\\bswitch\\b", "\\btry\\b", "\\bcatch\\b", "\\bfinally\\b"
        };
        for (String keyword : controlKeywords) {
            if (codeOnly.matches(".*" + keyword + "\\s*\\(.*") ||
                    codeOnly.matches(".*\\}\\s*" + keyword + "\\s*\\{.*")) {
                count++;
            }
        }

        if (codeOnly.matches("^case\\s+.*:.*") || codeOnly.matches("^default\\s*:.*")) count++;
        if (codeOnly.matches("^(public|private|protected|static|final|abstract)?\\s*(class|interface|enum)\\s+\\w+.*")) count++;
        if (codeOnly.matches("^(public|private|protected|static)?\\s*\\w+\\s+\\w+\\s*\\(.*\\)\\s*\\{?$")) {
            if (semicolonCount == 0) count++;
        }

        return count;
    }

    /**
     * Remove inline comments from a line of code
     * Handles both // and /* comments
     */
    private static String removeInlineComments(String line) {
        // Remove // comments
        int doubleSlashIndex = line.indexOf("//");
        if (doubleSlashIndex >= 0) {
            line = line.substring(0, doubleSlashIndex);
        }

        // Remove /* */ comments (simple approach)
        int blockCommentStart = line.indexOf("/*");
        int blockCommentEnd = line.indexOf("*/");
        if (blockCommentStart >= 0 && blockCommentEnd >= 0) {
            line = line.substring(0, blockCommentStart) + line.substring(blockCommentEnd + 2);
        }

        return line.trim();
    }

    /**
     * Check if a file path should be excluded from analysis
     */
    private boolean shouldExcludeFile(Path path) {
        String pathStr = path.toString().replace('\\', '/');

        // Exclude test files
        if (excludeTests) {
            if (pathStr.contains("/test/") ||
                    pathStr.contains("/tests/") ||
                    pathStr.endsWith("Test.java") ||
                    pathStr.endsWith("Tests.java")) {
                if (verboseOutput) {
                    System.out.println("  ⊗ Excluded (test): " + path.getFileName());
                }
                return true;
            }
        }

        // Exclude generated files
        if (excludeGenerated) {
            if (pathStr.contains("/generated/") ||
                    pathStr.contains("/target/generated-sources/") ||
                    pathStr.contains("/build/generated/")) {
                if (verboseOutput) {
                    System.out.println("  ⊗ Excluded (generated): " + path.getFileName());
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Analyze a single Java file
     */
    public FileMetrics analyzeFile(String filePath) throws IOException {
        return analyzeFile(new File(filePath));
    }

    /**
     * Analyze a single Java file
     */
    public FileMetrics analyzeFile(File file) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        int totalLines = lines.size();
        int blankLines = 0;
        int commentLines = 0;
        int logicalSLOC = 0;
        boolean inBlockComment = false;

        for (String line : lines) {
            String trimmed = line.trim();

            // Track block comments
            if (trimmed.contains("/*")) {
                inBlockComment = true;
            }

            // Blank line
            if (isBlankLine(line)) {
                blankLines++;
            }
            // Comment-only line or inside block comment
            else if (isCommentOnlyLine(line) || inBlockComment) {
                commentLines++;
            }
            // Code line - count logical statements
            else {
                logicalSLOC += countLogicalStatementsInLine(line);
            }

            // End of block comment
            if (trimmed.contains("*/")) {
                inBlockComment = false;
            }
        }

        int physicalSLOC = totalLines - blankLines - commentLines;

        return new FileMetrics(
                file.getName(),
                totalLines,
                blankLines,
                commentLines,
                physicalSLOC,
                logicalSLOC
        );
    }

    /**
     * Analyze all Java files in a directory (recursively)
     * Excludes test files by default
     */
    public ProjectMetrics analyzeProject(String projectPath) throws IOException {
        Path startPath = Paths.get(projectPath);
        List<FileMetrics> allMetrics = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(startPath)) {
            List<File> javaFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !shouldExcludeFile(p))  //  EXCLUDE TEST FILES
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            System.out.println("╔═══════════════════════════════════════════════════════════════");
            System.out.println("║ Analyzing project: " + projectPath);
            System.out.println("║ Found " + javaFiles.size() + " Java files (excluding tests)");
            if (excludeTests) {
                System.out.println("║ Test exclusion: ENABLED");
            }
            if (excludeGenerated) {
                System.out.println("║ Generated code exclusion: ENABLED");
            }
            System.out.println("╚═══════════════════════════════════════════════════════════════\n");

            for (File file : javaFiles) {
                try {
                    FileMetrics metrics = analyzeFile(file);
                    allMetrics.add(metrics);
                    System.out.println("  ✓ Analyzed: " + file.getName());
                } catch (IOException e) {
                    System.err.println("  ✗ Failed: " + file.getName());
                }
            }

            System.out.println();
        }

        return new ProjectMetrics(allMetrics);
    }

    /**
     * Command-line interface for the analyzer
     */
    public static void main(String[] args) {
        CodeMetricsAnalyzer analyzer = new CodeMetricsAnalyzer();

        // Parse command line arguments
        boolean showHelp = false;
        String targetPath = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--include-tests":
                    analyzer.setExcludeTests(false);
                    break;
                case "--include-generated":
                    analyzer.setExcludeGenerated(false);
                    break;
                case "--verbose":
                    analyzer.setVerboseOutput(true);
                    break;
                case "--help":
                case "-h":
                    showHelp = true;
                    break;
                default:
                    if (!args[i].startsWith("--")) {
                        targetPath = args[i];
                    }
            }
        }

        if (showHelp) {
            printHelp();
            return;
        }

        // Quick analysis mode
        if (targetPath == null) {
            System.out.println("╔═══════════════════════════════════════════════════════════════");
            System.out.println("║ Code Metrics Analyzer - QUICK ANALYSIS MODE");
            System.out.println("╚═══════════════════════════════════════════════════════════════\n");

            try {
                // Analyze entire project (excluding tests by default)
                System.out.println("═══ PROJECT ANALYSIS (Excluding Test Code) ═══\n");
                ProjectMetrics projectMetrics = analyzer.analyzeProject("src/main/java");
//                ProjectMetrics projectMetrics = analyzer.analyzeProject("src/main/java/ca/concordia/igo/service/PaymentService.java");
                System.out.println(projectMetrics.toDetailedString());

            } catch (IOException e) {
                System.err.println("Error during analysis: " + e.getMessage());
                System.err.println("\nNote: Make sure you're running from the project root directory.");
                System.err.println("Usage: java CodeMetricsAnalyzer <path> [options]\n");
                printHelp();
            }
            return;
        }

        // Command line mode
        File target = new File(targetPath);

        try {
            if (target.isFile() && target.getName().endsWith(".java")) {
                // Analyze single file
                FileMetrics metrics = analyzer.analyzeFile(target);
                System.out.println(metrics);

            } else if (target.isDirectory()) {
                // Analyze entire project
                ProjectMetrics projectMetrics = analyzer.analyzeProject(targetPath);
                System.out.println(projectMetrics.toDetailedString());

            } else {
                System.err.println("Error: Path must be a .java file or directory");
                System.exit(1);
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printHelp() {
        System.out.println("╔═══════════════════════════════════════════════════════════════");
        System.out.println("║ Code Metrics Analyzer - Usage");
        System.out.println("╠═══════════════════════════════════════════════════════════════");
        System.out.println("║ java CodeMetricsAnalyzer <path> [options]");
        System.out.println("║");
        System.out.println("║ Arguments:");
        System.out.println("║   <path>              File or directory to analyze");
        System.out.println("║");
        System.out.println("║ Options:");
        System.out.println("║   --include-tests     Include test files in analysis");
        System.out.println("║   --include-generated Include generated files in analysis");
        System.out.println("║   --verbose           Show detailed exclusion information");
        System.out.println("║   --help, -h          Show this help message");
        System.out.println("║");
        System.out.println("║ Examples:");
        System.out.println("║   java CodeMetricsAnalyzer src/main/java");
        System.out.println("║   java CodeMetricsAnalyzer src/main/java --verbose");
        System.out.println("║   java CodeMetricsAnalyzer PaymentService.java");
        System.out.println("║   java CodeMetricsAnalyzer src --include-tests");
        System.out.println("║");
        System.out.println("║ Default Behavior:");
        System.out.println("║   - Excludes test files (*Test.java, */test/*, */tests/*)");
        System.out.println("║   - Excludes generated code (*/generated/*, */target/*)");
        System.out.println("╚═══════════════════════════════════════════════════════════════");
    }
}