

# 一、 spring-boot-01-yml

## 1、 JavaBean数据绑定，yml语法

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

## 2、 @PropertySource注解，配置文件中读取值

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

## 3、 向容器中添加组件

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

## 4、 配置文件添加占位符

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

## 5、 profile多环境开发

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

## 6、 自动配置原理

### 6.1 源码

```java
public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware, ResourceLoaderAware, BeanFactoryAware, EnvironmentAware, Ordered {
    private static final AutoConfigurationImportSelector.AutoConfigurationEntry EMPTY_ENTRY = new AutoConfigurationImportSelector.AutoConfigurationEntry();
    private static final String[] NO_IMPORTS = new String[0];
    private static final Log logger = LogFactory.getLog(AutoConfigurationImportSelector.class);
    private static final String PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE = "spring.autoconfigure.exclude";
    private ConfigurableListableBeanFactory beanFactory;
    private Environment environment;
    private ClassLoader beanClassLoader;
    private ResourceLoader resourceLoader;

    public AutoConfigurationImportSelector() {
    }

    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        if (!this.isEnabled(annotationMetadata)) {
            return NO_IMPORTS;
        } else {
            AutoConfigurationMetadata autoConfigurationMetadata = AutoConfigurationMetadataLoader.loadMetadata(this.beanClassLoader);
            AutoConfigurationImportSelector.AutoConfigurationEntry autoConfigurationEntry = this.getAutoConfigurationEntry(autoConfigurationMetadata, annotationMetadata);
            return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
        }
    }

    protected AutoConfigurationImportSelector.AutoConfigurationEntry getAutoConfigurationEntry(AutoConfigurationMetadata autoConfigurationMetadata, AnnotationMetadata annotationMetadata) {
        if (!this.isEnabled(annotationMetadata)) {
            return EMPTY_ENTRY;
        } else {
            AnnotationAttributes attributes = this.getAttributes(annotationMetadata);
            List<String> configurations = this.getCandidateConfigurations(annotationMetadata, attributes);
            configurations = this.removeDuplicates(configurations);
            Set<String> exclusions = this.getExclusions(annotationMetadata, attributes);
            this.checkExcludedClasses(configurations, exclusions);
            configurations.removeAll(exclusions);
            configurations = this.filter(configurations, autoConfigurationMetadata);
            this.fireAutoConfigurationImportEvents(configurations, exclusions);
            return new AutoConfigurationImportSelector.AutoConfigurationEntry(configurations, exclusions);
        }
    }
```

获取获选配置数组

```java
List<String> configurations = this.getCandidateConfigurations(annotationMetadata, attributes);
return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
```

通过SpringFactoriesLoader.loadFactoryNames(this.getSpringFactoriesLoaderFactoryClass(), this.getBeanClassLoader());加载EnableAutoConfiguration.class;

```java
protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
    List<String> configurations = SpringFactoriesLoader.loadFactoryNames(this.getSpringFactoriesLoaderFactoryClass(), this.getBeanClassLoader());
    Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/spring.factories. If you are using a custom packaging, make sure that file is correct.");
    return configurations;
}

protected Class<?> getSpringFactoriesLoaderFactoryClass() {
    return EnableAutoConfiguration.class;
}
```

Enumeration<URL> urls = classLoader != null ? classLoader.getResources("META-INF/spring.factories") : ClassLoader.getSystemResources("META-INF/spring.factories");

将"META-INF/spring.factories"目录下的配置信息读取

```java
public static List<String> loadFactoryNames(Class<?> factoryType, @Nullable ClassLoader classLoader) {
    String factoryTypeName = factoryType.getName();
    return (List)loadSpringFactories(classLoader).getOrDefault(factoryTypeName, Collections.emptyList());
}

private static Map<String, List<String>> loadSpringFactories(@Nullable ClassLoader classLoader) {
    MultiValueMap<String, String> result = (MultiValueMap)cache.get(classLoader);
    if (result != null) {
        return result;
    } else {
        try {
            Enumeration<URL> urls = classLoader != null ? classLoader.getResources("META-INF/spring.factories") : ClassLoader.getSystemResources("META-INF/spring.factories");
            LinkedMultiValueMap result = new LinkedMultiValueMap();

            while(urls.hasMoreElements()) {
                URL url = (URL)urls.nextElement();
                UrlResource resource = new UrlResource(url);
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                Iterator var6 = properties.entrySet().iterator();

                while(var6.hasNext()) {
                    Entry<?, ?> entry = (Entry)var6.next();
                    String factoryTypeName = ((String)entry.getKey()).trim();
                    String[] var9 = StringUtils.commaDelimitedListToStringArray((String)entry.getValue());
                    int var10 = var9.length;

                    for(int var11 = 0; var11 < var10; ++var11) {
                        String factoryImplementationName = var9[var11];
                        result.add(factoryTypeName, factoryImplementationName.trim());
                    }
                }
            }

            cache.put(classLoader, result);
            return result;
        } catch (IOException var13) {
            throw new IllegalArgumentException("Unable to load factories from location [META-INF/spring.factories]", var13);
        }
    }
}
```

