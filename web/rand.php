<?php
$min = filter_input(INPUT_GET, 'min', FILTER_SANITIZE_STRING);
$max = filter_input(INPUT_GET, 'max', FILTER_SANITIZE_STRING);

if ($min >= 0 && $max <= 9999) {
	echo rand($min, $max);
} else {
	echo 0;
}
?>
