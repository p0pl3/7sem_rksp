package com.example.pr4;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyRepository extends JpaRepository<MyData, Long> {
    MyData findMyDataById(Long id);
}
