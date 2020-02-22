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
