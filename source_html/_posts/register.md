---
title: Register and obtain a license
categories:
  - Download
tags:
  - license
  - register
comments: false
---

## Credit card payments / G Pay

1. Fill out the fields below and check the "provide invoice" check box.
2. Click the "Submit request" button to obtain an assigned customer ID. An invoice will be sent to the registered email address.  Once payment has been received, you will be provided with a license key via email. Payments are processed by Square.

## Pay with Bitcoin

1. Fill out the fields below and click the "Submit request" button below to obtain an assigned customer ID, which also serves as a Bitcoin Wallet ID. Leave the "provide invoice" box unchecked. The [required Bitcoin amount](/software/license) will be calculated on the fly (using [blockchain.info](https://blockchain.info/)) and presented with the customer ID.
2. Deposit required funds into the wallet.
3. Wait for 6 confirmations (about 60 minutes). A link will be provided that will allow you to monitor progress of the transaction. 

<form action="insert.php" method="post">
<table>
<tr><td></td><td>All fields required</td></tr>
<tr><td>First Name:</td><td><input type="text" name="fname" size="50" /></td></tr>
<tr><td>Last Name: </td><td><input type="text" name="lname" size="50"  /></td></tr>
<tr><td>Institution:</td><td> <input type="text" name="institution" size="50" /></td></tr>
<tr><td>email:</td><td> <input type="text" name="email" size="50" /></td></tr>
<tr><td></td><td><input type="checkbox" name="box1" id="checkbox1" onclick="getChecked()"><label for="box1">  provide invoice</label></td></tr>
<tr><td></td><td><input type="submit" value="Submit request" /></td></tr>
</table>
</form>

