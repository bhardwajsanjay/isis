/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.runtimes.dflt.objectstores.sql.jdbc;

import org.apache.isis.applib.PersistFailedException;
import org.apache.isis.applib.value.Money;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.spec.feature.ObjectAssociation;
import org.apache.isis.runtimes.dflt.objectstores.sql.Results;
import org.apache.isis.runtimes.dflt.objectstores.sql.Sql;
import org.apache.isis.runtimes.dflt.objectstores.sql.mapping.FieldMapping;
import org.apache.isis.runtimes.dflt.objectstores.sql.mapping.FieldMappingFactory;

/**
 * Money needs to implement a two-column persistence, 1 for amount, 1 for 3-digit currency
 * 
 * @version $Rev$ $Date$
 */
public class JdbcMoneyValueMapper extends AbstractJdbcMultiFieldMapping {

	public static class Factory implements FieldMappingFactory {
        private final String type1;
        private final String type2;

        public Factory(String type1, String type2) {
            this.type1 = type1;
            this.type2 = type2;
        }

        @Override
		public FieldMapping createFieldMapping(final ObjectAssociation field) {
            return new JdbcMoneyValueMapper(field, type1, type2);
		}
	}

    private final String[] types = new String[2];
    private final String[] columnNames = new String[2];


    public JdbcMoneyValueMapper(ObjectAssociation field, String type1, String type2) {
        super(field, 2);
        this.types[0] = type1;
        this.types[1] = type2;

        String fieldName = field.getId();
        columnNames[0] = Sql.sqlFieldName(fieldName + "1");
        columnNames[1] = Sql.sqlFieldName(fieldName + "2");
    }

    @Override
    protected Object preparedStatementObject(int index, ObjectAdapter value){
        if (value == null) return null;
        
        Object o = value.getObject();
        
        if (o instanceof Money) {
            if (index == 0) {
                return ((Money) o).doubleValue();
            } else {
                return ((Money) o).getCurrency();
            }
        } else {
            throw new PersistFailedException("Invalid object type " + o.getClass().getCanonicalName()
                + " for MoneyValueMapper");
        }
    }
	

    @Override
    protected String columnType(int index) {
        return types[index];
	}

    @Override
    protected String columnName(int index) {
        return columnNames[index];
    }


    @Override
    protected Object getObjectFromResults(Results results) {
        double doubleValue = results.getDouble(columnName(0));
        String currencyValue = results.getString(columnName(1));

        Money moneyObject = new Money(doubleValue, currencyValue);

        return moneyObject;
    }





}