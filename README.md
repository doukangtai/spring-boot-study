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

## 6、自动配置原理

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

# 二、spring-boot-02-log

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

