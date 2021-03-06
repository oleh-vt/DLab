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

package com.epam.dlab;

public enum UserInstanceStatus {
    CREATING,
    CREATED,
    STARTING,
    CONFIGURING,
    RUNNING,
    STOPPING,
    STOPPED,
    TERMINATING,
    TERMINATED,
    FAILED,
    CREATING_IMAGE;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static UserInstanceStatus of(String status) {
        if (status != null) {
            for (UserInstanceStatus uis : UserInstanceStatus.values()) {
                if (status.equalsIgnoreCase(uis.toString())) {
                    return uis;
                }
            }
        }
        return null;
    }
    
    public boolean in(UserInstanceStatus ... statusList) {
    	for (UserInstanceStatus status : statusList) {
			if (this.equals(status)) {
				return true;
			}
		}
		return false;
    }
}