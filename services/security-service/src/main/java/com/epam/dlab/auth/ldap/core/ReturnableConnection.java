/******************************************************************************************************

 Copyright (c) 2016 EPAM Systems Inc.

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 *****************************************************************************************************/

package com.epam.dlab.auth.ldap.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionPool;

public class ReturnableConnection implements Closeable {

	private final LdapConnectionPool pool;
	private LdapConnection con;
	private final Lock lock = new ReentrantLock();
	
	public ReturnableConnection(LdapConnectionPool pool) {
		Objects.requireNonNull(pool);
		this.pool = pool;
	}
	
	public LdapConnection getConnection() throws Exception {
		try {
			lock.lock(); //just protect from inproper use
			if(con == null) {
				con = pool.borrowObject();
			} else {
				throw new IllegalStateException("Cannot reuse connection. Create new ReturnableConnection");
			}
		} finally {
			lock.unlock();
		}
		return con;
	}
	
	@Override
	public void close() throws IOException {
		try {
			pool.releaseConnection(con);
		} catch (LdapException e) {
			throw new IOException("LDAP Release Connection error",e);
		}

	}

}
