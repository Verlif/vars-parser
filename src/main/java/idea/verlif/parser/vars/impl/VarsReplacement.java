package idea.verlif.parser.vars.impl;

import idea.verlif.parser.vars.VarsHandler;

import java.util.HashMap;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/2 10:01
 */
public class VarsReplacement extends HashMap<String, String> implements VarsHandler {

    @Override
    public String handle(int position, String var, String content) {
        String s = get(content);
        return s == null ? var : s;
    }

}
