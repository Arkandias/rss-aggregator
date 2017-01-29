$(function() {
	var LOGIN_HTML = '<form><label for="login">Login:</label><input type="text" name="login" id="login-input"><label for="password">Password:</label><input type="password" name="password" id="password-input"></form><a id="login-button"><button>Login</button></a><a href="register.html" id="register-button"><button>Register</button></a>'

	if ($.cookie('rss-aggregator')) {
		var login = $.cookie('rss-aggregator');
		$('#login-div > *').remove()
		$('#login-div').append("<div>Welcome, " + login + ".</div><button id='logout-button'>Logout</button>");
		$('#logout-button').click(function() {
			$('#login-div > *').remove()
			$('#body-wrap > *').remove();
			$('#login-div').append(LOGIN_HTML);
			$.removeCookie('rss-aggregator');
		});
		$.ajax({
			url: 'php/get_rss_list.php',
			type: 'POST',
			dataType: 'json',
			data: {login: login},
		})
		.done(function(msg) {
			for (var i = msg.length - 1; i >= 0; i--) {
				$.getFeed({
					url: 'php/get_rss_by_id.php',
					type: 'GET',
					dataType: 'json',
					data: {id: msg[i]},
					success: function (feed) {
						addRSSFeed(feed, msg[i]);
					}
				})
			}
		})
		.fail(function() {
			console.log("error");
		})		
	}

	$('#search-button').click(function() {
		$.getFeed({
			url: "php/get_rss.php",
			data: {url: $('#search-bar').val()},
			success: function (feed) {
				$('#body-wrap > *').remove();
				addRSSFeed(feed);
			}
		});
	});

	$('#login-button button').click(function() {
		if ($('#login-input').val() == "" || $('#password-input').val() == "") {
			return;
		}
		$.ajax({
			url: 'php/connect.php',
			type: 'POST',
			dataType: "json",
			data: {user: $('#login-input').val(), pwd: $('#password-input').val()}
		})
		.done(function(msg) {
			if (msg.length == 0) {
				return ;
			}
			$.cookie('rss-aggregator', $('#login-input').val(), {expires: 1})

			var login = $('#login-input').val();
			$('#login-div > *').remove()
			$('#login-div').append("<div>Welcome, " + login + ".</div><button id='logout-button'>Logout</button>");
			$('#logout-button').click(function() {
				$('#login-div > *').remove()
				$('#body-wrap > *').remove();
				$('#login-div').append(LOGIN_HTML);
				$.removeCookie('rss-aggregator');
			});
			for (var i = msg.length - 1; i >= 0; i--) {
				$.getFeed({
					url: 'php/get_rss_by_id.php',
					type: 'GET',
					dataType: 'json',
					data: {id: msg[i]},
					success: function (feed) {
						addRSSFeed(feed, msg[i]);
					}
				})
			}
		})
		.fail(function(error) {
			if (error != "KO")
				$('body').append(error.responseText)
		});
	});

	function addRSSFeed(feed, link) {
		let div = $('<div></div>').addClass('rss-wrap').append(
			$("<div></div>").addClass('rss-title').append("<h2><a href='" + feed.link + "'>" + feed.title + "</a></h2><p class='link'>" + feed.updated + "</p>")
		).appendTo('#body-wrap');
		for (var i = feed.items.length - 1; i >= 0; i--) {
			div.append(
				$("<div></div>").addClass('rss-item')
				.append($("<div><a href='" + feed.items[i].link + "'>" + feed.items[i].title + "</a></div>").addClass('title'))
				.append("<p class='date'>" + feed.items[i].updated + "</p>" + feed.items[i].description)
			)
			feed.items[i]
		}

	}
});