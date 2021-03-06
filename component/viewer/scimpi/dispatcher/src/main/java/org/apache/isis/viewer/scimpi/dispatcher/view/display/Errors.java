/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.isis.viewer.scimpi.dispatcher.view.display;

import org.apache.isis.viewer.scimpi.dispatcher.AbstractElementProcessor;
import org.apache.isis.viewer.scimpi.dispatcher.processor.Request;

public class Errors extends AbstractElementProcessor {

    @Override
    public void process(final Request request) {
        final String cls = request.getOptionalProperty(CLASS);
        final StringBuffer buffer = new StringBuffer();
        write(request, cls, buffer);
        if (buffer.length() > 0) {
            request.appendHtml("<div class=\"error\">");
            request.appendHtml(buffer.toString());
            request.appendHtml("</div>");
        }
    }

    public static void write(final Request request, String cls, final StringBuffer buffer) {
        if (cls == null) {
            cls = "error";
        }
        final String message = (String) request.getContext().getVariable("_error-message");
        if (message != null) {
            buffer.append(message);
        }
        final String details = (String) request.getContext().getVariable("_error-details");
        if (details != null) {
            buffer.append(details);
        }

        /*
         * final MessageBroker messageBroker = IsisContext.getMessageBroker();
         * final List<String> warnings = messageBroker.getWarnings(); for (final
         * String warning : warnings) { buffer.append("<div class=\"" + cls +
         * "\">" + warning + "</div>"); }
         */
    }

    @Override
    public String getName() {
        return "errors";
    }

}
