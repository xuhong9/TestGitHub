package com.usion.demo.service.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import com.usion.demo.service.CacheService;

/**
 * @author usion
 */
@Service
public class CacheServiceImpl implements CacheService {
	
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * @see com.usion.demo.service.CacheService#clearAllCache()
	 */
	public void clearAllCache() {
		em.getEntityManagerFactory().getCache().evictAll();
	}

}
