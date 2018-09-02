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

package org.rundeck.storage.data.file;

import org.rundeck.storage.api.*;
import org.rundeck.storage.data.DataUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * $INTERFACE is ... User: greg Date: 2/18/14 Time: 10:03 AM
 */
public class FileTree<T extends ContentMeta> extends LockingTree<T> implements Tree<T> {
    private ContentFactory<T> contentFactory;
    private FilepathMapper filepathMapper;
    private MetadataMapper metadataMapper;

    public FileTree(ContentFactory<T> contentFactory, FilepathMapper filepathMapper, MetadataMapper metadataMapper) {
        this.contentFactory = contentFactory;
        this.filepathMapper = filepathMapper;
        this.metadataMapper = metadataMapper;
    }

    @Override
    public boolean hasPath(Path path) {
        validatePath(path);
        return filepathMapper.contentFileForPath(path).isFile() || filepathMapper.directoryForPath(path).isDirectory();
    }

    @Override
    public boolean hasResource(Path path) {
        validatePath(path);
        return filepathMapper.contentFileForPath(path).isFile() && filepathMapper.metadataFileFor(path).exists();
    }

    @Override
    public boolean hasDirectory(Path path) {
        validatePath(path);
        return filepathMapper.directoryForPath(path).isDirectory();
    }

    @Override
    public Resource<T> getResource(Path path) {
        validatePath(path);
        try {
            return loadResource(path, true);
        } catch (IOException e) {
            throw StorageException.readException(path, "Failed to read resource: " + path + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Resource<T> getPath(Path path) {
        validatePath(path);
        try {
            return loadResource(path, false);
        } catch (IOException e) {
            throw StorageException.readException(path, "Failed to read resource: " + path + ": " + e.getMessage(), e);
        }
    }

    private Resource<T> loadResource(Path path, final boolean requireFile) throws IOException {
        synchronized (pathSynch(path)) {
            File datafile = filepathMapper.contentFileForPath(path);
            if (!datafile.exists()) {
                throw StorageException.readException(path, "Path does not exist: " + path);
            }
            if (requireFile && datafile.isDirectory()) {
                throw StorageException.readException(
                    path,
                    String.format("Failed to read resource at path: %s: is a directory", path)
                );
            }
            boolean directory = datafile.isDirectory();
            if (!directory) {
                return new ContentMetaResource<T>(path, loader(path, datafile, filepathMapper.metadataFileFor(path)),
                        directory);
            } else {
                return new ContentMetaResource<T>(path, null, directory);
            }
        }
    }

    private T loader(Path path, File datafile, File metafile) throws IOException {
        return contentFactory.create(
                synchStream(path, DataUtil.lazyFileStream(datafile)),
                metadataMapper.readMetadata(metafile));
    }

    private Resource<T> storeResource(Path path, ContentMeta data) throws IOException {
        File datafile = filepathMapper.contentFileForPath(path);
        File metafile = filepathMapper.metadataFileFor(path);
        long len = writeContent(path, datafile, metafile, data);
        return new ContentMetaResource<T>(path, loader(path, datafile, metafile), false);
    }

    @Override
    public Set<Resource<T>> listDirectoryResources(Path path) {
        return filterResources(path, IsResourcePredicate);
    }

    @Override
    public Set<Resource<T>> listDirectory(Path path) {
        return filterResources(path, null);
    }

    @Override
    public Set<Resource<T>> listDirectorySubdirs(Path path) {
        return filterResources(path, IsDirResourcePredicate);
    }

    /**
     * TODO: use guava?
     *
     * @param <T>
     */
    static interface Predicate<T> {
        boolean apply(T t);
    }

    static Predicate<Resource> IsDirResourcePredicate = new Predicate<Resource>() {
        @Override
        public boolean apply(Resource resource) {
            return resource.isDirectory();
        }
    };

    static <T> Predicate<T> invert(final Predicate<T> pred) {
        return new Predicate<T>() {
            @Override
            public boolean apply(T resource) {
                return !pred.apply(resource);
            }
        };
    }

    static Predicate<Resource> IsResourcePredicate = invert(IsDirResourcePredicate);

    /**
     * Return a filtered set of resources
     *
     * @param path path
     * @param test predicate test, or null to match all
     *
     * @return set of matching resources
     */
    private Set<Resource<T>> filterResources(Path path, Predicate<Resource> test) {
        validatePath(path);
        if (!hasDirectory(path)) {
            throw StorageException.listException(path, "not a directory path: " + path);
        }
        File file = filepathMapper.directoryForPath(path);
        HashSet<Resource<T>> files = new HashSet<Resource<T>>();
        try {
            for (File file1 : file.listFiles()) {
                Resource<T> res = loadResource(filepathMapper.pathForContentFile(file1), false);
                if (null == test || test.apply(res)) {
                    files.add(res);
                }
            }
        } catch (IOException e) {
            throw StorageException.listException(path, "Failed to list directory: " + path + ": " + e.getMessage(), e);
        }
        return files;
    }

    @Override
    public boolean deleteResource(Path path) {
        validatePath(path);
        boolean content = false;
        boolean meta = false;
        synchronized (pathSynch(path)) {
            if (!hasResource(path)) {
                throw StorageException.deleteException(path, "Resource not found: " + path);
            }
            if (filepathMapper.contentFileForPath(path).exists()) {
                content = filepathMapper.contentFileForPath(path).delete();
            }
            if (filepathMapper.metadataFileFor(path).exists()) {
                meta = filepathMapper.metadataFileFor(path).delete();
            }
        }
        return content && meta;
    }

    private void validatePath(final Path path) {
        if (path.getPath().contains("../")) {
            throw StorageException.createException(path, "Invalid path: " + path);
        }
    }

    @Override
    public Resource<T> createResource(Path path, ContentMeta content) {
        validatePath(path);
        synchronized (pathSynch(path)) {
            if (hasResource(path)) {
                throw StorageException.createException(path, "Resource already exists: " + path);
            }
            try {
                return storeResource(path, content);
            } catch (IOException e) {
                throw StorageException.createException(path, "Failed to create resource: " + path + ": " + e.getMessage(),
                        e);
            }
        }
    }

    @Override
    public Resource<T> updateResource(Path path, ContentMeta content) {
        validatePath(path);
        synchronized (pathSynch(path)) {
            if (!hasResource(path)) {
                throw StorageException.updateException(path, "Resource does not exist: " + path);
            }
            try {
                return storeResource(path, content);
            } catch (IOException e) {
                e.printStackTrace();
                throw StorageException.updateException(path, "Failed to update resource: " + path + ": " + e.getMessage()
                        , e);
            }
        }
    }

    long writeContent(Path path, File datafile, File metafile, ContentMeta input) throws IOException {
        synchronized (pathSynch(path)) {
            metadataMapper.writeMetadata(input.getMeta(), metafile);
            if (!datafile.getParentFile().exists()) {
                datafile.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(datafile);
            try {
                return input.writeContent(out);
            } finally {
                out.close();
            }
        }
    }


}
