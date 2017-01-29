<?php
function get_connection()
{
	$conn = mysqli_connect(
		"localhost",
		"root",
		"",
		"rssaggregatordb"
	);
	return $conn;
}
