
import org.example.Formula;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FormulaTest {
    private static final String CSV_PATH = "./expressions.csv";
    private File csvFile;


    /**
     * 测试纯运算表达式生成（以减法为例）
     */
    @Test
    void testEquationPure() throws Exception {
        // 初始化：10个减法表达式，长度3（3个数字），每行3列
        Formula formula = new Formula(10, 1, 3, 3);
        formula.equationPure();

        // 验证文件是否生成
        assertTrue(csvFile.exists(), "纯运算CSV文件未生成");

        // 读取文件内容
        List<String> lines = Files.readAllLines(Paths.get(CSV_PATH));
        assertFalse(lines.isEmpty(), "CSV文件内容为空");

        // 验证表达式格式（每行最多3列，表达式包含 '-' 和 '='）
        for (String line : lines) {
            String[] columns = line.split(",");
            assertTrue(columns.length <= 3, "纯运算每行超出指定列数");
            for (String expr : columns) {
                expr = expr.trim(); // 去除对齐空格
                assertTrue(expr.contains("-"), "纯运算表达式未包含指定运算符 '-'");
                assertTrue(expr.endsWith("="), "纯运算表达式未以 '=' 结尾");
                // 验证除法场景（此处是减法，不涉及，仅作示例）
            }
        }
    }

    /**
     * 测试混合运算表达式生成（以加减混合为例）
     */
    @Test
    void testEquationMixed() throws Exception {
        // 初始化：8个加减混合表达式，长度2（2个数字），每行2列
        Formula formula = new Formula(8, 4, 2, 2);
        formula.equationMixed();

        assertTrue(csvFile.exists(), "混合运算CSV文件未生成");

        List<String> lines = Files.readAllLines(Paths.get(CSV_PATH));
        assertFalse(lines.isEmpty(), "混合运算CSV内容为空");

        // 验证表达式仅包含 '+' 或 '-'
        for (String line : lines) {
            String[] columns = line.split(",");
            assertTrue(columns.length <= 2, "混合运算每行超出指定列数");
            for (String expr : columns) {
                expr = expr.trim();
                boolean hasValidOp = expr.contains("+") || expr.contains("-");
                assertTrue(hasValidOp, "加减混合表达式包含无效运算符");
                assertTrue(expr.endsWith("="), "混合运算表达式未以 '=' 结尾");
            }
        }
    }

    /**
     * 测试综合运算表达式生成（包含所有运算符）
     */
    @Test
    void testEquationMax() throws Exception {
        // 初始化：6个综合表达式，长度3，每行2列
        Formula formula = new Formula(6, 6, 3, 2);
        formula.equationMax();

        assertTrue(csvFile.exists(), "综合运算CSV文件未生成");

        List<String> lines = Files.readAllLines(Paths.get(CSV_PATH));
        assertFalse(lines.isEmpty(), "综合运算CSV内容为空");

        // 验证表达式包含至少一种运算符，且除法无0除数
        for (String line : lines) {
            String[] columns = line.split(",");
            for (String expr : columns) {
                expr = expr.trim();
                // 检查是否包含有效运算符
                boolean hasOp = expr.contains("+") || expr.contains("-")
                        || expr.contains("×") || expr.contains("÷");
                assertTrue(hasOp, "综合表达式缺少运算符");
                assertTrue(expr.endsWith("="), "综合表达式未以 '=' 结尾");

                // 检查除法是否有0除数（如 "5÷0=" 是无效的）
                if (expr.contains("÷")) {
                    String[] parts = expr.split("÷");
                    // 分割后第二个部分应包含数字（如 "10÷5=3" → 分割后是 ["10", "5=3"]）
                    for (int i = 1; i < parts.length; i++) {
                        String numPart = parts[i].replaceAll("[^0-9]", ""); // 提取数字
                        assertFalse(numPart.isEmpty(), "除法表达式格式错误");
                        int divisor = Integer.parseInt(numPart);
                        assertNotEquals(0, divisor, "除法表达式出现0除数：" + expr);
                    }
                }
            }
        }
    }

    /**
     * 测试Setters方法是否生效
     */
    @Test
    void testSetters() {
        Formula formula = new Formula(1, 0, 2, 1);
        // 修改属性
        formula.setCount(5);
        formula.setKind(3); // 除法
        formula.setLength(3);
        formula.setColumn(5);

        // 间接验证：生成表达式并检查参数是否生效
        formula.equationPure();
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(CSV_PATH));
        } catch (Exception e) {
            fail("Setters测试失败：" + e.getMessage());
            return;
        }

        // 验证数量是否为5
        int totalExpr = 0;
        for (String line : lines) {
            totalExpr += line.split(",").length;
        }
        assertEquals(5, totalExpr, "setCount方法未生效");
    }
}
