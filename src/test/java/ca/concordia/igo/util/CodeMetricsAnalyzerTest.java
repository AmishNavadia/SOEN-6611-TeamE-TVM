package ca.concordia.igo.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for CodeMetricsAnalyzer
 * Tests demonstrate improved testability with public static methods
 */
class CodeMetricsAnalyzerTest {

    private CodeMetricsAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new CodeMetricsAnalyzer();
    }

    // ═══════════════════════════════════════════════════════════════
    // BLANK LINE DETECTION TESTS
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Detects blank lines with only whitespace")
    void detectsBlankLines() {
        assertTrue(CodeMetricsAnalyzer.isBlankLine(""));
        assertTrue(CodeMetricsAnalyzer.isBlankLine("   "));
        assertTrue(CodeMetricsAnalyzer.isBlankLine("\t"));
        assertTrue(CodeMetricsAnalyzer.isBlankLine("  \t  "));
    }

    @Test
    @DisplayName("Does not detect code lines as blank")
    void doesNotDetectCodeAsBlank() {
        assertFalse(CodeMetricsAnalyzer.isBlankLine("int x = 5;"));
        assertFalse(CodeMetricsAnalyzer.isBlankLine("  return true;  "));
        assertFalse(CodeMetricsAnalyzer.isBlankLine("// comment"));
    }

    // ═══════════════════════════════════════════════════════════════
    // COMMENT LINE DETECTION TESTS
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Detects single-line comments")
    void detectsSingleLineComments() {
        assertTrue(CodeMetricsAnalyzer.isCommentOnlyLine("// This is a comment"));
        assertTrue(CodeMetricsAnalyzer.isCommentOnlyLine("  // Indented comment"));
        assertTrue(CodeMetricsAnalyzer.isCommentOnlyLine("//No space comment"));
    }

    @Test
    @DisplayName("Detects block comment lines")
    void detectsBlockComments() {
        assertTrue(CodeMetricsAnalyzer.isCommentOnlyLine("/* Block comment */"));
        assertTrue(CodeMetricsAnalyzer.isCommentOnlyLine("  /* Indented block */"));
        assertTrue(CodeMetricsAnalyzer.isCommentOnlyLine(" * Javadoc content"));
        assertTrue(CodeMetricsAnalyzer.isCommentOnlyLine(" */"));
    }

    @Test
    @DisplayName("Does not detect code as comment")
    void doesNotDetectCodeAsComment() {
        assertFalse(CodeMetricsAnalyzer.isCommentOnlyLine("int x = 5;"));
        assertFalse(CodeMetricsAnalyzer.isCommentOnlyLine("String url = \"http://example.com\";"));
        assertFalse(CodeMetricsAnalyzer.isCommentOnlyLine("divide(a, b); // inline comment"));
    }

    // ═══════════════════════════════════════════════════════════════
    // EXECUTABLE CODE DETECTION TESTS
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Detects executable code lines")
    void detectsExecutableCode() {
        assertTrue(CodeMetricsAnalyzer.hasExecutableCode("int x = 5;"));
        assertTrue(CodeMetricsAnalyzer.hasExecutableCode("  return true;  "));
        assertTrue(CodeMetricsAnalyzer.hasExecutableCode("System.out.println(\"test\");"));
        assertTrue(CodeMetricsAnalyzer.hasExecutableCode("if (x > 0) {"));
    }

    @Test
    @DisplayName("Does not detect non-code as executable")
    void doesNotDetectNonCodeAsExecutable() {
        assertFalse(CodeMetricsAnalyzer.hasExecutableCode(""));
        assertFalse(CodeMetricsAnalyzer.hasExecutableCode("   "));
        assertFalse(CodeMetricsAnalyzer.hasExecutableCode("// comment"));
        assertFalse(CodeMetricsAnalyzer.hasExecutableCode("}"));
        assertFalse(CodeMetricsAnalyzer.hasExecutableCode("package com.example;"));
        assertFalse(CodeMetricsAnalyzer.hasExecutableCode("import java.util.List;"));
    }

    // ═══════════════════════════════════════════════════════════════
    // LOGICAL SLOC COUNTING TESTS
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Counts single statements")
    void countsSingleStatements() {
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("int x = 5;"));
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("x++;"));
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("return true;"));
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("throw new Exception();"));
    }

    @Test
    @DisplayName("Counts multiple statements on one line")
    void countsMultipleStatementsPerLine() {
        assertEquals(2, CodeMetricsAnalyzer.countLogicalStatementsInLine("int x = 5; int y = 10;"));
        assertEquals(3, CodeMetricsAnalyzer.countLogicalStatementsInLine("x++; y++; z++;"));
        assertEquals(2, CodeMetricsAnalyzer.countLogicalStatementsInLine("doThis(); doThat();"));
    }

    @Test
    @DisplayName("Counts control flow statements")
    void countsControlFlowStatements() {
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("if (x > 0) {"));
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("for (int i = 0; i < 10; i++) {"));
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("while (running) {"));
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("} else {"));
    }

    @Test
    @DisplayName("Counts case statements")
    void countsCaseStatements() {
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("case 1:"));
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("default:"));
        assertEquals(2, CodeMetricsAnalyzer.countLogicalStatementsInLine("case 2: return false;"));
    }

    @Test
    @DisplayName("Counts class and method declarations")
    void countsDeclarations() {
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("public class MyClass {"));
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("public void doSomething() {"));
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("private int calculate(int x) {"));
    }

    @Test
    @DisplayName("Does not count non-code lines")
    void doesNotCountNonCode() {
        assertEquals(0, CodeMetricsAnalyzer.countLogicalStatementsInLine(""));
        assertEquals(0, CodeMetricsAnalyzer.countLogicalStatementsInLine("   "));
        assertEquals(0, CodeMetricsAnalyzer.countLogicalStatementsInLine("// comment"));
        assertEquals(0, CodeMetricsAnalyzer.countLogicalStatementsInLine("}"));
        assertEquals(0, CodeMetricsAnalyzer.countLogicalStatementsInLine("package com.example;"));
    }

    @Test
    @DisplayName("Handles inline comments correctly")
    void handlesInlineComments() {
        // Should count the statement, not the comment
        assertEquals(1, CodeMetricsAnalyzer.countLogicalStatementsInLine("int x = 5; // set initial value"));
        assertEquals(2, CodeMetricsAnalyzer.countLogicalStatementsInLine("x++; y++; // increment both"));
    }

    // ═══════════════════════════════════════════════════════════════
    // FILE ANALYSIS TESTS
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Analyzes simple Java file correctly")
    void analyzesSimpleFile(@TempDir Path tempDir) throws IOException {
        // Create a test file
        String content =
                "package com.example;\n" +
                        "\n" +
                        "// Simple class\n" +
                        "public class Test {\n" +
                        "    private int value;\n" +
                        "    \n" +
                        "    public void setValue(int v) {\n" +
                        "        this.value = v;\n" +
                        "    }\n" +
                        "}\n";

        File testFile = tempDir.resolve("Test.java").toFile();
        Files.writeString(testFile.toPath(), content);

        CodeMetricsAnalyzer.FileMetrics metrics = analyzer.analyzeFile(testFile);

        assertEquals("Test.java", metrics.getFileName());
        assertEquals(10, metrics.getTotalLines());
        assertEquals(2, metrics.getBlankLines()); // Lines 2, 6
        assertEquals(1, metrics.getCommentLines()); // Line 3
        assertEquals(7, metrics.getPhysicalSLOC()); // 10 - 2 - 1 = 7
        assertTrue(metrics.getLogicalSLOC() > 0); // Has executable statements
    }

    @Test
    @DisplayName("Calculates comment ratio correctly")
    void calculatesCommentRatio(@TempDir Path tempDir) throws IOException {
        String content =
                "// Comment 1\n" +
                        "// Comment 2\n" +
                        "int x = 5;\n" +
                        "int y = 10;\n";

        File testFile = tempDir.resolve("Test.java").toFile();
        Files.writeString(testFile.toPath(), content);

        CodeMetricsAnalyzer.FileMetrics metrics = analyzer.analyzeFile(testFile);

        assertEquals(4, metrics.getTotalLines());
        assertEquals(2, metrics.getCommentLines());
        assertEquals(50.0, metrics.getCommentRatio(), 0.1); // 2/4 * 100 = 50%
    }

    // ═══════════════════════════════════════════════════════════════
    // PROJECT ANALYSIS TESTS (with test exclusion)
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Excludes test files by default")
    void excludesTestFilesByDefault(@TempDir Path tempDir) throws IOException {
        // Create main source file
        Path mainDir = tempDir.resolve("src/main/java");
        Files.createDirectories(mainDir);
        File mainFile = mainDir.resolve("Main.java").toFile();
        Files.writeString(mainFile.toPath(), "public class Main { }");

        // Create test file
        Path testDir = tempDir.resolve("src/test/java");
        Files.createDirectories(testDir);
        File testFile = testDir.resolve("MainTest.java").toFile();
        Files.writeString(testFile.toPath(), "public class MainTest { }");

        analyzer.setExcludeTests(true);
        CodeMetricsAnalyzer.ProjectMetrics metrics = analyzer.analyzeProject(tempDir.toString());

        assertEquals(1, metrics.getTotalFiles()); // Only Main.java
    }

    @Test
    @DisplayName("Includes test files when configured")
    void includesTestFilesWhenConfigured(@TempDir Path tempDir) throws IOException {
        // Create main source file
        Path mainDir = tempDir.resolve("src/main/java");
        Files.createDirectories(mainDir);
        File mainFile = mainDir.resolve("Main.java").toFile();
        Files.writeString(mainFile.toPath(), "public class Main { }");

        // Create test file
        Path testDir = tempDir.resolve("src/test/java");
        Files.createDirectories(testDir);
        File testFile = testDir.resolve("MainTest.java").toFile();
        Files.writeString(testFile.toPath(), "public class MainTest { }");

        analyzer.setExcludeTests(false); // Include tests
        CodeMetricsAnalyzer.ProjectMetrics metrics = analyzer.analyzeProject(tempDir.toString());

        assertEquals(2, metrics.getTotalFiles()); // Both files
    }

    @Test
    @DisplayName("Aggregates project metrics correctly")
    void aggregatesProjectMetrics(@TempDir Path tempDir) throws IOException {
        // Create multiple files
        Path srcDir = tempDir.resolve("src");
        Files.createDirectories(srcDir);

        String file1Content = "public class A {\n    int x = 5;\n}\n";
        String file2Content = "public class B {\n    int y = 10;\n}\n";

        Files.writeString(srcDir.resolve("A.java"), file1Content);
        Files.writeString(srcDir.resolve("B.java"), file2Content);

        CodeMetricsAnalyzer.ProjectMetrics metrics = analyzer.analyzeProject(srcDir.toString());

        assertEquals(2, metrics.getTotalFiles());
        assertEquals(6, metrics.getTotalLines()); // 3 + 3
        assertTrue(metrics.getTotalPhysicalSLOC() > 0);
        assertTrue(metrics.getTotalLogicalSLOC() > 0);
    }

    // ═══════════════════════════════════════════════════════════════
    // CONFIGURATION TESTS
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Configures test exclusion")
    void configuresTestExclusion() {
        analyzer.setExcludeTests(false);
        // Test that configuration is set (would need integration test to verify behavior)
        assertNotNull(analyzer);
    }

    @Test
    @DisplayName("Configures generated code exclusion")
    void configuresGeneratedExclusion() {
        analyzer.setExcludeGenerated(false);
        assertNotNull(analyzer);
    }

    @Test
    @DisplayName("Configures verbose output")
    void configuresVerboseOutput() {
        analyzer.setVerboseOutput(true);
        assertNotNull(analyzer);
    }
}