"META-INF/spring.factories"目录下自动配置类

```java
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,\
org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration,\
org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration,\
org.springframework.boot.autoconfigure.cloud.CloudServiceConnectorsAutoConfiguration,\
org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration,\
org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration,\
org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration,\
org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration,\
org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.couchbase.CouchbaseRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveRestClientAutoConfiguration,\
org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.ldap.LdapRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration,\
org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration,\
org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,\
org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration,\
org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration,\
org.springframework.boot.autoconfigure.elasticsearch.jest.JestAutoConfiguration,\
org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration,\
org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,\
org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration,\
org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration,\
org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration,\
org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration,\
org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration,\
org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration,\
org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration,\
org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration,\
org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration,\
org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration,\
org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration,\
org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration,\
org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration,\
org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration,\
org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration,\
org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration,\
org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration,\
org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration,\
org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration,\
org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration,\
org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration,\
org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration,\
org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration,\
org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration,\
org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration,\
org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,\
org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration,\
org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration,\
org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,\
org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration,\
org.springframework.boot.autoconfigure.rsocket.RSocketMessagingAutoConfiguration,\
org.springframework.boot.autoconfigure.rsocket.RSocketRequesterAutoConfiguration,\
org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration,\
org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,\
org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration,\
org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration,\
org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration,\
org.springframework.boot.autoconfigure.security.rsocket.RSocketSecurityAutoConfiguration,\
org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration,\
org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration,\
org.springframework.boot.autoconfigure.session.SessionAutoConfiguration,\
org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration,\
org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration,\
org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration,\
org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration,\
org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration,\
org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration,\
org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration,\
org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration,\
org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration,\
org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration,\
org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration,\
org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration,\
org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration,\
org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration,\
org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration,\
org.springframework.boot.autoconfigure.websocket.reactive.WebSocketReactiveAutoConfiguration,\
org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration,\
org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration,\
org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration,\
org.springframework.boot.autoconfigure.webservices.client.WebServiceTemplateAutoConfiguration
```

### 6.2 举例

- RedisAutoConfiguration

```java
@Configuration(
    proxyBeanMethods = false
)	// 表示是配置类
@ConditionalOnClass({RedisOperations.class})	// 判断是否有RedisOperations类
@EnableConfigurationProperties({RedisProperties.class})	// 开启指定类的配置属性功能，将指定类的属性与配置文件中的值绑定
@Import({LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class})	// 将两个类添加进容器
public class RedisAutoConfiguration {
    public RedisAutoConfiguration() {
    }

    @Bean	// 将RedisTemplate添加到IOC容器中
    @ConditionalOnMissingBean(
        name = {"redisTemplate"}
    )	// IOC容器中不存在redisTemplate，才会将RedisTemplate<Object, Object>添加进容器中
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        RedisTemplate<Object, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean	// 将RedisTemplate添加到IOC容器中
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) throws UnknownHostException {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
```

```java
@ConfigurationProperties(
    prefix = "spring.redis"
)
public class RedisProperties {
    private int database = 0;
    private String url;
    private String host = "localhost";
    private String password;
    private int port = 6379;
    private boolean ssl;
    private Duration timeout;
    private String clientName;
    private RedisProperties.Sentinel sentinel;
    private RedisProperties.Cluster cluster;
    private final RedisProperties.Jedis jedis = new RedisProperties.Jedis();
    private final RedisProperties.Lettuce lettuce = new RedisProperties.Lettuce();
```

将配置文件中前缀为"spring.redis"的值与RedisProperties类中的属性进行绑定

