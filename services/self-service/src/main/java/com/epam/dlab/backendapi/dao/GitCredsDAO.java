/***************************************************************************

 Copyright (c) 2016, EPAM SYSTEMS INC

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 ****************************************************************************/

package com.epam.dlab.backendapi.dao;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.bson.Document;

import com.epam.dlab.dto.exploratory.ExploratoryGitCreds;
import com.epam.dlab.dto.exploratory.ExploratoryGitCredsDTO;
import com.epam.dlab.exceptions.DlabException;

/** DAO for user exploratory.
 */
public class GitCredsDAO extends BaseDAO {
	private static final String FIELD_GIT_CREDS = "git_creds";

    /** Find and return the list of GIT credentials for user. 
     * @param user name.
     * @return
     */
    public ExploratoryGitCredsDTO findGitCreds(String user) {
    	return findGitCreds(user, false);
    }

    /** Find and return the list of GIT credentials for user. 
     * @param user name.
     * @param clearPassword clear user password if set to <b>true</b>.
     * @return
     */
    public ExploratoryGitCredsDTO findGitCreds(String user, boolean clearPassword) {
    	Optional<ExploratoryGitCredsDTO> opt = findOne(GIT_CREDS,
    													eq(ID, user),
    													fields(include(FIELD_GIT_CREDS), excludeId()),
    													ExploratoryGitCredsDTO.class);
    	ExploratoryGitCredsDTO creds = (opt.isPresent() ? opt.get() : new ExploratoryGitCredsDTO());
    	List<ExploratoryGitCreds> list = creds.getGitCreds();
    	if (clearPassword && list != null) {
    		for (ExploratoryGitCreds cred : list) {
				cred.setPassword(null);
			}
    	}
    	
    	return creds;
    }

    /** Update the GIT credentials for user.
     * @param user name.
     * @param dto GIT credentials.
     * @exception DlabException
     */
    public void updateGitCreds(String user, ExploratoryGitCredsDTO dto) throws DlabException {
    	List<ExploratoryGitCreds> list = findGitCreds(user).getGitCreds();
    	if (list != null && dto.getGitCreds() != null) {
        	Collections.sort(dto.getGitCreds());
    		// Restore passwords from Mongo DB.
    		for (ExploratoryGitCreds cred : dto.getGitCreds()) {
    			if (cred.getPassword() == null) {
    				int index = Collections.binarySearch(list, cred);
    				if (index >= 0) {
    					cred.setPassword(list.get(index).getPassword());
    				}
    			}
			}
    	}
    	
    	Document d = new Document(SET,
							convertToBson(dto)
								.append(ID, user));
    	updateOne(GIT_CREDS,
    				eq(ID, user),
    				d,
    				true);
    }
}