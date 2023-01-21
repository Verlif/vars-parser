package idea.verlif.parser.vars;

import idea.verlif.parser.vars.impl.VarsReplacement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stopwatch.Stopwatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Verlif
 * @version 1.0
 */
public class MainTest {

    /**
     * 基本字符串替换
     */
    @Test
    public void replaceTest() {
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

        Stopwatch stopwatch = Stopwatch.get("this");
        stopwatch.pin();
        PartContext context = new PartContext(s);
        System.out.println(context.apply(map));
        stopwatch.pin();
        System.out.println(stopwatch.getLastInterval(TimeUnit.MILLISECONDS) + "ms");
    }

    /**
     * 综合变量替换测试
     */
    @Test
    public void variableTest() {
        Map<String, String> map = new HashMap<>();
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            sb.append("#{");
            for (int j = 0; j < 2; j++) {
                sb.append((char) ('a' + random.nextInt(26)));
            }
            sb.append("}");
            map.put(sb.substring(2, sb.length() - 1), "哈哈");
            sb.setLength(0);
        }
        System.out.println("生成变量数\t\t\t\t:\t" + map.size());
        System.out.println("变量Key\t\t\t\t\t:\t" + Arrays.toString(map.keySet().toArray()));

        for (int i = 0; i < 2000; i++) {
            int rand = random.nextInt(2) + 2;
            sb.append("#{");
            for (int j = 0; j < rand; j++) {
                sb.append((char) ('a' + Math.random() * 26));
            }
            sb.append("}");
            for (int j = 0; j < Math.random() * 10; j++) {
                sb.append((char) ('a' + Math.random() * 26));
            }
        }
        String s = sb.toString();

        Stopwatch stopwatch = Stopwatch.get("this");
        System.out.println("生成测试字符串\t\t\t:\t\n" + s);
        System.out.println(">>> ------------------------------------------------ <<<\n");

        System.out.println("开始Part方法测试\t\t\t:\t");
        stopwatch.pin();
        PartContext context = new PartContext(s);
        String result = context.apply(map);
        stopwatch.pin();
        System.out.println(result);
        System.out.println("Part方法测试结束\t\t\t:\t" + stopwatch.getLastInterval(TimeUnit.MILLISECONDS) + "ms");
        System.out.println(">>> ------------------------------------------------ <<<\n");

        System.out.println("开始Vars方法测试\t\t\t:\t");
        stopwatch.pin();
        VarsReplacement replacement = new VarsReplacement();
        replacement.putAll(map);
        VarsContext varsContext = new VarsContext();
        varsContext.setAreaTag("#{", "}");
        String vars = varsContext.build(s, replacement);
        stopwatch.pin();
        System.out.println(vars);
        System.out.println("Vars方法测试结束\t\t\t:\t" + stopwatch.getLastInterval(TimeUnit.MILLISECONDS) + "ms");
        System.out.println(">>> ------------------------------------------------ <<<\n");

        System.out.println("开始Replace测试\t\t\t:\t");
        stopwatch.pin();
        String temp = s;
        for (String key : map.keySet()) {
            temp = temp.replace("#{" + key + "}", map.get(key));
        }
        stopwatch.pin();
        System.out.println(temp);
        System.out.println("Replace测试结束\t\t\t:\t" + stopwatch.getLastInterval(TimeUnit.MILLISECONDS) + "ms");
        System.out.println(">>> ------------------------------------------------ <<<\n");
    }

    /**
     * 从字符串中去除子字符测试
     */
    @Test
    public void removeStringTest() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("{adv");
            for (int j = 0; j < Math.random() * 10; j++) {
                sb.append((char) ('a' + Math.random() * 26));
            }
            sb.append("vas}");
            for (int j = 0; j < Math.random() * 10; j++) {
                sb.append((char) ('a' + Math.random() * 26));
            }
        }
        String s = sb.toString();

        Stopwatch stopwatch = Stopwatch.get("this");
        System.out.println("replace开始\t\t\t: ");
        stopwatch.pin();
        String re = s.replaceAll("\\{adv", "");
        re = re.replaceAll("vas}", "");
        stopwatch.pin();
        System.out.println("replace结束\t\t\t: " + stopwatch.getLastInterval(TimeUnit.MILLISECONDS) + "ms");
        System.out.println(re);
        System.out.println(">>> ------------------------------------------------ <<<\n");

        System.out.println("VarsContext开始\t\t: ");
        stopwatch.pin();
        VarsContext context = new VarsContext();
        context.setAreaTag("{adv", "vas}");
        String result = context.build(s, (position, var, content) -> content);
        stopwatch.pin();
        System.out.println("VarsContext结束\t\t: " + stopwatch.getLastInterval(TimeUnit.MILLISECONDS) + "ms");
        System.out.println(result);
        System.out.println(">>> ------------------------------------------------ <<<\n");

        System.out.println("PartContext开始\t\t: ");
        stopwatch.pin();
        PartContext partContext = new PartContext(s);
        String part = partContext.replaceWith("", "{adv", "vas}");
        stopwatch.pin();
        System.out.println("PartContext结束\t\t: " + stopwatch.getLastInterval(TimeUnit.MILLISECONDS) + "ms");
        System.out.println(part);
        System.out.println(">>> ------------------------------------------------ <<<\n");
    }

    /**
     * 简单的变量替换测试
     */
    @Test
    public void testVars2() {
        String s = "#{hello}, 我的名字是 #{name}, 来自 #{from}";
        VarsReplacement replacement = new VarsReplacement();
        replacement.put("hello", "你好");
        replacement.put("name", "Verlif");
        replacement.put("from", "四川");
        VarsContext varsContext = new VarsContext();
        varsContext.setAreaTag("#{", "}");
        System.out.println(varsContext.build(s, replacement));
    }

    @Before
    public void startStopwatch() {
        Stopwatch.start("this");
    }

    @After
    public void stopStopwatch() {
        Stopwatch stopwatch = Stopwatch.get("this");
        stopwatch.stop();
        System.out.println("---- 累计耗时 >> " + stopwatch.getIntervalLine(TimeUnit.MILLISECONDS) + "  毫秒");
    }
}
