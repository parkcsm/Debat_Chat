<?php
include "init.php";


$name = $_POST["name"];
$email = $_POST["email"];
$id = $_POST["id"];
$password = $_POST["password"];

$sql = "select * from user_info where id like '".$id."';";

$result = mysqli_query($con,$sql);
$response = array();

if(mysqli_num_rows($result)>0)
{
	$code = "reg_failed";
	$message = "사용자가 이미 존재합니다. 다른 아이디로 다시시도해주세요!";
	array_push($response, array("code"=>$code,"message"=>$message));
	echo json_encode($response);
}
else
{
	$sql = "insert into user_info(name,email,id,password) values('".$name."','".$email."','".$id."','".$password."');";
	$result = mysqli_query($con,$sql);

	$code = "reg_success";
	$message = "회원가입해주셔서 감사합니다! 이제 로그인하실수 있습니다!";
	array_push($response, array("code"=>$code,"message"=>$message));
	echo json_encode($response);

}

mysqli_close($con);
?>
