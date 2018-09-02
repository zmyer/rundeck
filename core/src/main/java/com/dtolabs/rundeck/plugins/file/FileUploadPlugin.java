/*
 * Copyright 2018 Rundeck, Inc. (http://rundeck.com)
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

package com.dtolabs.rundeck.plugins.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Plugin interface for handling uploaded files for job execution.
 *
 * @author greg
 * @since 2/15/17
 */
public interface FileUploadPlugin {
    /**
     * Initializes the plugin before use
     *
     */
    public void initialize();

    /**
     * Upload a file for the given job and file input name, and return an identifier to reference the
     * uploaded file.
     *
     * @param content content
     * @param length data length
     * @param refid unique identifier for the file
     *
     * @return identifier
     */
    public String uploadFile(
            final InputStream content,
            final long length,
            final String refid,
            Map<String, String> config
    ) throws IOException;

    /**
     * Retrieve the file by reference
     *
     * @param ref ref
     * @param out outputstream to write it to
     */
    public void retrieveFile(String ref, OutputStream out) throws IOException;

    /**
     * Return true if the file can be retrieved via {@link #retrieveFile(String, OutputStream)} or {@link
     * #retrieveFile(String)}
     *
     * @param ref ref
     *
     * @return true if the file can be retrieved, false if the retrieve call will fail
     */
    public boolean hasFile(String ref);

    /**
     * Retrieve the file if it is available locally, otherwise return null
     *
     * @param ref ref
     *
     * @return local file, or null if not directly available
     */
    public File retrieveLocalFile(String ref) throws IOException;

    /**
     * Retrieve the file by reference
     *
     * @param ref ref
     *
     * @return inputstream to read the file
     */
    public InputStream retrieveFile(String ref);

    /**
     * Remove the file
     *
     * @param reference
     *
     * @return
     */
    boolean removeFile(String reference);

    /**
     * Transition between states, allows plugin to determine behavior
     *
     * @param reference reference
     * @param state    the new external state of the file
     *
     * @return the new internal state of the file
     */
    InternalState transitionState(String reference, ExternalState state);

    /**
     * Represents file states known to rundeck
     */
    enum ExternalState {
        /**
         * The file has been used for an operation, and
         */
        Used,
        /**
         * The file will no longer be used
         */
        Unused,
        /**
         * The file should be deleted
         */
        Deleted,
    }

    /**
     * Represents file states known to the plugin
     */
    enum InternalState {
        /**
         * The internal stored file was deleted
         */
        Deleted,
        /**
         * The internal stored file is retained
         */
        Retained
    }
}
