package idea.verlif.parser.vars;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/3/28 11:02
 */
public class ParamLink {

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
        if (s.length() > 0) {
            ParamLink link = linkMap.computeIfAbsent(s.charAt(0), character -> new ParamLink(character, deep + 1));
            if (s.length() > 1) {
                link.link(s.substring(1));
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
