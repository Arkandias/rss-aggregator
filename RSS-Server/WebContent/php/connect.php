<?php
include 'DBLink.php';

$login = $_POST['user'];
$password = $_POST['pwd'];

$conn = get_connection();
$result = mysqli_query($conn, "SELECT USER.id, ASSOC.rss_domain_id FROM user USER, user_domain_assoc ASSOC WHERE ASSOC.user_id = USER.id AND login = '{$login}' AND password = '{$password}'");
if (mysqli_error($conn) != "") {
	echo mysqli_error($conn);
} else {
	$return = array();
	while ($row = mysqli_fetch_row($result)) {
		array_push($return, $row[1]);
	}
	echo json_encode($return);
}

mysqli_close($conn);