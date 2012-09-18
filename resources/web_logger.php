<?php
 
function show_logs() {
	$date = new DateTime();
	$date_str = $date->format('Y-m-d');
	$current_log = fopen("./logs/logs-".$date_str, 'r');
	$entry = null;
	echo '<html><body>';
	while( $entry = fgets($current_log)) {
		$fields = split("<!>", $entry);
		echo '<div class="entry">';
		echo '<span class="time">'.$fields[0].'</span><br/>';
		echo '<span class="origin">'.$fields[1].'</span><br/>';
		echo '<span class="message">'.$fields[2].'</span><br/>';
		echo '</div>';
	}
	echo '</body></html>';
}

function clear_logs() {
	rmdir('./logs');
	mkdir('./logs');
}

function add_log($time, $origin, $message) {
	$date = new DateTime();
	$date_str = $date->format('Y-m-d');
	
	if ( ! is_dir('./logs') ){
		mkdir('./logs');
	}
	$current_log = fopen("./logs/logs-".$date_str, 'a');
	$entry = '['.$time.']<!>'.$origin.'<!>'.$message.'\r\n';
	fwrite($current_log, $entry);
	fclose($current_log);
}


if ($_SERVER['REQUEST_METHOD'] == 'GET') {
	show_logs();
} elseif ($_SERVER['REQUEST_METHOD'] == 'PUT') {
	$put_vars = array();
	parse_str(file_get_contents("php://input"),$put_vars);
	var_dump($put_vars);
	add_log($put_vars['time'], $put_vars['origin'], $put_vars['message']);
} elseif ($_SERVER['REQUEST_METHOD'] == 'POST') {
	var_dump($_POST);
	add_log($_POST['time'], $_POST['origin'], $_POST['message']);
} elseif ($_SERVER['REQUEST_METHOD'] == 'DELETE') {
	clear_logs();
}

?>