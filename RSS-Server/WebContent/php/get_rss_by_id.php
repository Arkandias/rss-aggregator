<?php 
include 'DBLink.php';

$id = $_GET['id'];

$conn = get_connection();
$result = mysqli_query($conn, "SELECT link FROM rss_domain WHERE {$id} = id");
if (mysqli_error($conn) != "") {
	echo mysqli_error($conn);
} else {
	$link = mysqli_fetch_row($result)[0];
	echo file_get_contents($link);
}
mysqli_close($conn);