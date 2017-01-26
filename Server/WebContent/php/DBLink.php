<?php
function get_connection()
{
	$conn = mysqli_connect(
		"localhost",
		"root",
		"",
		"rssaggregatordb"
	);
	echo json_encode($conn);
	return $conn;
}