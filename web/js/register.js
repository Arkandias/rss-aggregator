$(function() {
	$("#submit-button").click(function(event) {
		var password = ($("#register-form input[name='password']").val() == $("#register-form input[name='confirm']").val() ? $("#register-form input[name='password']").val() : null)
		if (password == null) {
			$("#register-form input[name='confirm']").val("");
			$("#register-form input[name='confirm']").addClass('warning').attr('placeholder', 'Password mismatch');
			return;
		}
		var data = {
			login: $("#register-form input[name='login']"),
			password: $("#register-form input[name='password']")
		};
		$.ajax({
			url: 'php/register.php',
			type: 'POST',
			data: {login: $("#register-form input[name='login']"), password: password},
		})
		.done(function(msg) {
			if (msg	== "OK") return;
			console.log("Something wrong happened : " + msg)
		})
		.fail(function(msg) {
			console.log("msg");
		});		
	});
});