package com.usion.demo.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.usion.demo.service.BaseService;
import com.usion.util.dwz.Page;
import com.usion.util.dwz.springdata.PageUtils;

/**
 * @author usion
 */

public class BaseServiceImpl<T, ID extends Serializable> implements BaseService<T, ID> {

	private JpaRepository<T, ID> jpaRepository;
	
	public BaseServiceImpl(JpaRepository<T, ID> jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	/**   
	 * @param entity  
	 * @see com.usion.demo.service.BaseService#save(java.lang.Object)  
	 */
	@Transactional
	@Override
	public void save(T entity) {
		jpaRepository.save(entity);
	}

	/**   
	 * @param entity  
	 * @see com.usion.demo.service.BaseService#update(java.lang.Object)  
	 */
	@Transactional
	@Override
	public void update(T entity) {
		jpaRepository.save(entity);
	}

	/**   
	 * @param id  
	 * @see com.usion.demo.service.BaseService#delete(java.io.Serializable)  
	 */
	@Transactional
	@Override
	public void delete(ID id) {
		jpaRepository.delete(id);
	}

	/**   
	 * @param id
	 * @return  
	 * @see com.usion.demo.service.BaseService#get(java.io.Serializable)  
	 */
	@Override
	public T get(ID id) {
		return jpaRepository.findOne(id);
	}

	/**   
	 * @return  
	 * @see com.usion.demo.service.BaseService#findAll()  
	 */
	@Override
	public List<T> findAll() {
		return jpaRepository.findAll();
	}

	/**   
	 * @param page
	 * @return  
	 * @see com.usion.demo.service.BaseService#findAll(com.usion.util.dwz.Page)  
	 */
	@Override
	public List<T> findAll(Page page) {
		org.springframework.data.domain.Page<T> springDataPage = jpaRepository.findAll(PageUtils.createPageable(page));
		PageUtils.injectPageProperties(page, springDataPage);
		return springDataPage.getContent();
	}
}
