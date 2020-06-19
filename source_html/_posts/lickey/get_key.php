<?php
ini_set("include_path", '/home2/plapan/php:' . ini_get("include_path") );
$db = new mysqli('192.254.187.215','plapan_lic_admin','welcome','plapan_license', '3306');

if ($db->connect_errno) {
    die($db->connect_error);
}

$clean['email'] = $db->real_escape_string($_POST[email]);

$result = $db->query("SELECT wallet_id,  license_key, valid  FROM customer, payment WHERE payment.customer_id=customer.id AND customer.email='{$clean['email']}'");

if (!$result) {
    echo "Database Error";
} else {
$row = mysqli_fetch_array($result);
$w_id = $row[0];
$lic_key = $row[1];
$is_valid = $row[2];
  }

if(!$is_valid){
  $lic_key = "<Unpaid License Fee>";
}
    

mysqli_close($db);

$to = "peter.lapan@labsolns.com";
$subject = "LIMS*Nucleus License Key Retrieval";
$txt = "Wallet ID: " . $w_id."\r\nemail: ".$clean['email'];
$headers = "From: info@labsolns.com" . "\r\n" .

mail($to,$subject,$txt,$headers);



?>
<html>
<head>
        <link rel="icon" href="/images/las.png">  
<style>
body {
  background-color: #f2f2f2;
  font-family: "Arial";
}

</style>
 </head>
<body>
<img src="../images/las.png">    <br>

<h2>License Key Retrieval</h2>


<table>
<tr><td>Wallet ID:</td><td><input type="text" value="<?php echo $w_id ?>" id="myInput" size=40></td>

<!-- The button used to copy the text -->
<td> <button onclick="myFunction()">Copy Wallet ID to clipboard</button></td></tr>
<tr><td>License key:</td>
     <td><input type="text" value="<?php echo $lic_key ?>" id="genPasswordButton" size=40></td>

<!-- The button used to copy the text -->
<td><button onclick="copyPassword()">Copy License Key to clipboard</button></td></tr>
</table>

<script>


function copyPassword() {
  /* Get the text field */
  var copyText = document.getElementById("genPasswordButton");

  /* Select the text field */
  copyText.select();
  copyText.setSelectionRange(0, 99999); /*For mobile devices*/

  /* Copy the text inside the text field */
  document.execCommand("copy");

  /* Alert the copied text */
  alert("Copied the text: " + genPasswordButton);
} 

function myFunction() {
  /* Get the text field */
  var copyText = document.getElementById("myInput");

  /* Select the text field */
  copyText.select();
  copyText.setSelectionRange(0, 99999); /*For mobile devices*/

  /* Copy the text inside the text field */
  document.execCommand("copy");

  /* Alert the copied text */
  alert("Copied the text: " + myInput);
} 


</script>

</body>
</html>

 
