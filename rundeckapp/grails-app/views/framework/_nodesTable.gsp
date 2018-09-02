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

<g:if test="${params.declarenone && nodes.size()<1}">
    <span class="warn note">None</span>
</g:if>
<g:if test="${nodes && nodes.size()>0}">
    <g:if test="${tagsummary}">
    <div class="row">
    <div class="col-sm-12">
    <div class=" col-inset">
            <g:render template="tagsummary" model="${[tagsummary:tagsummary,link:[action:'nodes',controller:'framework',param:'nodeIncludeTags']]}"/>
    </div>
    </div>
    </div>
    </g:if>
    <div class="row row-space">
    <div class="col-sm-12 ">
    <table cellpadding="0" cellspacing="0" width="100%" id="nodesTable" class="nodesTable">
        <g:render template="nodesTableContent" model="${[nodes:nodes, colkeys: colkeys,params:params,total:total,allcount:allcount,page:page,max:max,nodeauthrun:nodeauthrun]}"/>
    </table>
    </div>
    </div>
    <g:if test="${page==0 && (page+1*max<total)}">
        <div class="row row-space" data-bind="visible: hasMoreNodes">
        <div class="col-sm-12" id="nodesPaging">
            <g:set var="remainCount" value="${total - (page+1*max)}"/>
            <g:if test="${remainCount>max}">
            <span
                    id="nextPageButton"
                    class="btn btn-default nodes_paging_next"
                    data-node-table-id="nodesTable"
                    data-node-paging-id="nodesPaging"
                    data-bind="click: updateNodesNextPage, visible: hasMultiplePages"
                    title="Load next ${enc(attr:max)} nodes...">Next <g:enc>${max}</g:enc>&hellip;</span>
            </g:if>
            <span
                    class="btn btn-default nodes_paging_all"
                    data-node-table-id="nodesTable"
                    data-node-paging-id="nodesPaging"
                    data-bind="click: updateNodesRemainingPages"
                    title="Load all remaining nodes...">Load <span data-bind="text: pageRemaining"></span> remaining&hellip;</span>
        </div>
        </div>
    </g:if>
</g:if>
<g:render template="/common/boxinfo" model="${[name:'nodetable',model:[total:nodes?.size(),allcount:total,filter:query?.filter]]}"/>

<div id="remoteEditholder" style="display:none" class="popout">
    <span id="remoteEditHeader">
            <span class="welcomeMessage">Edit node: <i class="rdicon node icon-small"></i> <span id="editNodeIdent"></span></span>
    </span>
    <span class="toolbar" id="remoteEditToolbar">
        <span class="action " onclick="_remoteEditCompleted();" title="Close the remote edit box and discard any changes"><g:img file="icon-tiny-removex-gray.png" /> Close remote editing</span>
    </span>
    <div id="remoteEditResultHolder" class="info message" style="display:none">
        <span id="remoteEditResultText" class="info message" >
        </span>
        <span class="action " onclick="_remoteEditContinue();"> Continue&hellip;</span>
    </div>
    <div id="remoteEditError" class="error note" style="display:none">
    </div>
    <div id="remoteEditTarget" >

    </div>
</div>
