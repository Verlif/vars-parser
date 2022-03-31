package idea.verlif.parser.vars;

import idea.verlif.parser.vars.PartContext;
import idea.verlif.parser.vars.VarsContext;
import idea.verlif.parser.vars.VarsHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/2/28 15:03
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        testSons();
    }

    private static void testSons2() {
        String s = "abcdefg";
        Map<String, String> map = new HashMap<>();
        map.put("abd", "哈哈");
        map.put("bcd", "哈哈");
        map.put("cde", "哈哈");
        map.put("efg", "哈哈");
        map.put("vds", "哈哈");
        map.put("cds", "哈哈");
        map.put("wez", "哈哈");
        map.put("zrz", "哈哈");
        System.out.println(Arrays.toString(map.keySet().toArray()));

        PartContext context = new PartContext(s);
        System.out.println(context.apply(map));
        System.out.println(System.currentTimeMillis());
    }

    private static void testSons() throws InterruptedException {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 2000; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 3; j++) {
                sb.append((char) ('a' + Math.random() * 26));
            }
            map.put(sb.toString(), "哈哈");
        }
        System.out.println("生成变量数\t\t\t\t:\t" + map.size());
        System.out.println("变量Key\t\t\t\t\t:\t" + Arrays.toString(map.keySet().toArray()));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 20000; i++) {
            sb.append((char) ('a' + Math.random() * 26));
        }
        String s = sb.toString();
        System.out.println("生成测试字符串\t\t\t:\t\n" + s);
        System.out.println("开始Son方法测试\t\t\t:\t" + System.currentTimeMillis());
        PartContext context = new PartContext(s);
        String result = context.apply(map);
        System.out.println(result);
        System.out.println("Son方法测试结束\t\t\t:\t" + System.currentTimeMillis());

        Thread.sleep(200);

        System.out.println("开始ReplaceAll测试\t\t:\t" + System.currentTimeMillis());
        String temp = s;
        for (String key : map.keySet()) {
            temp = temp.replaceAll(key, map.get(key));
        }
        System.out.println(temp);
        System.out.println("ReplaceAll测试结束\t\t:\t" + System.currentTimeMillis());
    }

    private static void testVars() {
        String s = "0123@{@{name}}@{}}@{{}@";
        VarsContext context = new VarsContext(s);
        String result = context.build(new VarsHandler() {
            @Override
            public String handle(int position, String var, String content) {
                System.out.println(position + " - " + content);
                return "";
            }
        });
        System.out.println("\n处理结果: \n" + result);
    }
}
