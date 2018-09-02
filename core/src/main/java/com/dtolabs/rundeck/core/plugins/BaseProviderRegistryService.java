/*
 * Copyright 2016 SimplifyOps, Inc. (http://simplifyops.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
* BaseRegistryService.java
* 
* User: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
* Created: 3/21/11 6:26 PM
* 
*/
package com.dtolabs.rundeck.core.plugins;

import com.dtolabs.rundeck.core.common.Framework;
import com.dtolabs.rundeck.core.common.IFramework;
import com.dtolabs.rundeck.core.common.ProviderService;
import com.dtolabs.rundeck.core.execution.service.ExecutionServiceException;
import com.dtolabs.rundeck.core.execution.service.MissingProviderException;
import com.dtolabs.rundeck.core.execution.service.ProviderCreationException;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * BaseProviderRegistryService is an abstract base that provides a registry of available service providers based on
 * simple names.  The service providers classes must have a no-arg constructor or a single-argument constructor with a {@link Framework}
 * argument
 *
 * @author Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
 */
public abstract class BaseProviderRegistryService<T> implements ProviderService<T>, ProviderRegistryService<T> {
    protected       HashMap<String, Class<? extends T>> registry;
    protected       HashMap<String, T>                  instanceregistry;
    protected final Framework                           framework;
    private         boolean                             cacheInstances = false;

    public BaseProviderRegistryService(Framework framework) {
        this.framework = framework;
        instanceregistry = new HashMap<>();
        registry = new HashMap<>();
    }

    public BaseProviderRegistryService(final Framework framework, final boolean cacheInstances) {
        this(framework);
        this.cacheInstances = cacheInstances;
    }

    public BaseProviderRegistryService(Framework framework, Map<String, Class<? extends T>> classes) {
        this.framework = framework;
        instanceregistry = new HashMap<>();
        registry = new HashMap<>(classes);
    }

    public BaseProviderRegistryService(
        final Map<String, Class<? extends T>> registry,
        final Framework framework,
        final boolean cacheInstances
    )
    {
        instanceregistry = new HashMap<>();
        this.registry = new HashMap<>(registry);
        this.framework = framework;
        this.cacheInstances = cacheInstances;
    }

    @Override
    public void registerClass(String name, Class<? extends T> clazz) {
        registry.put(name, clazz);
    }

    @Override
    public void registerInstance(String name, T object) {
        instanceregistry.put(name, object);
    }


    /**
     * Return the provider instance of the given name.
     */
    public T providerOfType(final String providerName) throws ExecutionServiceException {
        if (null == providerName) {
            throw new NullPointerException("provider name was null for Service: " + getName());
        }
        if (isCacheInstances()) {
            if (null == instanceregistry.get(providerName)) {
                T instance = createProviderInstanceOfType(providerName);
                instanceregistry.put(providerName, instance);
                return instance;
            }
            return instanceregistry.get(providerName);
        } else {
            return createProviderInstanceOfType(providerName);
        }
    }

    @Override
    public CloseableProvider<T> closeableProviderOfType(final String providerName) throws ExecutionServiceException {
        final T t = providerOfType(providerName);
        if (t == null) {
            return null;
        }
        return Closeables.closeableProvider(t);
    }

    public List<ProviderIdent> listProviders() {

        final HashSet<ProviderIdent> providers = new HashSet<>();

        for (final String s : registry.keySet()) {
            providers.add(new ProviderIdent(getName(), s));
        }
        for (final String s : instanceregistry.keySet()) {
            providers.add(new ProviderIdent(getName(), s));
        }
        return new ArrayList<ProviderIdent>(providers);
    }

    private T createProviderInstanceOfType(final String providerName) throws ExecutionServiceException {
        if (null == registry.get(providerName)) {
            throw new MissingProviderException("Not found", getName(),
                providerName);
        }
        final Class<? extends T> execClass = registry.get(providerName);
        return createProviderInstanceFromType(execClass, providerName);
    }

    protected T createProviderInstanceFromType(final Class<? extends T> execClass, final String providerName) throws
        ProviderCreationException {
        boolean ctrfound = true;
        try {
            final Constructor<? extends T> method = execClass.getDeclaredConstructor(new Class[]{Framework.class});
            final T executor = method.newInstance(framework);
            return executor;
        } catch (NoSuchMethodException e) {
            ctrfound = false;
        } catch (Exception e) {
            throw new ProviderCreationException("Unable to create provider instance: " + e.getMessage(), e, getName(),
                providerName);
        }
        try {
            final Constructor<? extends T> method = execClass.getDeclaredConstructor(new Class[0]);
            final T executor = method.newInstance();
            return executor;
        } catch (NoSuchMethodException e) {
            throw new ProviderCreationException(
                "No constructor found with signature (Framework) or (): " + e.getMessage(), e,
                getName(),
                providerName);
        } catch (Exception e) {
            throw new ProviderCreationException("Unable to create provider instance: " + e.getMessage(), e,
                getName(),
                providerName);
        }
    }

    protected boolean hasValidProviderSignature(final Class clazz) {

        try {
            final Constructor method = clazz.getDeclaredConstructor(new Class[]{Framework.class});
            return null != method;
        } catch (NoSuchMethodException e) {
        }
        try {
            final Constructor method = clazz.getDeclaredConstructor(new Class[0]);
            return null != method;
        } catch (NoSuchMethodException e) {
        }
        return false;
    }

    @Override
    public boolean isCacheInstances() {
        return cacheInstances;
    }

    @Override
    public void setCacheInstances(boolean cacheInstances) {
        this.cacheInstances = cacheInstances;
    }
}
