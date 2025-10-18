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
 * Comprehensive analyzer for code metrics including Physical SLOC, Logical SLOC, and Comment Ratio.
 *
 * <p><b>Metric Definitions:</b></p>
 * <ul>
 *   <li><b>Physical SLOC (PSLOC)</b>: Total lines - (blank lines + comment-only lines)</li>
 *   <li><b>Logical SLOC (LSLOC)</b>: Number of executable statements</li>
 *   <li><b>Comment Ratio (CR%)</b>: (Comment Lines / Total Lines) × 100</li>
 * </ul>
 *
 * <p><b>Usage Examples:</b></p>
 * <pre>
 * // Analyze a single file
 * CodeMetricsAnalyzer analyzer = new CodeMetricsAnalyzer();
 * FileMetrics metrics = analyzer.analyzeFile("PaymentService.java");
 * System.out.println(metrics);
 *
 * // Analyze entire project
 * ProjectMetrics projectMetrics = analyzer.analyzeProject("src/main/java");
 * System.out.println(projectMetrics.toDetailedString());
 * </pre>
 *
 * @author iGo Team
 * @version 1.0
 */
public class CodeMetricsAnalyzer {

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
                    .sorted(Comparator.comparingInt(FileMetrics::getPhysicalSLOC).reversed())
                    .collect(Collectors.toList());

            sb.append(String.format("║ %-40s %8s %8s %6s\n", "File", "PSLOC", "LSLOC", "CR%"));
            sb.append("╠═══════════════════════════════════════════════════════════════\n");

            for (FileMetrics fm : sorted) {
                String shortName = fm.getFileName().length() > 40
                        ? "..." + fm.getFileName().substring(fm.getFileName().length() - 37)
                        : fm.getFileName();
                sb.append(String.format("║ %-40s %8d %8d %5.1f%%\n",
                        shortName,
                        fm.getPhysicalSLOC(),
                        fm.getLogicalSLOC(),
                        fm.getCommentRatio()
                ));
            }

