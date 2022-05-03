package idea.verlif.parser.vars;

import java.util.HashSet;
import java.util.Set;

/**
 * 变量上下文
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/2/28 14:40
 */
public class VarsContext {

    /**
     * 忽略的变量的前缀
     */
    protected Set<Character> ignoredPrefix;

    /**
     * 忽略的变量的后缀
     */
    protected Set<Character> ignoredSuffix;

    /**
     * 变量左标记
     */
    protected String start = "@{";

    /**
     * 变量右标记
     */
    protected String end = "}";

    /**
     * 上下文
     */
    protected final String context;

    public VarsContext(String context) {
        this.context = context;
        ignoredPrefix = new HashSet<>();
        ignoredSuffix = new HashSet<>();
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * 设定区域变量标识
     *
     * @param start 左标识
     * @param end   右标识
     */
    public void setAreaTag(String start, String end) {
        this.start = start;
        this.end = end;
    }

    /**
     * 添加忽略的变量前缀
     *
     * @param c 变量前缀
     */
    public void addIgnoredPrefix(char c) {
        ignoredPrefix.add(c);
    }

    /**
     * 添加忽略的变量后缀
     *
     * @param c 变量后缀
     */
    public void addIgnoredSuffix(char c) {
        ignoredSuffix.add(c);
    }

    public String getContext() {
        return context;
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
        // 是否处于变量名内
        boolean sta = false;
        // 变量字符序号
        int i = -1;
        // 开始遍历
        for (int po = 0, size = chars.length; po < size; po++) {
            char c = chars[po];
            total.append(c);
            // 这里做的是分步处理，逻辑上更清晰。也可以将几个if统一起来，减少代码量
            if (sta) {
                i++;
                // 当当前位是被忽略的字符时
                if (i == 0 && ignoredPrefix.size() > 0 && ignoredPrefix.contains(c)) {
                    sta = false;
                    i = -1;
                } else {
                    sb.append(c);
                    if (c == endChars[en + 1]) {
                        en++;
                        // 提取到一个变量
                        if (en == endChars.length - 1) {
                            // 重置
                            en = -1;
                            i = -1;
                            sta = false;
                            // 提取变量名
                            String var = sb.substring(0, sb.length() - endChars.length);
                            // 清空变量缓存
                            sb.setLength(0);
                            // 当变量名最后一个是被忽略的字符时
                            if (ignoredSuffix.size() == 0 || !ignoredSuffix.contains(var.charAt(var.length() - endChars.length))) {
                                String fullName = start + var + end;
                                total.setLength(total.length() - fullName.length());
                                total.append(handler.handle(total.length() + 1, fullName, var));
                            }
                        }
                    } else if (en > -1 && c != endChars[en]) {
                        po -= en;
                        total.setLength(total.length() - en);
                        en = -1;
                    }
                }
            } else {
                // 对开始标识进行判定
                if (c == startChars[st + 1]) {
                    st++;
                    // 如果当前已判定的字符串是左标识时
                    if (st == startChars.length - 1) {
                        st = -1;
                        sta = true;
                    }
                } else if (st > -1) {
                    // 回退判定
                    po -= st;
                    total.setLength(total.length() - st);
                    st = -1;
                }
            }
        }
        return total.toString();
    }
}
