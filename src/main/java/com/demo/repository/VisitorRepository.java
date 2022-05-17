package com.demo.repository;

import com.demo.entity.Visitor;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface VisitorRepository extends JpaRepository<Visitor, Integer> {

    @NonNull
    Optional<Visitor> findByUsername(@NonNull String username);

    @NonNull
    Optional<Visitor> findByAccessToken(@NonNull String accesstoken);


}
