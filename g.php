<?php /* GPS LOCATOR */

/*

	THIS IS AN EXAMPLE OF THE POST SERVICE TO PLACE TO A PHP ONLINE SERVER
	You can record pos and put it on a googlemap for example :)
*/

try
{
    // filing
    $f = $_POST['usr'].".txt";
    $fh = fopen($f,'a');
    $s = date("d.m.y G:i:s").' '.$_POST['ctc']."\n";
    fwrite($fh, $s);
    fclose($fh);
    
    echo '1';
    
    // emailing
    $to      = 'yourEmail@server.com';
    $subject = 'gps-'.$_POST['usr'];
    $message = $s;
    $headers = 'From: /webServices/'.$_POST['usr'].'@g.php' . "\r\n" .
     'Reply-To: serverEmail@server.com' . "\r\n" .
     'X-Mailer: PHP/' . phpversion();
    
    //mail($to, $subject, $message, $headers);
    
}catch(Exception $ex){
    echo '0';
}

?>
