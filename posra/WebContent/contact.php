<?php
$field_name = $_POST['cf_name'];
$field_email = $_POST['cf_email'];
$field_message = $_POST['cf_message'];

$mail_to = 'br6@williams.edu';
$subject = 'Message from a site visitor '.$field_name;

$body_message = 'From: '.$field_name."\n";
$body_message .= 'E-mail: '.$field_email."\n";
$body_message .= 'Message: '.$field_message;

$headers = 'From: '.$field_email."\r\n";
$headers .= 'Reply-To: '.$field_email."\r\n";

$mail_status = mail($mail_to, $subject, $body_message, $headers);

if ($mail_status) { ?>
	<script type="text/javascript">
		alert('Thank you for the message. We will contact you shortly.');
		window.location = 'index.html#contactUs';
	</script>
<?php
}
else { ?>
	<script type="text/javascript">
		alert('Message failed. Please, send an email to gordon@template-help.com');
		window.location = 'index.html#contactUs';
	</script>
<?php
}
?>