```java
@Configuration(
    proxyBeanMethods = false
)
@ConditionalOnClass({GenericObjectPool.class, JedisConnection.class, Jedis.class})
class JedisConnectionConfiguration extends RedisConnectionConfiguration {
    JedisConnectionConfiguration(RedisProperties properties, ObjectProvider<RedisSentinelConfiguration> sentinelConfiguration, ObjectProvider<RedisClusterConfiguration> clusterConfiguration) {
        super(properties, sentinelConfiguration, clusterConfiguration);
    }
```

```java
abstract class RedisConnectionConfiguration {
    private final RedisProperties properties;
    private final RedisSentinelConfiguration sentinelConfiguration;
    private final RedisClusterConfiguration clusterConfiguration;

    protected RedisConnectionConfiguration(RedisProperties properties, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider, ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {
        this.properties = properties;
        this.sentinelConfiguration = (RedisSentinelConfiguration)sentinelConfigurationProvider.getIfAvailable();
        this.clusterConfiguration = (RedisClusterConfiguration)clusterConfigurationProvider.getIfAvailable();
    }

    protected final RedisStandaloneConfiguration getStandaloneConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(this.properties.getUrl())) {
            RedisConnectionConfiguration.ConnectionInfo connectionInfo = this.parseUrl(this.properties.getUrl());
            config.setHostName(connectionInfo.getHostName());
            config.setPort(connectionInfo.getPort());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        } else {
            config.setHostName(this.properties.getHost());
            config.setPort(this.properties.getPort());
            config.setPassword(RedisPassword.of(this.properties.getPassword()));
        }

        config.setDatabase(this.properties.getDatabase());
        return config;
    }
```

通过

**config.setHostName(this.properties.getHost());**
**config.setPort(this.properties.getPort());**
**config.setPassword(RedisPassword.of(this.properties.getPassword()));**

等方法将RedisProperties中绑定的值进行配置

**==因此，RedisProperties类中存在的属性都可以在配置文件中自己配置==**

在application.properties文件中**debug=true**使用开启debug，控制台会打印开启和未开启自动配置的信息

```java

============================
CONDITIONS EVALUATION REPORT
============================

开启的配置
Positive matches:
-----------------

   AopAutoConfiguration matched:
      - @ConditionalOnProperty (spring.aop.auto=true) matched (OnPropertyCondition)

   AopAutoConfiguration.ClassProxyingConfiguration matched:
      - @ConditionalOnMissingClass did not find unwanted class 'org.aspectj.weaver.Advice' (OnClassCondition)
      - @ConditionalOnProperty (spring.aop.proxy-target-class=true) matched (OnPropertyCondition)

   DispatcherServletAutoConfiguration matched:
      - @ConditionalOnClass found required class 'org.springframework.web.servlet.DispatcherServlet' (OnClassCondition)
      - found 'session' scope (OnWebApplicationCondition)

未开启的配置
Negative matches:
-----------------

   ActiveMQAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'javax.jms.ConnectionFactory' (OnClassCondition)

   AopAutoConfiguration.AspectJAutoProxyingConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'org.aspectj.weaver.Advice' (OnClassCondition)

   ArtemisAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'javax.jms.ConnectionFactory' (OnClassCondition)

```

# 二、 spring-boot-02-log

```java
@SpringBootTest
class SpringBoot02LogApplicationTests {

    // 日志级别由低到高：trace<debug<info<warn<error
    @Test
    void contextLoads() {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.trace("trace日志...");
        logger.debug("debug日志...");
        // springboot默认日志级别
        logger.info("info日志...");
        logger.warn("warn日志...");
        logger.error("error日志...");
    }

}
```

可以再application.properties中配置指定包下的日志级别

```properties
logging.level.com.dkt=debug
```

%d{yyyy/MM/dd-HH:mm:ss}——日志输出时间

%10thread——使用10个字符靠右对齐输出日志的进程名字

%-5level——日志级别，并且使用5个字符靠左对齐

%logger——日志输出者的名字

%msg——日志消息

%n——平台的换行符

**==logging.file.path和logging.file.name相比，同时配置时logging.file.name生效，级别更高==**

```properties
#在当前项目磁盘根目录创建spring-log文件夹，使用spring.log作为默认文件
#也可以指定盘符路径
#logging.file.path=/spring-log
#不指定路径，在当前项目根目录下生成hhhhh.log
#可以指定路径
logging.file.name=hhhhh.log
logging.pattern.console=%d{yyyy/MM/dd-HH:mm:ss}--->[%10thread]--->%-5level--->%logger--->%msg%n
logging.pattern.file=%d{yyyy/MM/dd-HH:mm}<--->[%thread]<--->%-5level<--->%logger<--->%msg%n
```