            sb.append("╚═══════════════════════════════════════════════════════════════\n");
            return sb.toString();
        }
    }

    /**
     * State machine for tracking multi-line comments
     */
    private enum ParseState {
        CODE,           // Normal code
        LINE_COMMENT,   // Single-line comment (//)
        BLOCK_COMMENT,  // Multi-line comment (/* ... */)
        JAVADOC         // JavaDoc comment (/** ... */)
    }

    /**
     * Analyze a single Java file by path
     *
     * @param filePath path to the Java file
     * @return FileMetrics object with all calculated metrics
     * @throws IOException if file cannot be read
     */
    public FileMetrics analyzeFile(String filePath) throws IOException {
        File file = new File(filePath);
        return analyzeFile(file);
    }

    /**
     * Analyze a single Java file
     *
     * @param file the Java file to analyze
     * @return FileMetrics object with all calculated metrics
     * @throws IOException if file cannot be read
     */
    public FileMetrics analyzeFile(File file) throws IOException {
        int totalLines = 0;
        int blankLines = 0;
        int commentLines = 0;
        int logicalSLOC = 0;

        ParseState state = ParseState.CODE;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                totalLines++;
                String trimmed = line.trim();

                // Handle blank lines
                if (trimmed.isEmpty()) {
                    blankLines++;
                    continue;
                }

                // Parse the line
                ParseResult result = parseLine(trimmed, state);
                state = result.endState;
                logicalSLOC += result.logicalStatements;

                // Count comment-only lines
                if (result.hasComment && !result.hasCode) {
                    commentLines++;
                }
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
     * Result of parsing a single line
     */
    private static class ParseResult {
        ParseState endState;
        boolean hasCode;
        boolean hasComment;
        int logicalStatements;

        ParseResult(ParseState endState, boolean hasCode, boolean hasComment, int logicalStatements) {
            this.endState = endState;
            this.hasCode = hasCode;
            this.hasComment = hasComment;
            this.logicalStatements = logicalStatements;
        }
    }

    /**
     * Parse a single line to determine if it contains code, comments, or both
     */
    private ParseResult parseLine(String line, ParseState initialState) {
        ParseState state = initialState;
        boolean hasCode = false;
        boolean hasComment = false;
        int logicalStatements = 0;

        StringBuilder codeBuffer = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            char next = (i + 1 < line.length()) ? line.charAt(i + 1) : '\0';

            switch (state) {
                case CODE:
                    if (c == '/' && next == '/') {
                        // Start of line comment
                        state = ParseState.LINE_COMMENT;
                        hasComment = true;
                        i++; // Skip next char

                        // Check accumulated code
                        if (codeBuffer.toString().trim().length() > 0) {
                            hasCode = true;
                            logicalStatements += countLogicalStatements(codeBuffer.toString());
                        }
                        codeBuffer.setLength(0);
                    } else if (c == '/' && next == '*') {
                        // Start of block comment or JavaDoc
                        if (i + 2 < line.length() && line.charAt(i + 2) == '*') {
                            state = ParseState.JAVADOC;
                            i += 2;
                        } else {
                            state = ParseState.BLOCK_COMMENT;
                            i++;
                        }
                        hasComment = true;

                        // Check accumulated code
                        if (codeBuffer.toString().trim().length() > 0) {
                            hasCode = true;
                            logicalStatements += countLogicalStatements(codeBuffer.toString());
                        }
                        codeBuffer.setLength(0);
                    } else {
                        codeBuffer.append(c);
                    }
                    break;

                case LINE_COMMENT:
                    // Line comments go until end of line
                    break;

                case BLOCK_COMMENT:
                case JAVADOC:
                    if (c == '*' && next == '/') {
                        // End of block comment
                        state = ParseState.CODE;
                        i++; // Skip */
                    }
                    break;
            }
        }

        // End of line processing
        if (state == ParseState.LINE_COMMENT) {
            state = ParseState.CODE;
        }

        // Check remaining code
        if (state == ParseState.CODE && codeBuffer.toString().trim().length() > 0) {
            hasCode = true;
            logicalStatements += countLogicalStatements(codeBuffer.toString());
        }

        return new ParseResult(state, hasCode, hasComment, logicalStatements);
    }

    /**
     * Count logical statements in a code snippet
     *
     * <p>Logical statements include:</p>
     * <ul>
     *   <li>Control flow: if, else, for, while, do, switch, case</li>
     *   <li>Method declarations and calls</li>
     *   <li>Variable declarations and assignments</li>
     *   <li>Return, throw, break, continue statements</li>
     *   <li>Try/catch/finally blocks</li>
     * </ul>
     */
    private int countLogicalStatements(String code) {
        int count = 0;
        String trimmed = code.trim();

        if (trimmed.isEmpty()) {
            return 0;
        }

        // Control flow keywords
        String[] keywords = {
                "\\bif\\b", "\\belse\\b", "\\bfor\\b", "\\bwhile\\b", "\\bdo\\b",
                "\\bswitch\\b", "\\bcase\\b", "\\breturn\\b", "\\bthrow\\b",
                "\\btry\\b", "\\bcatch\\b", "\\bfinally\\b", "\\bbreak\\b", "\\bcontinue\\b"
        };

        for (String keyword : keywords) {
            if (trimmed.matches(".*" + keyword + ".*")) {
                count++;
                break; // Count once per line
            }
        }

        // Method calls/declarations (identifier followed by parentheses)
        if (trimmed.matches(".*\\w+\\s*\\(.*")) {
            count++;
        }

        // Statements with semicolons
        if (trimmed.contains(";")) {
            if (count == 0) {
                count++;
            }
        }

        // Class/interface/enum declarations
        if (trimmed.matches("^(public|private|protected)?\\s*(static)?\\s*(final)?\\s*(class|interface|enum)\\s+.*")) {
            count++;
        }

        return Math.max(count, 0);
    }

    /**
     * Analyze all Java files in a directory (recursively)
     *
     * @param projectPath root directory path
     * @return ProjectMetrics with aggregated metrics
     * @throws IOException if directory cannot be read
     */
    public ProjectMetrics analyzeProject(String projectPath) throws IOException {
        Path startPath = Paths.get(projectPath);
        List<FileMetrics> allMetrics = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(startPath)) {
            List<File> javaFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            System.out.println("╔═══════════════════════════════════════════════════════════════");
            System.out.println("║ Analyzing project: " + projectPath);
            System.out.println("║ Found " + javaFiles.size() + " Java files");
            System.out.println("╚═══════════════════════════════════════════════════════════════\n");

            for (File file : javaFiles) {
                try {
                    FileMetrics metrics = analyzeFile(file);
                    allMetrics.add(metrics);
                    System.out.println("  ✓ Analyzed: " + file.getName());
                } catch (IOException e) {
                    System.err.println("  ✗ Failed: " + file.getName());
                    Logger.error("Failed to analyze file: " + file.getAbsolutePath(), e);
                }
            }

            System.out.println();
        }

        return new ProjectMetrics(allMetrics);
    }

    /**
     * Command-line interface for the analyzer
     *
     * <p><b>Usage:</b></p>
     * <pre>
     * java CodeMetricsAnalyzer &lt;file-or-directory&gt;
     * </pre>
     *
     * <p><b>Quick Analysis Mode:</b></p>
     * <p>If no arguments provided, runs demo analysis on specific files.
     * Modify the file paths below to analyze your desired files.</p>
     */
    public static void main(String[] args) {
        CodeMetricsAnalyzer analyzer = new CodeMetricsAnalyzer();

        // ═══════════════════════════════════════════════════════════════
        // QUICK ANALYSIS MODE - Modify these paths for your analysis
        // ═══════════════════════════════════════════════════════════════
        if (args.length == 0) {
            System.out.println("╔═══════════════════════════════════════════════════════════════");
            System.out.println("║ Code Metrics Analyzer - QUICK ANALYSIS MODE");
            System.out.println("╚═══════════════════════════════════════════════════════════════\n");

            try {
                // ─────────────────────────────────────────────────────────
                // EXAMPLE 1: Analyze a single file
                // ─────────────────────────────────────────────────────────
                System.out.println("═══ SINGLE FILE ANALYSIS ═══\n");
                FileMetrics metrics = analyzer.analyzeFile("src/main/java/ca/concordia/igo/service/PaymentService.java");
                System.out.println(metrics);

                System.out.println("\n" + "═".repeat(65) + "\n");

                // ─────────────────────────────────────────────────────────
                // EXAMPLE 2: Analyze entire project
                // ─────────────────────────────────────────────────────────
                System.out.println("═══ PROJECT ANALYSIS ═══\n");
                ProjectMetrics projectMetrics = analyzer.analyzeProject("src/main/java");
                System.out.println(projectMetrics.toDetailedString());

            } catch (IOException e) {
                System.err.println("Error during analysis: " + e.getMessage());
                System.err.println("\nNote: Make sure the file paths are correct relative to your current directory.");
                System.err.println("You can modify the file paths in the main() method.\n");

                System.out.println("╔═══════════════════════════════════════════════════════════════");
                System.out.println("║ Alternative Usage: Command Line Mode");
                System.out.println("╠═══════════════════════════════════════════════════════════════");
                System.out.println("║ java CodeMetricsAnalyzer <file-or-directory>");
                System.out.println("║");
                System.out.println("║ Examples:");
                System.out.println("║   java CodeMetricsAnalyzer PaymentService.java");
                System.out.println("║   java CodeMetricsAnalyzer src/main/java");
                System.out.println("╚═══════════════════════════════════════════════════════════════");
            }
            return;
        }

        // ═══════════════════════════════════════════════════════════════
        // COMMAND LINE MODE - Analyze path provided as argument
        // ═══════════════════════════════════════════════════════════════
        String path = args[0];
        File target = new File(path);

        try {
            if (target.isFile() && target.getName().endsWith(".java")) {
                // Analyze single file
                FileMetrics metrics = analyzer.analyzeFile(target);
                System.out.println(metrics);

            } else if (target.isDirectory()) {
                // Analyze entire project
                ProjectMetrics projectMetrics = analyzer.analyzeProject(path);
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
}
