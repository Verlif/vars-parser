package idea.verlif.parser.vars;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 嵌套类型变量上下文
 *
 * @author Verlif
 * @version 1.0
 */
public class StackVarsContext {

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
    private final char[] leftBeginTag;
    private final char[] leftFinishTag;
    /**
     * 变量右标记
     */
    private final char[] rightTag;

    public StackVarsContext(String leftTag, String rightTag) {
        this(leftTag.toCharArray(), new char[0], rightTag.toCharArray());
    }

    public StackVarsContext(char[] leftBeginTag, char[] leftFinishTag, char[] rightTag) {
        this.leftBeginTag = leftBeginTag;
        this.leftFinishTag = leftFinishTag;
        this.rightTag = rightTag;
        ignoredPrefix = new HashSet<>();
        ignoredSuffix = new HashSet<>();
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

    public String build(String context, StackVarsHandler varsHandler) {
        Index index = preBuild(context);
        return build(index, varsHandler);
    }

    public Index preBuild(String context) {
        return new Index(context);
    }

    public String build(Index index, StackVarsHandler varsHandler) {
        if (index.needParse) {
            String context = index.content;
            String leftStr = context.substring(0, index.ls);
            String rightStr = context.substring(index.r + rightTag.length);
            String leftContent = context.substring(index.ls + leftBeginTag.length, index.le);
            String centerStr = varsHandler.handle(
                    leftContent,
                    context.substring(index.le + leftFinishTag.length, index.r));
            if (index.nextParse) {
                return build(leftStr + centerStr + rightStr, varsHandler);
            } else {
                return leftStr + centerStr + rightStr;
            }
        } else {
            return index.content;
        }
    }

    private static final class LeftIndex {
        private int start;
        private int end;
    }

    public final class Index {
        private boolean needParse;
        private boolean nextParse;
        private final String content;
        private int ls;
        private int le;
        private int r;

        public Index(String context) {
            this.content = context;
            char[] chars = context.toCharArray();
            List<LeftIndex> leftIndexes = new ArrayList<>();
            List<Integer> rightIndexes = new ArrayList<>();
            int p = 0;
            // 左标签
            int state = -1;
            LeftIndex leftIndex = null;
            for (int i = 0, size = chars.length; i < size; ++i) {
                char c = chars[i];
                if (state == -1) {
                    if (c == leftBeginTag[p]) {
                        i--;
                        state = 0;
                    }
                } else if (state == 0) {
                    if (c == leftBeginTag[p]) {
                        // 继续判定
                        p++;
                        if (p == leftBeginTag.length) {
                            // 左侧标签左闭合匹配完毕
                            state = 1;
                            leftIndex = new LeftIndex();
                            leftIndex.start = i - p + 1;
                            // 重置标签指针
                            p = 0;
                            // 没有左侧闭合标签时
                            if (leftFinishTag.length == 0) {
                                leftIndex.end = i + 1;
                                leftIndexes.add(leftIndex);
                                // 左侧标签右闭合匹配完毕
                                state = -1;
                            }
                        }
                    } else if (p > 0) {
                        // 重置状态
                        i -= p;
                        p = 0;
                    }
                } else if (state == 1) {
                    if (c == leftFinishTag[p]) {
                        // 继续判定
                        p++;
                        if (p == leftFinishTag.length) {
                            leftIndex.end = i;
                            leftIndexes.add(leftIndex);
                            // 左侧标签右闭合匹配完毕
                            state = -1;
                            // 重置标签指针
                            p = 0;
                        }
                    } else {
                        // 重置指针
                        i -= p;
                        p = 0;
                    }
                }
            }
            // 右标签
            for (int i = 0, size = chars.length; i < size; ++i) {
                char c = chars[i];
                if (c == rightTag[p]) {
                    p++;
                    if (p == rightTag.length) {
                        rightIndexes.add(i - p + 1);
                        p = 0;
                    }
                } else {
                    p = 0;
                }
            }
            // 生成闭合的标签嵌套组
            int leftSize = leftIndexes.size();
            if (leftSize > 0) {
                LeftIndex left = null;
                int right = rightIndexes.get(0);
                for (int i = 0; i < leftIndexes.size(); i++) {
                    LeftIndex temp = leftIndexes.get(i);
                    if (temp.start > right) {
                        left = leftIndexes.get(i - 1);
                        break;
                    }
                }
                if (left == null) {
                    left = leftIndexes.get(leftSize - 1);
                }
                ls = left.start;
                le = left.end;
                r = right;
                if (leftSize > 1) {
                    nextParse = true;
                }
                needParse = true;
            }
        }


        private char[] merge(char[] chars, char[] array) {
            char[] total = new char[chars.length + array.length];
            System.arraycopy(chars, 0, total, 0, chars.length);
            System.arraycopy(array, 0, total, chars.length, array.length);
            return total;
        }

        private char[] merge(char[] cs1, char[] cs2, char[] cs3) {
            char[] total = new char[cs1.length + cs2.length + cs3.length];
            System.arraycopy(cs1, 0, total, 0, cs1.length);
            System.arraycopy(cs2, 0, total, cs1.length, cs2.length);
            System.arraycopy(cs3, 0, total, cs1.length + cs2.length, cs3.length);
            return total;
        }
    }
}