# 三、 spring-boot-03-restfulcrud

## 1、 静态资源映射规则

- **WebMvcAutoConfiguration类**

```java
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
    } else {
        Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
        CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
        if (!registry.hasMappingForPattern("/webjars/**")) {
            this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{"/webjars/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"}).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }

        String staticPathPattern = this.mvcProperties.getStaticPathPattern();
        if (!registry.hasMappingForPattern(staticPathPattern)) {
            this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{staticPathPattern}).addResourceLocations(WebMvcAutoConfiguration.getResourceLocations(this.resourceProperties.getStaticLocations())).setCachePeriod(this.getSeconds(cachePeriod)).setCacheControl(cacheControl));
        }

    }
}
```

### 1.1 访问webjars资源

https://www.webjars.org/导入jar资源的maven坐标

this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{"/webjars/**"}).addResourceLocations(new String[]{"classpath:/META-INF/resources/webjars/"})

所有"/webjars/**"资源都去jar的"classpath:/META-INF/resources/webjars/"路径下找

**==举例==**：http://localhost:8080/webjars/jquery/3.5.0/jquery.js

### 1.2 访问“/**”资源

this.customizeResourceHandlerRegistration(registry.addResourceHandler(new String[]{staticPathPattern}).addResourceLocations(WebMvcAutoConfiguration.getResourceLocations(this.resourceProperties.getStaticLocations()))

```java
@ConfigurationProperties(
    prefix = "spring.mvc"
)
public class WebMvcProperties {
    private Format messageCodesResolverFormat;
    private Locale locale;
    private WebMvcProperties.LocaleResolver localeResolver;
    private String dateFormat;
    private boolean dispatchTraceRequest;
    private boolean dispatchOptionsRequest;
    private boolean ignoreDefaultModelOnRedirect;
    private boolean publishRequestHandledEvents;
    private boolean throwExceptionIfNoHandlerFound;
    private boolean logResolvedException;
    private String staticPathPattern;
    private final WebMvcProperties.Async async;
    private final WebMvcProperties.Servlet servlet;
    private final WebMvcProperties.View view;
    private final WebMvcProperties.Contentnegotiation contentnegotiation;
    private final WebMvcProperties.Pathmatch pathmatch;

    public WebMvcProperties() {
        this.localeResolver = WebMvcProperties.LocaleResolver.ACCEPT_HEADER;
        this.dispatchTraceRequest = false;
        this.dispatchOptionsRequest = true;
        this.ignoreDefaultModelOnRedirect = true;
        this.publishRequestHandledEvents = true;
        this.throwExceptionIfNoHandlerFound = false;
        this.logResolvedException = false;
        this.staticPathPattern = "/**";
        this.async = new WebMvcProperties.Async();
        this.servlet = new WebMvcProperties.Servlet();
        this.view = new WebMvcProperties.View();
        this.contentnegotiation = new WebMvcProperties.Contentnegotiation();
        this.pathmatch = new WebMvcProperties.Pathmatch();
    }
```

this.staticPathPattern = "/**";

```java
@ConfigurationProperties(
    prefix = "spring.resources",
    ignoreUnknownFields = false
)
public class ResourceProperties {
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = new String[]{"classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"};
    private String[] staticLocations;
    private boolean addMappings;
    private final ResourceProperties.Chain chain;
    private final ResourceProperties.Cache cache;

    public ResourceProperties() {
        this.staticLocations = CLASSPATH_RESOURCE_LOCATIONS;
        this.addMappings = true;
        this.chain = new ResourceProperties.Chain();
        this.cache = new ResourceProperties.Cache();
    }
```

{"classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"};

所有的"/**"访问都去当前项目的"classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"这==4个静态资源路径==找

==**举例**==：http://localhost:8080/A/jquery-3.4.1.js

### 1.3 访问首页index.html

==4个静态资源路径==下的index.html可以被"/**"映射

==**举例**==：http://localhost:8080/

### 1.4 favicon.ico图标

springboot2.2取消默认ico，只需在==4个静态资源路径==之一下放favicon.ico即可

更改https://github.com/spring-projects/spring-boot/issues/17925

### 1.5 自定义静态资源路径

```java
@ConfigurationProperties(
    prefix = "spring.resources",
    ignoreUnknownFields = false
)
public class ResourceProperties {
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = new String[]{"classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"};
    private String[] staticLocations;
    private boolean addMappings;
    private final ResourceProperties.Chain chain;
    private final ResourceProperties.Cache cache;

    public ResourceProperties() {
        this.staticLocations = CLASSPATH_RESOURCE_LOCATIONS;
        this.addMappings = true;
        this.chain = new ResourceProperties.Chain();
        this.cache = new ResourceProperties.Cache();
    }
```

```properties
spring.resources.static-locations=classpath:/he/,classpath:/ha/
#数组用逗号分隔
```

## 2、 自定义WebMvcConfigurer

==**配置步骤：**==

1. @Configuration
2. implements WebMvcConfigurer

```java
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("lgtest");
    }
}
```

- 自定义添加**视图控制器和拦截器**

```java
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/dashboard").setViewName("dashboard");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MyInterceptor()).addPathPatterns("/**").excludePathPatterns("/", "/index.html", "/login", "/asserts/**");
    }
}
```

```java
public class MyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object user = request.getSession().getAttribute("user");
        if (user == null) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return false;
        } else {
            System.out.println("MyInterceptor:" + user);
            return true;
        }
    }
}
```

## 3、 PUT、DELETE请求

```java
public class HiddenHttpMethodFilter extends OncePerRequestFilter {
    private static final List<String> ALLOWED_METHODS;
    public static final String DEFAULT_METHOD_PARAM = "_method";
    private String methodParam = "_method";

    public HiddenHttpMethodFilter() {
    }

    public void setMethodParam(String methodParam) {
        Assert.hasText(methodParam, "'methodParam' must not be empty");
        this.methodParam = methodParam;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest requestToUse = request;
        if ("POST".equals(request.getMethod()) && request.getAttribute("javax.servlet.error.exception") == null) {
            String paramValue = request.getParameter(this.methodParam);
            if (StringUtils.hasLength(paramValue)) {
                String method = paramValue.toUpperCase(Locale.ENGLISH);
                if (ALLOWED_METHODS.contains(method)) {
                    requestToUse = new HiddenHttpMethodFilter.HttpMethodRequestWrapper(request, method);
                }
            }
        }

        filterChain.doFilter((ServletRequest)requestToUse, response);
    }
```

```java
@Bean
@ConditionalOnMissingBean({HiddenHttpMethodFilter.class})
@ConditionalOnProperty(
    prefix = "spring.mvc.hiddenmethod.filter",
    name = {"enabled"},
    matchIfMissing = false
)
public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
    return new OrderedHiddenHttpMethodFilter();
}
```

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional({OnPropertyCondition.class})
public @interface ConditionalOnProperty {
    String[] value() default {};

    String prefix() default "";

    String[] name() default {};

    String havingValue() default "";

    boolean matchIfMissing() default false;
}
```

1. ```java
   spring.mvc.hiddenmethod.filter.enabled=true
   ```

   ==matchIfMissing默认是false==，如果不在配置文件中配置property，会失败

   ==matchIfMissing如果是true==，即使不配置property，也会成功

2. ```html
   <input type="hidden" name="_method" value="PUT"/>
   <input type="hidden" name="_method" value="DELETE"/>
   ```

## 4、 自定错误页面

详见：**==ErrorMvcAutoConfiguration==**类中**==4个==**重要方法

1. 有模板引擎：会去templates/error/404.html或4xx.html或5xx.html加载对应status的页面

   页面能获取的信息；

   ​				timestamp：时间戳

   ​				status：状态码

   ​				error：错误提示

   ​				exception：异常对象

   ​				message：异常消息

   ​				errors：JSR303数据校验的错误都在这里

   ```html
   <h1>5xx</h1>
   <p>timestamp:[[${timestamp}]]</p>
   <p>status:[[${status}]]</p>
   <p>error:[[${error}]]</p>
   <p>exception:[[${exception}]]</p>
   <p>message:[[${message}]]</p>
   <p>errors:[[${errors}]]</p>
   ```

2. 没有模板引擎：会去静态资源路径下找

   - **==DefaultErrorViewResolver==**

   ```java
   public class DefaultErrorViewResolver implements ErrorViewResolver, Ordered {
   
   public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
       ModelAndView modelAndView = this.resolve(String.valueOf(status.value()), model);
       // 比如发生404却没有找到对应的error/404或error/404.html，则返回null
       // 在通过SERIES_VIEWS寻找对应的4xx或5xx页面，返回对应的错误页面
       if (modelAndView == null && SERIES_VIEWS.containsKey(status.series())) {
           modelAndView = this.resolve((String)SERIES_VIEWS.get(status.series()), model);
       }
   
       return modelAndView;
   }
   
   private ModelAndView resolve(String viewName, Map<String, Object> model) {
       // 在error/下找错误页面
       String errorViewName = "error/" + viewName;
       TemplateAvailabilityProvider provider = this.templateAvailabilityProviders.getProvider(errorViewName, this.applicationContext);
       // 有模板引擎直接返回ModelAndView，否则resolveResource解析资源
       return provider != null ? new ModelAndView(errorViewName, model) : this.resolveResource(errorViewName, model);
   }
   
   private ModelAndView resolveResource(String viewName, Map<String, Object> model) {
       String[] var3 = this.resourceProperties.getStaticLocations();
       int var4 = var3.length;
   
       for(int var5 = 0; var5 < var4; ++var5) {
           String location = var3[var5];
   
           try {
               Resource resource = this.applicationContext.getResource(location);
               resource = resource.createRelative(viewName + ".html");
               // 比如error/404.html存在，返回ModelAndView，否则返回null
               if (resource.exists()) {
                   return new ModelAndView(new DefaultErrorViewResolver.HtmlResourceView(resource), model);
               }
           } catch (Exception var8) {
           }
       }
   
       return null;
   }
       
       
       static {
           Map<Series, String> views = new EnumMap(Series.class);
           views.put(Series.CLIENT_ERROR, "4xx");
           views.put(Series.SERVER_ERROR, "5xx");
           SERIES_VIEWS = Collections.unmodifiableMap(views);
       }
   
   ```

3. 以上都没有，则会生成默认的空白错误页

- **==BasicErrorController&DefaultErrorAttributes==**

```java
@Controller
// 处理/error请求
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class BasicErrorController extends AbstractErrorController {
    // 浏览器发送的text/html请求，返回modelAndView视图页
    @RequestMapping(
        produces = {"text/html"}
    )
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = this.getStatus(request);
        Map<String, Object> model = Collections.unmodifiableMap(this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.TEXT_HTML)));
        response.setStatus(status.value());
        // 通过获取status和model等信息进行resolveErrorView解析错误视图页
        ModelAndView modelAndView = this.resolveErrorView(request, response, status, model);
        // 错误视图页不存在，则会从容器中找叫error的bean
        return modelAndView != null ? modelAndView : new ModelAndView("error", model);
    }

    // postman发送的返回json数据
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity(status);
        } else {
            Map<String, Object> body = this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.ALL));
            return new ResponseEntity(body, status);
        }
    }

