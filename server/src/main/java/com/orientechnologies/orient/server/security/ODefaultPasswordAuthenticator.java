/*
 *
 *  *  Copyright 2016 Orient Technologies LTD (info(at)orientechnologies.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientechnologies.com
 *
 */
package com.orientechnologies.orient.server.security;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.security.auth.Subject;

import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.security.OSecurityManager;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.security.OSecurityAuthenticator;
import com.orientechnologies.orient.server.config.OServerConfigurationManager;
import com.orientechnologies.orient.server.config.OServerUserConfiguration;

/**
 * Provides a default password authenticator.
 * 
 * @author S. Colin Leister
 * 
 */
public class ODefaultPasswordAuthenticator extends OSecurityAuthenticatorAbstract
{
	// Holds a map of the users specified in the security.json file.
	private ConcurrentHashMap<String, OServerUserConfiguration> _UsersMap = new ConcurrentHashMap<String, OServerUserConfiguration>();

	// OSecurityComponent
	public void config(final OServer oServer, final OServerConfigurationManager serverCfg, final ODocument jsonConfig)
	{
		super.config(oServer, serverCfg, jsonConfig);

		try
		{
			if(jsonConfig.containsField("users"))
			{
				List<ODocument> usersList = jsonConfig.field("users");

				for(ODocument userDoc : usersList)
				{
					if(userDoc.containsField("username") && userDoc.containsField("resources"))
					{
						final String user = userDoc.field("username");
						final String resources = userDoc.field("resources");
						String password = userDoc.field("password");
						
						if(!_UsersMap.containsKey(user))
						{
							if(password == null) password = "";
							
							OServerUserConfiguration userCfg = new OServerUserConfiguration(user, password, resources);
							_UsersMap.put(user, userCfg);
						}
						else
						{
							OLogManager.instance().error(this, "ODefaultPasswordAuthenticator.config() User: %s already exists", user);
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			OLogManager.instance().error(this, "ODefaultPasswordAuthenticator.config() Exception: %s", ex.getMessage());
		}
	}

	// OSecurityComponent
	// Called on removal of the authenticator.
	public void dispose()
	{
		synchronized(_UsersMap)
		{
			_UsersMap.clear();
			_UsersMap = null;
		}
	}
	
	// OSecurityAuthenticator
	// Returns the actual username if successful, null otherwise.
 	public String authenticate(final String username, final String password)
	{
		String principal = null;
		
		try
		{
			OServerUserConfiguration user = getUser(username);
			
			if(user != null && user.password != null && !user.password.isEmpty())
			{
				if(OSecurityManager.instance().checkPassword(password, user.password))
				{
					principal = user.name;
				}	
			}
		}
		catch(Exception ex)
		{
			OLogManager.instance().error(this, "ODefaultPasswordAuthenticator.authenticate() Exception: %s", ex.getMessage());
		}

		return principal;
	}
	
	// OSecurityAuthenticator
	// If not supported by the authenticator, return false.
	public boolean isAuthorized(final String username, final String resource)
	{
		if(username == null || resource == null) return false;
		
		OServerUserConfiguration userCfg = getUser(username);
		
		if(userCfg != null)
		{
			// Total Access
			if(userCfg.resources.equals("*")) return true;
			
			String[] resourceParts = userCfg.resources.split(",");
			
			for(String r : resourceParts)
			{
				if(r.equalsIgnoreCase(resource)) return true;
			}
		}
		
		return false;		
	}
	
	// OSecurityAuthenticator
	public OServerUserConfiguration getUser(final String username)
	{
		OServerUserConfiguration userCfg = null;
		
		synchronized(_UsersMap)
		{
			if(_UsersMap.containsKey(username))
			{
				userCfg = _UsersMap.get(username);
			}
		}
		
		return userCfg;		
	}
}