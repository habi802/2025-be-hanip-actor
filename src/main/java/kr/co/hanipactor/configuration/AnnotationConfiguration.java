package kr.co.hanipactor.configuration;

/*
    @EnableJpaAuditing 애노테이션이 GreenGramApplication에 위치하면 모든 테스트 때 항상 JPA 관련 빈을 필요로하기 때문에
    TDD Mybatis Slice Test 때 문제가 발생한다. 그래서 분리한다.

    Repository Test 시 주의점
    Configuration으로 Auditing 설정을 분리했다면, Repository 테스트 시에 @DataJpaTest와 함께 @Import(JpaAuditingConfiguration.class) 어노테이션을 붙여주어야한다.
    @Import(JpaAuditingConfiguration.class) 어노테이션이 없다면, JpaAuditingConfiguration 파일이 등록되지 않기 때문에,
    테스트시 createdAt이나 updatedAt에 등록, 수정 시간이 아닌 null 값이 들어갈 수 있다.
 */

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing //auditing 기능 활성화
@EnableDiscoveryClient
@ConfigurationPropertiesScan
public class AnnotationConfiguration {}
