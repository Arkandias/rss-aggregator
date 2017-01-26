<?php
include 'DBLink.php';

$login = $_GET['login'];
$password = $_GET['password'];

$conn = get_connection();
$result = mysqli_query($conn, "INSERT INTO user ('id', 'login', 'password') VALUES (NULL, '{$login}', '{$password}')");
if (mysqli_error($conn) != "") {
	echo json_encode(mysqli_error($conn));
} else {
	echo "{'msg': 'OK'}";
}
mysqli_close($conn);