```

```java
@Conditional({ErrorMvcAutoConfiguration.ErrorTemplateMissingCondition.class})
protected static class WhitelabelErrorViewConfiguration {
    private final ErrorMvcAutoConfiguration.StaticView defaultErrorView = new ErrorMvcAutoConfiguration.StaticView();

    protected WhitelabelErrorViewConfiguration() {
    }
	// 叫"error"的bean，这个bean是StaticView
    @Bean(
        name = {"error"}
    )
    @ConditionalOnMissingBean(
        name = {"error"}
    )
    public View defaultErrorView() {
        return this.defaultErrorView;
    }

    @Bean
    @ConditionalOnMissingBean
    public BeanNameViewResolver beanNameViewResolver() {
        BeanNameViewResolver resolver = new BeanNameViewResolver();
        resolver.setOrder(2147483637);
        return resolver;
    }
}
```

```java
private static class StaticView implements View {
    private static final MediaType TEXT_HTML_UTF8;
    private static final Log logger;

    private StaticView() {
    }
	// render渲染出空白默认错误页
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
```

获取status和model路线

```java
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class BasicErrorController extends AbstractErrorController {
    @RequestMapping(
        produces = {"text/html"}
    )
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = this.getStatus(request);
        Map<String, Object> model = Collections.unmodifiableMap(this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.TEXT_HTML)));
        response.setStatus(status.value());
        ModelAndView modelAndView = this.resolveErrorView(request, response, status, model);
        return modelAndView != null ? modelAndView : new ModelAndView("error", model);
    }




