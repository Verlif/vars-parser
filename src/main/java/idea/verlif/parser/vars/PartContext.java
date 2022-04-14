package idea.verlif.parser.vars;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/3/28 11:00
 */
public class PartContext {

    private final String context;

    public PartContext(String context) {
        this.context = context;
    }

    /**
     * 清除字符串中的一些字符串
     *
     * @param targetStr 需要清除的字符串数组
     * @return 清除后的结果
     */
    public String clear(String... targetStr) {
        return replaceWith("", targetStr);
    }

    /**
     * 使用替换数组依次替换字符串
     *
     * @param target 需替换的字符串
     * @param values 替换数组
     * @return 替换后的字符串
     */
    public String replace(String target, Object... values) {
        if (target == null || values.length == 0) {
            return context;
        }
        StringBuilder sb = new StringBuilder();
        char[] recs = context.toCharArray();
        // 当需要替换的为0长度字符时，进行每位插入
        if (target.length() == 0) {
            int i = 0, max = recs.length;
            for (int length = values.length; i < length && i < max; i++) {
                sb.append(values[i]).append(recs[i]);
            }
            if (i < max - 1) {
                sb.append(recs, i, max);
            }
            return sb.toString();
        }
        char[] tars = target.toCharArray();
        int i = 0, ofs = 0, flow = 0, max = recs.length;
        for (int length = recs.length; i < length; i++) {
            char c = recs[i];
            if (c == tars[flow]) {
                flow++;
            } else {
                i -= flow;
                sb.append(recs[i]);
                flow = 0;
            }
            // 匹配到目标字符串
            if (flow == tars.length) {
                flow = 0;
                sb.append(values[ofs++]);
                if (ofs == values.length) {
                    break;
                }
            }
        }
        if (i < max) {
            sb.append(recs, i + 1, max - i - 1);
        }
        if (flow > 0) {
            sb.append(recs, max - flow, flow);
        }
        return sb.toString();
    }

    /**
     * 替换字符串
     *
     * @param newStr 替换的结果字符串
     * @param oldStr 需替换的字符串数组
     * @return 替换结果
     */
    public String replaceWith(String newStr, String... oldStr) {
        if (oldStr.length == 0) {
            return context;
        }
        Map<String, String> map = new HashMap<>(oldStr.length);
        for (String s : oldStr) {
            map.put(s, newStr);
        }
        return apply(map);
    }

    public String apply(Map<String, String> params) {
        // 初始化参数链接表
        Map<Character, ParamLink> linkMap = new HashMap<>(params.size() + 1);
        for (String key : params.keySet()) {
            if (key.length() > 0) {
                char c = key.charAt(0);
                ParamLink link = linkMap.get(c);
                if (link == null) {
                    link = new ParamLink(c, 1);
                    linkMap.put(c, link);
                }
                if (key.length() > 1) {
                    link.link(key);
                }
            }
        }

        // 遍历目标字符串，并替换
        Map<Character, ParamLink> tempMap = new HashMap<>(linkMap);
        ParamLink temp = null;
        // 结果字符串构造器
        StringBuilder resultBuilder = new StringBuilder();
        // 变量参数字符串构造器
        StringBuilder paramBuilder = new StringBuilder();
        char[] chars = context.toCharArray();
        for (int i = 0, length = chars.length; i < length; i++) {
            char c = chars[i];
            if (temp == null) {
                temp = tempMap.get(c);
            } else {
                temp = temp.get(c);
            }
            if (temp == null) {
                // 已解析可能存在变量
                if (paramBuilder.length() > 0) {
                    // 提取以解析到的变量名
                    String param = paramBuilder.toString();

                    // 查看是否存在这样的变量参数
                    String value = params.get(param);
                    while (value == null && param.length() > 0) {
                        param = param.substring(0, param.length() - 1);
                        value = params.get(param);
                    }
                    if (value == null) {
                        // 变量无法匹配则回退（变量长度 - 1）位
                        i -= paramBuilder.length();
                        // 最终字符串增加变量第一位字符
                        resultBuilder.append(chars[i]);
                    } else {
                        resultBuilder.append(value);
                        i -= paramBuilder.length() - param.length() + 1;
                    }
                    // 清空变量
                    paramBuilder.setLength(0);
                } else {
                    resultBuilder.append(c);
                }
            } else {
                paramBuilder.append(c);
            }
        }
        if (paramBuilder.length() > 0) {
            // 提取以解析到的变量名
            String param = paramBuilder.toString();
            paramBuilder.setLength(0);

            // 查看是否存在这样的变量参数
            String value = params.get(param);
            if (value == null) {
                resultBuilder.append(param);
            } else {
                resultBuilder.append(value);
            }
        }
        return resultBuilder.toString();
    }

}
