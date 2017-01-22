<?php 
$url = $_GET['url'];
if ($url == "") {
	echo "Error: No URL provided";
	return ;
}

echo file_get_contents($url);