package eu.opertusmundi.api_auth.domain.usertype;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

// see also: https://github.com/hibernate/hibernate-reactive/issues/1285
public class StringArrayUserType implements UserType
{
    @Override
    public int[] sqlTypes()
    {
        // note: only to pass schema validation!! 
        // otherwise schema validator tries to compare Types.ARRAY with Types.NULL ...
        return new int[] { java.sql.Types.NULL }; 
    }

    @Override
    public Class returnedClass()
    {
        return String[].class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException
    {
        return Arrays.equals((String[]) x, (String[]) y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException
    {
        return Arrays.hashCode((String []) x);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
        throws HibernateException, SQLException
    {
        return rs.getObject(names[0]); 
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
        throws HibernateException, SQLException
    {
        if (value == null) {
            st.setNull(index, java.sql.Types.ARRAY);
        } else {
            st.setObject(index, value);
        }
    }

    private String[] _deepCopy(Object value)
    {
        if (value == null)
            return null;
        // note: do we really need to copy (since our arrays will be immutable)? 
        String[] arr = (String[]) value;
        return Arrays.copyOf(arr, arr.length);
    }
    
    @Override
    public Object deepCopy(Object value) throws HibernateException
    {
        return this._deepCopy(value);
    }

    @Override
    public boolean isMutable()
    {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException
    {
        return this._deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException
    {
        return this._deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException
    {
        return original;
    }
}
