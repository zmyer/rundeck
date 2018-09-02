%{--
  - Copyright 2016 SimplifyOps, Inc. (http://simplifyops.com)
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  -     http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  --}%
 <%--
    _renderServiceConfig.gsp
    
    Author: Greg Schueler <a href="mailto:greg@dtosolutions.com">greg@dtosolutions.com</a>
    Created: 8/16/11 5:00 PM
 --%>

<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${description}">

    <div class="row">
        <div class="col-sm-12">

            <g:render template="/framework/renderPluginDesc" model="${[
                    serviceName    : serviceName,
                    description    : description,
                    showPluginIcon : showPluginIcon,
                    showNodeIcon   : showNodeIcon,
                    hideTitle      : hideTitle,
                    hideDescription: hideDescription,
                    fullDescription: fullDescription
            ]}"/>

        </div>
    </div>
</g:if>
<div class="row">
    <g:set var="rkey" value="${g.rkey()}"/>
    <g:if test="${includeFormFields && saved}">
        <g:hiddenField name="${prefix}saved" value="true" class="wasSaved"/>
    </g:if>
    <g:hiddenField name="prefix" value="${prefix}"/>
    <g:hiddenField name="${prefix+'type'}" value="${type}"/>
    <div class="col-sm-12 form-horizontal">
    <g:if test="${values}">

        <span id="${enc(attr:rkey)}_summary">
            <g:if test="${description}">
                <g:each in="${description.properties}" var="prop">
                    <g:render template="/framework/pluginConfigPropertySummaryValue"
                              model="${[service: serviceName, provider: description.name, messagePrefix:messagePrefix, prop: prop, prefix: prefix, values: values, includeFormFields: includeFormFields]}"/>
                </g:each>
            </g:if>
        </span>
        <g:if test="${description}">
            <div  id="${enc(attr:rkey)}" style="display:none;">
                <g:each in="${description.properties}" var="prop">
                        <g:render template="/framework/pluginConfigPropertyValue"
                                  model="${[prop:prop,prefix:prefix,values:values,includeFormFields:includeFormFields]}"/>
                </g:each>
            </div>
        </g:if>
        <g:elseif test="${includeFormFields}">
            <g:expander key="${rkey}_inv">Properties</g:expander>
            <ul id="${enc(attr:rkey)}_inv" style="display:none">
                <g:each var="prop" in="${values}">
                    <li><g:enc>${prop?.key}: ${prop?.value}</g:enc></li>
                    <input type="hidden" name="${enc(attr: prefix + 'config.' + prop?.key)}"
                           value="${enc(attr: prop?.value)}"/>
                </g:each>
            </ul>
        </g:elseif>
    </g:if>
</div>

</div>
