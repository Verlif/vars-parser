package idea.verlif.parser.vars;

/**
 * 变量处理器
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/2/28 14:48
 */
public interface VarsHandler {

    /**
     * 处理变量
     *
     * @param var      全变量名
     * @param content  变量内部名称
     * @return 变量处理后的用于替换的字符
     */
    String handle(String var, String content);
}
