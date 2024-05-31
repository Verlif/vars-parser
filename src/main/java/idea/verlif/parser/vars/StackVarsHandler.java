package idea.verlif.parser.vars;

/**
 * 嵌套类型变量处理器
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/2/28 14:48
 */
public interface StackVarsHandler {

    /**
     * 处理变量
     *
     * @param left    左标识内内容
     * @param content 变量内部名称，去除了左右标识
     * @return 变量处理后的用于替换全变量名的字符，只会改变build(VarsHandler)方法返回值
     */
    String handle(String left, String content);
}
