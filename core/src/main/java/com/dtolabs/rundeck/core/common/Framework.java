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

package com.dtolabs.rundeck.core.common;

import com.dtolabs.rundeck.core.utils.IPropertyLookup;
import com.dtolabs.rundeck.core.utils.PropertyLookup;

import java.io.File;

/**
 * Created by greg on 2/20/15.
 */
public class Framework extends FrameworkBase implements IFilesystemFramework {

    private final IFilesystemFramework filesystemFramework;
    private IFrameworkProjectMgr filesystemFrameworkProjectManager;

    /**
     * Standard constructor
     */
    Framework(
            final IFilesystemFramework filesystemFramework,
            final IFrameworkProjectMgr frameworkProjectMgr,
            final IPropertyLookup lookup,
            final IFrameworkServices services,
            final IFrameworkNodes iFrameworkNodes
    )
    {
        super(frameworkProjectMgr, lookup, services, iFrameworkNodes);
        this.filesystemFramework=filesystemFramework;
        this.setFilesystemFrameworkProjectManager(frameworkProjectMgr);
    }
    /**
     * Standard constructor
     */
    Framework(
            final IFilesystemFramework filesystemFramework,
            final ProjectManager frameworkProjectMgr,
            final IPropertyLookup lookup,
            final IFrameworkServices services,
            final IFrameworkNodes iFrameworkNodes
    )
    {
        super(frameworkProjectMgr, lookup, services, iFrameworkNodes);
        this.filesystemFramework=filesystemFramework;
    }

    public IFilesystemFramework getFilesystemFramework() {
        return filesystemFramework;
    }

    @Override
    public File getConfigDir() {
        return getFilesystemFramework().getConfigDir();
    }

    @Override
    public File getFrameworkProjectsBaseDir() {
        return getFilesystemFramework().getFrameworkProjectsBaseDir();
    }

    @Override
    public File getLibextDir() {
        return getFilesystemFramework().getLibextDir(this);
    }

    @Override
    public File getLibextDir(final IFramework fwk) {
        return getFilesystemFramework().getLibextDir(this);
    }

    @Override
    public File getLibextCacheDir(final IFramework fwk) {return getFilesystemFramework().getLibextCacheDir(this);
    }

    @Override
    public File getLibextCacheDir() {
        return getFilesystemFramework().getLibextCacheDir(this);
    }

    @Override
    public File getBaseDir() {
        return getFilesystemFramework().getBaseDir();
    }

    public IFrameworkProjectMgr getFilesystemFrameworkProjectManager() {
        return filesystemFrameworkProjectManager;
    }

    public void setFilesystemFrameworkProjectManager(final IFrameworkProjectMgr filesystemFrameworkProjectManager) {
        this.filesystemFrameworkProjectManager = filesystemFrameworkProjectManager;
    }

    @Override
    public ProjectManager getFrameworkProjectMgr() {
        if(null!=getFilesystemFrameworkProjectManager()) {
            return getFilesystemFrameworkProjectManager();
        }
        return super.getFrameworkProjectMgr();
    }


}
