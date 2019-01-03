package com.activiti.dao.ch07;

import com.activiti.entity.ch07.Leave;

/**
 * @author restep
 * @date 2019/1/3
 */
public interface LeaveDao {
    void save(Leave leave);

    void delete(Integer id);

    Leave get(Integer id);
}
