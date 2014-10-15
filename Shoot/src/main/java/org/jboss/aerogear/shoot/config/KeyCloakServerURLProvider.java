/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.shoot.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

/**
 * This is a bean class which checks for a system parameter "KEYCLOAK_SERVER_URL".
 * 
 * This was created because there was a need for a configurable JavaScript location.
 */
@ApplicationScoped
@Named("keycloak")
public class KeyCloakServerURLProvider {
    private static final String SHOOT_PROPERTIES_FILENAME = "shoot.properties";
    private static final String KEYCLOAK_URL_PROPERTY_KEY = "keycloak.url";
    
    private String keyCloakServerUrl = "";
    
    @Inject
    private ServletContext servletContext;
    
    @PostConstruct
    public void findKeyCloakURL() {
        InputStream propertiesStream = servletContext.getResourceAsStream("WEB-INF/"+SHOOT_PROPERTIES_FILENAME);
        if (propertiesStream != null) {
            try {
                Properties properties = new Properties();
                properties.load(propertiesStream);
                keyCloakServerUrl = properties.getProperty(KEYCLOAK_URL_PROPERTY_KEY, "");
            } catch (IOException ex) {
                Logger.getLogger(KeyCloakServerURLProvider.class.getName()).log(Level.SEVERE, null, ex);
                keyCloakServerUrl = "";
            }
            
        }
        
    }
    
    public String getUrl() {
        return keyCloakServerUrl;
    }
    
}
