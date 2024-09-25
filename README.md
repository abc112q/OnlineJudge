注意一定要**启动code-sandbox**的项目才能判题 ，忘记启动这个项目耽误了半天

记得**启动nacos，redis，rabbitmq**
>naocs 启动：startup.cmd -m standalone 
> \n
> rabbitmq客户端启动：localhost:15672

判题逻辑其实很简单 就像leetcode一样 inputList输入用例就是系统自动的测试用例，然后我们将这个输入传给用户自己写的代码，得到用户输出
系统的output其实就是预期输出，最终判断一致的方法是将用户输出和系统输出比对，如果相同则判题通过

两数之和测试代码
```java
public class Main {
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        System.out.println((a+b));
    }
}
```



**完善点：**

死信队列:报错可能是因为尝试定义的队列与RabbitMQ中现有的队列属性不匹配导致的，手动删除之前创建的队列即可

统计用户通过率

redisson对提交接口(dosubmit)限流 限制用户连续点击
>使用redisson时记得引入依赖，和redisson的配置文件，否则redis连接不上
 ```java
@Data
@ConfigurationProperties(prefix = "spring.redis")
@Configuration
public class RedissonConfig {
    private Integer database;
    private String host;
    private Integer port;
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(database)
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
```

使用jwt做同意权限校验(配置文件、jwt工具类、登陆后生成token、loginuservo中存放token)
>如果使用jwt做权限校验，userfeignClient中的getLoginUser函数就不能通过session来获取用户了，因为这样会跟jwt冲突，
> 需要修改为从jwt的请求头中提取出用户id，并找到用户，从而获取用户登录态