public abstract class AbstractErrorController implements ErrorController {
    protected Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        WebRequest webRequest = new ServletWebRequest(request);
        return this.errorAttributes.getErrorAttributes(webRequest, includeStackTrace);
    }

    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception var4) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }



    
    

public interface ErrorAttributes {
    Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace);

    Throwable getError(WebRequest webRequest);
}





@Order(-2147483648)
public class DefaultErrorAttributes implements ErrorAttributes, HandlerExceptionResolver, Ordered {
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        // 错误属性状态码、error详细信息、路径
        Map<String, Object> errorAttributes = new LinkedHashMap();
        errorAttributes.put("timestamp", new Date());
        this.addStatus(errorAttributes, webRequest);
        this.addErrorDetails(errorAttributes, webRequest, includeStackTrace);
        this.addPath(errorAttributes, webRequest);
        return errorAttributes;
    }

```



- **==ErrorMvcAutoConfiguration.ErrorPageCustomizer==**

```java
@Bean
public ErrorMvcAutoConfiguration.ErrorPageCustomizer errorPageCustomizer(DispatcherServletPath dispatcherServletPath) {
    return new ErrorMvcAutoConfiguration.ErrorPageCustomizer(this.serverProperties, dispatcherServletPath);
}


private static class ErrorPageCustomizer implements ErrorPageRegistrar, Ordered {
        private final ServerProperties properties;
        private final DispatcherServletPath dispatcherServletPath;

