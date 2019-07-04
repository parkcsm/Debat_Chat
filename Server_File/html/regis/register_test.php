<?php

$name = $_POST["name"];
$email = $_POST["email"];
$user_name = $_POST["user_name"];
$password = $_POST["password"];

$host = "localhost";
$db_user = "root";
$db_password = "vlfks12";
$db_name = "user_db(register-login)";

$con = mysqli_connect($host,$db_user,$db_password,$db_name);

if($con)
{
        echo "Connection Success...";
}
else
{
        echo "Connection failed...";
}



$sql = "select * from user_info where email like '".$email."';";

$result = mysqli_query($con,$sql);
$response = array();

if(mysqli_num_rows($result)>0)
{
        $code = "reg_failed";
        $message = "User already exist.....";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);
}
else
{
        $sql = "insert into user_info values('".$name."','".$email."','".$user_name."','".$password."');";
        $result = mysqli_query($con,$sql);

        $code = "reg_success";
        $message = "Thank you for register with us. Now you can login.....";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

}

mysqli_close($con);




?>
