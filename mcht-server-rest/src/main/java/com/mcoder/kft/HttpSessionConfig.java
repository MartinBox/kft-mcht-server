package com.mcoder.kft;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * @author 1194688733@qq.com
 * @Description: TODO
 * @date 2019-01-09 17:43:50
 */
//@EnableSpringHttpSession
@Configuration
@EnableCaching
public class HttpSessionConfig {
/*
    @Bean
    public MapSessionRepository sessionRepository() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setCookieName("JSESSIONID");
        return new MapSessionRepository(new ConcurrentHashMap<>());
    }*/

   /* @Bean
    public <S extends ExpiringSession> SessionRepositoryFilter<? extends ExpiringSession> springSessionRepositoryFilter(SessionRepository<S> sessionRepository, ServletContext servletContext) {
        SessionRepositoryFilter<S> sessionRepositoryFilter = new SessionRepositoryFilter<S>(sessionRepository);
        sessionRepositoryFilter.setServletContext(servletContext);
        return sessionRepositoryFilter;
    }


    @Bean
    public SessionRepository<?> sessionRepository() {
        MapSessionRepository sessionRepository = new MapSessionRepository();
        return sessionRepository;
    }
*/
}