        protected ErrorPageCustomizer(ServerProperties properties, DispatcherServletPath dispatcherServletPath) {
            this.properties = properties;
            this.dispatcherServletPath = dispatcherServletPath;
        }

        public void registerErrorPages(ErrorPageRegistry errorPageRegistry) {
        // 获取配置页所在路径"/error"路径，发送/error/请求
            ErrorPage errorPage = new ErrorPage(this.dispatcherServletPath.getRelativePath(this.properties.getError().getPath()));
            errorPageRegistry.addErrorPages(new ErrorPage[]{errorPage});
        }

        public int getOrder() {
            return 0;
        }
}



public class ErrorProperties {
    @Value("${error.path:/error}")
    private String path = "/error";
    private boolean includeException;
    private ErrorProperties.IncludeStacktrace includeStacktrace;
    private final ErrorProperties.Whitelabel whitelabel;

    public ErrorProperties() {
        this.includeStacktrace = ErrorProperties.IncludeStacktrace.NEVER;
        this.whitelabel = new ErrorProperties.Whitelabel();
    }

    public String getPath() {
        return this.path;
    }


```

## 5、 自定义异常json数据

```java
@Controller
public class HelloController {

    @ResponseBody
    @RequestMapping(value = "/hello")
    public String hello(@RequestParam(value = "user") String user) {
        if ("123".equals(user)) {
            throw new UserNotExistException();
        }
        return "Hello world";
    }

}
```

自定义异常类

```java
public class UserNotExistException extends RuntimeException {

    public UserNotExistException() {
        super("用户不存在");
    }

}
```

自定义异常处理器，返回map的key：value，json数据

```java
@ControllerAdvice
public class MyExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = UserNotExistException.class)
    public Map<String, Object> myExceptionHandler(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", "user.not.exist.code");
        map.put("message", e.getMessage());
        return map;
    }

}
```

## 6、 自适应view和json页

```java
@Controller
// 处理/error请求
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class BasicErrorController extends AbstractErrorController {
    
    @RequestMapping(
        produces = {"text/html"}
    )
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        // 获取状态码
        HttpStatus status = this.getStatus(request);
        Map<String, Object> model = Collections.unmodifiableMap(this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.TEXT_HTML)));
        response.setStatus(status.value());
        // 根据状态码解析视图
        ModelAndView modelAndView = this.resolveErrorView(request, response, status, model);
        return modelAndView != null ? modelAndView : new ModelAndView("error", model);
    }

    // json数据与html同理
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity(status);
        } else {
            Map<String, Object> body = this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.ALL));
            return new ResponseEntity(body, status);
        }
    }

```

```java
public abstract class AbstractErrorController implements ErrorController {
    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception var4) {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }
    }

