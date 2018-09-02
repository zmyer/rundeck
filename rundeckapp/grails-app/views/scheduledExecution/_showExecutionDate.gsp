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

<g:render template="/scheduledExecution/execStatusText" model="${[execution: execution]}"/>
    <span style="${wdgt.styleVisible(if: execution.dateCompleted != null )}"
        data-bind="visible: completed()"
    >
        <g:message code="after" default="after"/> <span data-bind="text: execDurationHumanized(), attr: {title: execDurationSimple() } ">
        <g:if test="${execution.dateCompleted}">
            <g:relativeDate start="${execution.dateStarted}" end="${execution.dateCompleted}"/>
        </g:if>
        </span>
        <span class="timerel"><g:message code="at" default="at"/>
            <span data-bind="text: formatTimeAtDate(endTime()), attr: {title: endTime() }">
            <g:if test="${execution.dateCompleted}">
                <g:relativeDate atDate="${execution.dateCompleted}"/>
            </g:if>
            </span>
        </span>
    </span>
