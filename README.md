# 一、spring-boot-01-yml

## 1. JavaBean数据绑定，yml语法

### 1.1 格式

一般格式

```yaml
key: value
```

Date格式

```yaml
key: yyyy/MM/dd HH:mm:ss
```

Map格式

```yaml
maps:
  key: value
  key: value
行内写法
maps: {book: 西游记, phone: oneplus}
```

List格式

```yaml
lists:
  - value
  - value
行内写法
lists: [values, values]
```

### 1.2 举例

添加pom依赖，写yml时有提示

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

yml文件

```yaml
person:
  name: dkt
  age: 21
  student: true
  birth: 1999/03/03 23:12:59
  #  maps:
  #    book: 西游记
  #    phone: oneplus
  #  lists:
  #    - test
  #    - list集合
  dog:
    nickname: 小花狗
    age: 3
    parentsName: 小白狗和小棕狗
    children:
      - 老大
      - 老二
      - 老三
  maps: {book: 西游记, phone: oneplus}
  lists: [list01, list02]

server:
  port: 8081

dog:
  nickname: 小黄
  age: 1
  parents-name: 小黑狗和小花狗
  children:
    - 老六
    - 老九
```

==重点==

entity类与yml中的值绑定时，entity类需要添加**set和get方法**，否则无法绑定成功

==两种数据绑定方式对比==

|                | @ConfigurationProperties(prefix = "person") | @Value       |
| -------------- | ------------------------------------------- | ------------ |
| 功能           | 批量绑定配置文件中的值                      | 一个一个绑定 |
| 松散绑定       | 支持                                        | 不支持       |
| SpEL表达式     | 不支持                                      | 支持         |
| JSR303数据校验 | 支持                                        | 不支持       |
| 复杂数据绑定   | 支持                                        | 不支持       |

1. 从配置文件中读取单个值用@Value
2. JavaBean与配置文件中的数据绑定用@ConfigurationProperties(prefix = "person")

```java
@RestController
public class TestController {

    @Value("${person.age}")
    private Integer age;

    @RequestMapping("/test")
    public String test() {
        System.out.println(age);
        return "年龄：" + age;
    }

}
```

1. @Value("${person.name}")从配置文件中读取值
2. @Value("#{2+3}")使用SpEl表达式可以计算
3. @Value("false")直接赋值

```java
@Component
//@ConfigurationProperties(prefix = "person")
public class Person {

    @Value("${person.name}")
    private String name;
    @Value("#{2+3}")
    private Integer age;
    @Value("false")
    private Boolean student;
```

- 功能

```java
@Component
@ConfigurationProperties(prefix = "person")
public class Person {

    private String name;
    private Integer age;
    private Boolean student;
    private Date birth;
    private Map<String, Object> maps;
    private List<Object> lists;
    private Dog dog;
```

```java
@Component
//@ConfigurationProperties(prefix = "person")
public class Person {

    @Value("${person.name}")
    private String name;
    @Value("#{2+3}")
    private Integer age;
    @Value("false")
    private Boolean student;
    private Date birth;
    private Map<String, Object> maps;
    private List<Object> lists;
    private Dog dog;
```

- 松散绑定

```java
@Component
@ConfigurationProperties(prefix = "dog")
public class Dog {

    private String nickname;
    private Integer age;
    private String parentsName;
    private List<String> children;
```

```yaml
dog:
  nickname: 小黄
  age: 1
  parents-name: 小黑狗和小花狗
  children:
    - 老六
    - 老九
```

```java
@Component
//@ConfigurationProperties(prefix = "dog")
public class Dog {

    private String nickname;
    private Integer age;
    @Value("${dog.parents-name}")
    private String parentsName;
    private List<String> children;
```

**@Value松散绑定时，报错**

```java
@Component
//@ConfigurationProperties(prefix = "dog")
public class Dog {

    private String nickname;
    private Integer age;
    @Value("${dog.parentsName}")
    private String parentsName;
    private List<String> children;
```

- SpEL表达式

仅@Value支持

```java
@Component
//@ConfigurationProperties(prefix = "person")
public class Person {

    @Value("${person.name}")
    private String name;
    @Value("#{2+3}")
    private Integer age;
    @Value("false")
    private Boolean student;
```

- JSR303数据校验

```java
@Component
@ConfigurationProperties(prefix = "person")
@Validated	开启校验
public class Person {

//    @Value("${person.name}")
//    @Email	邮箱校验
    @NotNull	非空校验
    private String name;
//    @Value("#{2+3}")
    private Integer age;
//    @Value("false")
//    @AssertFalse	必须为false校验
    private Boolean student;
```

- 复杂数据绑定

@Value不支持list、map等复杂数据绑定

```java
@Value("${dog.children}")
private List<String> children;
```

## 2、@PropertySource注解，配置文件中读取值

- "classpath:/person.properties"扫描类路径下的person.properties文件，将**==前缀为person==的值与JavaBean绑定**

```java
@ConfigurationProperties(prefix = "person")
@Component
@PropertySource(value = {"classpath:/person.properties"})
public class Person {

    private String name;
    private Integer age;
    private Boolean student;
    private Date birth;
    private Map<String, Object> maps;
    private List<Object> lists;
    private Dog dog;
```

```properties
person.name=孙悟空
person.age=3000
person.birth=123/4/5 12:12:12
person.student=false
person.maps.k1=v1
person.maps.k2=v2
person.lists=v1,v2,v3
```

## 3、向容器中添加组件

- 添加@Configuration、@Bean两个注解将Cat添加进spring容器

```java
@Configuration
public class MyConfig {

    // return待添加进容器中的Bean，容器中组件默认的id为方法名
    @Bean
    public Cat cat() {
        return new Cat();
    }

}
```

- 测试结果true

```java
@SpringBootTest
class SpringBoot01YmlApplicationTests {

    @Autowired
    ApplicationContext ioc;

    @Test
    void testIoc() {
        boolean b = ioc.containsBean("cat");
        System.out.println(b);
    }
```

## 4、配置文件添加占位符

```yaml
${random.value}
${random.int}等等。。。。。
```

```yaml
person:
  name: dkt_${random.value}
  age: ${random.int}
  student: true
  birth: 1999/03/03 23:12:59
  #  maps:
  #    book: 西游记
  #    phone: oneplus
  #  lists:
  #    - test
  #    - list集合
  dog:
    nickname: 小花狗_${person.name}
    age: 3
    parentsName: 小白狗和小棕狗_${dd.name:你爸爸}
    children:
      - 老大
      - 老二
      - 老三
  maps: {book: 西游记, phone: oneplus}
  lists: [list01, list02]
```

==注意==

${dd.name:你爸爸}表示dd.name不存在使用“你爸爸”作为默认值

## 5、profile多环境开发

### 5.1 多profile文件：application-{profile}.properties/yml

开发环境：新建文件application-dev.properties

生产环境：新建文件application-prod.properties

激活指定profile：在application.properties中spring.profiles.active=prod激活prod环境

### 5.2 yml支持文档块方式

```yaml
---
spring:
  profiles:
    active: prod
---
server:
  port: 8088
spring:
  profiles: dev
---
server:
  port: 8090
spring:
  profiles: prod
```