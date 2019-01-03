package com.activiti.dao.impl.ch07;

import com.activiti.dao.ch07.LeaveDao;
import com.activiti.entity.ch07.Leave;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author restep
 * @date 2019/1/3
 */
@Repository
public class LeaveDaoImpl implements LeaveDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Leave leave) {
        sessionFactory.getCurrentSession().saveOrUpdate(leave);
    }

    @Override
    public void delete(Integer id) {
        sessionFactory.getCurrentSession().delete(id);
    }

    @Override
    public Leave get(Integer id) {
        return (Leave) sessionFactory.getCurrentSession().get(Leave.class, id);
    }
}
