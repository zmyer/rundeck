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
  Created by IntelliJ IDEA.
  User: greg
  Date: 10/4/13
  Time: 10:23 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page import="grails.converters.JSON; com.dtolabs.rundeck.server.authorization.AuthConstants" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <meta name="layout" content="base"/>
    <meta name="tabpage" content="home"/>
    <title><g:appTitle/></title>
    <g:if test="${projectNames.size()<50}">
        <g:embedJSON data="${[projectNames:projectNames,projectNamesTotal:projectNames.size()]}" id="projectNamesData"/>
    </g:if>
    <g:else>
        <g:embedJSON data="${[projectNames:projectNames[0..49],projectNamesTotal:projectNames.size()]}" id="projectNamesData"/>
    </g:else>
    <g:embedJSON data="${[loaded:true,execCount:execCount,totalFailedCount:totalFailedCount,recentUsers:recentUsers,recentProjects:recentProjects]}" id="statsData"/>
    <g:embedJSON data="${[
            pagingInitialMax:grailsApplication.config.rundeck?.gui?.home?.projectList?.pagingInitialMax?:15,
            pagingRepeatMax:grailsApplication.config.rundeck?.gui?.home?.projectList?.pagingRepeatMax?:50,
            summaryRefresh:!(grailsApplication.config.rundeck?.gui?.home?.projectList?.summaryRefresh in ['false',false]),
            refreshDelay:grailsApplication.config.rundeck?.gui?.home?.projectList?.summaryRefreshDelay?:30000,
            doPaging:!(grailsApplication.config.rundeck?.gui?.home?.projectList?.doPaging in ['false',false]),
            pagingDelay:grailsApplication.config.rundeck?.gui?.home?.projectList?.pagingDelay?:2000
    ]}" id="homeDataPagingParams"/>
    <asset:javascript src="menu/home.js"/>

</head>
<body>

<div class="row">
    <div class="col-sm-12">
        <g:render template="/common/messages"/>
    </div>
</div>
<div class="container-fluid" data-bind="if: projectCount()>0 || !loadedProjectNames()">
    <div class="row">
        <g:if test="${isFirstRun}">
            <div class="col-sm-12">
              <div class="card">
                <div class="card-header">
                  <h2 class="card-title">
                    <g:message code="app.firstRun.title" args="${[g.appTitle(),grailsApplication.metadata['build.ident']]}"/>
                  </h2>                  
                </div>
                  <div class="card-content">
                    <g:markdown><g:autoLink>${message(code: "app.firstRun.md")}</g:autoLink></g:markdown>
                    <p class="h6 text-primary" style="margin-top:1em;">
                        <g:message code="you.can.see.this.message.again.by.clicking.the" />
                        <g:link action="welcome" controller="menu"><g:message code="version.number" /></g:link>
                        <g:message code="in.the.page.footer" />
                    </p>
                  </div>
              </div>
            </div>
        </g:if>
        <div class="col-sm-12 col-md-5">
          <div class="card">
            <div class="card-content" style="padding-bottom: 20px;">
              <span class="h3 text-primary">
                  <span data-bind="messageTemplate: projectNamesTotal, messageTemplatePluralize:true">
                      <g:message code="page.home.section.project.title" />|<g:message code="page.home.section.project.title.plural" />
                  </span>
              </span>
              <auth:resourceAllowed action="create" kind="project" context="application">
                <g:link controller="framework" action="createProject" class="btn  btn-success pull-right">
                    <g:message code="page.home.new.project.button.label" />
                    <b class="glyphicon glyphicon-plus"></b>
                </g:link>
              </auth:resourceAllowed>
            </div>
            <!--
            <div class="card-footer">
              <hr>
              <div class="row">
                <auth:resourceAllowed action="create" kind="project" context="application">
                    <div class="col-sm-4">
                        <g:link controller="framework" action="createProject" class="btn  btn-success pull-right">
                            <g:message code="page.home.new.project.button.label" />
                            <b class="glyphicon glyphicon-plus"></b>
                        </g:link>
                    </div>
                </auth:resourceAllowed>
              </div>
            </div>
          -->
          </div>
        </div>
        <div class="col-sm-12 col-md-7">
          <div class="card">
            <div class="card-content">
              <span data-bind="if: !loaded()">
                  <asset:image class="loading-spinner" src="spinner-gray.gif" width="32px" height="32px"/>
              </span>
              <div data-bind="if: projectCount() > 1 && loaded()">
                %{--app summary info--}%
                  <span class="h4">
                    <span class="summary-count" data-bind="css: { 'text-info': execCount()>0, 'text-primary': execCount()<1 }">
                          <span data-bind="text: execCount"></span>
                    </span>
                    <span data-bind="messageTemplate: execCount, messageTemplatePluralize:true">
                      <g:message code="Execution" />|<g:message code="Execution.plural" />
                    </span>
                    <g:message code="page.home.duration.in.the.last.day" />
                    <span class="summary-count" data-bind="css: { 'text-warning': totalFailedCount()>0, 'text-primary': totalFailedCount()<1 }">
                      <span data-bind="messageTemplate: totalFailedCount">
                        <g:message code="page.home.project.executions.0.failed.parenthetical" />
                      </span>
                    </span>
                  </span>
                  <div data-bind="if: recentProjectsCount()>1">
                    <g:message code="in" />
                    <span class="text-info" data-bind="text: recentProjectsCount()"></span>
                    <g:message code="Project.plural" />:
                    <span data-bind="foreach: recentProjects">
                      <a href="${g.createLink(action:'index',controller:'menu',params:[project:'<$>'])}" data-bind="urlPathParam: $data, text: $data"></a>
                    </span>
                  </div>
                  <div data-bind="if: recentUsersCount()>0">
                    <g:message code="by" />
                    <span class="text-info" data-bind="text: recentUsersCount"></span>
                    <span data-bind="messageTemplate: recentUsersCount(),messageTemplatePluralize:true">
                        <g:message code="user" />:|<g:message code="user.plural" />:
                    </span>
                    <span data-bind="text: recentUsers().join(', ')"></span>
                  </div>
                </div>
            </div>
          </div>
        </div>
    </div>
