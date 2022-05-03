# VarsParser

文本变量操作器  
非常小巧的解析器，无任何三方依赖，主要用于对可变数据进行变量化操作。

## 使用

### VarsContext

__区域变量上下文__

这里的区域变量是指被左右标识符包裹的变量，例如`${name}`。`VarsContext`默认以`@{`开头，以`}`结尾。
可以通过`VarsContext.setStart(String)`和`VarsContext.setEnd(String)`进行修改。

以下是一个简单的使用案例：

```java
String s =
        "<!--   <script src=\"/js/jquery.min.js\"></script>\n" +
        "       <script src=\"/js/bootstrap.min.js\"></script>\n" +
        "       <script src=\"/ajax/libs/bootstrap-select/bootstrap-select.min.js\"></script>\n" +
        "       <script src=\"/ajax/libs/bootstrap-select/bootstrap-select.js\"></script>\n" +
        "-->";
VarsContext context = new VarsContext(s);
context.setStart("<");
context.setEnd(">");
// 或者使用
context.setAreaTag("<", ">");
// 过滤一些符号，例如下一行代码则会过滤“<!”。过滤只对左标识生效。 
context.addIgnoredPrefix('!');
context.addIgnoredPrefix('/');
context.addIgnoredSuffix('-');
String result = context.build(new VarsHandler() {

    /**
     * 处理变量
     *
     * @param position 全变量名的第一个字符在整个内容的位置
     * @param var      全变量名，包括了左右标识
     * @param content  变量内部名称，去除了左右标识
     * @return 变量处理后的用于替换全变量名的字符，只会改变build(VarsHandler)方法返回值
     */
    @Override
    public String handle(int position, String var, String content) {
        System.out.println(content);
        return var;
    }
});
System.out.println("\n处理结果: \n" + result);
```

运行上述代码，可以得到以下结果：

```text
script src="/js/jquery.min.js"
script src="/js/bootstrap.min.js"
script src="/ajax/libs/bootstrap-select/bootstrap-select.min.js"
script src="/ajax/libs/bootstrap-select/bootstrap-select.js"

处理结果: 
<!--   <script src="/js/jquery.min.js"></script>
       <script src="/js/bootstrap.min.js"></script>
       <script src="/ajax/libs/bootstrap-select/bootstrap-select.min.js"></script>
       <script src="/ajax/libs/bootstrap-select/bootstrap-select.js"></script>
-->
```

理论上使用`replaceAll`同样可以做到，但是`replaceAll`无法检阅每个标签的内容。

### 注意

变量解析器并不支持变量内嵌，例如：

```text
@{@{name}}
```

只能被解析为变量`@{name`，而不是`@{name}`或`name`

------

### PartContext

__部件变量上下文__

这里的部件变量指的是一般的整体变量，例如`name`，对应的是一个确切的字符串。  
通常情况下，我们会使用`String.replaceAll(String)`来替换，例如`"filename.suffix".repalceAll("\\.", "_")`将`.`替换为`_`。  
但是遇到一些特殊情况时，例如一个长文本有一套替换规则时，`String.replaceAll(String)`可能就不是很好用了。

所以这里提供了 __部件变量上下文__ 来作为替代品。

```java
String s = "abcdefg";
Map<String, String> map = new HashMap<>();
map.put("abd", "哈哈");
map.put("bcd", "哈哈");
map.put("cde", "哈哈");
map.put("efg", "哈哈");

PartContext context = new PartContext(s);
System.out.println(context.apply(map));
// 由于结果词相同，所以上述的方式等效于以下方法
context.replaceWith("哈哈", "abc", "bcd", "cde", "efg");
```

运行所得结果是：

```text
a哈哈哈哈
```

可以看出，虽然`cde`也在字符串中出现，但是`bcd`更早地匹配了，所以`cde`被拆开了。  
另外对于`abcd`与`abc`两个变量的匹配遵循 __长度优先__，也就是优先匹配`abcd`。

这样做与循环`String.replaceAll(String)`的区别是：

* 从字符串最左端依次判定，避免`replaceAll`出现的匹配不可控性
* 非正则匹配，只是单纯的字符串替换，不需要担心变量名是否符合正则要求。
* 速度极快（ __20000__ 长度字符串使用 __2000__ 个变量参数替换，时间在 __50ms__ 以下）
* 避免`replaceAll`过程中，替换后的字符串再次被命中的问题

------

#### PartContext.place

PartContext.place与PartContext.placeWith使用了相同的参数格式，但两者的结果不一样。
PartContext.placeWith中的第一个参数表示了替换结果词，后续的参数表示了替换目标。
而PartContext.place正好相反，第一个参数表示了替换目标，后续的参数表示了依次替换的结果词。

例如：

```java
String s = "你好?，我是?";
PartContext context = new PartContext(s);
System.out.println(context.replace("?", "Verlif", "Filrev"));
```

所得的结果就是：

```text
你好Verlif，我是Filrev
```

## 总述

以下是使用推荐：

* 需要 __少量__ 的 __常量替换__ ，例如将`name`替换为`Verlif`这种推荐使用`String.replaceAll`或者`PartContext.replace`。
* 涉及到一些替换规范的，例如只能使用 __正则表达__ 的，请使用`String.replaceAll`。
* 需要对 __格式变量__ 进行区分 __操作__ 的，例如`#{name}`这种被`#{`与`}`包裹的变量进行单独处理，推荐使用`VarsContext.build`。
* 需要 __大量__ 的 __常量替换__ ，例如 __屏蔽字词表__ 推荐使用`PartContext.apply`。
* 需要进行 __格式变量__ 替换，例如`#{hello}, 我的名字是#{name}, 来自#{from}`这种，推荐使用`VarsContext.build(VarsReplacement)`。

## 添加依赖

1. 添加Jitpack仓库源

> maven
> ```xml
> <repositories>
>    <repository>
>        <id>jitpack.io</id>
>        <url>https://jitpack.io</url>
>    </repository>
> </repositories>
> ```

> Gradle
> ```text
> allprojects {
>   repositories {
>       maven { url 'https://jitpack.io' }
>   }
> }
> ```

2. 添加依赖

> maven
> ```xml
>    <dependencies>
>        <dependency>
>            <groupId>com.github.Verlif</groupId>
>            <artifactId>vars-parser</artifactId>
>            <version>0.6.3</version>
>        </dependency>
>    </dependencies>
> ```

> Gradle
> ```text
> dependencies {
>   implementation 'com.github.Verlif:vars-parser:0.6.3'
> }
> ```

## 效率

效率是不是最好我不保证，反正比`replaceAll`好太多了。可以自行验证。