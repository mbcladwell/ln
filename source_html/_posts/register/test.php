
<?php


$db = new mysqli('192.254.187.215','plapan_lic_admin','welcome','plapan_license', '3306');

if ($db->connect_errno) {
    die($db->connect_error);
}


//ini_set("allow_url_fopen", 1);
$url = 'https://blockchain.info/charts/market-price?format=json';
$obj = json_decode(file_get_contents($url), true);
//echo var_dump($obj);

$myArray = array();
foreach ($obj['values'] as &$value) {
    array_push($myArray, $value[y]);
}

$a = array_filter($myArray);
if(count($a)) {
    echo $average = array_sum($a)/count($a);
    echo round($average, 4);
}


//echo $obj['values'][1][y];

echo "  end of script;"
?>
