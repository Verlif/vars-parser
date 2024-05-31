package idea.verlif.parser.vars;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 参数链结
 */
public class ParamLink implements Serializable {

    /**
     * 点位字符
     */
    private final char c;

    /**
     * 点位深度
     */
    private final int deep;

    /**
     * 后续点位表
     */
    private final Map<Character, ParamLink> linkMap;

    public ParamLink(char c, int deep) {
        this.c = c;
        this.deep = deep;
        this.linkMap = new HashMap<>();
    }

    public void link(String s) {
        if (s.length() > deep) {
            int nextDeep = deep + 1;
            char c = s.charAt(deep);
            ParamLink link = linkMap.computeIfAbsent(c, c1 -> new ParamLink(c1, nextDeep));
            if (s.length() > nextDeep) {
                link.link(s);
            }
        }
    }

    public ParamLink get(char c) {
        return linkMap.get(c);
    }

    public int getDeep() {
        return deep;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParamLink paramLink = (ParamLink) o;
        return c == paramLink.c;
    }

    @Override
    public int hashCode() {
        return Objects.hash(c);
    }
}
