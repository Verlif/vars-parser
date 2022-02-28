# VarsParser

文本变量操作器  
非常小巧的解析器，无任何三方依赖，主要用于对可变数据进行变量化操作。

## 使用

变量默认以`@{`开头，以`}`结尾。
可以通过`VarsContext.setStart(String)`和`VarsContext.setEnd(String)`进行替换。

以下是一个简单的使用案例：

```java
String s = "你好鸭@{user}，我是@{name}。";
VarsContext context = new VarsContext(s);
// VarsHandler是一个接口，用于处理变量
String build = context.build(new VarsHandler() {
    @Override
    public String handle(String var, String content) {
        switch (content) {
            case "user":
                return "Verlif";
            case "name":
                return "小助手";
            default:
                return var;
        }
    }
});
```

输出`build`文本，可以得到`你好鸭Verlif，我是小助手。`。

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
>            <version>0.1</version>
>        </dependency>
>    </dependencies>
> ```

> Gradle
> ```text
> dependencies {
>   implementation 'com.github.Verlif:vars-parser:0.1'
> }
> ```

## 效率

效率是不是最好我不保证，反正比`replaceAll`好太多了。可以自行验证。