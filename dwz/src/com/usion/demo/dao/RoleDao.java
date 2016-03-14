package com.usion.demo.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.usion.demo.entity.main.Role;

/**
 * @author usion
 */
public interface RoleDao extends JpaRepository<Role, Long> {
	Page<Role> findByNameContaining(String name, Pageable pageable);
}
