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

package org.apache.isis.applib;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.apache.isis.applib.util.NameUtils;

public class Identifier implements Comparable<Identifier> {

    private static final List<String> EMPTY_LIST_OF_STRINGS = Collections.<String>emptyList();

    /**
     * What type of feature this identifies.
     */
    public static enum Type {
        CLASS, PROPERTY_OR_COLLECTION, ACTION
    }

    public static enum Depth {
        CLASS {
            @Override
            public String toIdentityString(final Identifier identifier) {
                return identifier.toClassIdentityString();
            }
        },
        CLASS_MEMBERNAME {
            @Override
            public String toIdentityString(final Identifier identifier) {
                return identifier.toClassAndNameIdentityString();
            }
        },
        CLASS_MEMBERNAME_PARMS {
            @Override
            public String toIdentityString(final Identifier identifier) {
                return identifier.toFullIdentityString();
            }
        },
        MEMBERNAME_ONLY {
            @Override
            public String toIdentityString(final Identifier identifier) {
                return identifier.toNameIdentityString();
            }
        },
        PARMS_ONLY {
            @Override
            public String toIdentityString(final Identifier identifier) {
                return identifier.toParmsIdentityString();
            }
        };
        public abstract String toIdentityString(Identifier identifier);
    }

    public static Depth CLASS = Depth.CLASS;
    public static Depth CLASS_MEMBERNAME = Depth.CLASS_MEMBERNAME;
    public static Depth CLASS_MEMBERNAME_PARMS = Depth.CLASS_MEMBERNAME_PARMS;
    public static Depth MEMBERNAME_ONLY = Depth.MEMBERNAME_ONLY;
    public static Depth PARMS_ONLY = Depth.PARMS_ONLY;

    // ///////////////////////////////////////////////////////////////////////////
    // Factory methods
    // ///////////////////////////////////////////////////////////////////////////

    public static Identifier classIdentifier(final Class<?> cls) {
        return classIdentifier(cls.getName());
    }

    public static Identifier classIdentifier(final String className) {
        return new Identifier(className, "", EMPTY_LIST_OF_STRINGS, Type.CLASS);
    }

    public static Identifier propertyOrCollectionIdentifier(final Class<?> declaringClass,
        final String propertyOrCollectionName) {
        return propertyOrCollectionIdentifier(declaringClass.getCanonicalName(), propertyOrCollectionName);
    }

    public static Identifier propertyOrCollectionIdentifier(final String declaringClassName,
        final String propertyOrCollectionName) {
        return new Identifier(declaringClassName, propertyOrCollectionName, EMPTY_LIST_OF_STRINGS,
            Type.PROPERTY_OR_COLLECTION);
    }

    public static Identifier actionIdentifier(final Class<?> declaringClass, final String actionName,
        final Class<?>... parameterClasses) {
        return actionIdentifier(declaringClass.getCanonicalName(), actionName, classNamesOf(parameterClasses));
    }

    public static Identifier actionIdentifier(final String declaringClassName, final String actionName,
        final Class<?>... parameterClasses) {
        return actionIdentifier(declaringClassName, actionName, classNamesOf(parameterClasses));
    }

    public static Identifier actionIdentifier(final String declaringClassName, final String actionName,
        final List<String> parameterClassNames) {
        return new Identifier(declaringClassName, actionName, parameterClassNames, Type.ACTION);
    }