```

由于status从request域中获取，所以在request域中添加"javax.servlet.error.status_code"

由于BasicErrorController类处理/error请求，所以"forward:/error"转发此请求

```java
@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = UserNotExistException.class)
    public String myExceptionHandler(Exception e, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        request.setAttribute("javax.servlet.error.status_code", 500);
        map.put("code", "user.not.exist.code");
        map.put("message", e.getMessage());
        return "forward:/error";
    }

}
```

## 7、 自适应view和json页，同时可以添加自定义数据

最终都是通过getErrorAttributes这个方法获取Attributes

**两个条件：**DefaultErrorAttributes implements ErrorAttributes和@ConditionalOnMissingBean(
    value = {ErrorAttributes.class},
    search = SearchStrategy.CURRENT
)

只要重写DefaultErrorAttributes的getErrorAttributes方法即可添加自定义数据

```java
@Order(-2147483648)
public class DefaultErrorAttributes implements ErrorAttributes, HandlerExceptionResolver, Ordered {
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = new LinkedHashMap();
        errorAttributes.put("timestamp", new Date());
        this.addStatus(errorAttributes, webRequest);
        this.addErrorDetails(errorAttributes, webRequest, includeStackTrace);
        this.addPath(errorAttributes, webRequest);
        return errorAttributes;
    }

```

```java
@Configuration(
    proxyBeanMethods = false
)
@ConditionalOnWebApplication(
    type = Type.SERVLET
)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@AutoConfigureBefore({WebMvcAutoConfiguration.class})
@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class, WebMvcProperties.class})
public class ErrorMvcAutoConfiguration {

@Bean
@ConditionalOnMissingBean(
    value = {ErrorAttributes.class},
    search = SearchStrategy.CURRENT
)
public DefaultErrorAttributes errorAttributes() {
    return new DefaultErrorAttributes(this.serverProperties.getError().isIncludeException());
}
```

```java
@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(value = UserNotExistException.class)
    public String myExceptionHandler(Exception e, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        request.setAttribute("javax.servlet.error.status_code", 500);
        map.put("code", "user.not.exist.code");
        map.put("message", e.getMessage());
        // 将自定义数据添加进request域中
        request.setAttribute("msg", map);
        return "forward:/error";
    }

}
```

```java
@Component
public class MyErrorAttribute extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> map = super.getErrorAttributes(webRequest, includeStackTrace);
        map.put("my_message", "DKT");
        // 从request域中获取自定义数据
        // 0表示request域，1表示session域
        Object msg = webRequest.getAttribute("msg", 0);
        map.put("msg", msg);
        return map;
    }
}
```

```java
public interface RequestAttributes {
    int SCOPE_REQUEST = 0;
    int SCOPE_SESSION = 1;
	@Nullable
	Object getAttribute(String name, int scope);
```

**==总结错误页生成过程：==**主要分析ErrorMvcAutoConfiguration类中生成错误页的**==逻辑==**

1、通过此方法发送/error请求

```java
@Bean
public ErrorMvcAutoConfiguration.ErrorPageCustomizer errorPageCustomizer(DispatcherServletPath dispatcherServletPath) {
```

2、此方法接受/error请求，并请求获取Attribute，最后解析生成resources目录下自定义的view

```java
@Bean
@ConditionalOnMissingBean(
    value = {ErrorController.class},
    search = SearchStrategy.CURRENT
)
public BasicErrorController basicErrorController(ErrorAttributes errorAttributes, ObjectProvider<ErrorViewResolver> errorViewResolvers) {
```

3、通过此方法获取Attribute

```java
@Bean
@ConditionalOnMissingBean(
    value = {ErrorAttributes.class},
    search = SearchStrategy.CURRENT
)
public DefaultErrorAttributes errorAttributes() {
```

4、通过此方法解析生成view

```java
@Bean
@ConditionalOnBean({DispatcherServlet.class})
@ConditionalOnMissingBean({ErrorViewResolver.class})
DefaultErrorViewResolver conventionErrorViewResolver() {
```

5、若解析view失败，会找name=“error”的Bean，最终会渲染生成springboot默认的错误页面

```java
@Configuration(
    proxyBeanMethods = false
)
@ConditionalOnProperty(
    prefix = "server.error.whitelabel",
    name = {"enabled"},
    matchIfMissing = true
)
@Conditional({ErrorMvcAutoConfiguration.ErrorTemplateMissingCondition.class})
protected static class WhitelabelErrorViewConfiguration {
    private final ErrorMvcAutoConfiguration.StaticView defaultErrorView = new ErrorMvcAutoConfiguration.StaticView();

    protected WhitelabelErrorViewConfiguration() {
    }

    @Bean(
        name = {"error"}
    )
    @ConditionalOnMissingBean(
        name = {"error"}
    )
    public View defaultErrorView() {
        return this.defaultErrorView;
    }

    @Bean
    @ConditionalOnMissingBean
    public BeanNameViewResolver beanNameViewResolver() {
        BeanNameViewResolver resolver = new BeanNameViewResolver();
        resolver.setOrder(2147483637);
        return resolver;
    }
}
```