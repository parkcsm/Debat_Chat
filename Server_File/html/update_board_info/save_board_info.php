<?php
include "init_board_info.php";

$Writer = $_POST["Writer"];
$Post_Image_Url = $_POST["Post_Image_Url"];
$Post_Text = $_POST["Post_Text"];
$Regdate = $_POST["Regdate"];

$response = array();

        $sql = "insert into post_info(Writer,Post_Image_Url,Post_Text,Regdate) values('".$Writer."','".$Post_Image_Url."','".$Post_Text."','".$Regdate."');";
        $result = mysqli_query($con,$sql);

        $code = "successfully posted!";
        $message = "글이 성공적으로 등록되었습니다.)";
        array_push($response, array("code"=>$code,"message"=>$message));
        echo json_encode($response);

mysqli_close($con);

?>

