$.ajax({
	url: 'http://localhost:2345/connect',
	type: 'POST',
	data: {user:"prout", pwd:"prout"}
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
