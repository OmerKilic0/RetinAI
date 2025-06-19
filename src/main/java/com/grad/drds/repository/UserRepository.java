package com.grad.drds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grad.drds.entity.User_;

@Repository
public interface UserRepository extends JpaRepository<User_, Integer>{
	User_ findById(int id);
	User_ findByEmail(String email);
	boolean existsByEmail(String email);
	List<User_> findAll();
}