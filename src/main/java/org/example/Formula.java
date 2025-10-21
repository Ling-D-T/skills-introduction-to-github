package org.example;

import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Formula { // 类名首字母大写（规范）
    private int count;    // 表达式数量
    private int kind;     // 运算类型（0-6）
    private int length;   // 表达式长度（2或3）
    private int column;   // 每行列数

    // 构造方法
    public Formula(int count, int kind, int length, int column) {
        this.count = count;
        this.kind = kind;
        this.length = (length == 2 || length == 3) ? length : 2; // 限制长度为2或3
        this.column = column;
    }

    // Setter方法：参数类型统一为int（与属性一致）
    public void setCount(int count) {
        this.count = count;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public void setLength(int length) {
        this.length = (length == 2 || length == 3) ? length : 2;
    }

    public void setColumn(int column) {
        this.column = column;
    }


    public void writeFile(String expression, boolean isNewLine, boolean isAppend) {
        String csvFilePath = "./expressions.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath, isAppend))) {
            writer.write(expression);
            if (isNewLine) {
                writer.newLine(); // 换行
            }
        } catch (IOException e) {
            System.err.println("文件写入失败：" + e.getMessage());
        }
    }

    /**
     * 生成纯运算表达式（同一运算符）
     */
    public void equationPure() {
        Random random = new Random();
        char operator = switch (this.kind) {
            case 0 -> '+';
            case 1 -> '-';
            case 2 -> '×';
            case 3 -> '÷';
            default -> '+'; // 默认加法
        };

        // 先清空文件（首次写入用覆盖模式）
        writeFile("", false, false);

        StringBuilder rowBuilder = new StringBuilder(); // 拼接一行的表达式

        for (int i = 1; i <= count; ) {
            for (int j = 1; j <= column && i <= count; j++, i++) {
                int m = random.nextInt(101); // 0-100
                int n = getValidNumber(random, operator); // 处理除法除数≠0

                String expression;
                if (length == 3) {
                    int t = getValidNumber(random, operator);
                    // 字符串拼接表达式
                    expression = m + "" + operator + n + operator + t + "=";
                } else {
                    expression = m + "" + operator + n + "=";
                }

                // 格式化表达式（左对齐，占15字符，确保列对齐）
                String formattedExpr = String.format("%-15s", expression);
                rowBuilder.append(formattedExpr);

                // 列分隔符（最后一列不加逗号）
                if (j < column) {
                    rowBuilder.append(",");
                }
            }
            // 写入一行（追加模式）
            writeFile(rowBuilder.toString(), true, true);
            rowBuilder.setLength(0); // 清空，准备下一行
        }
        System.out.println("纯运算表达式已保存到 expressions.csv");
    }

    /**
     * 生成混合运算表达式（如加减混合、乘除混合）
     */
    public void equationMixed() {
        Random random = new Random();
        // 确定混合类型（4:加减混合；5:乘除混合）
        boolean isAddSub = (kind == 4);

        // 先清空文件
        writeFile("", false, false);

        StringBuilder rowBuilder = new StringBuilder();

        for (int i = 1; i <= count; ) {
            for (int j = 1; j <= column && i <= count; j++, i++) {
                int m = random.nextInt(101);
                int n = getValidNumber(random, isAddSub ? '+' : '×'); // 加减不限制0，乘除除数≠0

                // 随机生成第一个运算符（加减或乘除）
                char o = isAddSub ? (random.nextBoolean() ? '+' : '-') : (random.nextBoolean() ? '×' : '÷');

                String expression;
                if (length == 3) {
                    int tx = getValidNumber(random, o); // 第三个运算数（除法≠0）
                    // 随机生成第二个运算符（同类型）
                    char op = isAddSub ? (random.nextBoolean() ? '+' : '-') : (random.nextBoolean() ? '×' : '÷');
                    expression = m + "" + o + n+ op + tx + "=";
                } else {
                    expression = m + "" + o + n + "=";
                }

                // 格式化对齐
                String formattedExpr = String.format("%-15s", expression);
                rowBuilder.append(formattedExpr);
                if (j < column) {
                    rowBuilder.append(",");
                }
            }
            writeFile(rowBuilder.toString(), true, true);
            rowBuilder.setLength(0);
        }
        System.out.println("混合运算表达式已保存到 expressions.csv");
    }

    /**
     * 生成综合运算表达式（包含所有运算符）
     */
    public void equationMax() {
        Random random = new Random();

        // 先清空文件
        writeFile("", false, false);

        StringBuilder rowBuilder = new StringBuilder();

        for (int i = 1; i <= count; ) {
            for (int j = 1; j <= column && i <= count; j++, i++) {
                int m = random.nextInt(101);
                // 随机第一个运算符
                char o = getRandomOperator(random);
                int n = getValidNumber(random, o); // 除数≠0

                String expression;
                if (length == 3) {
                    char op = getRandomOperator(random); // 随机第二个运算符
                    int tx = getValidNumber(random, op);
                    expression = m + "" + o + n + op + tx + "=";
                } else {
                    expression = m + "" + o + n + "=";
                }

                // 格式化对齐
                String formattedExpr = String.format("%-15s", expression);
                rowBuilder.append(formattedExpr);
                if (j < column) {
                    rowBuilder.append(",");
                }
            }
            writeFile(rowBuilder.toString(), true, true);
            rowBuilder.setLength(0);
        }
        System.out.println("综合运算表达式已保存到 expressions.csv");
    }

    /**
     * 工具方法：获取随机运算符（+、-、×、÷）
     */
    private char getRandomOperator(Random random) {
        return switch (random.nextInt(4)) {
            case 0 -> '+';
            case 1 -> '-';
            case 2 -> '×';
            case 3 -> '÷';
            default -> '+';
        };
    }

    /**
     * 工具方法：获取有效运算数（除法时确保≠0）
     */
    private int getValidNumber(Random random, char operator) {
        if (operator == '÷') {
            int num;
            do {
                num = random.nextInt(101);
            } while (num == 0); // 除法除数不能为0
            return num;
        } else {
            return random.nextInt(101); // 其他运算符允许0
        }
    }
}
