package org.openmrs.module.bahmniIEApps.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Form;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.bahmniIEApps.dao.BahmniFormDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BahmniFormDaoImpl implements BahmniFormDao{
    private SessionFactory sessionFactory;

    @Autowired
    public BahmniFormDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Form> getDraftFormByName(String name) throws DAOException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Form.class);
        criteria.add(Restrictions.eq("name", name));
        criteria.add(Restrictions.eq("retired", Boolean.valueOf(false)));
        criteria.add(Restrictions.eq("published", Boolean.valueOf(false)));
        criteria.addOrder(Order.desc("version"));
        return criteria.list();
    }

    @Override
    public List<Form> getAllPublishedForms(boolean includeRetired) throws DAOException {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Form.class);
        if(!includeRetired) {
            criteria.add(Restrictions.eq("retired", Boolean.valueOf(false)));
        }
        criteria.add(Restrictions.eq("published", Boolean.valueOf(true)));
        criteria.addOrder(Order.desc("name"));
        criteria.addOrder(Order.desc("version"));
        return criteria.list();
    }
}
