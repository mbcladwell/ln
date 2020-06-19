<?php

$email = $_GET[email];


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

<h2>Request for Invoice</h2>

Thank you for your request.<br><br>

An invoice in the amount of $5000 for a LIMS*Nucleus license key<br><br>
will be emailed to: <?php echo $email?><br><br><br><br>

<a href="mailto:info@labsolns.com?Subject=License Key Invoice" target="_top">info@labsolns.com</a>

<script>



</script>

</body>
</html>

 
