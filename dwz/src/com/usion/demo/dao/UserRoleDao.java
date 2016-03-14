package com.usion.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.usion.demo.entity.main.UserRole;

/**
 * @author usion
 */

public interface UserRoleDao extends JpaRepository<UserRole, Long> {
	List<UserRole> findByUserId(Long userId);
}
