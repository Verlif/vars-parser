package idea.verlif.parser.vars;

import idea.verlif.parser.vars.impl.VarsReplacement;

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
        String s = "hehellollo";
        System.out.println(s.replace("hello", ""));
        testVars2();
    }

    private static void testParts2() {
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

    private static void testParts() throws InterruptedException {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 2000; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("#{");
            for (int j = 0; j < 3; j++) {
                sb.append((char) ('a' + Math.random() * 26));
            }
            sb.append("}");
            map.put(sb.toString(), "哈哈");
        }
        System.out.println("生成变量数\t\t\t\t:\t" + map.size());
        System.out.println("变量Key\t\t\t\t\t:\t" + Arrays.toString(map.keySet().toArray()));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            sb.append("#{");
            for (int j = 0; j < 3; j++) {
                sb.append((char) ('a' + Math.random() * 26));
            }
            sb.append("}");
            for (int j = 0; j < Math.random() * 10; j++) {
                sb.append((char) ('a' + Math.random() * 26));
            }
        }
        String s = sb.toString();

        System.out.println("生成测试字符串\t\t\t:\t\n" + s);
        System.out.println("开始Part方法测试\t\t\t:\t" + System.currentTimeMillis());
        PartContext context = new PartContext(s);
        String result = context.apply(map);
        System.out.println(result);
        System.out.println("Part方法测试结束\t\t\t:\t" + System.currentTimeMillis());

        System.out.println("开始Vars方法测试\t\t\t:\t" + System.currentTimeMillis());
        VarsReplacement replacement = new VarsReplacement();
        replacement.putAll(map);
        VarsContext varsContext = new VarsContext(s);
        varsContext.setAreaTag("#{", "}");
        String vars = varsContext.build(replacement);
        System.out.println(vars);
        System.out.println("Vars方法测试结束\t\t\t:\t" + System.currentTimeMillis());

        Thread.sleep(200);

        System.out.println("开始Replace测试\t\t:\t" + System.currentTimeMillis());
        String temp = s;
        for (String key : map.keySet()) {
            temp = temp.replace(key, map.get(key));
        }
        System.out.println(temp);
        System.out.println("Replace测试结束\t\t:\t" + System.currentTimeMillis());
    }

    private static void testVars() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("{");
            for (int j = 0; j < Math.random() * 10; j++) {
                sb.append((char) ('a' + Math.random() * 26));
            }
            sb.append("}");
            for (int j = 0; j < Math.random() * 10; j++) {
                sb.append((char) ('a' + Math.random() * 26));
            }
        }
        String s = sb.toString();

        System.out.println("replace开始\t\t\t: " + System.currentTimeMillis());
        String re = s.replaceAll("\\{", "");
        re = re.replaceAll("}", "");
        System.out.println("replace结束\t\t\t: " + System.currentTimeMillis());
        System.out.println(re);
        System.out.println();

        System.out.println("VarsContext开始\t\t: " + System.currentTimeMillis());
        VarsContext context = new VarsContext(s);
        context.setAreaTag("{", "}");
        String result = context.build((position, var, content) -> content);
        System.out.println("VarsContext结束\t\t: " + System.currentTimeMillis());
        System.out.println(result);
        System.out.println();

        System.out.println("PartContext开始\t\t: " + System.currentTimeMillis());
        PartContext partContext = new PartContext(s);
        String part = partContext.replace("", "{", "}");
        System.out.println("PartContext结束\t\t: " + System.currentTimeMillis());
        System.out.println(part);
    }

    private static void testVars2() {
        String s = "#{hello}, 我的名字是 #{name}, 来自 #{from}";
        VarsReplacement replacement = new VarsReplacement();
        replacement.put("hello", "你好");
        replacement.put("name", "Verlif");
        replacement.put("from", "四川");
        VarsContext varsContext = new VarsContext(s);
        varsContext.setAreaTag("#{", "}");
        System.out.println(varsContext.build(replacement));
    }
}
