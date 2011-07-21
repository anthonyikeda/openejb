/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.openejb.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.openejb.jee.ApplicationClient;
import org.apache.xbean.finder.ClassFinder;

/**
 * @version $Rev$ $Date$
 */
public class ClientModule extends Module implements DeploymentModule {
    private final ValidationContext validation;
    private ApplicationClient applicationClient;
    private ClassLoader classLoader;
    private String mainClass;
    private boolean ejbModuleGenerated;
    private AtomicReference<ClassFinder> finder;
    private final Set<String> localClients = new HashSet<String>();
    private final Set<String> remoteClients = new HashSet<String>();
    private ID id;
    private final Set<String> watchedResources = new TreeSet<String>();

    public ClientModule(ApplicationClient applicationClient, ClassLoader classLoader, String jarLocation, String mainClass, String moduleId) {
        this.applicationClient = applicationClient;
        this.classLoader = classLoader;
        this.mainClass = mainClass;

        File file = (jarLocation == null) ? null : new File(jarLocation);
        this.id = new ID(//null, applicationClient,
            moduleId, file, null, this);
        validation = new ValidationContext(ClientModule.class, jarLocation);
    }

    public boolean isEjbModuleGenerated() {
        return ejbModuleGenerated;
    }

    public void setEjbModuleGenerated(boolean ejbModuleGenerated) {
        this.ejbModuleGenerated = ejbModuleGenerated;
    }

    public ClassFinder getFinder() {
        return (finder != null)? finder.get(): null;
    }

    public void setFinderReference(AtomicReference<ClassFinder> finder) {
        this.finder = finder;
    }

    public AtomicReference<ClassFinder> getFinderReference() {
        return this.finder;
    }

    public ValidationContext getValidation() {
        return validation;
    }

    public String getModuleId() {
        return id.getName();
    }

    public ApplicationClient getApplicationClient() {
        return applicationClient;
    }

    public void setApplicationClient(ApplicationClient applicationClient) {
        this.applicationClient = applicationClient;
    }

    public Set<String> getLocalClients() {
        return localClients;
    }

    public Set<String> getRemoteClients() {
        return remoteClients;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String getJarLocation() {
        return (id.getLocation() != null) ? id.getLocation().getAbsolutePath() : null;
    }

    public void setJarLocation(String jarLocation) {
        this.id = new ID(//null, applicationClient,
            id.getName(), new File(jarLocation), id.getUri(), this);
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public Set<String> getWatchedResources() {
        return watchedResources;
    }
}
