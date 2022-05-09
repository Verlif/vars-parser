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
     * @param position 全变量名的第一个字符在整个内容的位置，从0开始
     * @param var      全变量名，包括了左右标识
     * @param content  变量内部名称，去除了左右标识
     * @return 变量处理后的用于替换全变量名的字符，只会改变build(VarsHandler)方法返回值
     */
    String handle(int position, String var, String content);
}
