package org.bahmni.module.bahmni.ie.apps.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.bahmni.module.bahmni.ie.apps.model.BahmniForm;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.db.DAOException;
import org.bahmni.module.bahmni.ie.apps.dao.BahmniFormDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BahmniFormDaoImpl implements BahmniFormDao {

	private SessionFactory sessionFactory;

	@Autowired
	public BahmniFormDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
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
		return getAllForms(null, includeRetired, false);
	}

	@Override
	public List<Form> getAllForms(String formName, boolean includeRetired, boolean includeDraftState) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Form.class);
		if (StringUtils.isNotEmpty(formName)) {
			criteria.add(Restrictions.eq("name", formName));
		}
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", Boolean.valueOf(false)));
		}
		if (!includeDraftState) {
			criteria.add(Restrictions.eq("published", Boolean.valueOf(true)));
		}
		criteria.addOrder(Order.asc("name"));
		criteria.addOrder(Order.desc("version"));
		return criteria.list();
	}

	@Override
	public List<Form> getAllFormsByListOfUuids(List<String> formUuids) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Form.class);
		criteria.add(Restrictions.in("uuid", formUuids));
		criteria.add(Restrictions.eq("retired", Boolean.valueOf(false)));
		criteria.add(Restrictions.eq("published", Boolean.valueOf(true)));
		return criteria.list();
	}

	@Override
	public List<BahmniForm> getAllPublishedFormsWithNameTranslation(boolean includeRetired) throws DAOException {
		Session currentSession = sessionFactory.getCurrentSession();
		String retiredQueryCondition= includeRetired? "" : "and F.retired=false ";
		Query query =  currentSession.createQuery( String.format("Select COALESCE(FR.valueReference,'[]') as nameTranslation, " +
				"F.name as name, F.uuid as uuid, F.version as version, F.published as published " +
				"from FormResource FR right outer join FR.form F with FR.datatypeClassname!=" +
				"'org.bahmni.customdatatype.datatype.FileSystemStorageDatatype' where F.published=true " +
				"%s order by F.name asc, F.version desc", retiredQueryCondition))
				.setResultTransformer( Transformers.aliasToBean(BahmniForm.class));

		return query.list();
	}
}
