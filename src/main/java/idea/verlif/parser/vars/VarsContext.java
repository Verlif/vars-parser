package idea.verlif.parser.vars;

/**
 * 变量上下文
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/2/28 14:40
 */
public class VarsContext {

    public static String start = "@{";

    public static String end = "}";

    /**
     * 上下文
     */
    private final String context;

    public VarsContext(String context) {
        this.context = context;
    }

    public static void setStart(String start) {
        VarsContext.start = start;
    }

    public static void setEnd(String end) {
        VarsContext.end = end;
    }

    public String build(VarsHandler handler) {
        // 对变量区间进行构造
        char[] startChars = start.toCharArray();
        char[] endChars = end.toCharArray();
        // 获取字符串的字符数组
        char[] chars = context.toCharArray();
        // 用StringBuilder作为全文的临时容器
        StringBuilder total = new StringBuilder();
        // 用StringBuilder作为变量储存的临时容器
        StringBuilder sb = new StringBuilder();
        int st = -1, en = -1;
        boolean sta = false;
        int p = 0;
        // 开始遍历
        for (char c : chars) {
            total.append(c);
            p++;
            // 这里做的是分步处理，逻辑上更清晰。也可以将几个if统一起来，减少代码量
            if (sta) {
                sb.append(c);
                if (c == endChars[en + 1]) {
                    en++;
                }
                // 提取到一个变量
                if (en == endChars.length - 1) {
                    en = -1;
                    sta = false;
                    // TODO
                    String var = sb.substring(0, sb.length() - endChars.length);
                    // 清空变量缓存
                    sb.delete(0, sb.length());
                    total.delete(total.length() - var.length() - startChars.length - 1, total.length());
                    total.append(handler.handle(start + var + end, var));
                }
            } else {
                if (c == startChars[st + 1]) {
                    st++;
                }
                if (st == startChars.length - 1) {
                    st = -1;
                    sta = true;
                }
            }
        }
        return total.toString();
    }
}
