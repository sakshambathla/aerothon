package com.airbus.hackathon.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.beans.PropertyVetoException;

@Configuration
@Component
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = {"com.airbus.hackathon"})
public class AppConfiguration {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${httpClient.connection.pool.size:200}")
    private String poolMaxTotal;

    @Value("${httpClientFactory.connection.timeout:50000}")
    private String connectionTimeOut;

    @Value("${httpClientFactory.read.timeout:300}")
    private String readTimeOut;

    @Value("${threadpool.keepAliveSeconds:5}")
    private String keepAliveSeconds;

    @Value("${threadpool.awaitTerminationSeconds:30}")
    private String awaitTerminationSeconds;

    private static final Logger log = LoggerFactory.getLogger(AppConfiguration.class);

    @Bean
    public RestTemplate restTemplate() {
        return restTemplate(Integer.parseInt(connectionTimeOut), Integer.parseInt(readTimeOut), Integer.parseInt(poolMaxTotal));
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public ComboPooledDataSource primaryDataSource() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("org.postgresql.Driver");
        } catch (PropertyVetoException pve) {
            log.error("Cannot load datasource driver org.postgresql.Driver : " + pve.getMessage(), pve);
        }
        dataSource.setJdbcUrl(datasourceUrl);
        dataSource.setUser(datasourceUsername);
        dataSource.setPassword(datasourcePassword);
        dataSource.setInitialPoolSize(Integer.parseInt(initialPoolSize));
        dataSource.setMinPoolSize(Integer.parseInt(minPoolSize));
        dataSource.setMaxPoolSize(Integer.parseInt(maxPoolSize));
        dataSource.setMaxIdleTime(Integer.parseInt(maxIdleTime));
        dataSource.setIdleConnectionTestPeriod(5);
        dataSource.setPreferredTestQuery("SELECT 1");
        return dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(primaryDataSource()).packages("com.oyo.prepaid").build();
    }

    public HttpClient httpClient(int noOfConnections) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(noOfConnections);
        return HttpClientBuilder.create().setConnectionManager(connectionManager).build();
    }

    public ClientHttpRequestFactory httpRequestFactory(int connectionTimeout, int readTimeout, int maxConnections) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient(maxConnections));
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }

    public RestTemplate restTemplate(int connectionTimeout, int readTimeout, int maxConnections) {
        RestTemplate template = new RestTemplate(httpRequestFactory(connectionTimeout, readTimeout, maxConnections));
        List<HttpMessageConverter<?>> messageConverters = template.getMessageConverters();
        messageConverters.add(new FormHttpMessageConverter());
        template.setMessageConverters(messageConverters);
        return template;
    }

    @Override
    public Executor getAsyncExecutor() {
        int availProcessorCount = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(availProcessorCount);
        executor.setMaxPoolSize(2 * availProcessorCount);
        executor.setKeepAliveSeconds(Integer.parseInt(keepAliveSeconds));
        executor.setAwaitTerminationSeconds(Integer.parseInt(awaitTerminationSeconds));
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setQueueCapacity(availProcessorCount);
        executor.setThreadNamePrefix("prepaid-thread-");
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

}
