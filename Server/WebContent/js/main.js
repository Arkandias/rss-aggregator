$(function() {
	console.log('"ltest')
	$('#search-button').click(function(event) {
		$.getFeed({
			url: "php/get_rss.php",
			data: {url: $('#search-bar').val()},
			success: function (feed) {
				$('#body-wrap > *').remove();
				$("#body-wrap").append(
					$("<div></div>").addClass('rss-title').append("<h2><a href='" + feed.link + "'>" + feed.title + "</a></h2><p class='link'>" + feed.updated + "</p>")
				);
				for (var i = feed.items.length - 1; i >= 0; i--) {
					$("#body-wrap").append(
						$("<div></div>").addClass('rss-item')
						.append($("<div><a href='" + feed.items[i].link + "'>" + feed.items[i].title + "</a></div>").addClass('title'))
						.append("<p class='date'>" + feed.items[i].updated + "</p>" + feed.items[i].description)
					)
					feed.items[i]
				}
			}
		});
	});
	$('#login-button button')
	$('#login-button button').click(function(event) {
		$.ajax({
			url: 'api/connectUser',
			type: 'POST',
			dataType: "json",
			data: {user:"co", pwd:"co"}
		})
		.done(function(msg) {
			console.log(msg);
		})
		.fail(function(msg) {
			console.log(msg);
		})
		.always(function() {
			console.log("complete");
		});
	});
});