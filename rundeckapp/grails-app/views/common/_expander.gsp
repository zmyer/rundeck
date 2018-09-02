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
<span class="${enc(attr:classnames?:'')} ${classnames&&classnames.indexOf('textbtn-')>=0?'': 'textbtn-default'} textbtn  expandComponentControl toggle ${open=='true'?'expanded':'closed'}" onmousedown="${raw(enc(attr:(key?'Expander.toggle(this,\''+enc(attr:enc(js:key))+'\')':jsfunc?jsfunc:'Expander.toggle(this)')))};return false;" style="padding:2px;${enc(attr:style)}" id="_exp_${enc(attr:key)}">
  <g:enc rawtext="${text != null && !imgfirst ? text:''}"/>
  <g:if test="${hideGlyphicon != 'true'}">
    <b class="glyphicon glyphicon-chevron-${open == 'true' ? 'down' : 'right'}"></b>
  </g:if>
  <g:enc rawtext="${text != null && imgfirst ? text:''}"/>
</span>
