# 一、spring-boot-01-yml

## 1. yml语法

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

举例

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

添加pom依赖，写yml时有提示

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

**注意：**两种写法，相同含义

```
parents-name: 小黑狗和小花狗
parentsName: 小白狗和小棕狗
```

==重点==

entity类与yml中的值绑定时，entity类需要添加**set和get方法**，否则无法绑定成功