</div>
<g:if test="${projectNames.size()<1}">
  <div class="container-fluid">
    <div class="row">
        <div class="col-sm-12">
          <div class="card">
            <div class="card-content">
              <auth:resourceAllowed action="create" kind="project" context="application" has="false">
                  <div class="well">
                      <g:set var="roles" value="${request.subject?.getPrincipals(com.dtolabs.rundeck.core.authentication.Group.class)?.collect { it.name }?.join(", ")}"/>
                      <h2 class="text-warning">
                          <g:message code="no.authorized.access.to.projects" />
                      </h2>
                      <p>
                          <g:message code="no.authorized.access.to.projects.contact.your.administrator.user.roles.0" args="[roles]" />
                      </p>
                  </div>
              </auth:resourceAllowed>
              <auth:resourceAllowed action="create" kind="project" context="application" has="true">
                  <div class="jumbotron">
                      <h2><g:message code="app.firstRun.title" args="${[g.appTitle(),grailsApplication.metadata['build.ident']]}"/></h2>
                      <g:markdown><g:autoLink>${message(code: "app.firstRun.md")}</g:autoLink></g:markdown>
                      <p>
                          <g:message code="page.home.get.started.message" />
                      </p>
                      <p>
                          <g:link controller="framework" action="createProject" class="btn  btn-success btn-lg ">
                              <g:message code="page.home.new.project.button.label" />
                              <b class="glyphicon glyphicon-plus"></b>
                          </g:link>
                      </p>
                      <span class="text-small text-primary">
                          <g:message code="you.can.see.this.message.again.by.clicking.the" />
                          <g:link action="welcome" controller="menu"><g:message code="version.number" /></g:link>
                          <g:message code="in.the.page.footer" />
                      </span>
                  </div>
              </auth:resourceAllowed>
            </div>
          </div>
        </div>
    </div>
  </div>
