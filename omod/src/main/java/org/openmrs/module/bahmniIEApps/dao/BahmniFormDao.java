package org.openmrs.module.bahmniIEApps.dao;

import org.openmrs.Form;
import org.openmrs.api.db.DAOException;

import java.util.List;

public interface BahmniFormDao {
    List<Form> getDraftFormByName(String name) throws DAOException;
}
