$.ajax({
	url: 'api/connectUser',
	type: 'POST',
	data: {user:"co", pwd:"co"}
})
.done(function() {
	console.log("success");
})
.fail(function(msg) {
	console.log(msg);
})
.always(function() {
	console.log("complete");
});