</g:if>
<div class="container-fluid">
  <div class="row">
    <div class="col-xs-12">
      <div class="card">
        <div class="card-content">
          <div data-bind="if: !loadedProjectNames() && projectCount()<1">
            <div class="">
                <g:message code="page.home.loading.projects" />
                <asset:image class="loading-spinner" src="spinner-gray.gif" width="32px" height="32px"/>
            </div>
          </div>
          <div data-bind="if: projectCount()>0">
            <div class="input-group">
              <!-- <span class="input-group-addon"><i class="fa fa-search"></i></span> -->
              <input type="search" name="search" placeholder="${message(code:"page.home.search.projects.input.placeholder")}" class="form-control input-sm" data-bind="value: search" />
              <span class="input-group-addon"><g:icon name="search"/></span>
            </div>
            <div data-bind="if: search()">
              <div class="alert alert-info">
                <span data-bind="messageTemplate: searchedProjectsCount(), messageTemplatePluralize:true, css: { 'text-info': searchedProjectsCount()>0, 'text-warning': searchedProjectsCount()<1 }">
                    <g:message code="page.home.search.project.title" />|<g:message code="page.home.search.project.title.plural" />
                </span>
              </div>
            </div>
          </div>
          <div data-bind="foreach: { data: searchedProjects(), as: 'project' } ">
          %{--Template for project details--}%
            <div class="project_list_item" data-bind="attr: { 'data-project': project }, ">
              <div class="row">
                <div class="col-sm-6 col-md-4">
                  <a href="${g.createLink(action:'index',controller:'menu',params:[project:'<$>'])}" data-bind="urlPathParam: project" class="h3">
                    <i class="fas fa-archive"></i>
                    <!-- <i class="glyphicon glyphicon-tasks"></i> -->
                    <span data-bind="if: $root.projectForName(project) && $root.projectForName(project).label">
                      <span data-bind="text: $root.projectForName(project).label"></span>
                    </span>
                    <span data-bind="ifnot: $root.projectForName(project) && $root.projectForName(project).label">
                      <span data-bind="text: project"></span>
                    </span>
                  </a>
                    <span data-bind="if: !$root.projectForName(project).executionEnabled()">
                        <span class=" text-warning  has_tooltip" data-placement="right" data-bind="bootstrapTooltip: true" title="${message(code:'project.execution.disabled')}">
                            <i class="glyphicon glyphicon-pause"></i>
                        </span>
                    </span>
                    <span data-bind="if: !$root.projectForName(project).scheduleEnabled()">
                        <span class=" text-warning has_tooltip"  data-placement="right"  data-bind="bootstrapTooltip: true" title="${message(code:'project.schedule.disabled')}">
                            <i class="glyphicon glyphicon-ban-circle"></i>
                        </span>
                    </span>

                  <div data-bind="if: $root.projectForName(project)">
                    <span class="text-info" data-bind="text: $root.projectForName(project).description"></span>
                  </div>

                </div>
                  <div class="col-sm-6 col-md-4">
                    <span data-bind="if: $root.projectForName(project)">
                      <a class="h4" data-bind="css: { 'text-primary': $root.projectForName(project).execCount()<1 }, urlPathParam: project " href="${g.createLink(controller: "reports", action: "index", params: [project: '<$>'])}">
                        <span class="summary-count " data-bind="css: { 'text-primary': $root.projectForName(project).execCount()<1, 'text-info':$root.projectForName(project).execCount()>0 } ">
                            <span data-bind="text: $root.projectForName(project).loaded()?$root.projectForName(project).execCount():''"></span>
                            <span data-bind="if: !$root.projectForName(project).loaded()" >...</span>
                          </span>
                          <span data-bind="messageTemplate: $root.projectForName(project).execCount(), messageTemplatePluralize: true">
                              <g:message code="Execution" />|<g:message code="Execution.plural" />
                          </span>
                          <g:message code="page.home.duration.in.the.last.day" /></a>

                          <span data-bind="if: $root.projectForName(project).failedCount()>0">
                              <a data-bind="urlPathParam: project "
                                 class="text-warning"
                                 href="${g.createLink(
                                         controller: "reports",
                                         action: "index",
                                         params: [project: '<$>', statFilter: 'fail']
                                 )}">
                                  <span data-bind="messageTemplate: $root.projectForName(project).failedCount()">
                                      <g:message code="page.home.project.executions.0.failed.parenthetical"/>
                                  </span>
                              </a>
                          </span>
                        <div>
                          <div data-bind="if: $root.projectForName(project).userCount()>0">
                            <g:message code="by" />
                            <span class="text-info" data-bind="text: $root.projectForName(project).userCount()">
                            </span>
                            <span data-bind="messageTemplate: $root.projectForName(project).userCount(),messageTemplatePluralize:true">
                                <g:message code="user" />:|<g:message code="user.plural" />:
                            </span>
                            <span data-bind="text: $root.projectForName(project).userSummary().join(', ')"></span>
                          </div>
                        </div>
                      </span>
                    </div>
                    <div data-bind="if: $root.projectForName(project)">
                      <div class="col-sm-12 col-md-4" >
                        <div class="pull-right">
                          <span data-bind="if: !$root.projectForName(project).loaded()">
                              <g:img class="loading-spinner" file="spinner-gray.gif" width="24px" height="24px"/>
                          </span>
                          <span data-bind="if: $root.projectForName(project).auth().admin">
                              <a href="${g.createLink(controller: "framework", action: "editProject", params: [project: '<$>'])}"
                                  data-bind="urlPathParam: project"
                                 class="btn btn-default btn-sm">
                                  <g:message code="gui.menu.Admin"/>
                              </a>
                          </span>
                          <div class="btn-group " data-bind="if: $root.projectForName(project).auth().jobCreate">
                            <button type="button" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown">
                                <g:message code="create.job.button.label" />
                                <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu pull-right" role="menu">
                              <li>
                                  <a href="${g.createLink(controller: "scheduledExecution", action: "create", params: [project: '<$>'])}" data-bind="urlPathParam: project">
                                    <i class="glyphicon glyphicon-plus"></i>
                                    <g:message code="new.job.button.label" />
                                </a>
                              </li>
                              <li class="divider"></li>
                              <li>
                                <a href="${g.createLink(controller: "scheduledExecution", action: "upload", params: [project: '<$>'])}" data-bind="urlPathParam: project" class="">
                                  <i class="glyphicon glyphicon-upload"></i>
                                    <g:message code="upload.definition.button.label" />
                                </a>
                              </li>
                            </ul>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div data-bind="if: $root.projectForName(project)">
                    <div class="row row-space" data-bind="if: $root.projectForName(project).showMessage() ">
                      <div class="col-sm-12">
                        <div data-bind="if: $root.projectForName(project).showMotd() ">
                          <span data-bind="if: $root.projectForName(project).readme().motdHTML()">
                              <span data-bind="html: $root.projectForName(project).readme().motdHTML()"></span>
                          </span>
                        </div>
                        <div data-bind="if:  $root.projectForName(project).showReadme() ">
                          <span data-bind="if: $root.projectForName(project).readme().readmeHTML()">
                              <span data-bind="html: $root.projectForName(project).readme().readmeHTML()"></span>
                          </span>
                        </div>
                      </div>
                    </div>
                  </div>
              </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
