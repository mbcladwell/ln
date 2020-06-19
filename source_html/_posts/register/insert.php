<?php
ini_set("include_path", '/home2/plapan/php:' . ini_get("include_path") );
$db = new mysqli('192.254.187.215','plapan_lic_admin','welcome','plapan_license', '3306');

if ($db->connect_errno) {
    die($db->connect_error);
}

$proceed = true;

$clean['fname'] = $db->real_escape_string($_POST[fname]);
$clean['lname'] = $db->real_escape_string($_POST[lname]);
$clean['institution'] = $db->real_escape_string($_POST[institution]);
$clean['email'] = $db->real_escape_string($_POST[email]);

$box1 = $db->real_escape_string($_POST[box1]);

$converted_box1 = $box1 ? 'true' : 'false';


if (!filter_var($clean['email'], FILTER_VALIDATE_EMAIL)) {
  $comment = "<font color=\"red\">Invalid registration information.<br>Wallet/Customer ID not generated.</font>";
  $proceed = false;
}

if($proceed){

$result1 = $db->query("SELECT wallet_id FROM customer, payment WHERE customer.email='{$clean['email']}' AND payment.customer_id=customer.id");

if(mysqli_num_rows($result1)>0){  //if email already registered
    $row = mysqli_fetch_array($result1);
    $w_id = $row[0];
    }else{
        $result2 = $db->query("CALL new_customer('{$clean['fname']}','{$clean['lname']}', '{$clean['institution']}','{$clean['email']}', 'bitcoin', @returned_wallet_id )");
        if (mysqli_num_rows($result2)==0) {
            echo "Database Error";
            } else {    
            $row2 = mysqli_fetch_array($result2);
            $w_id = $row2[0];
        }
}   

$bcinfo_link = "https://www.blockchain.com/btc/address/".$w_id;

mysqli_close($db);


$to = "peter.lapan@labsolns.com";
$subject = "New LIMS*Nucleus Registration";
$txt = "Wallet ID: " . $w_id. "\r\nemail: ".$clean['email']. "\r\n\ninvoice requested?:  ".$converted_box1;
$headers = "From: info@labsolns.com" . "\r\n" .

mail($to,$subject,$txt,$headers);


    if($box1){
        // requested invoice
        header("Location:creditcard.php?email=".$clean['email']);
        exit();
    }

}  //end of if proceed

//ini_set("allow_url_fopen", 1);
$url = 'https://blockchain.info/q/24hrprice';
$average = json_decode(file_get_contents($url), true);

$btc_needed = round(5000/$average,4);
 require_once("Image/QRCode.php");

$qr_text = "bitcoin ".$w_id."?amount=".$btc_needed;

unlink("qrcode.png");
$qrcode_pre = new Image_QRCode();
$qrcode = $qrcode_pre->makeCode( $qr_text, array('image_type' => 'png', 'output_type' => 'return'));
imagepng($qrcode, "qrcode.png");
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
<br><br>
<p>The current value of Bitcoin is $<?php echo $average ?> per coin, an average of the largest exchanges around the world as tabulated by <a href="https://blockchain.info/q/24hrprice">BlockChainInfo</a>.<br> 
A $5000 license requires <?php echo " ".$btc_needed ?> Bitcoin.</p><br>

Make your payment and retrieve your license key <a href="../lickey/">here</a>.<br><br>

You can monitor progress of the transaction <a href="<?php echo $bcinfo_link ?>">here</a>.<br><br>

<?php echo $comment ?><br><br>

<table>
<tr><td>Wallet ID:</td><td><input type="text" value="<?php echo $w_id ?>" id="myInput" size=40></td>

<!-- The button used to copy the text -->
<td> <button onclick="myFunction()">Copy Wallet ID to clipboard</button></td></tr>
<tr><td>Bitcoin amount:</td>
     <td><input type="text" value="<?php echo $btc_needed ?>" id="genPasswordButton" size=40></td>

<!-- The button used to copy the text -->
<td><button onclick="copyPassword()">Copy Bitcoin amount to clipboard</button></td></tr>
</table>
<br><br><br>
<table>
<tr><td>QR code</td><td><img src="qrcode.png" width="160" height="160"><br></td></tr>
<tr><td>Encoded QR text:</td><td>"<?php echo $qr_text ?>"</td></tr>
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

function getChecked(){
    var box1 = document.getElementById("checkbox1");
}


</script>

</body>
</html>

 
