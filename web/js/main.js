$(function() {
	$.getFeed({
		url     : "http://xkcd.com/rss.xml",
		success : function (feed) {
		  console.log(feed);
		  // do more stuff here
		}
	});
});