    /**
     * Helper, used within contructor chaining
     */
    private static List<String> classNamesOf(final Class<?>[] parameterClasses) {
        if (parameterClasses == null) {
            return EMPTY_LIST_OF_STRINGS;
        }
        final List<String> parameterClassNames = Lists.newArrayList();
        for (Class<?> parameterClass : parameterClasses) {
            parameterClassNames.add(parameterClass.getName());
        }
        return parameterClassNames;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Instance variables
    // ///////////////////////////////////////////////////////////////////////////

    private final String className;
    private final String memberName;
    private final List<String> parameterNames;
    private final Type type;
    private String identityString;

    /**
     * Caching of {@link #toString()}, for performance.
     */
    private String asString = null;

    // ///////////////////////////////////////////////////////////////////////////
    // Constructor
    // ///////////////////////////////////////////////////////////////////////////

    private Identifier(final String className, final String memberName, final List<String> parameterNames, final Type type) {
        this.className = className;
        this.memberName = memberName;
        this.parameterNames = Collections.unmodifiableList(parameterNames);
        this.type = type;
    }

    public String getClassName() {
        return className;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMemberNaturalName() {
        return NameUtils.naturalName(memberName);
    }

    public List<String> getMemberParameterNames() {
        return parameterNames;
    }

    public List<String> getMemberParameterNaturalNames() {
        return NameUtils.naturalNames(parameterNames);
    }

    public Type getType() {
        return type;
    }

    /**
     * Convenience method.
     * 
     * @return
     */
    public boolean isPropertyOrCollection() {
        return type == Type.PROPERTY_OR_COLLECTION;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // toXxxString
    // ///////////////////////////////////////////////////////////////////////////

    public String toIdentityString(final Depth depth) {
        return depth.toIdentityString(this);
    }

    public String toClassIdentityString() {
        return toClassIdentityString(new StringBuilder()).toString();
    }

    public StringBuilder toClassIdentityString(final StringBuilder buf) {
        return buf.append(className);
    }

    public String toNameIdentityString() {
        return toNameIdentityString(new StringBuilder()).toString();
    }

    public StringBuilder toNameIdentityString(final StringBuilder buf) {
        return buf.append(memberName);
    }

    public String toClassAndNameIdentityString() {
        return toClassAndNameIdentityString(new StringBuilder()).toString();
    }

    public StringBuilder toClassAndNameIdentityString(final StringBuilder buf) {
        return toClassIdentityString(buf).append("#").append(memberName);
    }

    public String toParmsIdentityString() {
        return toParmsIdentityString(new StringBuilder()).toString();
    }

    public StringBuilder toParmsIdentityString(final StringBuilder buf) {
        if (type == Type.ACTION) {
            appendParameterNamesTo(buf);
        }
        return buf;
    }

    private void appendParameterNamesTo(final StringBuilder buf) {
        buf.append('(');
        Joiner.on(',').appendTo(buf, parameterNames);
        buf.append(')');
    }

    public String toNameParmsIdentityString() {
        return getMemberName() + toParmsIdentityString();
    }

    public StringBuilder toNameParmsIdentityString(final StringBuilder buf) {
        buf.append(getMemberName());
        toParmsIdentityString(buf);
        return buf;
    }

    public String toFullIdentityString() {
        if (identityString == null) {
            if (memberName.length() == 0) {
                identityString = toClassIdentityString();
            } else {
                final StringBuilder buf = new StringBuilder();
                toClassAndNameIdentityString(buf);
                toParmsIdentityString(buf);
                identityString = buf.toString();
            }
        }
        return identityString;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // compareTo
    // ///////////////////////////////////////////////////////////////////////////

    @Override
    public int compareTo(final Identifier o2) {
        return toString().compareTo(o2.toString());
    }

    // ///////////////////////////////////////////////////////////////////////////
    // equals, hashCode
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * REVIEW: why not just compare the {@link #toString()} representations?
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Identifier)) {
            return false;
        }
        final Identifier other = (Identifier) obj;
        return equals(other);
    }

    public boolean equals(final Identifier other) {
        return equals(other.className, className) && equals(other.memberName, other.memberName)
            && equals(other.parameterNames, parameterNames);
    }

    private boolean equals(final String a, final String b) {
        if (a == b) {
            return true;
        }

        return a != null && a.equals(b);
    }

    private boolean equals(final List<String> a, final List<String> b) {
        if (a == null && b == null) {
            return true;
        } else if (a == null && b != null) {
            return false;
        } else if (a != null && b == null) {
            return false;
        } else if (a != null && b != null) {
            return a.equals(b);
        }
        return true;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // toString
    // ///////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        if (asString == null) {
            final StringBuilder buf = new StringBuilder();
            buf.append(className);
            buf.append('#');
            buf.append(memberName);
            appendParameterNamesTo(buf);
            asString = buf.toString();
        }
        return asString;
    }

}
