package com.usion.demo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.usion.demo.dao.RoleDao;
import com.usion.demo.entity.main.Role;
import com.usion.demo.service.RoleService;
import com.usion.demo.shiro.ShiroDbRealm;
import com.usion.util.dwz.Page;
import com.usion.util.dwz.springdata.PageUtils;

/**
 * @author usion
 */
@Service
@Transactional(readOnly=true)
public class RoleServiceImpl extends BaseServiceImpl<Role, Long> implements RoleService {
	
	private RoleDao roleDao;
	
	@Autowired(required = false)
	private ShiroDbRealm shiroRealm;
	
	/**  
	 * 构造函数
	 * @param jpaRepository  
	 */ 
	@Autowired
	public RoleServiceImpl(RoleDao roleDao) {
		super((JpaRepository<Role, Long>) roleDao);
		this.roleDao = roleDao;
	}

	/**   
	 * @param role  
	 * @see com.usion.demo.service.RoleService#update(com.usion.demo.entity.main.Role)  
	 */
	@Transactional
	public void update(Role role) {
		roleDao.save(role);
		shiroRealm.clearAllCachedAuthorizationInfo();
	}

	/**   
	 * @param id  
	 * @see com.usion.demo.service.RoleService#delete(java.lang.Long)  
	 */
	@Transactional
	public void delete(Long id) {
		roleDao.delete(id);
		shiroRealm.clearAllCachedAuthorizationInfo();
	}

	/**   
	 * @param page
	 * @param name
	 * @return  
	 * @see com.usion.demo.service.RoleService#find(com.usion.util.dwz.Page, java.lang.String)  
	 */
	public List<Role> find(Page page, String name) {
		org.springframework.data.domain.Page<Role> roles = 
				(org.springframework.data.domain.Page<Role>)roleDao.findByNameContaining(name, PageUtils.createPageable(page));
		PageUtils.injectPageProperties(page, roles);
		return roles.getContent();
	}

}
