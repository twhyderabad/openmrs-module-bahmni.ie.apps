package org.openmrs.module.bahmniIEApps.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.openmrs.Form;
import org.openmrs.module.bahmniIEApps.dao.BahmniFormDao;
import org.openmrs.module.bahmniIEApps.model.BahmniForm;
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

    @Override
    public List<Form> getDraftFormByName(String name){
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Form.class);
        criteria.add(Restrictions.eq("name", name));
        criteria.add(Restrictions.eq("retired", Boolean.valueOf(false)));
        criteria.add(Restrictions.eq("published", Boolean.valueOf(false)));
        criteria.addOrder(Order.desc("version"));
        return criteria.list();
    }

    @Override
    public List<Form> getAllPublishedForms(boolean includeRetired) {
        return getAllForms(null, includeRetired, false);
    }

    @Override
    public List<Form> getAllForms(String formName, boolean includeRetired, boolean includeDraftState){
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Form.class);
        if(StringUtils.isNotEmpty(formName)) {
            criteria.add(Restrictions.eq("name", formName));
        }
        if(!includeRetired) {
            criteria.add(Restrictions.eq("retired", Boolean.valueOf(false)));
        }
        if(!includeDraftState) {
            criteria.add(Restrictions.eq("published", Boolean.valueOf(true)));
        }
        criteria.addOrder(Order.asc("name"));
        criteria.addOrder(Order.desc("version"));
        return criteria.list();
    }

    public List<BahmniForm> getLatestPublishedFormRevisions(List<String> formNamesToIgnore){
        Query sqlQuery = sessionFactory.getCurrentSession().createSQLQuery("select name, max(version) version\n" +
                "from form\n" +
                "where published=1 and name not in (:ignoreForms)\n" +
                "group by name")
                .addScalar("name", StandardBasicTypes.STRING)
                .addScalar("version",StandardBasicTypes.STRING)
                .setParameter("ignoreForms",formNamesToIgnore)
                .setResultTransformer(Transformers.aliasToBean(BahmniForm.class));
        return sqlQuery.list();
    }

    public List<Form> getFormDetails(List<BahmniForm> formNamesAndVersionList){
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Form.class);
        for (BahmniForm bahmniForm : formNamesAndVersionList) {
            criteria.add(Restrictions.and(Restrictions.eq("name",bahmniForm.getName()), Restrictions.eq("version",bahmniForm.getVersion())));
        }
        return criteria.list();
    }
}
