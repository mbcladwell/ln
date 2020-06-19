<html>
    <head><meta name="generator" content="Hexo 3.8.0">
  <meta charset="utf-8">
  
  <title>License | Laboratory Automation Solutions</title>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <meta name="description" content="Copyright 2019 Laboratory Automation Solutions info@labsolns.comThis file is part of LIMS*Nucleus.LIMS*Nucleus can not be copied and/or distributed without the express permission of Laboratory Automat">

<meta property="og:site_name" content="Laboratory Automation Solutions">
<meta property="og:description" content="Copyright 2019 Laboratory Automation Solutions info@labsolns.comThis file is part of LIMS*Nucleus.LIMS*Nucleus can not be copied and/or distributed without the express permission of Laboratory Automat">
<meta property="og:locale" content="default">
<meta property="og:updated_time" content="2019-09-26T10:17:27.765Z">
<meta name="twitter:card" content="summary">
<meta name="twitter:title" content="License">
<meta name="twitter:description" content="Copyright 2019 Laboratory Automation Solutions info@labsolns.comThis file is part of LIMS*Nucleus.LIMS*Nucleus can not be copied and/or distributed without the express permission of Laboratory Automat">
  
    <link rel="icon" href="/css/images/las2.ico">
  
    <link href="//fonts.googleapis.com/css?family=Source+Code+Pro" rel="stylesheet" type="text/css">
  
  <link rel="stylesheet" href="/software/css/style.css">
  
</head>
<body>
<?php


$db = new mysqli('192.254.187.215','plapan_lic_admin','welcome','plapan_license', '3306');

if ($db->connect_errno) {
    die($db->connect_error);
}


$clean['fname'] = $db->real_escape_string($_POST[fname]);
$clean['lname'] = $db->real_escape_string($_POST[lname]);
$clean['institution'] = $db->real_escape_string($_POST[institution]);
$clean['email'] = $db->real_escape_string($_POST[email]);
$clean['currency'] = $db->real_escape_string($_POST[currency]);



$result = $db->query("CALL new_customer('{$clean['fname']}','{$clean['lname']}','gi','{$clean['email']}', '{$clean['currency']}', @returned_wallet_id )");


if (!$result) {
    echo "Database Error";
} else {
$row = mysqli_fetch_array($result);
$w_id = $row[0];
  }

mysqli_close($db);

$to = "peter.lapan@labsolns.com";
$subject = "New LIMS*Nucleus Registration";
$txt = "Wallet ID: " . $w_id;
$headers = "From: webmaster@example.com" . "\r\n" .

mail($to,$subject,$txt,$headers);

?>



<img src="las.png">
<br><br><h3>The <?php echo $clean['currency'] ?> wallet id:</h3>
<br>
<input type="text" value="<?php echo $row[0] ?>" id="myInput" size=40>
<!-- The button used to copy the text -->
<button onclick="myFunction()">Copy to clipboard</button>
<br>
<br>has been assigned to the email: <?php echo $clean['email']?>
<br><br>
<ol>
    <li>Deposit a small amount (10 cents) to the wallet. </li>
    <li>Wait for receipt confirmation</li> 
    <li>Deposit balance</li>
    <li>Enter transaction ID into license dialog box</li>
</ol>
<br><br>

<p>
Questions?:
<a href="mailto:info@labsolns.com?Subject=LIMS*Nucleus%20licensing" target="_top">info@labsolns.com</a>
</p>



<script>


function myFunction() {
  /* Get the text field */
  var copyText = document.getElementById("myInput");

  /* Select the text field */
  copyText.select();
  copyText.setSelectionRange(0, 99999); /*For mobile devices*/

  /* Copy the text inside the text field */
  document.execCommand("copy");

  /* Alert the copied text */
  alert("Copied wallet id to clipboard");
} 
</script>

</body>
</html>
