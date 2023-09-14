package com.alco.searchapp.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories(basePackages = "com.alco.searchapp.repository")
@EnableR2dbcAuditing
@Configuration
public class RepositoryConfiguration {
}
