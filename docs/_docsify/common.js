/*
 * Copyright © 2016~2020 leeyazhou (coderhook@gmail.com)
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
window.$docsify = {
	name : 'CRPC文档',
	repo : 'https://github.com/leeyazhou/crpc',
	loadSidebar : "_docsify/_sidebar.md",
	loadNavbar : "_docsify/_navbar.md",
	auto2top : true,
	search : 'auto',
	mergeNavbar: true,
	executeScript: true,
    maxLevel: 4,
	subMaxLevel : 1,
	coverpage : {
		"/" : "_docsify/_coverpage.md"
	},
	toc : {
		scope : '.markdown-section',
		headings : 'h1, h2, h3',
		title : 'Contents',
	},
	footer : {
		copy : '<span>CRPC &copy; 2019 ~ 2020</span>',
		auth : 'leeyazhou',
		pre : '',
		style : 'text-align: middle;'
	},
	remoteMarkdown: {
		tag: 'remoteMarkdownUrl',
	},
	plugins : [ EditOnGithubPlugin
			.create('https://github.com/leeyazhou/crpc/tree/master/docs/')]
}
