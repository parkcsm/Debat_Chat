<?php
include "init.php";

$id = $_POST["id"];
$password = $_POST["password"];

$sql = "select name, email from user_info where id like '".$id."' and password like '".$password."';";

$result = mysqli_query($con,$sql);
$response = array();

if(mysqli_num_rows($result)>0)
{
	$row = mysqli_fetch_row($result);
	$name = $row[0];
	$email = $row[1];
	$code = "로그인에 성공했습니다!";
	array_push($response,array("code"=>$code,"name"=>$name,"email"=>$email));
	echo json_encode($response);
}
else
{
	$code = "login_failed";
	$message = "로그인에 실패했습니다. 다시시도해주세요!";
	array_push($response,array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}

mysqli_close($con);

?>
