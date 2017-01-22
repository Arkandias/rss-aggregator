$(function() {
	$("#submit-button").click(function(event) {
		var login = $("#register-form input[name='login']").val();
		if (login == "") {
			$("#register-form input[name='login']").attr('placeholder', 'Please enter a login').val("");
		}
		var password = ($("#register-form input[name='password']").val() == $("#register-form input[name='confirm']").val() ? $("#register-form input[name='password']").val() : null)
		if (password == null) {
			$("#register-form input[name='confirm']").val("").addClass('warning').attr('placeholder', 'Password mismatch');
			return;
		}
		var data = {
			login: login,
			password: password
		};
		$.ajax({
			url: 'php/register.php',
			type: 'POST',
			data: {login: $("#register-form input[name='login']"), password: password},
		})
		.done(function(msg) {
			if (msg	== "OK") {
				// Set cookies
				window.location.href="index.php";
				return ;
			}
			console.log("Something wrong happened : " + msg)
		})
		.fail(function(msg) {
			console.log("msg");
		});		
	});
});