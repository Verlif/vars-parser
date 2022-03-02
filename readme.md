# VarsParser

文本变量操作器  
非常小巧的解析器，无任何三方依赖，主要用于对可变数据进行变量化操作。

## 使用

变量默认以`@{`开头，以`}`结尾。
可以通过`VarsContext.setStart(String)`和`VarsContext.setEnd(String)`进行替换。

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

## 注意

变量解析器并不支持变量内嵌，例如：

```text
@{@{name}}
```

只能被解析为变量`@{name`，而不是`@{name}`或`name`

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
>            <version>0.2</version>
>        </dependency>
>    </dependencies>
> ```

> Gradle
> ```text
> dependencies {
>   implementation 'com.github.Verlif:vars-parser:0.2'
> }
> ```

## 效率

效率是不是最好我不保证，反正比`replaceAll`好太多了。可以自行验证。