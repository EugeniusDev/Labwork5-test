package edu.froliak.labwork5.config;

/*
  @author eugen
  @project labwork5
  @class AuditorAwareImpl
  @version 1.0.0
  @since 4/16/2025 - 13.03
*/

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        //return Optional.of("admin");
        return Optional.of(System.getProperty("user.name"));
    }
}